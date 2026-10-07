package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 前台登录参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalLoginParam {

    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
