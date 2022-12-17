package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return 分页查询评论
     */
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    /**
     * @param entityType
     * @param entityId
     * @return 返回所有评论的数
     */
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 两次 DML 操作，使用事务保证一致性
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 插入评论，过滤html标记，敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子的评论数
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
