package vip.xiaonuo.biz.modular.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 群聊讨论消息实体
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Getter
@Setter
@TableName("biz_chat_message")
public class BizChatMessage {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 群聊类型: PROJECT(项目小组讨论), LEADER_TEACHER(组长与教师领航群) */
    @Schema(description = "群聊类型: PROJECT, LEADER_TEACHER")
    private String chatType;

    /** 关联目标ID: 项目ID 或 GLOBAL_LEADER_TEACHER */
    @Schema(description = "关联目标ID: 项目ID 或 GLOBAL_LEADER_TEACHER")
    private String chatTargetId;

    /** 发送者用户ID(client_user.id) */
    @Schema(description = "发送者用户ID")
    private String senderId;

    /** 发送者姓名 */
    @Schema(description = "发送者姓名")
    private String senderName;

    /** 发送者身份: TEACHER(老师), LEADER(组长), MEMBER(组员) */
    @Schema(description = "发送者身份")
    private String senderRole;

    /** 发送者头像 */
    @Schema(description = "发送者头像")
    private String senderAvatar;

    /** 消息正文内容 */
    @Schema(description = "消息正文内容")
    private String content;

    /** 消息类型: TEXT, IMAGE, FILE, SYSTEM */
    @Schema(description = "消息类型: TEXT, IMAGE, FILE, SYSTEM")
    private String messageType;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 发送时间 */
    @Schema(description = "发送时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 更新时间 */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 更新人 */
    @Schema(description = "更新人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;
}
