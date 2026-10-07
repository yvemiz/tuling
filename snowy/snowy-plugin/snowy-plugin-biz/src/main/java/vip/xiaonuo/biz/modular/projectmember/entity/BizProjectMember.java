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
package vip.xiaonuo.biz.modular.projectmember.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目成员管理实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_project_member")
public class BizProjectMember {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 项目ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 成员学生用户ID（关联SYS_USER.ID，组员可进多个组） */
    @Schema(description = "成员学生用户ID（关联SYS_USER.ID，组员可进多个组）")
    private String studentId;

    /** 团队内角色：LEADER-组长, MEMBER-普通组员 */
    @Schema(description = "团队内角色：LEADER-组长, MEMBER-普通组员")
    private String memberRole;

    /** 组内职责描述（如算法负责人、前端开发、报告撰写等） */
    @Schema(description = "组内职责描述（如算法负责人、前端开发、报告撰写等）")
    private String dutyRemark;

    /** 加入时间 */
    @Schema(description = "加入时间")
    private Date joinTime;

    /** 成员状态：ACTIVE-正式在组, QUIT-已退出 */
    @Schema(description = "成员状态：ACTIVE-正式在组, QUIT-已退出")
    private String memberStatus;

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
