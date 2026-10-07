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
package vip.xiaonuo.biz.modular.competitionproject.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞赛报名关联添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizCompetitionProjectAddParam {

    /** 竞赛ID（关联BIZ_COMPETITION.ID） */
    @Schema(description = "竞赛ID（关联BIZ_COMPETITION.ID）")
    private String competitionId;

    /** 参赛形式：PROJECT-团队参赛, INDIVIDUAL-个人参赛 */
    @Schema(description = "参赛形式：PROJECT-团队参赛, INDIVIDUAL-个人参赛")
    private String signupType;

    /** 参赛项目组ID（多对多关联BIZ_PROJECT.ID，团队参赛时必填，仅组长可操作） */
    @Schema(description = "参赛项目组ID（多对多关联BIZ_PROJECT.ID，团队参赛时必填，仅组长可操作）")
    private String projectId;

    /** 参赛学生ID（个人参赛时必填，关联SYS_USER.ID） */
    @Schema(description = "参赛学生ID（个人参赛时必填，关联SYS_USER.ID）")
    private String studentId;

    /** 指导老师用户ID（关联SYS_USER.ID） */
    @Schema(description = "指导老师用户ID（关联SYS_USER.ID）")
    private String instructorTeacherId;

    /** 报名时间 */
    @Schema(description = "报名时间")
    private Date enrollTime;

    /** 参赛资格审核状态：PENDING-待审核, PASSED-报名成功, REJECTED-已驳回 */
    @Schema(description = "参赛资格审核状态：PENDING-待审核, PASSED-报名成功, REJECTED-已驳回")
    private String auditStatus;

    /** 最终赛果/名次（如一等奖/银奖/进入决赛等） */
    @Schema(description = "最终赛果/名次（如一等奖/银奖/进入决赛等）")
    private String competitionResult;

    /** 扩展报名资料(JSON) */
    @Schema(description = "扩展报名资料(JSON)")
    private String extJson;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
