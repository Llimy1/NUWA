package org.project.nuwabackend.api.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.auth.request.SingUpRequestDto;
import org.project.nuwabackend.dto.auth.request.SocialSignUpRequestDto;
import org.project.nuwabackend.dto.auth.response.MemberIdResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.auth.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignUpController {

    private final GlobalService globalService;
    private final SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SingUpRequestDto singUpRequestDto) {
        log.info("SignUp API 호출");
        Long memberId = signUpService.signUp(singUpRequestDto);
        MemberIdResponseDto memberIdResponseDto = new MemberIdResponseDto(memberId);

        GlobalSuccessResponseDto<Object> signUpSuccessResponse =
                globalService.successResponse(SIGNUP_SUCCESS.getMessage(), memberIdResponseDto);

        return ResponseEntity.status(CREATED).body(signUpSuccessResponse);
    }

    @PostMapping("/signup/social")
    public ResponseEntity<Object> socialSignUp(@RequestBody SocialSignUpRequestDto socialSignUpRequestDto) {
        log.info("Social SignUp API 호출");
        Long memberId = signUpService.socialSignUp(socialSignUpRequestDto);
        MemberIdResponseDto memberIdResponseDto = new MemberIdResponseDto(memberId);

        GlobalSuccessResponseDto<Object> socialSignUpSuccessResponse =
                globalService.successResponse(SOCIAL_LOGIN_SUCCESS.getMessage(), memberIdResponseDto);

        return ResponseEntity.status(CREATED).body(socialSignUpSuccessResponse);
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<Object> duplicateNickname(@RequestParam(name = "nickname") String nickname) {
        log.info("Nickname Duplicate API 호출");
        signUpService.duplicateNickname(nickname);

        GlobalSuccessResponseDto<Object> nicknameUseResponse =
                globalService.successResponse(NICKNAME_USE_OK.getMessage(), null);
        return ResponseEntity.status(OK).body(nicknameUseResponse);
    }

    @GetMapping("/check/email")
    public ResponseEntity<Object> duplicateEmail(@RequestParam(name = "email") String email) {
        log.info("Email Duplicate API 호출");
        signUpService.duplicateNickname(email);

        GlobalSuccessResponseDto<Object> emailUseResponse =
                globalService.successResponse(EMAIL_USE_OK.getMessage(), null);
        return ResponseEntity.status(OK).body(emailUseResponse);
    }
}
