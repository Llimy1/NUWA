package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponse;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectChannelService {


    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectMessageRepository directMessageRepository;
    private final DirectChannelRepository directChannelRepository;

    private final DirectMessageQueryService directMessageQueryService;



    // 다이렉트 채널 생성
    @Transactional
    public String createDirectChannel(String email, DirectChannelRequest directChannelRequest) {
        log.info("다이렉트 채널 생성");

        Long joinMemberId = directChannelRequest.joinMemberId();
        Long workSpaceId = directChannelRequest.workSpaceId();;

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace workSpace = createWorkSpaceMember.getWorkSpace();

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember joinWorkSpaceMember = workSpaceMemberRepository.findById(joinMemberId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        // 워크스페이스 존재하고 멤버도 전부 존재하면 채널 저장
        Direct direct = Direct.createDirectChannel(workSpace, createWorkSpaceMember, joinWorkSpaceMember);

        Direct saveDirect = directChannelRepository.save(direct);

        // RoomId 반환
        return saveDirect.getRoomId();
    }

    // 다이렉트 채널 리스트 조회
    // TODO: 다이렉트 채널 조회 -> 마지막 메세지 시간이 제일 빠른 순으로(?)
    // TODO: test code
    // 현재는 생성 순으로 반환
    public Slice<DirectChannelListResponse> directChannelSliceSortByCreatedDate(Long workSpaceId, Pageable pageable) {
        log.info("채널 리스트 반환 (생성 시간 순)");
        return directChannelRepository.findDirectChannelByWorkSpaceId(workSpaceId, pageable)
                .map(direct -> DirectChannelListResponse.builder()
                .roomId(direct.getRoomId())
                .name(direct.getName())
                .createMemberName(direct.getCreateMember().getName())
                .joinMemberName(direct.getJoinMember().getName())
                .build());
    }

    // TODO: test code
    // 채널 리스트 반환 -> 채팅방 별로 마지막 채팅, 인원, 채팅방 정보, 읽지 않은 채팅 카운트
    public DirectChannelListResponseDto directChannelSliceSortByMessageCreateDate(String email, Long workSpaceId, Pageable pageable) {

        List<DirectChannelResponseDto> directChannelResponseDtoList = new ArrayList<>();

        // 워크스페이스 리스트 가져오기
        Slice<Direct> directChannelByWorkSpaceId =
                directChannelRepository.findDirectChannelByWorkSpaceId(workSpaceId, pageable);

        // 리스트를 순회하면서 해당 id에 맞는 마지막 채팅과 시간 가져오기
        directChannelByWorkSpaceId.forEach(direct -> {
            PageRequest pageRequest = PageRequest.of(0, 1);

            Long unReadCount = directMessageQueryService.countUnReadMessage(direct.getRoomId(), email);

            // 마지막 채팅과 시간 가져오기
            Slice<DirectMessage> directMessageByRoomIdOrderByCreatedAt =
                    directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAt(direct.getRoomId(), pageRequest);

            if (directMessageByRoomIdOrderByCreatedAt.hasContent()) {
                DirectMessage directMessage =
                        directMessageByRoomIdOrderByCreatedAt.getContent().get(0);

                DirectChannelResponseDto directChannelResponseDto = DirectChannelResponseDto.builder()
                        .roomId(direct.getRoomId())
                        .name(direct.getName())
                        .workSpaceId(direct.getId())
                        .createMemberName(direct.getCreateMember().getName())
                        .joinMemberName(direct.getJoinMember().getName())
                        .unReadCount(unReadCount)
                        .lastMessage(directMessage.getContent())
                        .createdAt(directMessage.getCreatedAt())
                        .build();
                directChannelResponseDtoList.add(directChannelResponseDto);
            }
        });

        // 해당 DTO에 맵핑된 생성 시간으로 재정렬하여 최근 메세지 순으로 채팅방 정렬
        List<DirectChannelResponseDto> sortByCreatedAtResponseList = directChannelResponseDtoList.stream()
                .sorted(Comparator.comparing(DirectChannelResponseDto::createdAt).reversed())
                .toList();

        // 페이징 정보 추가
        boolean hasNext = directChannelByWorkSpaceId.hasNext();
        int currentPage = directChannelByWorkSpaceId.getNumber();
        int pageSize = directChannelByWorkSpaceId.getSize();

        return DirectChannelListResponseDto.builder()
                .directChannelResponseDtoList(sortByCreatedAtResponseList)
                .hasNext(hasNext)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
