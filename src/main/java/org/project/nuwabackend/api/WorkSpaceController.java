package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceIdResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceMemberIdResponse;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.WorkSpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.JOIN_WORK_SPACE_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final GlobalService globalService;

    @PostMapping("/workspace")
    public ResponseEntity<Object> createWorkSpace(@MemberEmail String email, @RequestBody WorkSpaceRequestDto workSpaceRequestDto) {
        log.info("워크스페이스 생성 API 호출");
        Long workSpaceId = workSpaceService.createWorkSpace(email, workSpaceRequestDto);

        WorkSpaceIdResponse workSpaceIdResponse = new WorkSpaceIdResponse(workSpaceId);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                globalService.successResponse(
                        CREATE_WORK_SPACE_SUCCESS.getMessage(),
                        workSpaceIdResponse);

        return ResponseEntity.status(CREATED).body(createWorkSpaceSuccessResponse);
    }

    @PostMapping("/workspace/member")
    public ResponseEntity<Object> joinWorkSpaceMember(@MemberEmail String email,
                                                      @RequestBody WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 참가 API 호출");
        Long workSpaceMemberId = workSpaceService.joinWorkSpaceMember(email, workSpaceMemberRequestDto);
        WorkSpaceMemberIdResponse workSpaceMemberIdResponse = new WorkSpaceMemberIdResponse(workSpaceMemberId);
        GlobalSuccessResponseDto<Object> joinWorkSpaceMemberSuccessResponse =
                globalService.successResponse(JOIN_WORK_SPACE_SUCCESS.getMessage(), workSpaceMemberIdResponse);

        return ResponseEntity.status(OK).body(joinWorkSpaceMemberSuccessResponse);
    }
}
