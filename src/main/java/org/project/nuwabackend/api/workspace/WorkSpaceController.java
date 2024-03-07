package org.project.nuwabackend.api.workspace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.response.FavoriteWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.dto.workspace.response.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceIdResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceMemberIdResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceInfoResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceMemberInfoResponse;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.workspace.WorkSpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.JOIN_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.READ_MY_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.READ_MY_WORK_SPACE_MEMBER_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_INFO_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_MEMBER_QUIT_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_MEMBER_STATUS_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_MEMBER_TYPE_RELOCATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_USE_SUCCESS;
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

    @PostMapping("/workspace/join")
    public ResponseEntity<Object> joinWorkSpaceMember(@MemberEmail String email,
                                                      @RequestBody WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 참가 API 호출");
        Long workSpaceMemberId = workSpaceService.joinWorkSpaceMember(email, workSpaceMemberRequestDto);
        WorkSpaceMemberIdResponse workSpaceMemberIdResponse = new WorkSpaceMemberIdResponse(workSpaceMemberId);
        GlobalSuccessResponseDto<Object> joinWorkSpaceMemberSuccessResponse =
                globalService.successResponse(JOIN_WORK_SPACE_SUCCESS.getMessage(), workSpaceMemberIdResponse);

        return ResponseEntity.status(OK).body(joinWorkSpaceMemberSuccessResponse);
    }

    // 본인이 속한 워크스페이스 조회
    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkspaces(@MemberEmail String email) {
        log.info("워크스페이스 조회 API 호출");
        List<WorkSpaceInfoResponse> workSpaceInfoResponse = workSpaceService.getWorkspacesByMemberEmail(email);

        GlobalSuccessResponseDto<Object> getWorkspacesSuccessResponse = globalService.successResponse(
                READ_MY_WORK_SPACE_SUCCESS.getMessage(),
                workSpaceInfoResponse);
        return ResponseEntity.status(OK).body(getWorkspacesSuccessResponse);
    }
//
//    @GetMapping("/workspaces/mine")
//    public ResponseEntity<Object> getMyWorkSpaces(@MemberEmail String email) {
//        log.info("워크스페이스 조회 API 호출2");
//        List<WorkSpace> myWorkSpaces = workSpaceService.findWorkspacesByMemberEmail(email);
//        WorkSpaceInfoResponse WorkSpaceInfoResponse = new WorkSpaceInfoResponse(myWorkSpaces);
//
//        GlobalSuccessResponseDto<Object> getMyWorkspacesSuccessResponse = globalService.successResponse(
//                READ_MY_WORK_SPACE_SUCCESS.getMessage(),
//                WorkSpaceInfoResponse);
//        return ResponseEntity.ok(getMyWorkspacesSuccessResponse);
//    }

    @GetMapping("/workspace/{workSpaceId}/members")
    public ResponseEntity<Object> getWorkspaceMembers(@PathVariable Long workSpaceId) {
        List<WorkSpaceMemberInfoResponse> WorkSpaceMemberInfoResponse = workSpaceService.getAllMembersByWorkspace(workSpaceId);

        GlobalSuccessResponseDto<Object> getWorkspacesSuccessResponse = globalService.successResponse(
                READ_MY_WORK_SPACE_MEMBER_SUCCESS.getMessage(),
                WorkSpaceMemberInfoResponse);
        return ResponseEntity.status(OK).body(getWorkspacesSuccessResponse);
    }

    @GetMapping("/workspace/check")
    public ResponseEntity<Object> duplicateWorkSpaceName(@RequestParam(name = "workSpaceName") String workSpaceName) {
        log.info("워크스페이스 이름 중복 확인 API 호출");
        workSpaceService.duplicateWorkSpaceName(workSpaceName);

        GlobalSuccessResponseDto<Object> workSpaceUseSuccessResponse =
                globalService.successResponse(WORK_SPACE_USE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceUseSuccessResponse);
    }

    @GetMapping("/workspace/{workSpaceId}/member")
    public ResponseEntity<Object> individualWorkSpaceMemberInfo(
            @MemberEmail String email,
            @PathVariable(value = "workSpaceId") Long workSpaceId) {

        IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfoResponseDto =
                workSpaceService.individualWorkSpaceMemberInfo(email, workSpaceId);

        GlobalSuccessResponseDto<Object> individualWorkSpaceMemberInfoSuccessResponse =
                globalService.successResponse(
                INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS.getMessage(),
                        individualWorkSpaceMemberInfoResponseDto);

        return ResponseEntity.status(OK).body(individualWorkSpaceMemberInfoSuccessResponse);
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

    // 워크스페이스 멤버 정보 편집
    @PatchMapping("/workspace/{workSpaceId}/member")
    public ResponseEntity<Object> updateWorkSpaceMember(@MemberEmail String email,
                                                        @PathVariable(value = "workSpaceId") Long workSpaceId,
                                                        @RequestBody WorkSpaceMemberUpdateRequestDto workSpaceMemberUpdateRequestDto) {
        log.info("워크스페이스 멤버 정보 편집 API 호출");
        workSpaceService.updateWorkSpaceMember(email, workSpaceId, workSpaceMemberUpdateRequestDto);

        GlobalSuccessResponseDto<Object> workSpaceMemberUpdateSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceMemberUpdateSuccessResponse);
    }

    // 워크스페이스 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 많은 순으로)
    @GetMapping("/workspace/{workSpaceId}/favorite")
    public ResponseEntity<Object> favoriteWorkSpaceMemberList(@MemberEmail String email,
                                                              @PathVariable(value = "workSpaceId") Long workSpaceId) {
        log.info("워크스페이스 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 많은 순으로) API 호출");
        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtoList =
                workSpaceService.favoriteWorkSpaceMemberList(email, workSpaceId);
        GlobalSuccessResponseDto<Object> favoriteWorkSpaceMemberReturnSuccessResponse =
                globalService.successResponse(FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS.getMessage(), favoriteWorkSpaceMemberInfoResponseDtoList);
        return ResponseEntity.status(OK).body(favoriteWorkSpaceMemberReturnSuccessResponse);
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
    // TODO: test code
    @PatchMapping("/workspace/{workSpaceMemberId}/relocate")
    public ResponseEntity<Object> relocateCreateWorkSpaceMemberType(@PathVariable(value = "workSpaceMemberId") Long workSpaceMemberId,
                                                                    @MemberEmail String email,
                                                                    @RequestParam(value = "workSpaceId") Long workSpaceId) {
        log.info("권한 변경 API 호출");
        workSpaceService.relocateCreateWorkSpaceMemberType(workSpaceMemberId, email, workSpaceId);

        GlobalSuccessResponseDto<Object> workSpaceMemberTypeRelocateSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_TYPE_RELOCATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceMemberTypeRelocateSuccessResponse);
    }

    // 워크스페이스 나가기
    // TODO : test code
    @PatchMapping("/workspace/{workSpaceId}/quit")
    public ResponseEntity<Object> quitWorkSpaceMember(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                      @MemberEmail String email) {
        log.info("워크스페이스 나가기 API 호출");
        workSpaceService.quitWorkSpaceMember(email, workSpaceId);

        GlobalSuccessResponseDto<Object> quitWorkSpaceMemberSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_QUIT_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(quitWorkSpaceMemberSuccessResponse);
    }
}
