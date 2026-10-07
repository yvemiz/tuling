package vip.xiaonuo.biz.modular.chat.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户群聊已读状态实体
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Getter
@Setter
@TableName("biz_chat_read")
public class BizChatRead {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 群聊类型: PROJECT, LEADER_TEACHER */
    @Schema(description = "群聊类型")
    private String chatType;

    /** 关联目标ID */
    @Schema(description = "关联目标ID")
    private String chatTargetId;

    /** 最后阅读时间 */
    @Schema(description = "最后阅读时间")
    private Date lastReadTime;

    /** 最后一条已读消息ID */
    @Schema(description = "最后一条已读消息ID")
    private String lastReadMsgId;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private Date updateTime;
}
