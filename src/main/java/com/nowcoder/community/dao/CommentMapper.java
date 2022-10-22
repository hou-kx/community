package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CommentMapper {
    // 查询出所有评论
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    // 查询所有的评论数量，来计算分页数
    int selectCountByEntity(int entityType, int entityId);

    //添加评论
    int insertComment(Comment comment);
}
