package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponse;
import org.project.nuwabackend.dto.message.DirectMessageDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.REDIS_DIRECT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectChannelService {


    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;

    private final DirectChannelRepository directChannelRepository;

    private final DirectChannelRedisRepository directChannelRedisRepository;


    // 다이렉트 채널 생성
    @Transactional
    public String createDirectChannel(String email, DirectChannelRequest directChannelRequest) {
        log.info("다이렉트 채널 생성");

        String directJoinMember = directChannelRequest.joinMemberName();
        Long workSpaceId = directChannelRequest.workSpaceId();;

        // 워크스페이스가 존재하는지 확인
        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember joinWorkSpaceMember = workSpaceMemberRepository.findByName(directJoinMember)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        // 워크스페이스 존재하고 멤버도 전부 존재하면 채널 저장
        Direct direct = Direct.createDirectChannel(workSpace, createWorkSpaceMember, joinWorkSpaceMember);

        Direct saveDirect = directChannelRepository.save(direct);

        // RoomId 반환
        return saveDirect.getRoomId();
    }

    // 다이렉트 채널 리스트 조회
    // TODO: 다이렉트 채널 조회 -> 마지막 메세지 시간이 제일 빠른 순으로(?)
    // TODO: test code
    // 현재는 생성 순으로 반환
    public Slice<DirectChannelListResponse> directChannelSliceSortByCreatedDate(Long workSpaceId, Pageable pageable) {
        log.info("채널 리스트 반환 (생성 시간 순)");
        return directChannelRepository.findDirectChannelByWorkSpaceId(workSpaceId, pageable)
                .map(direct -> DirectChannelListResponse.builder()
                .roomId(direct.getRoomId())
                .name(direct.getName())
                .createMemberName(direct.getCreateMember().getName())
                .joinMemberName(direct.getJoinMember().getName())
                .build());
    }



    // Redis에 채널 입장 정보 저장
    @Transactional
    public void saveDirectChannelMemberInfo(String directChannelRoomId, String email) {
        log.info("채널 입장 정보 저장");
        DirectChannelRedis directChannelInfo =
                DirectChannelRedis.createDirectChannelRedis(directChannelRoomId, email);

        directChannelRedisRepository.save(directChannelInfo);
    }

    // Redis에 채널 입장 정보 삭제
    @Transactional
    public void deleteDirectChannelMemberInfo(String directChannelRoomId, String email) {
        DirectChannelRedis directChannelRedis = directChannelRedisRepository.findByDirectRoomIdAndEmail(directChannelRoomId, email)
                .orElseThrow(() -> new NotFoundException(REDIS_DIRECT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO));

        directChannelRedisRepository.delete(directChannelRedis);
    }

    // 채팅방 인원이 2명인지 확인 => 다이렉트 메세지를 보냈을 때 바로 읽음 처리를 하기 위한 메소드
    public boolean isAllConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 2;
    }

    // 채팅방 인원이 1명인지 확인 => 채팅방 연결시 현재 인원이 존재 하는지 확인을 위한 메소드
    public boolean isConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 1;
    }

}
