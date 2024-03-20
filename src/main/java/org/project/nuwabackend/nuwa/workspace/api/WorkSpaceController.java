package org.project.nuwabackend.nuwa.workspace.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.workspace.WorkSpaceIdResponse;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.workspacemember.service.WorkSpaceMemberService;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceService;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.CREATE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_INFO_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_MEMBER_STATUS_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_MEMBER_TYPE_RELOCATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_QUIT_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_USE_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final WorkSpaceMemberService workSpaceMemberService;
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

    @GetMapping("/workspace/check")
    public ResponseEntity<Object> duplicateWorkSpaceName(@RequestParam(name = "workSpaceName") String workSpaceName) {
        log.info("워크스페이스 이름 중복 확인 API 호출");
        workSpaceService.duplicateWorkSpaceName(workSpaceName);

        GlobalSuccessResponseDto<Object> workSpaceUseSuccessResponse =
                globalService.successResponse(WORK_SPACE_USE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceUseSuccessResponse);
    }

    // 워크스페이스 정보 편집
    @PatchMapping("/workspace/{workSpaceId}")
    public ResponseEntity<Object> updateWorkSpace(@MemberEmail String email,
                                                  @PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @RequestBody WorkSpaceUpdateRequestDto workSpaceUpdateRequestDto) {
        log.info("워크스페이스 정보 편집 API 호출");
        workSpaceService.updateWorkSpace(email, workSpaceId, workSpaceUpdateRequestDto);

        GlobalSuccessResponseDto<Object> workSpaceUpdateSuccessResponse =
                globalService.successResponse(WORK_SPACE_INFO_UPDATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceUpdateSuccessResponse);
    }

    // 워크스페이스 상태 편집
    @PatchMapping("/workspace/{workSpaceId}/member/status")
    public ResponseEntity<Object> updateWorkSpaceMemberStatus(@MemberEmail String email,
                                                              @PathVariable(value = "workSpaceId") Long workSpaceId,
                                                              @RequestParam(value = "workSpaceMemberStatus") String workSpaceMemberStatus) {
        log.info("워크스페이스 상태 편집 API 호출");
        workSpaceService.updateWorkSpaceMemberStatus(email, workSpaceId, workSpaceMemberStatus);

        GlobalSuccessResponseDto<Object> updateWorkSpaceMemberStatusSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_STATUS_UPDATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(updateWorkSpaceMemberStatusSuccessResponse);
    }

    // 워크스페이스 권한 변경
    @PatchMapping("/workspace/{workSpaceMemberId}/relocate")
    public ResponseEntity<Object> relocateCreateWorkSpaceMemberType(@PathVariable(value = "workSpaceMemberId") Long workSpaceMemberId,
                                                                    @MemberEmail String email,
                                                                    @RequestParam(value = "workSpaceId") Long workSpaceId,
                                                                    @RequestParam(value = "type") WorkSpaceMemberType type) {
        log.info("권한 변경 API 호출");
        workSpaceService.relocateCreateWorkSpaceMemberType(workSpaceMemberId, email, workSpaceId, type);

        GlobalSuccessResponseDto<Object> workSpaceMemberTypeRelocateSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_TYPE_RELOCATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceMemberTypeRelocateSuccessResponse);
    }

    // 워크스페이스 나가기
    @PatchMapping("/workspace/{workSpaceId}/quit")
    public ResponseEntity<Object> quitWorkSpaceMember(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                      @MemberEmail String email) {
        log.info("워크스페이스 나가기 API 호출");
        workSpaceMemberService.quitWorkSpaceMember(email, workSpaceId);

        GlobalSuccessResponseDto<Object> quitWorkSpaceMemberSuccessResponse =
                globalService.successResponse(WORK_SPACE_QUIT_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(quitWorkSpaceMemberSuccessResponse);
    }
}
