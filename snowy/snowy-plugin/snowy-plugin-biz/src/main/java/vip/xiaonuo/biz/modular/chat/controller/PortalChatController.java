package vip.xiaonuo.biz.modular.chat.controller;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.chat.entity.BizChatMessage;
import vip.xiaonuo.biz.modular.chat.service.BizChatService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 竞赛与项目群聊互动控制器
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Tag(name = "前台门户-群聊与讨论控制器")
@RestController
@Validated
@RequestMapping("/client/portal/chat")
public class PortalChatController {

    @Resource
    private BizChatService bizChatService;

    @Operation(summary = "发送群聊消息")
    @SaClientCheckLogin
    @PostMapping("/send")
    public CommonResult<BizChatMessage> send(@RequestBody JSONObject param) {
        String chatType = param.getStr("chatType");
        String chatTargetId = param.getStr("chatTargetId");
        String content = param.getStr("content");
        String messageType = param.getStr("messageType");
        return bizChatService.sendMessage(chatType, chatTargetId, content, messageType);
    }

    @Operation(summary = "获取群聊历史消息")
    @SaClientCheckLogin
    @GetMapping("/history")
    public CommonResult<List<BizChatMessage>> getHistory(
            @RequestParam String chatType,
            @RequestParam String chatTargetId,
            @RequestParam(required = false, defaultValue = "60") Integer limit) {
        return bizChatService.getHistory(chatType, chatTargetId, limit);
    }

    @Operation(summary = "获取未读消息数及最新消息")
    @SaClientCheckLogin
    @GetMapping("/unreadCount")
    public CommonResult<JSONObject> getUnreadCount(
            @RequestParam String chatType,
            @RequestParam String chatTargetId) {
        return bizChatService.getUnreadCount(chatType, chatTargetId);
    }

    @Operation(summary = "标记消息为已读")
    @SaClientCheckLogin
    @PostMapping("/markRead")
    public CommonResult<String> markRead(@RequestBody JSONObject param) {
        String chatType = param.getStr("chatType");
        String chatTargetId = param.getStr("chatTargetId");
        return bizChatService.markRead(chatType, chatTargetId);
    }

    @Operation(summary = "获取群成员列表（支持自动同步进退群与去重）")
    @SaClientCheckLogin
    @GetMapping("/groupMembers")
    public CommonResult<List<JSONObject>> getGroupMembers(
            @RequestParam String chatType,
            @RequestParam String chatTargetId) {
        return bizChatService.getGroupMembers(chatType, chatTargetId);
    }

    @Operation(summary = "获取当前用户的群聊权限状态")
    @SaClientCheckLogin
    @GetMapping("/checkPermissions")
    public CommonResult<JSONObject> checkPermissions(
            @RequestParam(required = false) String projectId) {
        return bizChatService.checkMyChatPermissions(projectId);
    }
}
