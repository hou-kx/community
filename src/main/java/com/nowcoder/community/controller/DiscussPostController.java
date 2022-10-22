package com.nowcoder.community.controller;

import com.fasterxml.jackson.databind.ObjectReader;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.ObjectName;
import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "未登录！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 若程序执行出现了问题，后面会统一的进行处理
        return CommunityUtil.getJSONString(0, "帖子发布成功！");
    }

    /**
     * 提供查询帖子详情的接口，返回页面就不用 @RequestBody，String 返回的是页面地址
     * <p>
     * // 只要实体类型，作为参数输入，Spring MVC 都会主动的将其存入Model中
     *
     * @param postId 1
     * @param model  传参给前端页面
     * @return 页面路径
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable("discussPostId") int postId, Model model, Page page) {
        // 1、帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        model.addAttribute("discussPost", discussPost);
        // 2、作者信息
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 3、评论  3.1 分页初始化
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(discussPost.getCommentCount());    // 直接从帖子详情里取，或者去数据据库里计算，显然直接取效率高

        // 3.2 查询帖子列表，当前页 ：帖子-》评论 -》评论的评论，回复
        // 评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, postId, page.getOffset(), page.getLimit());
        // 对展示的对象进行过一个进一步的封装； 评论 VO列表   Vo -> view object
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 3.2.1 这是帖子的评论，评论，和评论的作者
                Map<String, Object> commentVo = new HashMap<>();
                // 帖子主体
                commentVo.put("comment", comment);
                // 发帖人
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 3.2.2 还有帖子评论的评论，称之为回复，回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复主体
                        replyVo.put("reply", reply);
                        // 回复作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标,这里是回复某人的评论
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 整合到回复列表
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 显示回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        // 添加到Model中加载
        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";   // 返回静态页面地址不加 /
    }

}
