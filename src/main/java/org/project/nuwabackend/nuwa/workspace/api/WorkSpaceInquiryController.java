package org.project.nuwabackend.nuwa.workspace.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.FavoriteWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.workspace.WorkSpaceInfoResponse;
import org.project.nuwabackend.nuwa.workspacemember.dto.response.WorkSpaceMemberInfoResponse;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.project.nuwabackend.global.response.type.SuccessMessage.FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.READ_MY_WORK_SPACE_MEMBER_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.READ_MY_WORK_SPACE_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceInquiryController {

    private final WorkSpaceInquiryService workSpaceInquiryService;
    private final GlobalService globalService;

    // 본인이 속한 워크스페이스 조회
    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkspaces(@MemberEmail String email) {
        log.info("워크스페이스 조회 API 호출");
        List<WorkSpaceInfoResponse> workSpaceInfoResponse = workSpaceInquiryService.getWorkspacesByMemberEmail(email);

        GlobalSuccessResponseDto<Object> getWorkspacesSuccessResponse = globalService.successResponse(
                READ_MY_WORK_SPACE_SUCCESS.getMessage(),
                workSpaceInfoResponse);
        return ResponseEntity.status(OK).body(getWorkspacesSuccessResponse);
    }

    @GetMapping("/workspace/{workSpaceId}/members")
    public ResponseEntity<Object> getWorkspaceMembers(@PathVariable Long workSpaceId) {
        List<WorkSpaceMemberInfoResponse> WorkSpaceMemberInfoResponse = workSpaceInquiryService.getAllMembersByWorkspace(workSpaceId);

        GlobalSuccessResponseDto<Object> getWorkspacesSuccessResponse = globalService.successResponse(
                READ_MY_WORK_SPACE_MEMBER_SUCCESS.getMessage(),
                WorkSpaceMemberInfoResponse);
        return ResponseEntity.status(OK).body(getWorkspacesSuccessResponse);
    }

    @GetMapping("/workspace/{workSpaceId}/member")
    public ResponseEntity<Object> individualWorkSpaceMemberInfo(
            @MemberEmail String email,
            @PathVariable(value = "workSpaceId") Long workSpaceId) {

        IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfoResponseDto =
                workSpaceInquiryService.individualWorkSpaceMemberInfo(email, workSpaceId);

        GlobalSuccessResponseDto<Object> individualWorkSpaceMemberInfoSuccessResponse =
                globalService.successResponse(
                        INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS.getMessage(),
                        individualWorkSpaceMemberInfoResponseDto);

        return ResponseEntity.status(OK).body(individualWorkSpaceMemberInfoSuccessResponse);
    }

    // 워크스페이스 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 많은 순으로)
    @GetMapping("/workspace/{workSpaceId}/favorite")
    public ResponseEntity<Object> favoriteWorkSpaceMemberList(@MemberEmail String email,
                                                              @PathVariable(value = "workSpaceId") Long workSpaceId) {
        log.info("워크스페이스 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 많은 순으로) API 호출");
        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtoList =
                workSpaceInquiryService.favoriteWorkSpaceMemberList(email, workSpaceId);
        GlobalSuccessResponseDto<Object> favoriteWorkSpaceMemberReturnSuccessResponse =
                globalService.successResponse(FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS.getMessage(), favoriteWorkSpaceMemberInfoResponseDtoList);
        return ResponseEntity.status(OK).body(favoriteWorkSpaceMemberReturnSuccessResponse);
    }
}
