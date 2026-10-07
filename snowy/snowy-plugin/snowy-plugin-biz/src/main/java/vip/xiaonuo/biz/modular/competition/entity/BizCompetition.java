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
package vip.xiaonuo.biz.modular.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞赛信息管理实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_competition")
public class BizCompetition {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 竞赛名称 */
    @Schema(description = "竞赛名称")
    private String title;

    /** 竞赛级别：SCHOOL-校级, PROVINCE-省级, NATIONAL-国家级, INTERNATIONAL-国际级 */
    @Schema(description = "竞赛级别：SCHOOL-校级, PROVINCE-省级, NATIONAL-国家级, INTERNATIONAL-国际级")
    private String competitionLevel;

    /** 主办单位/承办方 */
    @Schema(description = "主办单位/承办方")
    private String organizer;

    /** 竞赛类别（计算机/算法/创客/数学建模等） */
    @Schema(description = "竞赛类别（计算机/算法/创客/数学建模等）")
    private String category;

    /** 报名开始时间 */
    @Schema(description = "报名开始时间")
    private Date signupStartTime;

    /** 报名截止时间 */
    @Schema(description = "报名截止时间")
    private Date signupEndTime;

    /** 竞赛开始时间 */
    @Schema(description = "竞赛开始时间")
    private Date competitionStartTime;

    /** 竞赛结束时间 */
    @Schema(description = "竞赛结束时间")
    private Date competitionEndTime;

    /** 最少参赛人数（1表示允许单人参赛） */
    @Schema(description = "最少参赛人数（1表示允许单人参赛）")
    private Integer minMembers;

    /** 最多参赛人数（若为1则为单人竞赛，无须建组；大于1则须以组为单位且组长报名） */
    @Schema(description = "最多参赛人数（若为1则为单人竞赛，无须建组；大于1则须以组为单位且组长报名）")
    private Integer maxMembers;

    /** 竞赛要求与参赛规程 */
    @Schema(description = "竞赛要求与参赛规程")
    private String requirements;

    /** 规程附件文件ID（关联DEV_FILE.ID） */
    @Schema(description = "规程附件文件ID（关联DEV_FILE.ID）")
    private String attachmentFileId;

    /** 竞赛状态：PENDING-未开始, SIGNUP-报名中, ONGOING-进行中, REVIEWING-评审中, FINISHED-已结束 */
    @Schema(description = "竞赛状态：PENDING-未开始, SIGNUP-报名中, ONGOING-进行中, REVIEWING-评审中, FINISHED-已结束")
    private String status;

    /** 发布教师用户ID（关联SYS_USER.ID，仅教师可增删改） */
    @Schema(description = "发布教师用户ID（关联SYS_USER.ID，仅教师可增删改）")
    private String publishTeacherId;

    /** 浏览次数 */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /** 关注人数 */
    @Schema(description = "关注人数")
    private Integer followCount;

    /** 扩展配置(JSON) */
    @Schema(description = "扩展配置(JSON)")
    private String extJson;

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
