package org.project.nuwabackend.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.Canvas;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CANVAS_DELETE_FAIL;
import static org.project.nuwabackend.global.type.ErrorMessage.CANVAS_UPDATE_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class CanvasQueryService {

    private final MongoTemplate mongoTemplate;

    // 캔버스 조회
    public List<Canvas> canvasList(Long workSpaceId, Long workSpaceMemberId) {

        System.out.println("workSpaceId = " + workSpaceId);
        System.out.println("workSpaceMemberId = " + workSpaceMemberId);
        Query query = workSpaceMemberId != null ?
                new Query(Criteria.where("workspace_id").is(workSpaceId)
                        .and("create_member_id").is(workSpaceMemberId))
                :
                new Query(Criteria.where("workspace_id").is(workSpaceId));

        return mongoTemplate.find(query, Canvas.class);
    }

    // 캔버스 수정
    public void updateCanvas(String canvasId, Long workSpaceMemberId, Long workSpaceId, String title, String content) {
        Query query = new Query(Criteria.where("id").is(canvasId)
                .and("workspace_id").is(workSpaceId)
                .and("create_member_id").is(workSpaceMemberId));

        Update update = new Update().set("canvas_title", title).set("canvas_content", content);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Canvas.class);

        if (updateResult.getMatchedCount() == 0) {
            throw new IllegalArgumentException(CANVAS_UPDATE_FAIL.getMessage());
        }
    }

    // 캔버스 삭제
    public void deleteCanvas(String canvasId, Long workSpaceId, Long workSpaceMemberId) {
        Query query = new Query(Criteria.where("id").is(canvasId)
                .and("workspace_id").is(workSpaceId)
                .and("create_member_id").is(workSpaceMemberId));

        DeleteResult remove = mongoTemplate.remove(query, Canvas.class);

        if (remove.getDeletedCount() == 0) {
            throw new IllegalArgumentException(CANVAS_DELETE_FAIL.getMessage());
        }
    }

}