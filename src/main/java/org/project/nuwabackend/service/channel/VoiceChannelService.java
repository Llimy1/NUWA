package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Voice;
import org.project.nuwabackend.domain.channel.VoiceJoinMember;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.VoiceChannelJoinMemberRequestDto;
import org.project.nuwabackend.dto.channel.request.VoiceChannelRequestDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.VoiceChannelRepository;
import org.project.nuwabackend.repository.jpa.VoiceJoinMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: test code 추후 로직 작성 후
public class VoiceChannelService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;

    private final VoiceChannelRepository voiceChannelRepository;
    private final VoiceJoinMemberRepository voiceJoinMemberRepository;

    // 음성 채널 생성
    public Long createVoiceChannel(String email, VoiceChannelRequestDto voiceChannelRequestDto) {
        Long workSpaceId = voiceChannelRequestDto.workSpaceId();
        String voiceChannelName = voiceChannelRequestDto.voiceChannelName();


        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace workSpace = createWorkSpaceMember.getWorkSpace();

        Voice voiceChannel = Voice.createVoiceChannel(voiceChannelName, workSpace, createWorkSpaceMember);

        Voice saveVoiceChannel = voiceChannelRepository.save(voiceChannel);

        return saveVoiceChannel.getId();
    }

    // 채팅 채널 참가
    public void joinVoiceChannel(VoiceChannelJoinMemberRequestDto voiceChannelJoinMemberRequestDto) {
        List<Long> joinMemberIdList = voiceChannelJoinMemberRequestDto.joinMemberIdList();
        Long chatChannelId = voiceChannelJoinMemberRequestDto.voiceChannelId();

        Voice voiceChannel = voiceChannelRepository.findById(chatChannelId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        List<VoiceJoinMember> voiceJoinMemberList = new ArrayList<>();
        for (Long id : joinMemberIdList) {
            WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));


            VoiceJoinMember voiceJoinMember = VoiceJoinMember.createVoiceJoinMember(workSpaceMember, voiceChannel);

            voiceJoinMemberList.add(voiceJoinMember);
        }

        voiceJoinMemberRepository.saveAll(voiceJoinMemberList);
    }
}
