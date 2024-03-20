package org.project.nuwabackend.nuwa.inquiry.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.inquiry.dto.response.InquireIdResponse;
import org.project.nuwabackend.nuwa.inquiry.dto.request.IntroductionInquiryMailRequestDto;
import org.project.nuwabackend.nuwa.inquiry.dto.request.ServiceInquiryMailRequestDto;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.inquiry.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.project.nuwabackend.global.response.type.SuccessMessage.CREATE_INQUIRY_MAIL_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InquiryEmailController {

    private final GlobalService globalService;
    private final MailService mailService;

        @PostMapping("/mail")
        public ResponseEntity<Object> mail (@MemberEmail String email, @RequestBody IntroductionInquiryMailRequestDto mailDto) throws Exception {
            log.info("도입문의 API 호출");
            Long inquireId = mailService.answerMail(email, mailDto);

            InquireIdResponse inquireIdResponse = new InquireIdResponse(inquireId);

            GlobalSuccessResponseDto<Object> createInquireSuccessResponse =
                    globalService.successResponse(
                            CREATE_INQUIRY_MAIL_SUCCESS.getMessage(),
                            inquireIdResponse);

            return ResponseEntity.status(CREATED).body(createInquireSuccessResponse);
        }

        @PostMapping("/mail/attached")
        public ResponseEntity<Object> mail(@MemberEmail String email,
                                                 @RequestPart(name = "serviceInquiryMailRequestDto") ServiceInquiryMailRequestDto serviceInquiryMailRequestDto,
                                                 @RequestPart(name = "fileList", required = false) List<MultipartFile> multipartFileList) throws Exception {
            log.info("서비스 문의 메일 발송 API 호출");

            Long inquireId = mailService.answerMail(email, serviceInquiryMailRequestDto, multipartFileList);

            InquireIdResponse inquireIdResponse = new InquireIdResponse(inquireId);

            GlobalSuccessResponseDto<Object> createInquireSuccessResponse =
                    globalService.successResponse(CREATE_INQUIRY_MAIL_SUCCESS.getMessage(), inquireIdResponse);

            return ResponseEntity.status(CREATED).body(createInquireSuccessResponse);
        }



}
