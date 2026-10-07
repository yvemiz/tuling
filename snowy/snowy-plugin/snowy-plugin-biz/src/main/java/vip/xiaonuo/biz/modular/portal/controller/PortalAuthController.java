package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.biz.modular.portal.param.PortalLoginParam;
import vip.xiaonuo.biz.modular.portal.param.PortalRegisterParam;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

/**
 * 前台认证与登录控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-认证控制器")
@RestController
@Validated
@RequestMapping("/client/portal/auth")
public class PortalAuthController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "前台账号密码登录")
    @PostMapping("/login")
    public CommonResult<JSONObject> login(@RequestBody @Valid PortalLoginParam param) {
        return portalService.login(param);
    }

    @Operation(summary = "前台用户自主注册（支持学生与老师）")
    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody @Valid PortalRegisterParam param) {
        return portalService.register(param);
    }

    @Operation(summary = "前台一键演示快捷登录（STUDENT/LEADER/TEACHER）")
    @GetMapping("/demoLogin")
    public CommonResult<JSONObject> demoLogin(@RequestParam(defaultValue = "STUDENT") String role) {
        return portalService.demoLogin(role);
    }

    @Operation(summary = "前台退出登录")
    @SaClientCheckLogin
    @GetMapping("/logout")
    public CommonResult<String> logout() {
        StpClientUtil.logout();
        return CommonResult.ok();
    }
}
