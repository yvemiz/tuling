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
package vip.xiaonuo.biz.modular.memberscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 组员表现打分实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_member_score")
public class BizMemberScore {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 所属项目ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "所属项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 被评价的组员学生ID（关联SYS_USER.ID） */
    @Schema(description = "被评价的组员学生ID（关联SYS_USER.ID）")
    private String studentId;

    /** 打分人用户ID（组长或老师，关联SYS_USER.ID） */
    @Schema(description = "打分人用户ID（组长或老师，关联SYS_USER.ID）")
    private String evaluatorId;

    /** 打分人身份：LEADER-组长手动评分, TEACHER-老师评分 */
    @Schema(description = "打分人身份：LEADER-组长手动评分, TEACHER-老师评分")
    private String evaluatorRole;

    /** 表现评分（0-100） */
    @Schema(description = "表现评分（0-100）")
    private BigDecimal score;

    /** 表现评价与评语（保密限制：仅老师和该组组长有权限查看） */
    @Schema(description = "表现评价与评语（保密限制：仅老师和该组组长有权限查看）")
    private String remark;

    /** 打分考核周期/阶段（如第3周、中期考核） */
    @Schema(description = "打分考核周期/阶段（如第3周、中期考核）")
    private String scorePeriod;

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
