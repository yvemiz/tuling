package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 论坛审核处理参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalAuditParam {

    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "帖子ID不能为空")
    private String postId;

    @Schema(description = "审核决议：PASS-通过, REJECT-驳回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核结果不能为空")
    private String auditResult;

    @Schema(description = "审核意见与理由说明")
    private String auditReason;
}
