package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
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

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 私信列表
     */
    @RequestMapping(path = "/conversation/list", method = RequestMethod.GET)
    public String getConversationList(Model model, Page page) {
        // 1、设置分页信息
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/conversation/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 2、回话列表
        List<Message> conversations = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversationVoList = new ArrayList<>();
        if (conversations != null) {
            for (Message conversation : conversations) {
                Map<String, Object> conversationVo = new HashMap<>();
                conversationVo.put("conversation", conversation);
                conversationVo.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));
                conversationVo.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                conversationVo.put("target", userService.findUserById(targetId));

                conversationVoList.add(conversationVo);
            }
        }
        model.addAttribute("conversations", conversationVoList);
        // 3、未读消息数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "site/letter";
    }

    /**
     * 私信详情
     * 1、分页展示消息列表
     * 2、设置未读消息为已读
     */
    @RequestMapping(path = "/conversation/detail/{conversationId}", method = RequestMethod.GET)
    public String getConversationDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        // 1、 设置分页信息
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/conversation/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        // 2、 消息列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letterVoList = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                Map<String, Object> letterVo = new HashMap<>();
                letterVo.put("letter", letter);
                letterVo.put("fromUser", userService.findUserById(letter.getFromId()));
                letterVoList.add(letterVo);
            }
        }
        model.addAttribute("letters", letterVoList);
        // 3、私信目标用户
        model.addAttribute("target", getLetterTarget(conversationId));
        // 4、若存在维多的消息则需要设置已读！
        List<Integer> ids = getUnreadIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/letter-detail";
    }

    /**
     * 获取列表中，未读私信的 ID
     */
    private List<Integer> getUnreadIds(List<Message> messageList) {
        List<Integer> ids = new ArrayList<>();
        if (messageList != null) {
            for (Message message : messageList) {
                if (message.getToId() == hostHolder.getUser().getId() && message.getStatus() == MESSAGE_STATUS_UNREAD) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 获得与当前用户对话的目标用户信息
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        return userService.findUserById(hostHolder.getUser().getId() == id0 ? id1 : id0);
    }

    /**
     * 响应异步发送消息  请求
     */
    @RequestMapping(path = "/message/send", method = RequestMethod.POST)
    @ResponseBody
    public String sentMessage(String toName, String content) {
        User target = userService.findUserByName(toName);
        // 1、判断目标用是否存在
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！", toName);
        }
        // 2、构建发送消息对象
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        // 2.1 保证回话 ID 为 "小_大" 的用户 id 组合。
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setCreateTime(new Date());

        // 3、 写入数据库
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0, "发送消息成功", message);
    }
}
