package org.project.nuwabackend.nuwa.workspace.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.WorkSpaceContentSearchResponseDto;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceContentSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_CONTENT_SEARCH_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceContentSearchController {

    private final WorkSpaceContentSearchService workSpaceContentSearchService;
    private final GlobalService globalService;

    @GetMapping("/workspace/search/content/{workSpaceId}")
    public ResponseEntity<Object> workSpaceContentSearch(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                         @RequestParam(value = "fileName", required = false) String fileName,
                                                         @RequestParam(value = "canvasTitle", required = false) String canvasTitle) {
        log.info("전체 검색 API");
        WorkSpaceContentSearchResponseDto workSpaceContentSearchResponseDto =
                workSpaceContentSearchService.workSpaceContentSearch(workSpaceId, fileName, canvasTitle);

        GlobalSuccessResponseDto<Object> workSpaceContentSearchSuccessResponse =
                globalService.successResponse(WORK_SPACE_CONTENT_SEARCH_SUCCESS.getMessage(), workSpaceContentSearchResponseDto);

        return ResponseEntity.status(OK).body(workSpaceContentSearchSuccessResponse);
    }
}
