package org.project.nuwabackend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.Inquire;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.dto.IntroductionInquiryMailRequestDto;
import org.project.nuwabackend.dto.ServiceInquiryMailRequestDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.InquireRepository;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.type.InquireType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final InquireRepository inquireRepository;
    private final MemberRepository memberRepository;

    @Value("${spring.mail.username}")
    private String from;
    String nuwalogo = "src/main/resources/nuwalogo.png";
    String instagram = "src/main/resources/instagram.png";
    String facebook = "src/main/resources/facebook.png";
    String kakaotalk = "src/main/resources/kakaotalk.png";



    @Transactional
    public Long answerMail(String email, IntroductionInquiryMailRequestDto mailDto) throws Exception {
        log.info("도입 문의 메일 발송 서비스");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        // 멤버 조회
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));

        messageHelper.setFrom(from);  // 보낸 사람
        messageHelper.setTo("vvvv4449@gmail.com");  // 받는 사람 관리자 메일주소
        messageHelper.setSubject("도입문의 " + mailDto.name());  // 제목

        // 내용
        String htmlContent = buildHtmlContent(mailDto);

        messageHelper.setText(htmlContent, true);

        // 이미지 파일 추가
        addInlineImage("nuwalogo", nuwalogo, messageHelper);
        addInlineImage("instagram", instagram, messageHelper);
        addInlineImage("facebook", facebook, messageHelper);
        addInlineImage("kakaotalk", kakaotalk, messageHelper);

        mailSender.send(message);
        Inquire inquire = new Inquire(InquireType.INTRODUCTION, findMember);
        Inquire saveInquire = inquireRepository.save(inquire);
        return saveInquire.getId();

    }
    private String buildHtmlContent(IntroductionInquiryMailRequestDto mailDto) {
        String msgg = "<table id=\"conWrap\" style=\"display: block; max-width: 450px; padding: 0 12px; margin: 0 auto; width: 100%;\">\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <table class=\"conTop\" style=\"width: 100%;\">\n" +
                "        <tr>\n" +
                "          <td style=\"padding-bottom: 32px;\">\n" +
                "            <h1><a href=\"#\"><img src=\"cid:nuwalogo\" alt=\"Nowa\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\"></a></h1>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td class=\"contents\" style=\"color: #242424; letter-spacing: -0.028rem; margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\">\n" +
                "            <p class=\"contentsText\" style=\"font-weight: 600; font-size: 22px; padding-bottom: 12px; margin-bottom: 12px; border-bottom: 1px solid #00000010;\">\n" +
                "              도입문의사항\n" +
                "            </p>\n" +
                "              <table class=\"inquiry\" style=\"font-size: 14px;\">\n";
        msgg += "              <tr><td style=\"padding-bottom: 12px;\">이름:  " + mailDto.name() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">지역:  " + mailDto.countryRegion() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">회사명:  " + mailDto.companyName() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">직책:  " + mailDto.position() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">전화번호:  " + mailDto.phoneNumber() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">이메일:  " + mailDto.email() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">부서명:  " + mailDto.departmentName() + "</td></tr>\n" +
                "              <tr><td style=\"padding-bottom: 12px;\">인원수:  " + mailDto.numberOfPeople() + "</td></tr>\n" +
                "            </table>\n" +
                "            <p class=\"contentsDetail\" style=\"font-size: 14px; font-weight: 300; line-height: 1.2; padding-top: 12px; margin-top: 12px; border-top: 1px solid #00000010;\">\n" +
                "              <span class=\"contentsBold\" style=\"display: block; padding-bottom: 8px; font-weight: 600;\">내용</span> " + mailDto.content() +
                "            </p>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <table class=\"conBtm\" style=\"width: 100%;\">\n" +
                "        <tr>\n" +
                "          <td>\n" +
                "            <h1><a href=\"#\"><img src=\"cid:nuwalogo\" alt=\"Nowa\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\"></a></h1>\n" +
                "          </td>\n" +
                "          <td class=\"sns\" style=\"text-align: right;\">\n" +
                "            <a href=\"#\" style=\"margin-right: 10px;\"><img src=\"cid:instagram\" alt=\"Instagram\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\"></a>\n" +
                "            <a href=\"#\" style=\"margin-right: 10px;\"><img src=\"cid:facebook\" alt=\"Facebook\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\"></a>\n" +
                "            <a href=\"#\"><img src=\"cid:kakaotalk\" alt=\"Kakaotalk\" style=\"margin: 0; padding: 0; font-family: 'pretendard'; box-sizing: border-box;\"></a>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td colspan=\"2\" class=\"btmR2\" style=\"padding: 16px 0;\">\n" +
                "            <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px;\">블로그</a>\n" +
                "            <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px;\">구독취소</a>\n" +
                "            <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px;\">정책</a>\n" +
                "            <a href=\"#\" style=\"margin-right: 12px; text-decoration: none; color: #afafaf; font-size: 12px;\">고객지원센터</a>\n" +
                "            <a href=\"#\" style=\"text-decoration: none; color: #afafaf; font-size: 12px;\">NUWA커뮤니티</a>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td colspan=\"2\" id=\"copyright\" class=\"btmR3\">\n" +
                "            @2024 NUWA Technologies LLC, a Salesforce company <br>\n" +
                "            415 Mission Street, San Francisco CA94105 <br>\n" +
                "            All rights reserved.\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n";
        return msgg;
    }

    @Transactional
    public Long answerMail(String email, ServiceInquiryMailRequestDto mailDto, List<MultipartFile> multipartFileList) throws Exception {
        log.info("서비스 문의 메일 발송 서비스");

        // 멤버 조회
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setFrom(from);  // 보낸 사람
        messageHelper.setTo("vvvv4449@gmail.com");  // 받는 사람 관리자 메일주소
        messageHelper.setSubject("서비스문의 " + mailDto.subject());  // 제목

        // 파일첨부
        for (MultipartFile multipartFile : multipartFileList) {
            String filename = multipartFile.getOriginalFilename();

            if (filename != null && !filename.trim().isEmpty()) {
                ByteArrayResource source = new ByteArrayResource(multipartFile.getBytes());
                messageHelper.addAttachment(filename, source);
            }
        }

        // 내용
        String htmlContent = buildHtmlContent(mailDto,multipartFileList);
        messageHelper.setText(htmlContent, true);

        // 이미지 파일 추가
        addInlineImage("nuwalogo", nuwalogo, messageHelper);
        addInlineImage("instagram", instagram, messageHelper);
        addInlineImage("facebook", facebook, messageHelper);
        addInlineImage("kakaotalk", kakaotalk, messageHelper);

        mailSender.send(message);
        Inquire inquire = new Inquire(InquireType.SERVICE, findMember);
        Inquire saveInquire = inquireRepository.save(inquire);

        return saveInquire.getId();
    }

    private String buildHtmlContent(ServiceInquiryMailRequestDto mailDto, List<MultipartFile> multipartFileList) {

        StringBuilder msgg = new StringBuilder();

            msgg.append("<body><table style=\"margin: 0 auto; padding: 0 12px; max-width: 450px; width: 100%; box-sizing: border-box; font-family: 'pretendard';\">")
                .append("<tr><td style=\"text-align: center;\"><img src=\"cid:nuwalogo\" alt=\"Nuwa\" style=\"margin: 0; padding: 0; box-sizing: border-box;\"></td></tr>")
                .append("<tr><td style=\"color: #242424; letter-spacing: -0.028rem; padding-bottom: 32px;\">")
                .append("<p style=\"font-weight: 600; font-size: 22px; margin: 0; padding-bottom: 12px; border-bottom: 1px solid #00000010;\">관리자님!<br>서비스가 계속해서 성장할 수 있도록 회원님들의 문의사항을 적극 반영할 수 있도록 노력해야 합니다 🙌</p>")
                .append("</td></tr>")
                .append("<tr><td style=\"font-size: 14px; font-weight: 300; line-height: 1.2;\">")
                .append(mailDto.email()).append("<br>")
                .append("<br>")
                .append("<strong>내용:</strong><br>")
                .append(mailDto.content())
                .append("</td></tr>");

            msgg.append("<br>")
                .append("<img src=\"cid:nuwalogo\" alt=\"Nuwa\" style=\"margin: 0; padding: 0; box-sizing: border-box;\">")
                .append("</td></tr>")

                .append("<tr><td style=\"padding-top: 12px; text-align: center;\">")
                .append("<a href=\"#\" style=\"border-radius: 50px; padding: 12px 0; text-decoration: none; color: #fff; font-size: 18px; font-weight: 600; background: linear-gradient(90deg, #5158FF 0%, rgba(81, 88, 255, 0.80) 100%);\">NUWA 열기</a>")
                .append("</td></tr>")
                .append("<tr><td style=\"padding-top: 16px; text-align: center;\">")
                .append("<img src=\"cid:instagram\" alt=\"Instagram\" style=\"vertical-align: middle;\">")
                .append("<img src=\"cid:facebook\" alt=\"Facebook\" style=\"vertical-align: middle; margin-left: 10px;\">")
                .append("<img src=\"cid:kakaotalk\" alt=\"Kakaotalk\" style=\"vertical-align: middle; margin-left: 10px;\">")
                .append("</td></tr>")
                .append("<tr><td style=\"font-size: 12px; color: #afafaf; padding-top: 16px; text-align: center;\">")
                .append("블로그 | 구독취소 | 정책 | 고객지원센터 | NUWA커뮤니티")
                .append("</td></tr>")
                .append("<tr><td style=\"font-size: 12px; color: #afafaf; padding-top: 16px; text-align: center;\">")
                .append("@2024 NUWA Technologies LLC, a Salesforce company<br>415 Mission Street, San Francisco CA94105<br>All rights reserved.")
                .append("</td></tr>")
                .append("</table></body>");

        return msgg.toString();


    }

    private void addInlineImage(String imageId, String imagePath, MimeMessageHelper messageHelper) throws Exception {
        FileSystemResource imageResource = new FileSystemResource(new File(imagePath));
        messageHelper.addInline(imageId, imageResource);
    }
}