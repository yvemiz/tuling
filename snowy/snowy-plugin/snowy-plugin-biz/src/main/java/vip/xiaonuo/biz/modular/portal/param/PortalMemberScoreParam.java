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
 * 组长/老师对成员表现手动打分参数（仅老师与组长可见）
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalMemberScoreParam {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目ID不能为空")
    private String projectId;

    @Schema(description = "被评分的学生ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学生ID不能为空")
    private String studentId;

    @Schema(description = "表现评分(0-100)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "0.0", message = "评分不能低于0分")
    @DecimalMax(value = "100.0", message = "评分不能超过100分")
    private BigDecimal score;

    @Schema(description = "保密评语（仅老师与本组组长可见）")
    private String remark;

    @Schema(description = "考核阶段/周期（如中期考核、阶段一等）")
    private String scorePeriod = "阶段综合表现";
}
