package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 查询帖子返回的是一个列表，传入userId 以后可能查询用户发的帖子，若userId为0则查询所有的帖子
     * 提前考虑到分页的功能，传入参数要有一个，起始条目下表，每页显示数量
     * 这个是个动态的SQL语句，判断userId是否为0
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param 如果只有一个参数，并且在<if>标签里判断这个参数使用，则必须使用别名设置

    /**
     * @param userId 用户id
     * @return 用户发表的帖子个数，userId 为 0 则查询所有用户
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 新增帖子
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 通过帖子的id 查询详情
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 修改帖子评论数，添加评论的时候冗余的修改帖子表
     */
    int updateCommentCount(int id, int CommentCount);
}
