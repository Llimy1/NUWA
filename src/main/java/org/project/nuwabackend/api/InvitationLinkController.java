package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.invite.request.InvitationLinkRequest;
import org.project.nuwabackend.dto.invite.response.InvitationLinkResponse;
import org.project.nuwabackend.dto.invite.request.InviteByMailRequest;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceInfoResponse;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.InvitationLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_INVITATION_LINK_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.READ_INVITATION_LINK_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_INQUIRY_MAIL_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvitationLinkController {

    private final InvitationLinkService invitationLinkService;
    private final GlobalService globalService;

    @PostMapping("/invite")
    public ResponseEntity<Object> generateInvitation(@MemberEmail String email, @RequestBody InvitationLinkRequest invitationLinkRequest) {
        log.info("초대 링크 발급 API 호출");
        String link = invitationLinkService.getOrCreateInvitationLink(email, invitationLinkRequest);

        InvitationLinkResponse invitationLinkResponse = new InvitationLinkResponse(link);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                globalService.successResponse(
                        CREATE_INVITATION_LINK_SUCCESS.getMessage(),
                        invitationLinkResponse);

        return ResponseEntity.status(CREATED).body(createWorkSpaceSuccessResponse);
    }

    @GetMapping("/invite")
    public ResponseEntity<Object> getInvitation(@RequestParam("token") String token) {
        log.info("초대 링크 조회 API 호출");

        WorkSpaceInfoResponse workSpaceInfoResponse = invitationLinkService.getWorkspaceByToken(token);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                globalService.successResponse(
                        READ_INVITATION_LINK_SUCCESS.getMessage(),
                        workSpaceInfoResponse);

        return ResponseEntity.status(CREATED).body(createWorkSpaceSuccessResponse);
    }


    @PostMapping("/invite/link")
    public ResponseEntity<Object> getInvitation(@MemberEmail String email, @RequestBody InviteByMailRequest inviteByMailRequest) throws Exception{
        log.info("초대 링크 이메일 발송 API 호출");

        List<String> InvitationByMailResponse = invitationLinkService.inviteByMail(email, inviteByMailRequest);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                globalService.successResponse(
                        CREATE_INQUIRY_MAIL_SUCCESS.getMessage(),
                        InvitationByMailResponse);

        return ResponseEntity.status(CREATED).body(createWorkSpaceSuccessResponse);
    }
}
