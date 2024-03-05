package org.project.nuwabackend.service.workspace;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.domain.channel.QChat.chat;
import static org.project.nuwabackend.domain.channel.QChatJoinMember.chatJoinMember;
import static org.project.nuwabackend.domain.member.QMember.member;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// TODO: integrated test code
public class WorkSpaceMemberQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    public List<WorkSpaceMember> chatCreateMemberOrJoinMemberNotInEmailAndChannelId(List<String> emailList, Long channelId) {
        WorkSpaceMember createMember = jpaQueryFactory.select(chat.createMember)
                .from(chat)
                .where(
                        channelIdEq(channelId),
                        emailNotIn(emailList))
                .fetchOne();
//
        List<WorkSpaceMember> joinMemberList = jpaQueryFactory.select(chatJoinMember.joinMember)
                .from(chatJoinMember)
                .join(chatJoinMember.chatChannel, chat)
                .where(
                        channelIdEq(channelId),
                        emailNotIn(emailList))
                .fetch();

        if (createMember != null) {
            joinMemberList.add(createMember);
        }

        return joinMemberList;
    }

    private BooleanExpression emailNotIn(List<String> emailList) {
        return !emailList.isEmpty() ? member.email.notIn(emailList) : null;
    }

    private BooleanExpression channelIdEq(Long channelId) {
        return chat.id.eq(channelId);
    }
}
