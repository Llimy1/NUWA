package org.project.nuwabackend.service.workspace;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.Canvas;
import org.project.nuwabackend.domain.multimedia.QFile;
import org.project.nuwabackend.domain.workspace.QWorkSpace;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.domain.multimedia.QFile.file;
import static org.project.nuwabackend.domain.workspace.QWorkSpace.workSpace;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceSearchQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    public void workSpaceContentSearch(Long workSpaceId) {

        jpaQueryFactory.selectFrom(file);

    }

    private BooleanExpression workSpaceIdEq(Long workSpaceId) {
        return workSpace.id.eq(workSpaceId);
    }



}
