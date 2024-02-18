package org.project.nuwabackend.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Message Service Test")
@ExtendWith(MockitoExtension.class)
class DirectMessageServiceTest {

    @Mock
    DirectMessageRepository directMessageRepository;
    @Mock
    DirectChannelService directChannelService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    DirectMessageService directMessageService;

    DirectMessageRequestDto directMessageRequestDto;
    DirectMessageResponseDto directMessageResponseDto;
    DirectMessage directMessage;
    Member member;

    @BeforeEach
    void setup() {

        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";


        String directChannelRoomId = "directChannelRoomId";
        Long senderId = 1L;
        String directChannelContent = "directChannelContent";
        Long readCount = 1L;
        String senderName = "senderName";


        directMessageRequestDto = DirectMessageRequestDto.builder()
                .roomId(directChannelRoomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(directChannelContent)
                .build();

        directMessageResponseDto = DirectMessageResponseDto.builder()
                .roomId(directChannelRoomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(directChannelContent)
                .readCount(readCount)
                .createdAt(LocalDateTime.now())
                .build();

        directMessage = DirectMessage.createDirectMessage(
                directChannelRoomId,
                senderId,
                senderName,
                directChannelContent,
                readCount);

        member = Member.createMember(
                email,
                password,
                nickname,
                phoneNumber);

    }


    @Test
    @DisplayName("[Service] Save Direct Message Test")
    void saveDirectMessageTest() {
        //given
        given(directMessageRepository.save(any()))
                .willReturn(directMessage);

        //when
        directMessageService.saveDirectMessage(directMessageResponseDto);

        //then
        verify(directMessageRepository).save(directMessage);
    }

    // TODO: 메세지 보내기 테스트 다시 작성
//    @Test
//    @DisplayName("[Service] Send Message Test")
//    void sendMessageTest() {
//        //given
//        String accessToken = "accessToken";
//        String email = "email";
//        boolean isAllConnected = true;
//
//        given(jwtUtil.getEmail(anyString()))
//                .willReturn(email);
//
//        given(memberRepository.findByEmail(anyString()))
//                .willReturn(Optional.of(member));
//
//        given(directChannelService.isAllConnected(anyString()))
//                .willReturn(isAllConnected);
//        given(directMessageService.sendMessage(anyString(), any()))
//                .willReturn(directMessageDto);
//
//        //when
//        DirectMessageDto directMessageDto1 = directMessageService.sendMessage(accessToken, directMessageDto);
//
//        //then
//        assertThat(directMessageDto1.content()).isEqualTo(directMessageDto.content());
//        assertThat(directMessageDto1.senderId()).isEqualTo(directMessageDto.senderId());
//        assertThat(directMessageDto1.senderName()).isEqualTo(directMessageDto.senderName());
//        assertThat(directMessageDto1.roomId()).isEqualTo(directMessageDto.roomId());
//        assertThat(directMessageDto1.readCount()).isEqualTo(directMessageDto.readCount());
//
//    }

    // TODO: 메세지 조회
//    @Test
//    @DisplayName("[Service] Direct Message Slice Sort By Date Test")
//    void directMessageSliceSortByDateTest() {
//        //given
//        //when
//        //then
//    }
}