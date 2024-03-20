package org.project.nuwabackend.nuwa.file.service;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.file.dto.response.FileInfoResponseDto;
import org.project.nuwabackend.nuwa.file.dto.response.TopSevenFileInfoResponseDto;
import org.project.nuwabackend.nuwa.file.type.FileType;
import org.project.nuwabackend.nuwa.file.type.FileUploadType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.SEARCH_FILE_NAME_NOT_FOUND;
import static org.project.nuwabackend.nuwa.domain.multimedia.QFile.file;
import static org.project.nuwabackend.nuwa.domain.workspace.QWorkSpaceMember.workSpaceMember;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    // 파일 조건 별 조회
    public Slice<FileInfoResponseDto> fileList(Long workSpaceId, String fileExtension, FileUploadType fileUploadType, Pageable pageable) {
        log.info("파일 조회 Query");

        List<FileInfoResponseDto> fileInfoResponseDtoList = jpaQueryFactory.select(fileConstructorDto())
                .from(file)
                .join(file.workSpaceMember, workSpaceMember)
                .where(
                        workSpaceIdEq(workSpaceId),
                        fileUploadTypeEq(fileUploadType),
                        fileExtensionEq(fileExtension),
                        directTypeNe()

                )
                .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return convertSlice(fileInfoResponseDtoList, pageable);
    }

    // 파일 검색
    public Slice<FileInfoResponseDto> searchFileName(Long workSpaceId, String fileName, String fileExtension, FileUploadType fileUploadType, Pageable pageable) {
        log.info("파일 검색 Query");
        List<FileInfoResponseDto> searchFileInfoResponseDtoList = jpaQueryFactory.select(fileConstructorDto())
                .from(file)
                .join(file.workSpaceMember, workSpaceMember)
                .where(
                        searchFileName(fileName),
                        workSpaceIdEq(workSpaceId),
                        fileUploadTypeEq(fileUploadType),
                        fileExtensionEq(fileExtension),
                        directTypeNe()

                )
                .fetch();

        return convertSlice(searchFileInfoResponseDtoList, pageable);
    }

    // 최근 생성 시간 순 7개 파일 조회
    public List<TopSevenFileInfoResponseDto> topSevenFileOrderByCreatedAt(Long workSpaceId) {
        return jpaQueryFactory.select(topSevenFileInfoResponseDto())
                .from(file)
                .where(
                        workSpaceIdEq(workSpaceId),
                        directTypeNe())
                .orderBy(file.createdAt.desc())
                .offset(0)
                .limit(7)
                .fetch();
    }

    // FileInfoResponseDto Constructor
    private ConstructorExpression<FileInfoResponseDto> fileConstructorDto() {
        return Projections.constructor(FileInfoResponseDto.class,
                file.id.as("fileId"),
                file.url.as("fileUrl"),
                file.fileName.as("fileName"),
                file.fileSize.as("fileSize"),
                file.fileExtension.as("fileExtension"),
                file.fileUploadType.as("fileUploadType"),
                file.fileType.as("fileType"),
                workSpaceMember.id.as("fileMemberUploadId"),
                workSpaceMember.name.as("fileMemberUploadName"),
                file.createdAt);
    }

    // TopSevenFileInfoResponseDto Constructor
    private ConstructorExpression<TopSevenFileInfoResponseDto> topSevenFileInfoResponseDto() {
        return Projections.constructor(TopSevenFileInfoResponseDto.class,
                file.id.as("fileId"),
                file.fileName.as("fileName"),
                file.fileSize.as("fileSize"),
                file.fileExtension.as("fileExtension"),
                file.createdAt);
    }

    private BooleanExpression workSpaceIdEq(Long workSpaceId) {
        return file.workSpace.id.eq(workSpaceId);
    }

    private BooleanExpression fileUploadTypeEq(FileUploadType fileUploadType) {
        return fileUploadType != null ? file.fileUploadType.eq(fileUploadType) : null;
    }

    private BooleanExpression fileExtensionEq(String extension) {
        return hasText(extension) ? file.fileExtension.eq(extension) : null;
    }

    private BooleanExpression searchFileName(String fileName) {

        if (hasText(fileName)) {
            return file.fileName.contains(fileName);
        } else {
            throw new IllegalArgumentException(SEARCH_FILE_NAME_NOT_FOUND.getMessage());
        }
    }

    private BooleanExpression directTypeNe() {
        return file.fileType.ne(FileType.DIRECT);
    }

    // Slice로 변환
    private Slice<FileInfoResponseDto> convertSlice(List<FileInfoResponseDto> fileInfoResponseDtoList, Pageable pageable) {
        // hasNext 판단
        boolean hasNext = fileInfoResponseDtoList.size() > pageable.getPageSize();
        List<FileInfoResponseDto> fileContent = hasNext ? fileInfoResponseDtoList.subList(0, pageable.getPageSize()) : fileInfoResponseDtoList;

        return new SliceImpl<>(fileContent, pageable, hasNext);
    }

    // 정렬
    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
        return new OrderSpecifier(order, fieldPath);
    }

    // 정렬
    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "createdAt": OrderSpecifier<?> orderCreatedAt = getSortedColumn(direction, file, "createdAt");
                        ORDERS.add(orderCreatedAt);
                        break;
                    case "fileName": OrderSpecifier<?> orderName = getSortedColumn(direction, file, "fileName");
                        ORDERS.add(orderName);
                        break;
                    case "fileSize": OrderSpecifier<?> orderSize = getSortedColumn(direction, file, "fileSize");
                        ORDERS.add(orderSize);
                        break;
                    case "fileExtension": OrderSpecifier<?> orderExtension = getSortedColumn(direction, file, "fileExtension");
                        ORDERS.add(orderExtension);
                    default:
                        break;
                }
            }
        }
        return ORDERS;
    }
}
