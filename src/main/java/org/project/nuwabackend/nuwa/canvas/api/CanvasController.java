package org.project.nuwabackend.nuwa.canvas.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.canvas.dto.request.CanvasRequestDto;
import org.project.nuwabackend.nuwa.canvas.dto.response.CanvasResponseDto;
import org.project.nuwabackend.global.annotation.custom.CustomPageable;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.canvas.service.CanvasService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.project.nuwabackend.global.response.type.SuccessMessage.CANVAS_CREATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CANVAS_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CANVAS_SEARCH_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.DELETE_CANVAS_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.UPDATE_CANVAS_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CanvasController {

    private final CanvasService canvasService;
    private final GlobalService globalService;

    @PostMapping("/canvas/{workSpaceId}")
    public ResponseEntity<Object> createCanvas(@MemberEmail String email,
                                               @PathVariable(value = "workSpaceId") Long workSpaceId,
                                               @RequestBody CanvasRequestDto canvasRequestDto) {
        log.info("캔버스 생성 API");
        canvasService.createCanvas(email, workSpaceId, canvasRequestDto);
        GlobalSuccessResponseDto<Object> canvasCreateSuccessResponseDto =
                globalService.successResponse(CANVAS_CREATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(CREATED).body(canvasCreateSuccessResponseDto);
    }

    @GetMapping("/canvas/{workSpaceId}")
    public ResponseEntity<Object> canvasList(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                             @RequestParam(value = "workSpaceMemberId", required = false) Long workSpaceMemberId,
                                             @CustomPageable Pageable pageable) {
        log.info("캔버스 조회 API");
        Slice<CanvasResponseDto> canvasResponseDtoSlice =
                canvasService.canvasList(workSpaceId, workSpaceMemberId, pageable);

        GlobalSuccessResponseDto<Object> canvasSliceSuccessResponseDto =
                globalService.successResponse(CANVAS_LIST_RETURN_SUCCESS.getMessage(), canvasResponseDtoSlice);

        return ResponseEntity.status(OK).body(canvasSliceSuccessResponseDto);
    }

    @GetMapping("canvas/workspace/{workSpaceId}")
    public ResponseEntity<Object> canvasListByWorkSpace(@PathVariable(value = "workSpaceId") Long workSpaceId) {
        log.info("캔버스 조회 (워크스페이스) API");
        List<CanvasResponseDto> canvasResponseDtoList = canvasService.canvasListByWorkSpace(workSpaceId);

        GlobalSuccessResponseDto<Object> canvasListSuccessResponseDto =
                globalService.successResponse(CANVAS_LIST_RETURN_SUCCESS.getMessage(), canvasResponseDtoList);

        return ResponseEntity.status(OK).body(canvasListSuccessResponseDto);
    }

    @GetMapping("/canvas/search/{workSpaceId}")
    public ResponseEntity<Object> canvasSearchList(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                             @RequestParam(value = "title", required = false) String title,
                                             @CustomPageable Pageable pageable) {
        log.info("캔버스 검색 API");
        Slice<CanvasResponseDto> canvasResponseDtoSlice =
                canvasService.searchCanvas(workSpaceId, title, pageable);

        GlobalSuccessResponseDto<Object> canvasSliceSuccessResponseDto =
                globalService.successResponse(CANVAS_SEARCH_LIST_RETURN_SUCCESS.getMessage(), canvasResponseDtoSlice);

        return ResponseEntity.status(OK).body(canvasSliceSuccessResponseDto);
    }

    @PatchMapping("/canvas/{workSpaceId}")
    public ResponseEntity<Object> updateCanvas(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                               @RequestParam(value = "canvasId") String canvasId,
                                               @RequestBody CanvasRequestDto canvasRequestDto) {
        log.info("캔버스 수정 API");
        canvasService.updateCanvas(workSpaceId, canvasId, canvasRequestDto);

        GlobalSuccessResponseDto<Object> canvasUpdateSuccessResponseDto =
                globalService.successResponse(UPDATE_CANVAS_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(canvasUpdateSuccessResponseDto);
    }

    @DeleteMapping("/canvas/{workSpaceId}")
    public ResponseEntity<Object> deleteCanvas(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                               @MemberEmail String email,
                                               @RequestParam(value = "canvasId") String canvasId) {
        log.info("캔버스 삭제 API");
        canvasService.deleteCanvas(email, workSpaceId, canvasId);

        GlobalSuccessResponseDto<Object> canvasUpdateSuccessResponseDto =
                globalService.successResponse(DELETE_CANVAS_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(canvasUpdateSuccessResponseDto);
    }
}
