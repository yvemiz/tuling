package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 学生创建项目组并成为组长参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalProjectCreateParam {

    @Schema(description = "关联的竞赛ID（为UNASSIGNED或空时表示暂未参与）")
    private String competitionId;

    @Schema(description = "项目团队名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    @Schema(description = "项目描述与愿景", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目描述不能为空")
    private String projectDesc;

    @Schema(description = "队伍成员上限人数，默认5")
    private Integer maxMemberLimit = 5;

    @Schema(description = "是否私密：YES-私密, NO-公开，默认NO")
    private String isPrivate = "NO";
}
