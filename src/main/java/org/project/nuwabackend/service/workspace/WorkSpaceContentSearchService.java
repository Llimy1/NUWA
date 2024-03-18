package org.project.nuwabackend.service.workspace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceContentSearchResponseDto;
import org.project.nuwabackend.dto.canvas.response.CanvasResponseDto;
import org.project.nuwabackend.dto.file.response.FileSearchInfoResponseDto;
import org.project.nuwabackend.service.canvas.CanvasService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.SEARCH_TITLE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceContentSearchService {

    private final CanvasService canvasService;
    private final FileService fileService;

    public WorkSpaceContentSearchResponseDto workSpaceContentSearch(Long workSpaceId, String fileName, String canvasTitle) {
        List<FileSearchInfoResponseDto> fileSearchInfoResponseDto =
                fileService.fileSearchAll(workSpaceId, fileName);

        List<CanvasResponseDto> canvasResponseDtoList =
                canvasService.searchAllCanvas(workSpaceId, canvasTitle);

        if (fileSearchInfoResponseDto == null && canvasResponseDtoList == null) {
            throw new IllegalArgumentException(SEARCH_TITLE_NOT_FOUND.getMessage());
        }

        return WorkSpaceContentSearchResponseDto.builder()
                .fileSearchInfoResponseDtoList(fileSearchInfoResponseDto)
                .canvasResponseDtoList(canvasResponseDtoList)
                .build();

    }
}
