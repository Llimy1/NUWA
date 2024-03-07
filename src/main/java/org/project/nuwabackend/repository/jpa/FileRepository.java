package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.multimedia.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByIdIn(List<Long> fileIdList);

    List<File> findByWorkSpaceId(Long workSpaceId);

    @Query("DELETE FROM File f WHERE f.workSpace.id = :workSpaceId AND f.channel.roomId = :roomId")
    @Modifying(clearAutomatically = true)
    void deleteByWorkSpaceIdAndChannelRoomId(@Param("workSpaceId") Long workSpaceId, @Param("roomId") String roomId);

    @Query("DELETE FROM File f WHERE f.workSpace.id = :workSpaceId")
    @Modifying(clearAutomatically = true)
    void deleteByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);
}
