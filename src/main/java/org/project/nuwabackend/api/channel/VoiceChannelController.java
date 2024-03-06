package org.project.nuwabackend.api.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.VoiceChannelJoinMemberRequestDto;
import org.project.nuwabackend.dto.channel.request.VoiceChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.VoiceChannelIdResponseDto;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.VoiceChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_VOICE_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.JOIN_VOICE_CHANNEL_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
// TODO: test code 추후 로직 작성 후
public class VoiceChannelController {

    private final VoiceChannelService voiceChannelService;
    private final GlobalService globalService;

    @PostMapping("/channel/voice")
    public ResponseEntity<Object> createChatChannel(@MemberEmail String email, @RequestBody VoiceChannelRequestDto voiceChannelRequestDto) {
        log.info("음성 채널 생성 API");
        Long voiceChannelId = voiceChannelService.createVoiceChannel(email, voiceChannelRequestDto);

        VoiceChannelIdResponseDto voiceChannelIdResponseDto = new VoiceChannelIdResponseDto(voiceChannelId);
        GlobalSuccessResponseDto<Object> createVoiceChannelSuccessResponse =
                globalService.successResponse(
                        CREATE_VOICE_CHANNEL_SUCCESS.getMessage(),
                        voiceChannelIdResponseDto);

        return ResponseEntity.status(CREATED).body(createVoiceChannelSuccessResponse);
    }

    @PostMapping("/channel/voice/join")
    public ResponseEntity<Object> joinChatChannel(@RequestBody VoiceChannelJoinMemberRequestDto voiceChannelJoinMemberRequestDto) {
        log.info("채팅 채널 참여 API");
        voiceChannelService.joinVoiceChannel(voiceChannelJoinMemberRequestDto);
        GlobalSuccessResponseDto<Object> joinVoiceChannelSuccessResponse =
                globalService.successResponse(JOIN_VOICE_CHANNEL_SUCCESS.getMessage(), null);

        return ResponseEntity.status(CREATED).body(joinVoiceChannelSuccessResponse);
    }
}
