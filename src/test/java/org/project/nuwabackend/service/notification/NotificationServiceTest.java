package org.project.nuwabackend.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.notification.EmitterRepository;
import org.project.nuwabackend.repository.jpa.notification.NotificationRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("[Service] Notification Service Test")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    EmitterRepository emitterRepository;

    @InjectMocks
    NotificationService notificationService;

    Member member;
    WorkSpace workSpace;
    WorkSpaceMember workSpaceMember;

    private static final Long DEFAULT_TIME_OUT = 1000L * 60 * 29;

    String email = "abcd@gmail.com";
    @BeforeEach
    void setup() {
        String workSpaceName = "nuwa";
        String workSpaceImage = "N";
        String workSpaceIntroduce = "개발";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        ReflectionTestUtils.setField(workSpace, "id", 1L);

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
        ReflectionTestUtils.setField(workSpaceMember, "id", 1L);

    }

    @Test
    @DisplayName("[Service] Notification Subscribe Test")
    void notificationSubscribeTest() {
        //given
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember));

        Long workSpaceMemberId = workSpaceMember.getId();

        String emitterId = workSpaceMemberId + "_" + System.currentTimeMillis();

        SseEmitter emitter = new SseEmitter(DEFAULT_TIME_OUT);
        Map<String, Object> emitterMap = new HashMap<>();
        emitterMap.put(emitterId, emitter);

        given(emitterRepository.save(anyString(), any()))
                .willReturn(emitter);

        given(emitterRepository.findAllEventCacheStartWithId(anyString()))
                .willReturn(emitterMap);

        //when
        SseEmitter returnEmitter = notificationService.subscribe(email, workSpace.getId(), emitterId);

        //then
        assertThat(returnEmitter).isNotNull();
    }
}