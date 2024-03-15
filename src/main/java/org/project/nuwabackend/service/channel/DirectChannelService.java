package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelInfoResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.type.MessageType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.type.MessageType.FILE;
import static org.project.nuwabackend.type.MessageType.IMAGE;
import static org.project.nuwabackend.type.MessageType.TEXT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectChannelService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectMessageRepository directMessageRepository;
    private final DirectChannelRepository directChannelRepository;

    private final DirectMessageQueryService directMessageQueryService;


    // 다이렉트 채널 생성
    @Transactional
    public String createDirectChannel(String email, DirectChannelRequestDto directChannelRequestDto) {
        log.info("다이렉트 채널 생성");

        Long joinMemberId = directChannelRequestDto.joinMemberId();
        Long workSpaceId = directChannelRequestDto.workSpaceId();

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long createMemberId = createWorkSpaceMember.getId();

        WorkSpace workSpace = createWorkSpaceMember.getWorkSpace();

        // 워크스페이스에 멤버가 존재 하는지 확인
        WorkSpaceMember joinWorkSpaceMember = workSpaceMemberRepository.findById(joinMemberId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        // 이미 채팅방이 존재하면 해당 Room Id 반환
        Optional<Direct> optionalDirect = directChannelRepository.findByCreateMemberIdOrJoinMemberId(createMemberId, joinMemberId);
        if (optionalDirect.isPresent()) {
            Direct direct = optionalDirect.get();

            if (direct.getIsCreateMemberDelete()) direct.restoreCreateMember();
            else if (direct.getIsJoinMemberDelete()) direct.restoreJoinMember();

            return direct.getRoomId();
        }

        // 워크스페이스 존재하고 멤버도 전부 존재하면 채널 저장
        Direct direct = Direct.createDirectChannel(workSpace, createWorkSpaceMember, joinWorkSpaceMember);

        Direct saveDirect = directChannelRepository.save(direct);

        // RoomId 반환
        return saveDirect.getRoomId();
    }

    // roomId로 채널 정보 조회
    public DirectChannelInfoResponseDto directChannelInfo(Long workSpaceId, String roomId) {
        Direct direct = directChannelRepository.findByWorkSpaceIdAndRoomId(workSpaceId, roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        return DirectChannelInfoResponseDto.builder()
                .channelId(direct.getId())
                .channelName(direct.getName())
                .createMemberId(direct.getCreateMember().getId())
                .createMemberName(direct.getCreateMember().getName())
                .createMemberImage(direct.getCreateMember().getImage())
                .isCreateDelete(direct.getIsCreateMemberDelete())
                .joinMemberId(direct.getJoinMember().getId())
                .joinMemberName(direct.getJoinMember().getName())
                .joinMemberImage(direct.getJoinMember().getImage())
                .isJoinDelete(direct.getIsJoinMemberDelete())
                .build();
    }

    // 다이렉트 채널 리스트 조회
    // 현재는 생성 순으로 반환
    public Slice<DirectChannelListResponseDto> directChannelSlice(String email, Long workSpaceId, Pageable pageable) {
        log.info("채널 리스트 반환");

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        return directChannelRepository.findDirectChannelByCreateMemberIdOrJoinMemberId(findWorkSpaceMemberId, pageable)
                .map(direct -> DirectChannelListResponseDto.builder()
                        .roomId(direct.getRoomId())
                        .name(direct.getName())
                        .workSpaceId(workSpaceId)
                        .createMemberId(direct.getCreateMember().getId())
                        .joinMemberId(direct.getJoinMember().getId())
                        .createMemberName(direct.getCreateMember().getName())
                        .joinMemberName(direct.getJoinMember().getName())
                        .build());
    }

    // 내가 속한 워크스페이스
    // 내가 속한 채팅방에
    // 마지막 채팅, 읽지 않은 채팅 카운트가 필요함
    // 채널 리스트 반환 -> 채팅방 별로 마지막 채팅, 인원, 채팅방 정보, 읽지 않은 채팅 카운트
    public Slice<DirectChannelResponseDto> directChannelSliceSortByMessageCreateDateDesc(String email, Long workSpaceId, Pageable pageable) {
        log.info("채팅방 마지막 메세지 순으로 정렬");
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        // 워크스페이스 리스트 가져오기 -> 내가 생성을 한 또는 내가 참여를 한 채팅방 리스트 가져오기 (생성 시간 별로 나눌 필요가 없음 -> 마지막 채팅의 시간 순으로 정렬을 해야함)
        List<Direct> directChannelList =
                directChannelRepository.findDirectChannelListByCreateMemberIdOrJoinMemberId(findWorkSpaceMemberId);

        // 리스트를 순회하면서 해당 roomId에 맞는 마지막 채팅과 시간 가져오기
        // 해당 DTO에 맵핑된 생성 시간으로 재정렬하여 최근 메세지 순으로 채팅방 정렬
        List<DirectChannelResponseDto> directChannelResponseDtoList =
                directChannelResponseDtoList(directChannelList, email, workSpaceId)
                        .stream()
                        .sorted(Comparator.comparing(DirectChannelResponseDto::getMessageCreatedAt,
                                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .toList();

        return sliceDtoResponse(directChannelResponseDtoList, pageable);
    }

    // 검색
    public Slice<DirectChannelResponseDto> searchDirectChannelSliceSortByMessageCreateDateDesc(String email, Long workSpaceId, String workSpaceMemberName, Pageable pageable) {
        log.info("검색한 채팅방 마지막 메세지 순으로 정렬");

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        // 워크스페이스 리스트 가져오기 -> 내가 생성을 한 또는 내가 참여를 한 채팅방 리스트 가져오기 (생성 시간 별로 나눌 필요가 없음 -> 마지막 채팅의 시간 순으로 정렬을 해야함)
        List<Direct> searchDirectChannelList =
                directChannelRepository.findSearchDirectChannelByCreateMemberIdOrJoinMemberId(findWorkSpaceMemberId, workSpaceMemberName);

        // 리스트를 순회하면서 해당 roomId에 맞는 마지막 채팅과 시간 가져오기
        // 해당 DTO에 맵핑된 생성 시간으로 재정렬하여 최근 메세지 순으로 채팅방 정렬
        List<DirectChannelResponseDto> searchDirectChannelResponseDtoList =
                directChannelResponseDtoList(searchDirectChannelList, email, workSpaceId)
                        .stream()
                        .sorted(Comparator.comparing(DirectChannelResponseDto::getMessageCreatedAt,
                                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .toList();

        // 페이징 정보 추가
        return sliceDtoResponse(searchDirectChannelResponseDtoList, pageable);
    }

    // Slice(페이징)
    private Slice<DirectChannelResponseDto> sliceDtoResponse(List<DirectChannelResponseDto> directChannelResponseDtoList, Pageable pageable) {
        boolean hasNext = directChannelResponseDtoList.size() > pageable.getPageSize();
        List<DirectChannelResponseDto> directContent = hasNext ? directChannelResponseDtoList.subList(0, pageable.getPageSize()) : directChannelResponseDtoList;

        return new SliceImpl<>(directContent, pageable, hasNext);
    }

    // 채팅 정보 넣기
    private List<DirectChannelResponseDto> directChannelResponseDtoList(List<Direct> directList, String email, Long workSpaceId) {
        List<DirectChannelResponseDto> directChannelResponseDtoList = new ArrayList<>();

        directList.forEach(direct -> {
            PageRequest pageRequest = PageRequest.of(0, 1);

            Long unReadCount = directMessageQueryService.countUnReadMessage(direct.getRoomId(), email, workSpaceId);

            DirectChannelResponseDto directChannelResponseDto = DirectChannelResponseDto.builder()
                    .roomId(direct.getRoomId())
                    .name(direct.getName())
                    .workSpaceId(direct.getWorkSpace().getId())
                    .createMemberId(direct.getCreateMember().getId())
                    .joinMemberId(direct.getJoinMember().getId())
                    .createMemberName(direct.getCreateMember().getName())
                    .joinMemberName(direct.getJoinMember().getName())
                    .createMemberImage(direct.getCreateMember().getImage())
                    .joinMemberImage(direct.getJoinMember().getImage())
                    .unReadCount(unReadCount)
                    .build();

            // 마지막 채팅과 시간 가져오기
            Slice<DirectMessage> directMessageByRoomIdOrderByCreatedAt =
                    directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAtDesc(direct.getRoomId(), pageRequest);

            if (directMessageByRoomIdOrderByCreatedAt.hasContent()) {
                String rawString;

                DirectMessage directMessage =
                        directMessageByRoomIdOrderByCreatedAt.getContent().get(0);

                if (directMessage.getMessageType().equals(IMAGE)) {
                    rawString = "사진";
                } else if (directMessage.getMessageType().equals(FILE)) {
                    rawString = "파일";
                } else if (directMessage.getMessageType().equals(TEXT)){
                    rawString = directMessage.getRawString().get(0);
                } else {
                    rawString = "";
                }

                directChannelResponseDto.setLastMessage(rawString);
                directChannelResponseDto.setMessageCreatedAt(directMessage.getCreatedAt());
            }

            directChannelResponseDtoList.add(directChannelResponseDto);
        });

        return directChannelResponseDtoList;
    }

    // 워크스페이스 id에 해당되는 모든 다이렉트 채널 삭제
    // TODO: integrated test code
    @Transactional
    public void deleteDirectChannelList(Long workSpaceId) {
        directChannelRepository.deleteDirectByWorkSpaceId(workSpaceId);
    }

    // TODO: integrated test code
    // 채널 삭제 -> 나에게만 삭제 / 서로 삭제시 -> 완전 삭제
    @Transactional
    public Direct deleteChannelMember(Long workSpaceId, String email, String roomId) {
        Direct findDirectChannel = directChannelRepository.findByWorkSpaceIdAndRoomIdAndEmail(workSpaceId, roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        WorkSpaceMember createMember = findDirectChannel.getCreateMember();
        Long createMemberId = createMember.getId();
        WorkSpaceMember joinMember = findDirectChannel.getJoinMember();
        Long joinMemberId = joinMember.getId();

        // 나에게만 삭제
        if (findWorkSpaceMemberId.equals(createMemberId)) findDirectChannel.deleteCreateMember();
        else if (joinMemberId.equals(findWorkSpaceMemberId)) findDirectChannel.deleteJoinMember();

        // 양쪽 전부 삭제
        return findDirectChannel.getIsCreateMemberDelete() && findDirectChannel.getIsJoinMemberDelete() ? findDirectChannel : null;

    }
}
