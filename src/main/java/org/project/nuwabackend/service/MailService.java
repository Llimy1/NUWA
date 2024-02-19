package org.project.nuwabackend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.Inquire;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.dto.IntroductionInquiryMailRequestDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.InquireRepository;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.type.InquireType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

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
        log.info("도입문의 메일 발송 서비스");

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
        FileSystemResource nowaLogoResource = new FileSystemResource(new File(nuwalogo));
        messageHelper.addInline("nuwalogo", nowaLogoResource);
        // 이미지 파일 추가 (instagram)
        FileSystemResource instagramResource = new FileSystemResource(new File(instagram));
        messageHelper.addInline("instagram", instagramResource);

        // 이미지 파일 추가 (facebook)
        FileSystemResource facebookResource = new FileSystemResource(new File(facebook));
        messageHelper.addInline("facebook", facebookResource);

        // 이미지 파일 추가 (kakaotalk)
        FileSystemResource kakaotalkResource = new FileSystemResource(new File(kakaotalk));
        messageHelper.addInline("kakaotalk", kakaotalkResource);

        System.out.println("----------------------------------");
        System.out.println(mailDto.name().toString());
        System.out.println(message.toString());

        mailSender.send(message);
        Inquire inquire = new Inquire(InquireType.INTRODUCTION, findMember);
        Inquire saveInquire = inquireRepository.save(inquire);
        return saveInquire.getId();

    }


    private String buildHtmlContent(IntroductionInquiryMailRequestDto mailDto) {
        StringBuilder sb = new StringBuilder();

//        sb.append("<html><head></head><body><div id=\"conWrap\" style=\"display: flex; max-width: 450px; padding: 0 12px; margin: 0 auto; flex-flow: column; gap: 96px;\">");
        sb.append("<div>");//html
        sb.append("<div>");//conwrap
        sb.append("<div>"); //class=\"conTop\" style=\"display: flex; flex-flow: column; gap: 32px;\">"
        sb.append("<h1><a href=\"#\"><img src=\"cid:nuwalogo\" alt=\"Nuwa\"></a></h1>");
        sb.append("<div class=\"contents\" style=\"color: #242424; letter-spacing: -0.028rem;\">");
        sb.append("<p class=\"contentsText\" style=\"font-weight: 600; font-size: 22px; padding-bottom: 12px; margin-bottom: 12px; border-bottom: 1px solid #00000010;\">도입문의</p>");
        sb.append("<div class=\"inquiry\" style=\"font-size: 14px;\">");
        sb.append("<p>이름: ").append(mailDto.name()).append("</p>");
        sb.append("<p>이름: ").append(mailDto.countryRegion()).append("</p>");
        sb.append("<p>회사명: ").append(mailDto.companyName()).append("</p>");
        sb.append("<p>직책: ").append(mailDto.position()).append("</p>");
        sb.append("<p>전화번호: ").append(mailDto.phoneNumber()).append("</p>");
        sb.append("<p>이메일: ").append(mailDto.email()).append("</p>");
        sb.append("<p>부서명: ").append(mailDto.departmentName()).append("</p>");
        sb.append("<p>인원수: ").append(mailDto.numberOfPeople()).append("</p>");
        sb.append("</div>");
        sb.append("<p class=\"contentsDetail\" style=\"font-size: 14px; font-weight: 300; line-height: 1.2; padding-top: 12px; margin-top: 12px; border-top: 1px solid #00000010;\">");
        sb.append("<span class=\"contentsBold\" style=\"display: block; padding-bottom: 8px; font-weight: 600;\">내용</span>");
        sb.append(mailDto.content());
        sb.append("</p>");
        sb.append("</div><!-- contents -->");
        sb.append("</div><!-- //conTop -->");
        sb.append("<p>");
        sb.append("</p>");

        sb.append("<div>");

        sb.append("<div>");
        sb.append("<div class=\"btmR1\" style=\"display: flex; flex-flow: row nowrap; justify-content: space-between; align-items: center;\">");
        sb.append("<h1><a href=\"#\"><img src=\"cid:nuwalogo\" alt=\"Nuwa\"></a></h1>");
        sb.append("<div class=\"sns\">");
        sb.append("<a href=\"#\"><img src=\"cid:instagram\" alt=\"Instagram\"></a>");
        sb.append("<a href=\"#\"><img src=\"cid:facebook\" alt=\"Facebook\"></a>");
        sb.append("<a href=\"#\"><img src=\"cid:kakaotalk\" alt=\"Kakaotalk\"></a>");
        sb.append("</div>");
        sb.append("</div>");

        sb.append("<div class=\"btmR2\" style=\"display: flex; padding: 16px 0;\">");
        sb.append("<a href=\"#\" style=\"display: flex; text-decoration: none; color: #afafaf; font-size: 12px;\">블로그</a>");
        sb.append("<a href=\"#\" style=\"display: flex; text-decoration: none; color: #afafaf; font-size: 12px;\">구독취소</a>");
        sb.append("<a href=\"#\" style=\"display: flex; text-decoration: none; color: #afafaf; font-size: 12px;\">정책</a>");
        sb.append("<a href=\"#\" style=\"display: flex; text-decoration: none; color: #afafaf; font-size: 12px;\">고객지원센터</a>");
        sb.append("<a href=\"#\" style=\"display: flex; text-decoration: none; color: #afafaf; font-size: 12px;\">NUWA커뮤니티</a>");
        sb.append("</div>");
        sb.append("<div id=\"copyright\" class=\"btmR3\" style=\"text-decoration: none; color: #afafaf; font-size: 12px; font-weight: 300;\">");
        sb.append("@2024 NUWA Technologies LLC, a Salesforce company <br>");
        sb.append("415 Mission Street, San Francisco CA94105 <br>");
        sb.append("All rights reserved.");
        sb.append("</div>");
        sb.append("</div><!-- //conBtm -->");
        sb.append("</div>");


        sb.append("</div><!-- //conWrap -->");
        sb.append("</div>");

        return sb.toString();
    }






}


