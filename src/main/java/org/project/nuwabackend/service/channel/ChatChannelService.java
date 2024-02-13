package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequest;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatChannelService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;

    private final ChatChannelRepository channelRepository;

    // 채팅 채널 생성
//    public void createChatChannel(String email, ChatChannelRequest chatChannelRequest) {
//        Long workSpaceId = chatChannelRequest.workSpaceId();
//        List<String> joinMemberNameList = chatChannelRequest.joinMemberNameList();
//
//        // 워크스페이스가 존재하는지 확인
//        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
//                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));
//
//        // TODO: fetch join 또는 in 절로 쿼리문 한번에 출력 가능하지 않을까 생각 -> 수정 예정
//        // 워크스페이스에 멤버가 존재 하는지 확인
//        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmail(email)
//                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
//
//        // 워크스페이스에 멤버가 존재 하는지 확인
//        WorkSpaceMember joinWorkSpaceMember = workSpaceMemberRepository.findByName(directJoinMember)
//                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
//
//    }

}
