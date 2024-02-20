package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Chat;
import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequest;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequest;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatChannelService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;

    private final ChatChannelRepository chatChannelRepository;
    private final ChatJoinMemberRepository chatJoinMemberRepository;

    // 채팅 채널 생성
    // TODO: test code
    public Long createChatChannel(String email, ChatChannelRequest chatChannelRequest) {
        Long workSpaceId = chatChannelRequest.workSpaceId();
        String chatChannelName = chatChannelRequest.chatChannelName();

        // 워크스페이스가 존재하는지 확인
        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Chat chatChannel = Chat.createChatChannel(chatChannelName, workSpace, createWorkSpaceMember);

        Chat saveChatChannel = chatChannelRepository.save(chatChannel);

        return saveChatChannel.getId();
    }

    // 채팅 채널 참가
    // TODO: test code
    public void joinChatChannel(ChatChannelJoinMemberRequest chatChannelJoinMemberRequest) {
        List<Long> joinMemberIdList = chatChannelJoinMemberRequest.joinMemberIdList();
        Long chatChannelId = chatChannelJoinMemberRequest.chatChannelId();

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


}
