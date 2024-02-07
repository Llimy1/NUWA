package org.project.nuwabackend.api.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.service.message.DirectMessageService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectMessageController {

    private final SimpMessagingTemplate template;
    private final DirectMessageService directMessageService;

    private static final String DIRECT_DESTINATION = "/sub/direct";

    // 처음으로 다이렉트 채널 생성을 했을 때
    // TODO: 다이렉트 생성 API 후 roomId -> 그 id로 connect 할 때
    // TODO: 초기 메세지 정하고 알림 모두에게 전송
    @MessageMapping("/direct/enter")
    public void directEnter(DirectMessageRequestDto directMessageRequestDto) {
        log.trace("Direct Message Enter");
        String roomId = directMessageRequestDto.roomId();
        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                directMessageRequestDto);
    }

    // 메세지 보낼 때
    @MessageMapping("/direct/send")
    public void directSend(@Header("Authorization") String accessToken, DirectMessageRequestDto directMessageRequestDto) {
        log.trace("Direct Message Send");
        String roomId = directMessageRequestDto.roomId();

        DirectMessageResponseDto directMessageResponseDto = directMessageService.directSendMessage(accessToken, directMessageRequestDto);

        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                directMessageResponseDto);

        directMessageService.saveDirectMessage(accessToken, directMessageRequestDto);
    }

    // 채팅방을 아예 떠날 때
    @MessageMapping("/direct/quit")
    public void directQuit(DirectMessageRequestDto directMessageRequestDto) {
        log.trace("Direct Message Quit");
        String roomId = directMessageRequestDto.roomId();
        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                directMessageRequestDto);
    }
}
