package org.project.nuwabackend.nuwa.workspacemember.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.nuwa.workspacemember.dto.response.WorkSpaceMemberIdResponse;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.workspacemember.service.WorkSpaceMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.JOIN_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkSpaceMemberController {

    private final WorkSpaceMemberService workSpaceMemberService;
    private final GlobalService globalService;

    @PostMapping("/workspace/join")
    public ResponseEntity<Object> joinWorkSpaceMember(@MemberEmail String email,
                                                      @RequestBody WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 참가 API 호출");
        Long workSpaceMemberId = workSpaceMemberService.joinWorkSpaceMember(email, workSpaceMemberRequestDto);
        WorkSpaceMemberIdResponse workSpaceMemberIdResponse = new WorkSpaceMemberIdResponse(workSpaceMemberId);
        GlobalSuccessResponseDto<Object> joinWorkSpaceMemberSuccessResponse =
                globalService.successResponse(JOIN_WORK_SPACE_SUCCESS.getMessage(), workSpaceMemberIdResponse);

        return ResponseEntity.status(OK).body(joinWorkSpaceMemberSuccessResponse);
    }

    // 워크스페이스 멤버 정보 편집
    @PatchMapping("/workspace/{workSpaceId}/member")
    public ResponseEntity<Object> updateWorkSpaceMember(@MemberEmail String email,
                                                        @PathVariable(value = "workSpaceId") Long workSpaceId,
                                                        @RequestBody WorkSpaceMemberUpdateRequestDto workSpaceMemberUpdateRequestDto) {
        log.info("워크스페이스 멤버 정보 편집 API 호출");
        workSpaceMemberService.updateWorkSpaceMember(email, workSpaceId, workSpaceMemberUpdateRequestDto);

        GlobalSuccessResponseDto<Object> workSpaceMemberUpdateSuccessResponse =
                globalService.successResponse(WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(workSpaceMemberUpdateSuccessResponse);
    }
}
