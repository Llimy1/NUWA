package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Chat;
import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.ChatChannelInfoResponseDto;
import org.project.nuwabackend.dto.channel.response.ChatChannelListResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.CREATE_CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatChannelService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;

    private final ChatChannelRepository chatChannelRepository;
    private final ChatJoinMemberRepository chatJoinMemberRepository;

    // 채팅 채널 생성
    @Transactional
    public String createChatChannel(String email, ChatChannelRequestDto chatChannelRequestDto) {
        Long workSpaceId = chatChannelRequestDto.workSpaceId();
        String chatChannelName = chatChannelRequestDto.chatChannelName();

        // 해당 워크스페이스에 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace workSpace = createWorkSpaceMember.getWorkSpace();

        Chat chatChannel = Chat.createChatChannel(chatChannelName, workSpace, createWorkSpaceMember);

        Chat saveChatChannel = chatChannelRepository.save(chatChannel);

        return saveChatChannel.getRoomId();
    }

    // 채팅 채널 참가
    public void joinChatChannel(ChatChannelJoinMemberRequestDto chatChannelJoinMemberRequestDto) {
        List<Long> joinMemberIdList = chatChannelJoinMemberRequestDto.joinMemberIdList();
        Long chatChannelId = chatChannelJoinMemberRequestDto.chatChannelId();

        Chat chatChannel = chatChannelRepository.findById(chatChannelId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        List<ChatJoinMember> chatJoinMemberList = new ArrayList<>();
        for (Long id : joinMemberIdList) {
            WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

            ChatJoinMember chatJoinMember = ChatJoinMember.createChatJoinMember(workSpaceMember, chatChannel);
            chatJoinMemberList.add(chatJoinMember);
        }

        chatJoinMemberRepository.saveAll(chatJoinMemberList);
    }

    // 채팅 채널 INFO
    public ChatChannelInfoResponseDto joinChatChannelInfo(Long workSpaceId, String roomId) {
        Chat findChat = chatChannelRepository.findByWorkSpaceIdAndRoomId(workSpaceId, roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        return ChatChannelInfoResponseDto.builder()
                .channelId(findChat.getId())
                .channelName(findChat.getName())
                .build();

    }

    // 채팅방 리스트 조회
    public Slice<ChatChannelListResponseDto> chatChannelList(Long workSpaceId, Pageable pageable) {
        return chatChannelRepository.findByWorkSpaceId(workSpaceId, pageable)
                .map(chat -> ChatChannelListResponseDto.builder()
                        .workSpaceId(workSpaceId)
                        .channelId(chat.getId())
                        .name(chat.getName())
                        .roomId(chat.getRoomId())
                        .build());
    }

    // 워크스페이스 id에 해당되는 모든 채팅 채널 삭제
    // TODO: integrated test code
    @Transactional
    public void deleteChatChannelList(Long workSpaceId) {
        chatChannelRepository.deleteChatByWorkSpaceId(workSpaceId);
    }

    // 채팅 채널 삭제 -> 생성한 인원만 가능
    // TODO: integrated test code
    @Transactional
    public void deleteChatChannel(Long workSpaceId, String email, String roomId) {
        Chat findChatChannel = chatChannelRepository.findByRoomIdAndEmailAndWorkSpaceId(roomId, email ,workSpaceId)
                .orElseThrow(() -> new NotFoundException(CREATE_CHANNEL_NOT_FOUND));

        chatChannelRepository.delete(findChatChannel);
    }
}
