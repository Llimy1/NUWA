package org.project.nuwabackend.nuwa.channel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.project.nuwabackend.nuwa.domain.channel.ChatJoinMember;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelInfoResponseDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelListResponseDto;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.CHAT_JOIN_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

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
    public Long createChatChannel(String email, ChatChannelRequestDto chatChannelRequestDto) {
        Long workSpaceId = chatChannelRequestDto.workSpaceId();
        String chatChannelName = chatChannelRequestDto.chatChannelName();

        // 해당 워크스페이스에 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace workSpace = createWorkSpaceMember.getWorkSpace();

        Chat chatChannel = Chat.createChatChannel(chatChannelName, workSpace, createWorkSpaceMember);

        Chat saveChatChannel = chatChannelRepository.save(chatChannel);

        return saveChatChannel.getId();
    }

    // 채팅 채널 참가
    @Transactional
    public void joinChatChannel(ChatChannelJoinMemberRequestDto chatChannelJoinMemberRequestDto) {
        List<Long> joinMemberIdList = chatChannelJoinMemberRequestDto.joinMemberIdList();
        Long chatChannelId = chatChannelJoinMemberRequestDto.chatChannelId();

        Chat chatChannel = chatChannelRepository.findById(chatChannelId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        List<ChatJoinMember> chatJoinMemberList = new ArrayList<>();
        for (Long id : joinMemberIdList) {
            boolean existJoinMember = chatJoinMemberRepository.findByChatChannelIdAndJoinMemberId(chatChannelId, id).isPresent();

            if (chatChannel.getCreateMember().getId().equals(id)) {
                chatChannel.restoreCreateMember();
            } else if (existJoinMember) {
                ChatJoinMember chatJoinMember =
                        chatJoinMemberRepository.findByChatChannelIdAndJoinMemberId(chatChannelId, id).get();
                chatJoinMember.restoreJoinMember();
            } else {
                WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

                ChatJoinMember chatJoinMember = ChatJoinMember.createChatJoinMember(workSpaceMember, chatChannel);
                chatJoinMemberList.add(chatJoinMember);
            }
        }

        chatJoinMemberRepository.saveAll(chatJoinMemberList);

        int increaseMemberCount = chatJoinMemberList.size();

        chatChannel.increaseChatMemberCount(increaseMemberCount);
    }

    // 채팅 채널 INFO
    public ChatChannelInfoResponseDto joinChatChannelInfo(Long workSpaceId, String roomId) {
        Chat findChat = chatChannelRepository.findByWorkSpaceIdAndRoomId(workSpaceId, roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long findChatId = findChat.getId();
        Long createMemberId = findChat.getCreateMember().getId();
        List<Long> joinMemberIdList = new ArrayList<>(chatJoinMemberRepository.findByChatChannelId(findChatId)
                .stream()
                .map(ChatJoinMember::getJoinMember)
                .map(WorkSpaceMember::getId)
                .toList());

        joinMemberIdList.add(createMemberId);

        return ChatChannelInfoResponseDto.builder()
                .channelId(findChat.getId())
                .channelName(findChat.getName())
                .memberList(joinMemberIdList)
                .build();

    }

    // 채팅 채널 멤버 INFO
    public ChatChannelInfoResponseDto joinChatChannelMemberInfo(Long workSpaceId, String roomId) {
        Chat findChat = chatChannelRepository.findByWorkSpaceIdAndRoomId(workSpaceId, roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long findChatId = findChat.getId();
        Long createMemberId = findChat.getCreateMember().getId();
        List<Long> joinMemberIdList = new ArrayList<>(chatJoinMemberRepository.findByChatChannelId(findChatId)
                .stream()
                .map(ChatJoinMember::getJoinMember)
                .map(WorkSpaceMember::getId)
                .toList());

        joinMemberIdList.add(createMemberId);

        List<Long> notJoinMemberIdList = workSpaceMemberRepository.findListByNotJoinMember(joinMemberIdList)
                .stream()
                .map(WorkSpaceMember::getId)
                .toList();

        return ChatChannelInfoResponseDto.builder()
                .channelId(findChat.getId())
                .channelName(findChat.getName())
                .memberList(notJoinMemberIdList)
                .build();
    }

    // 채팅방 리스트 조회 (내가 참여된)
    public Slice<ChatChannelListResponseDto> chatChannelList(String email, Long workSpaceId, Pageable pageable) {

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        List<ChatChannelListResponseDto> chatList =
                chatChannelRepository.findChatListByWorkSpaceMemberId(workSpaceId, findWorkSpaceMemberId)
                        .stream().map(chat -> ChatChannelListResponseDto.builder()
                                .workSpaceId(workSpaceId)
                                .channelId(chat.getId())
                                .name(chat.getName())
                                .roomId(chat.getRoomId())
                                .createdAt(chat.getCreatedAt())
                                .build()).toList();

        List<ChatChannelListResponseDto> chatJoinMemberList =
                chatJoinMemberRepository.findChatJoinMemberListByWorkSpaceIdAndWorkSpaceMemberId(workSpaceId, findWorkSpaceMemberId)
                        .stream().map(chatJoin -> ChatChannelListResponseDto.builder()
                                .workSpaceId(workSpaceId)
                                .channelId(chatJoin.getChatChannel().getId())
                                .name(chatJoin.getChatChannel().getName())
                                .roomId(chatJoin.getChatChannel().getRoomId())
                                .createdAt(chatJoin.getChatChannel().getCreatedAt())
                                .build()).toList();

        List<ChatChannelListResponseDto> chatChannelListResponseDtoList = new ArrayList<>();
        chatChannelListResponseDtoList.addAll(chatList);
        chatChannelListResponseDtoList.addAll(chatJoinMemberList);
        List<ChatChannelListResponseDto> sortedDto = chatChannelListResponseDtoList.stream()
                .sorted(Comparator.comparing(ChatChannelListResponseDto::createdAt).reversed()).toList();

        return sliceDtoResponse(sortedDto, pageable);
    }

    // 채팅 채널 나가기
    @Transactional
    public String chatChannelQuit(String email, Long workSpaceId, Long channelId) {
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Chat findChat = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();
        Long createMemberId = findChat.getCreateMember().getId();

        if (findWorkSpaceMemberId.equals(createMemberId)) {
            findChat.deleteCreateMember();
            findChat.decreaseChatMemberCount();
        } else {
            ChatJoinMember chatJoinMember = chatJoinMemberRepository.findByChatChannelIdAndWorkSpaceMemberId(channelId, findWorkSpaceMemberId)
                    .orElseThrow(() -> new NotFoundException(CHAT_JOIN_MEMBER_NOT_FOUND));
            chatJoinMember.deleteJoinMember();
            findChat.decreaseChatMemberCount();
        }

        return findChat.getChatMemberCount() == 0 ? findChat.getRoomId() : null;
    }

    // 워크스페이스 id에 해당되는 모든 채팅 채널 삭제
    @Transactional
    public void deleteChatChannelList(Long workSpaceId) {
        chatChannelRepository.deleteChatByWorkSpaceId(workSpaceId);
    }

    // 채팅 채널 삭제
    @Transactional
    public void deleteChatChannel(Long workSpaceId, String roomId) {
        Chat findChatChannel = chatChannelRepository.findByRoomIdAndEmailAndWorkSpaceId(roomId, workSpaceId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        chatChannelRepository.delete(findChatChannel);
    }

    // 채팅 참여 멤버 전부 삭제
    @Transactional
    public void deleteChatJoinMember(String roomId) {
        List<ChatJoinMember> findJoinMemberList = chatJoinMemberRepository.findByJoinMemberList(roomId);
        chatJoinMemberRepository.deleteAll(findJoinMemberList);
    }

    // 워크스페이스에 해당된 채팅 참여 멤버 전부 삭제
    @Transactional
    public void deleteChatJoinMemberByWorkSpaceId(Long workSpaceId) {
        List<ChatJoinMember> chatJoinMemberList = chatJoinMemberRepository.findByWorkSpaceId(workSpaceId);
        chatJoinMemberRepository.deleteAll(chatJoinMemberList);
    }

    // Slice(페이징)
    private Slice<ChatChannelListResponseDto> sliceDtoResponse(List<ChatChannelListResponseDto> chatChannelResponseDtoList, Pageable pageable) {
        boolean hasNext = chatChannelResponseDtoList.size() > pageable.getPageSize();
        List<ChatChannelListResponseDto> chatContent = hasNext ? chatChannelResponseDtoList.subList(0, pageable.getPageSize()) : chatChannelResponseDtoList;

        return new SliceImpl<>(chatContent, pageable, hasNext);
    }
}
