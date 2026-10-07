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
package vip.xiaonuo.biz.modular.aievaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 团队AI评估实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_ai_team_evaluation")
public class BizAiEvaluation {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 评估的项目组ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "评估的项目组ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 触发评估人用户ID（组长或老师，关联SYS_USER.ID） */
    @Schema(description = "触发评估人用户ID（组长或老师，关联SYS_USER.ID）")
    private String triggerUserId;

    /** 评估批次/周期（如 2026-Q1, STAGE_1 等） */
    @Schema(description = "评估批次/周期（如 2026-Q1, STAGE_1 等）")
    private String evaluationBatch;

    /** 组员算法整体完成度百分比(0-100) */
    @Schema(description = "组员算法整体完成度百分比(0-100)")
    private BigDecimal algorithmCompletionRate;

    /** 竞赛参与度与活跃度得分(0-100) */
    @Schema(description = "竞赛参与度与活跃度得分(0-100)")
    private BigDecimal competitionParticipationScore;

    /** AI加权综合评估得分(0-100) */
    @Schema(description = "AI加权综合评估得分(0-100)")
    private BigDecimal aiTotalScore;

    /** AI生成的诊断报告文本（含各维度深度分析、优势分析、短板预警及提升建议） */
    @Schema(description = "AI生成的诊断报告文本（含各维度深度分析、优势分析、短板预警及提升建议）")
    private String aiEvaluationReport;

    /** 执行评分的AI模型版本/规则版本 */
    @Schema(description = "执行评分的AI模型版本/规则版本")
    private String aiModelVersion;

    /** 扩展指标数据(JSON) */
    @Schema(description = "扩展指标数据(JSON)")
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
