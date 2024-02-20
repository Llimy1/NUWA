package org.project.nuwabackend.api.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponse;
import org.project.nuwabackend.dto.channel.response.DirectChannelRoomIdResponse;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_CREATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DirectChannelController {

    private final DirectChannelService directChannelService;
    private final GlobalService globalService;

    // 채팅방 생성하기
    @PostMapping("/channel/direct")
    public ResponseEntity<Object> createDirectChannel(@MemberEmail String email, @RequestBody DirectChannelRequest directChannelRequest) {
        log.info("채팅방 생성 API 호출");
        String directChannelRoomId = directChannelService.createDirectChannel(email, directChannelRequest);

        DirectChannelRoomIdResponse directChannelRoomIdResponse =
                new DirectChannelRoomIdResponse(directChannelRoomId);

        GlobalSuccessResponseDto<Object> directChannelCreateSuccessResponse =
                globalService.successResponse(
                        DIRECT_CHANNEL_CREATE_SUCCESS.getMessage(),
                        directChannelRoomIdResponse);

        return ResponseEntity.status(CREATED).body(directChannelCreateSuccessResponse);
    }


    // TODO: test code
    @GetMapping("/channel/direct/{workSpaceId}")
    public ResponseEntity<Object> directChannelSliceSortByCreatedDate(
            @PathVariable("workSpaceId") Long workSpaceId,
            @CustomPageable Pageable pageable) {
        log.info("채널 리스트 조회 API 호출");
        Slice<DirectChannelListResponse> directChannelListResponses =
                directChannelService.directChannelSliceSortByCreatedDate(workSpaceId, pageable);

        GlobalSuccessResponseDto<Object> directChannelListSuccessResponse =
                globalService.successResponse(
                        DIRECT_CHANNEL_LIST_RETURN_SUCCESS.getMessage(),
                        directChannelListResponses);

        return ResponseEntity.status(OK).body(directChannelListSuccessResponse);
    }

    // 채팅창 나가기 (Redis 정보 삭제)
    @PostMapping("/channel/direct/{directChannelRoomId}")
    public ResponseEntity<Object> deleteDirectChannelMemberInfo(
            @PathVariable("directChannelRoomId") String directChannelRoomId,
            @MemberEmail String email) {
        log.info("채팅방 나가기(Redis 정보 삭제)");
        directChannelService.deleteDirectChannelMemberInfo(directChannelRoomId, email);

        GlobalSuccessResponseDto<Object> deleteDirectChannelMemberInfo =
                globalService.successResponse(
                        DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage(),
                        null
                );

        return ResponseEntity.status(OK).body(deleteDirectChannelMemberInfo);
    }

    // 채널 삭제하기



}
