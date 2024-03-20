package org.project.nuwabackend.nuwa.workspacemember.service;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.project.nuwabackend.nuwa.domain.channel.ChatJoinMember;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.nuwa.domain.channel.QChat.chat;
import static org.project.nuwabackend.nuwa.domain.channel.QChatJoinMember.chatJoinMember;
import static org.project.nuwabackend.nuwa.domain.member.QMember.member;
import static org.project.nuwabackend.nuwa.domain.workspace.QWorkSpaceMember.workSpaceMember;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceMemberQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    public List<WorkSpaceMember> chatCreateMemberOrJoinMemberNotInEmailAndChannelId(List<String> emailList, Long channelId) {
        List<WorkSpaceMember> workSpaceMemberList = new ArrayList<>();

        Chat chatOne = jpaQueryFactory.selectFrom(chat)
                .join(chat.createMember.member, member)
                .join(chat.createMember, workSpaceMember)
                .where(
                        channelIdEq(channelId),
                        emailNotIn(emailList),
                        createMemberIsDeleteEq())
                .fetchOne();

        List<ChatJoinMember> chatJoinList = jpaQueryFactory.selectFrom(chatJoinMember)
                .join(chatJoinMember.chatChannel, chat)
                .join(chatJoinMember.joinMember.member, member)
                .where(
                        channelIdEq(channelId),
                        emailNotIn(emailList),
                        joinMemberIsDeleteEq())
                .fetch();

        if (chatOne != null) {
            workSpaceMemberList.add(chatOne.getCreateMember());
        }

        chatJoinList.forEach(join -> {
            workSpaceMemberList.add(join.getJoinMember());
        });

        return workSpaceMemberList;
    }

    private BooleanExpression emailNotIn(List<String> emailList) {
        return !emailList.isEmpty() ? member.email.notIn(emailList) : null;
    }

    private BooleanExpression channelIdEq(Long channelId) {
        return chat.id.eq(channelId);
    }

    private BooleanExpression createMemberIsDeleteEq() {
        return chat.createMember.isDelete.eq(false);
    }

    private BooleanExpression joinMemberIsDeleteEq() {
        return chatJoinMember.joinMember.isDelete.eq(false);
    }
}
