package org.project.nuwabackend.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Channel Redis Service Test")
@ExtendWith(MockitoExtension.class)
class DirectChannelRedisServiceTest {

    @Mock
    DirectChannelRedisRepository directChannelRedisRepository;
    @InjectMocks
    DirectChannelRedisService directChannelRedisService;

    private DirectChannelRedis directChannelRedisOne;
    private DirectChannelRedis directChannelRedisTwo;
    private List<DirectChannelRedis> directChannelRedisList = new ArrayList<>();

    @BeforeEach
    void setup() {

        String directRedisRoomId = "directRoomId";
        String emailRedis = "redisEmail";

        String emailRedisTwo = "redisEmailTwo";


        directChannelRedisOne = DirectChannelRedis.createDirectChannelRedis(
                directRedisRoomId,
                emailRedis
        );

        directChannelRedisTwo = DirectChannelRedis.createDirectChannelRedis(
                directRedisRoomId,
                emailRedisTwo
        );

        directChannelRedisList.add(directChannelRedisOne);
        directChannelRedisList.add(directChannelRedisTwo);
    }


    @Test
    @DisplayName("[Service] Save Direct Channel Info Redis")
    void saveDirectChannelInfoRedis() {
        //given
        given(directChannelRedisRepository.save(any()))
                .willReturn(directChannelRedisOne);

        //when
        directChannelRedisService.saveChannelMemberInfo(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());

        //then
        verify(directChannelRedisRepository).save(directChannelRedisOne);
    }

    @Test
    @DisplayName("[Service] Delete Direct Channel Info Redis")
    void deleteDirectChannelInfoRedis() {
        //given
        given(directChannelRedisRepository.findByDirectRoomIdAndEmail(anyString(), anyString()))
                .willReturn(Optional.of(directChannelRedisOne));

        //when
        directChannelRedisService.deleteChannelMemberInfo(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());

        //then
        verify(directChannelRedisRepository).findByDirectRoomIdAndEmail(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());
    }

    @Test
    @DisplayName("[Service] Is All Connected Test")
    void isAllConnectedTest() {
        //given
        given(directChannelRedisRepository.findByDirectRoomId(anyString()))
                .willReturn(directChannelRedisList);

        //when
        boolean allConnected = directChannelRedisService.
                isAllConnected(directChannelRedisOne.getDirectRoomId());

        //then
        assertThat(allConnected).isTrue();
    }
}