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
package vip.xiaonuo.biz.modular.projectranking.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目综合排行榜实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_project_ranking")
public class BizProjectRanking {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 项目组ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "项目组ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 所属竞赛ID（若为全局总排行榜则为NULL，关联BIZ_COMPETITION.ID） */
    @Schema(description = "所属竞赛ID（若为全局总排行榜则为NULL，关联BIZ_COMPETITION.ID）")
    private String competitionId;

    /** 榜单期数/学期标识（如 2026_SPRING） */
    @Schema(description = "榜单期数/学期标识（如 2026_SPRING）")
    private String rankingPeriod;

    /** 教师评定得分(0-100) */
    @Schema(description = "教师评定得分(0-100)")
    private BigDecimal teacherScore;

    /** AI综合评分(0-100) */
    @Schema(description = "AI综合评分(0-100)")
    private BigDecimal aiScore;

    /** 加权计算最终总得分(0-100) */
    @Schema(description = "加权计算最终总得分(0-100)")
    private BigDecimal finalScore;

    /** 排名名次（1, 2, 3...） */
    @Schema(description = "排名名次（1, 2, 3...）")
    private Integer rankPosition;

    /** 老师是否已发布：YES-已正式发布上榜（全员可见）, NO-草稿/未发布 */
    @Schema(description = "老师是否已发布：YES-已正式发布上榜（全员可见）, NO-草稿/未发布")
    private String isPublished;

    /** 发布排行榜时间 */
    @Schema(description = "发布排行榜时间")
    private Date publishTime;

    /** 发布操作教师ID（关联SYS_USER.ID） */
    @Schema(description = "发布操作教师ID（关联SYS_USER.ID）")
    private String publishTeacherId;

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
