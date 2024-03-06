package org.project.nuwabackend.api.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelInfoResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelRoomIdResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.DirectChannelRedisService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_CREATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.SEARCH_DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DirectChannelController {

    private final DirectChannelService directChannelService;
    private final DirectChannelRedisService directChannelRedisService;
    private final GlobalService globalService;

    // 채팅방 생성하기
    @PostMapping("/channel/direct")
    public ResponseEntity<Object> createDirectChannel(@MemberEmail String email, @RequestBody DirectChannelRequestDto directChannelRequestDto) {
        log.info("채팅방 생성 API 호출");
        String directChannelRoomId = directChannelService.createDirectChannel(email, directChannelRequestDto);

        DirectChannelRoomIdResponseDto directChannelRoomIdResponseDto =
                new DirectChannelRoomIdResponseDto(directChannelRoomId);

        GlobalSuccessResponseDto<Object> directChannelCreateSuccessResponse =
                globalService.successResponse(
                        DIRECT_CHANNEL_CREATE_SUCCESS.getMessage(),
                        directChannelRoomIdResponseDto);

        return ResponseEntity.status(CREATED).body(directChannelCreateSuccessResponse);
    }

    @GetMapping("/channel/direct/{workSpaceId}")
    public ResponseEntity<Object> directChannelSlice(
            @PathVariable(value = "workSpaceId") Long workSpaceId,
            @MemberEmail String email,
            @CustomPageable Pageable pageable) {
        log.info("채널 리스트 조회 API 호출");
        Slice<DirectChannelListResponseDto> directChannelListResponses =
                directChannelService.directChannelSlice(email, workSpaceId, pageable);

        GlobalSuccessResponseDto<Object> directChannelListSuccessResponse =
                globalService.successResponse(
                        DIRECT_CHANNEL_LIST_RETURN_SUCCESS.getMessage(),
                        directChannelListResponses);

        return ResponseEntity.status(OK).body(directChannelListSuccessResponse);
    }

    // TODO: test code
    @GetMapping("/channel/direct/info/{workSpaceId}")
    public ResponseEntity<Object> directChannelInfo(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                    @RequestParam(value = "directChannelRoomId") String directChannelRoomId) {
        log.info("채팅방 정보 조회 API 호출");
        DirectChannelInfoResponseDto directChannelInfoResponseDto = directChannelService.directChannelInfo(workSpaceId, directChannelRoomId);

        GlobalSuccessResponseDto<Object> directChannelInfoReturnSuccessResponse =
                globalService.successResponse(DIRECT_CHANNEL_INFO_RETURN_SUCCESS.getMessage(), directChannelInfoResponseDto);

        return ResponseEntity.status(OK).body(directChannelInfoReturnSuccessResponse);
    }

    @GetMapping("/channel/direct/v2/{workSpaceId}")
    public ResponseEntity<Object> directChannelSliceSortByMessageCreateDate(
            @PathVariable(value = "workSpaceId") Long workSpaceId,
            @MemberEmail String email,
            @CustomPageable Pageable pageable) {
        log.info("마지막 채팅 순 채널 리스트 조회");
        Slice<DirectChannelResponseDto> directChannelListResponseDto =
                directChannelService.directChannelSliceSortByMessageCreateDateDesc(email, workSpaceId, pageable);

        GlobalSuccessResponseDto<Object> directChannelListSuccessResponse =
                globalService.successResponse(
                        DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS.getMessage(),
                        directChannelListResponseDto);
        return ResponseEntity.status(OK).body(directChannelListSuccessResponse);
    }

    @GetMapping("/channel/direct/search/{workSpaceId}")
    // TODO: test code
    public ResponseEntity<Object> searchDirectChannelSliceSortByMessageCreateDate(
            @PathVariable(value = "workSpaceId") Long workSpaceId,
            @RequestParam(value = "workSpaceMemberName") String workSpaceMemberName,
            @MemberEmail String email,
            @CustomPageable Pageable pageable) {
        log.info("검색한 마지막 채팅 순 채널 리스트 조회 API");
        Slice<DirectChannelResponseDto> searchDirectChannelListResponseDto =
                directChannelService.searchDirectChannelSliceSortByMessageCreateDateDesc(email, workSpaceId, workSpaceMemberName, pageable);

        GlobalSuccessResponseDto<Object> directChannelListSuccessResponse =
                globalService.successResponse(
                        SEARCH_DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS.getMessage(),
                        searchDirectChannelListResponseDto);
        return ResponseEntity.status(OK).body(directChannelListSuccessResponse);
    }

    // 채팅창 나가기 (Redis 정보 삭제)
    @PostMapping("/channel/direct/{directChannelRoomId}")
    public ResponseEntity<Object> deleteDirectChannelMemberInfo(
            @PathVariable(value = "directChannelRoomId") String directChannelRoomId,
            @MemberEmail String email) {
        log.info("채팅방 나가기(Redis 정보 삭제)");
        directChannelRedisService.deleteChannelMemberInfo(directChannelRoomId, email);

        GlobalSuccessResponseDto<Object> deleteDirectChannelMemberInfo =
                globalService.successResponse(
                        DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage(),
                        null
                );

        return ResponseEntity.status(OK).body(deleteDirectChannelMemberInfo);
    }

    // 채널 삭제하기



}
