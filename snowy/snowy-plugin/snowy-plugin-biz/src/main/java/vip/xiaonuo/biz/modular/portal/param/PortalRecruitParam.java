package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 组长发布招募需求参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalRecruitParam {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目ID不能为空")
    private String projectId;

    @Schema(description = "招聘标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "招聘标题不能为空")
    private String title;

    @Schema(description = "拟招募人数")
    private Integer recruitCount = 1;

    @Schema(description = "技能要求与详细说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "招募技能要求不能为空")
    private String requirementDesc;

    @Schema(description = "截止时间")
    private Date deadline;
}
