package org.project.nuwabackend.nuwa.invite.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.redis.InvitationLinkRedis;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.invite.dto.request.InvitationLinkRequest;
import org.project.nuwabackend.nuwa.invite.dto.request.InviteByMailRequest;
import org.project.nuwabackend.nuwa.workspace.dto.response.workspace.WorkSpaceInfoResponse;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.global.response.type.ErrorMessage;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspace.repository.WorkSpaceRepository;
import org.project.nuwabackend.nuwa.invite.repository.InvitationLinkRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.REDIS_TOKEN_NOT_FOUND_INFO;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationLinkService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final InvitationLinkRedisRepository invitationLinkRedisRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;


    // S3에 호스팅된 이미지 url
    String nuwaLogoUrl = "https://nuwabucket.s3.ap-northeast-2.amazonaws.com/mail/nuwalogo.png";
    String instagramUrl = "https://nuwabucket.s3.ap-northeast-2.amazonaws.com/mail/instagram.png";
    String facebookUrl = "https://nuwabucket.s3.ap-northeast-2.amazonaws.com/mail/facebook.png";
    String kakaotalkUrl = "https://nuwabucket.s3.ap-northeast-2.amazonaws.com/mail/kakaotalk.png";



    @Transactional
    public String getOrCreateInvitationLink(String email, InvitationLinkRequest invitationLinkRequest) {

        Long workSpaceId = invitationLinkRequest.workSpaceId();

        // 해당 워크스페이스의 멤버인지 확인
        workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        return invitationLinkRedisRepository.findTopByWorkSpaceIdOrderByTokenDesc(workSpaceId)
                .map(invitation -> constructInvitationLink(invitation.getToken()))
                .orElseGet(() -> createNewInvitation(workSpaceId));
    }

    private String createNewInvitation(Long workSpaceId) {
        String token = "join/" + workSpaceId;

        return constructInvitationLink(token);
    }

    private String constructInvitationLink(String token) {
        // 토큰을 Base64로 인코딩
        String encodedToken = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));

        // 인코딩된 토큰을 사용하여 URL 구성
        return "https://nu-wa.online/api/invite/" + encodedToken;
    }


    public WorkSpaceInfoResponse getWorkspaceByToken(String token) {
        log.info("초대 링크 조회 서비스");

        // 토큰으로
        InvitationLinkRedis invitationLink = invitationLinkRedisRepository.findFirstByToken(token)
                .orElseThrow(() -> new NotFoundException(REDIS_TOKEN_NOT_FOUND_INFO));


        // 워크스페이스 찾기
        WorkSpace workSpace = workSpaceRepository.findById(invitationLink.getWorkSpaceId())
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        return WorkSpaceInfoResponse.builder()
                .workspaceId(workSpace.getId())
                .workSpaceName(workSpace.getName())
                .workSpaceImage(workSpace.getImage())
                .workSpaceIntroduce(workSpace.getIntroduce()).build();
    }

    @Transactional
    public List<String> inviteByMail(String email, InviteByMailRequest inviteByMailRequest) throws Exception {
        log.info("초대 링크 이메일 발송 서비스");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        // 멤버 조회 (발송자 정보만 필요)
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));

        // 워크스페이스 ID로 워크스페이스 찾기
        WorkSpace workSpace = workSpaceRepository.findById(inviteByMailRequest.workSpaceId())
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        messageHelper.setFrom(from); // 보낸 사람
        messageHelper.setSubject("NUWA에서 초대가 도착했습니다!");

        // 받는 사람 설정 (여럿)
        List<String> emailAddressList = inviteByMailRequest.emailAddress();
        for (String toEmail : emailAddressList) {
            messageHelper.addTo(toEmail);
        }

        String encodedId = createNewInvitation(inviteByMailRequest.workSpaceId());

        String htmlContent = buildHtmlContent(workSpace, encodedId);
        messageHelper.setText(htmlContent, true);
        mailSender.send(message);

        return emailAddressList;
    }

    private String buildHtmlContent(WorkSpace workSpace, String encodedId) {
        String workspaceName = workSpace.getName();
        String msg = "<table id=\"conWrap\" style=\"display: block; max-width: 450px; padding: 0 12px; margin: 0 auto; width: 100%; font-family: 'pretendard'; box-sizing: border-box;\">\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <table class=\"conTop\" style=\"width: 100%\">\n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                        <h1>\n" +
                "                            <a href=\"\"><img src=\"" + nuwaLogoUrl + "\" alt=\"Nuwa\" /></a>\n" +
                "                        </h1>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td class=\"contents\" style=\"color: #242424; letter-spacing: -0.028rem\">\n" +
                "                        <p class=\"contentsText\" style=\"font-weight: 600; font-size: 22px; padding-bottom: 12px; margin-bottom: 12px\">\n" +
                "                            <span class=\"workspaceName\" style=\"color: #5158ff\">" + workspaceName + "</span>에서 초대가 <br />\n" +
                "                            도착했습니다. 🙌\n" +
                "                        </p>\n" +
                "                        <p class=\"contentsDetail\" style=\"font-size: 14px; font-weight: 300; line-height: 1.2\">\n" +
                "                            좋습니다! 팀이 계속해서 커지고 있습니다<br />\n" +
                "                            <span style=\"color: #5158ff\">" + workspaceName + "</span> 워크스페이스에 참여하여 팀원과 소통하기 위해<br />\n" +
                "                            NUWA로 이동합니다.\n" +
                "                        </p>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"text-align: center\">\n" +
                "                        <a href=\"" + encodedId + "\"" + "class=\"openNuwa\" style=\"width: 100%; display: inline-block; border-radius: 10px; padding: 10px 0px; margin-top: 30px; margin-bottom: 20px; text-align: center; text-decoration: none; color: #fff; font-size: 18px; font-weight: 600; background: linear-gradient(90deg, #5158ff 0%, rgba(81, 88, 255, 0.8) 100%)\">NUWA 열기</a>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <table class=\"conBtm\" style=\"width: 100%\">\n" +
                "                <tr>\n" +
                "                    <td style=\"text-align: left\">\n" +
                "                        <a href=\"#\"><img src=\"" + nuwaLogoUrl + "\" alt=\"Nuwa\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box\" /></a>\n" +
                "                    </td>\n" +
                "                    <td style=\"text-align: right\">\n" +
                "                        <a href=\"#\" style=\"margin-right: 10px\"><img src=\"" + instagramUrl + "\" alt=\"Instagram\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box\" /></a>\n" +
                "                        <a href=\"#\" style=\"margin-right: 10px\"><img src=\"" + facebookUrl + "\" alt=\"Facebook\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box\" /></a>\n" +
                "                        <a href=\"#\"><img src=\"" + kakaotalkUrl + "\" alt=\"Kakaotalk\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box\" /></a>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td colspan=\"2\" class=\"btmR2\" style=\"padding: 16px 0\">\n" +
                "                        <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px\">블로그</a>\n" +
                "                        <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px\">구독취소</a>\n" +
                "                        <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px\">정책</a>\n" +
                "                        <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px\">고객지원센터</a>\n" +
                "                        <a href=\"#\" style=\"text-decoration: none; color: #afafaf; font-size: 12px\">NUWA커뮤니티</a>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td colspan=\"2\" id=\"copyright\" class=\"btmR3\" style=\"color: #afafaf; font-size: 12px\">\n" +
                "                        @2024 NUWA Technologies LLC, a Salesforce company <br />\n" +
                "                        415 Mission Street, San Francisco CA94105 <br />\n" +
                "                        All rights reserved.\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>";
        return msg;
    }
}
