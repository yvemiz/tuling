package vip.xiaonuo.biz.modular.portalmgr.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 社区论坛板块实体
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Getter
@Setter
@TableName("biz_forum_board")
public class BizForumBoard {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 板块编码 */
    @Schema(description = "板块编码")
    private String boardCode;

    /** 板块名称 */
    @Schema(description = "板块名称")
    private String boardName;

    /** 图标/Emoji */
    @Schema(description = "图标/Emoji")
    private String icon;

    /** 主题颜色 */
    @Schema(description = "主题颜色")
    private String color;

    /** 板块描述 */
    @Schema(description = "板块描述")
    private String description;

    /** 发帖权限: ALL全员, LEADER仅队长, TEACHER仅教师 */
    @Schema(description = "发帖权限")
    private String postPermission;

    /** 状态: ENABLE启用, DISABLE停用 */
    @Schema(description = "状态")
    private String status;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 删除标志 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;
}
