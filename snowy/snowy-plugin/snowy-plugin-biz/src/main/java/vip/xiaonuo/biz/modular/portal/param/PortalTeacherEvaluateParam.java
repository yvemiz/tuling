package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 老师对项目组综合评估与上榜评分参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalTeacherEvaluateParam {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目ID不能为空")
    private String projectId;

    @Schema(description = "竞赛ID")
    private String competitionId;

    @Schema(description = "榜单期数（如2026_SPRING）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "榜单期数不能为空")
    private String rankingPeriod;

    @Schema(description = "教师评定得分(0-100)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "教师评分不能为空")
    @DecimalMin(value = "0.0", message = "评分不能低于0分")
    @DecimalMax(value = "100.0", message = "评分不能超过100分")
    private BigDecimal teacherScore;
}
