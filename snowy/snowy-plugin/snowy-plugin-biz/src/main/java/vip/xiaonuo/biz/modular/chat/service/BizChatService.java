package vip.xiaonuo.biz.modular.chat.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.chat.entity.BizChatMessage;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 群聊与讨论服务接口
 *
 * @author antigravity
 * @date 2026/10/05
 **/
public interface BizChatService extends IService<BizChatMessage> {

    /** 发送消息 */
    CommonResult<BizChatMessage> sendMessage(String chatType, String chatTargetId, String content, String messageType);

    /** 获取群聊历史消息列表 */
    CommonResult<List<BizChatMessage>> getHistory(String chatType, String chatTargetId, Integer limit);

    /** 获取未读消息数 */
    CommonResult<JSONObject> getUnreadCount(String chatType, String chatTargetId);

    /** 标记已读 */
    CommonResult<String> markRead(String chatType, String chatTargetId);

    /** 获取群成员列表（针对组长教师大群支持自动去重，若不再是组长自动移出） */
    CommonResult<List<JSONObject>> getGroupMembers(String chatType, String chatTargetId);

    /** 检查当前用户群聊权限与未读概要 */
    CommonResult<JSONObject> checkMyChatPermissions(String projectId);
}
