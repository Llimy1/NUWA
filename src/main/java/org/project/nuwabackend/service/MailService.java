package org.project.nuwabackend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.dto.MailRequestDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;



    public void answerMail(MailRequestDto mailDto) throws Exception {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(message,"UTF-8");

//            SimpleMailMessage message = new SimpleMailMessage();
            h.setFrom(from);  // 보낸 사람
            //h.setTo("vvvv4449@gmail.com");  // 받는 사람 관리자 메일주소
            h.setTo("vvvv4449@gmail.com");  // 받는 사람 관리자 메일주소
            h.setSubject("도입문의" + mailDto.name());  // 제목
        // 내용
        String htmlContent = buildHtmlContent(mailDto);
        h.setText(htmlContent, true);
//            h.setText(mailDto.content());  // 내용
            System.out.println("----------------------------------");
            System.out.println(mailDto.name().toString());
            System.out.println(message.toString());

            mailSender.send(message);

    }

    private String buildHtmlContent(MailRequestDto mailDto) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>");
        sb.append("table {");
        sb.append("  width: 100%;");
        sb.append("  border-collapse: collapse;");
        sb.append("}");

        sb.append("th, td {");
        sb.append("  border: 1px solid black;");
        sb.append("  padding: 5px;");
        sb.append("}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>도입문의</h1>");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>이름</th>");
        sb.append("<td>").append(mailDto.name()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>전화번호</th>");
        sb.append("<td>").append(mailDto.phoneNumber()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>이메일</th>");
        sb.append("<td>").append(mailDto.email()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>국가/지역</th>");
        sb.append("<td>").append(mailDto.countryRegion()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>회사명</th>");
        sb.append("<td>").append(mailDto.companyName()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>부서명</th>");
        sb.append("<td>").append(mailDto.departmentName()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>부서명</th>");
        sb.append("<td>").append(mailDto.numberOfPeople()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<th>문의 내용</th>");
        sb.append("<td>").append(mailDto.content()).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
