package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.MailRequestDto;
import org.project.nuwabackend.service.MailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QNAEmail {
    private final MailService mailService;
    @PostMapping("/mail")
    public String mail (@RequestBody MailRequestDto mailDto
                      ) throws Exception {
        mailService.answerMail(mailDto);
        return mailDto.name() + "님 곧 답변드리도록 하겠습니다. 감사합니다 :)\n";
    }
}
