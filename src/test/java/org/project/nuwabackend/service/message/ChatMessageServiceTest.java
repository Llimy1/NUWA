package org.project.nuwabackend.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.mongo.ChatMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageListResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageResponseDto;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.websocket.repository.ChatMessageRepository;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.project.nuwabackend.nuwa.websocket.service.ChatMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Chat Message Service Test")
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    ChatChannelRepository chatChannelRepository;
    @Mock
    ChatJoinMemberRepository chatJoinMemberRepository;
    @Mock
    ChatMessageRepository chatMessageRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    ChatMessageService chatMessageService;

    Member member;
    WorkSpace workSpace;
    WorkSpaceMember workSpaceMember;

    ChatMessageResponseDto chatMessageResponseDto;
    String email = "abcd@gmail.com";

    @BeforeEach
    void setup() {
        String workSpaceName = "nuwa";
        String workSpaceImage = "N";
        String workSpaceIntroduce = "개발";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        String password = "abcd1234";
        String nickname = "nickname";
        String phoneNumber = "01000000000";

        member = Member.createMember(email, password, nickname, phoneNumber);

        String workSpaceMemberName = "abcd";
        String workSpaceMemberJob = "backend";
        String workSpaceMemberImage = "B";

        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                member, workSpace);

        Long workSpaceId = 1L;
        String roomId = "chat";
        Long senderId = 1L;
        String senderName = "senderName";
        String content = "chatMessage";
        List<String> rawString = new ArrayList<>(List.of("rawString"));

        chatMessageResponseDto = ChatMessageResponseDto.builder()
                .messageId("messageId")
                .workSpaceId(workSpaceId)
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .senderImage("image")
                .content(content)
                .rawString(rawString)
                .isEdited(false)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("[Service] Save Chat Message Test")
    void saveChatMessageTest() {
        //given
        Long workSpaceId = chatMessageResponseDto.getWorkSpaceId();
        String roomId = chatMessageResponseDto.getRoomId();
        Long senderId = chatMessageResponseDto.getSenderId();
        String senderName = chatMessageResponseDto.getSenderName();
        String senderImage = chatMessageResponseDto.getSenderImage();
        String content = chatMessageResponseDto.getContent();
        List<String> rawString = chatMessageResponseDto.getRawString();
        LocalDateTime createdAt = chatMessageResponseDto.getCreatedAt();

        ChatMessage chatMessage =
                ChatMessage.createChatMessage(workSpaceId, roomId, senderId, senderName, content, senderImage, rawString, MessageType.TEXT, createdAt);

        given(chatMessageRepository.save(any()))
                .willReturn(chatMessage);

        //when
        chatMessageService.saveChatMessage(chatMessageResponseDto);

        //then
        verify(chatMessageRepository).save(chatMessage);
    }

    @Test
    @DisplayName("[Service] Chat Message Slice Sort By Date")
    void chatMessageSliceSortByDate() {
        //given
        Long workSpaceId = chatMessageResponseDto.getWorkSpaceId();
        String roomId = chatMessageResponseDto.getRoomId();
        Long senderId = chatMessageResponseDto.getSenderId();
        String senderName = chatMessageResponseDto.getSenderName();
        String senderImage = chatMessageResponseDto.getSenderImage();
        String content = chatMessageResponseDto.getContent();
        List<String> rawString = chatMessageResponseDto.getRawString();
        LocalDateTime createdAt = chatMessageResponseDto.getCreatedAt();

        ChatMessage chatMessage =
                ChatMessage.createChatMessage(workSpaceId, roomId, senderId, senderName, content, senderImage, rawString, MessageType.TEXT, createdAt);

        List<ChatMessage> chatMessageList = new ArrayList<>(List.of(chatMessage));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        SliceImpl<ChatMessage> chatMessageSlice =
                new SliceImpl<>(chatMessageList, pageRequest, false);

        Slice<ChatMessageListResponseDto> chatMessageListResponseDtoSlice =
                chatMessageSlice.map(chat -> ChatMessageListResponseDto.builder()
                        .messageId(chatMessage.getId())
                        .workSpaceId(chatMessage.getWorkSpaceId())
                        .roomId(chatMessage.getRoomId())
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImage(chatMessage.getSenderImage())
                        .content(chatMessage.getContent())
                        .isEdited(chatMessage.getIsEdited())
                        .isDeleted(chatMessage.getIsDeleted())
                        .rawString(rawString)
                        .messageType(chatMessage.getMessageType())
                        .createdAt(chatMessage.getCreatedAt())
                        .build());

        given(chatMessageRepository.findChatMessageByRoomIdOrderByCreatedAtDesc(anyString(), any()))
                .willReturn(chatMessageSlice);

        //when
        Slice<ChatMessageListResponseDto> chatMessageListResponseDtoList =
                chatMessageService.chatMessageSliceSortByDate(roomId, pageRequest);

        //then
        assertThat(chatMessageListResponseDtoList.getContent())
                .containsAll(chatMessageListResponseDtoSlice.getContent());
        assertThat(chatMessageListResponseDtoList.getNumber())
                .isEqualTo(chatMessageListResponseDtoSlice.getNumber());
        assertThat(chatMessageListResponseDtoList.getSize())
                .isEqualTo(chatMessageListResponseDtoSlice.getSize());
    }
}