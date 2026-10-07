/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.projectflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 组队申请流转实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_project_apply_invite")
public class BizProjectFlow {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 项目ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 涉及的学生用户ID（关联SYS_USER.ID） */
    @Schema(description = "涉及的学生用户ID（关联SYS_USER.ID）")
    private String studentId;

    /** 流转方向：INVITE-组长邀请学生, APPLY-学生主动申请加入 */
    @Schema(description = "流转方向：INVITE-组长邀请学生, APPLY-学生主动申请加入")
    private String direction;

    /** 申请意向角色：LEADER-竞聘组长, MEMBER-申请组员 */
    @Schema(description = "申请意向角色：LEADER-竞聘组长, MEMBER-申请组员")
    private String applyRole;

    /** 发起人用户ID（组长或学生，关联SYS_USER.ID） */
    @Schema(description = "发起人用户ID（组长或学生，关联SYS_USER.ID）")
    private String operatorId;

    /** 附言/申请理由/邀请语 */
    @Schema(description = "附言/申请理由/邀请语")
    private String message;

    /** 流转状态：PENDING-待处理, ACCEPTED-已接受并入组, REJECTED-已拒绝 */
    @Schema(description = "流转状态：PENDING-待处理, ACCEPTED-已接受并入组, REJECTED-已拒绝")
    private String status;

    /** 处理时间 */
    @Schema(description = "处理时间")
    private Date handleTime;

    /** 处理备注 */
    @Schema(description = "处理备注")
    private String handleRemark;

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @Schema(description = "修改用户")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;
}
