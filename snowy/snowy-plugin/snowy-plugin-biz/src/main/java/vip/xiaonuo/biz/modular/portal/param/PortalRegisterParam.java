package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 前台注册参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalRegisterParam {

    @Schema(description = "学号（学生注册只需填写学号与密码，后端根据学号自动匹配预导入档案）")
    private String studentNo;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "确认密码")
    private String confirmPassword;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "角色身份：STUDENT-学生, TEACHER-老师")
    private String role;

    @Schema(description = "学号/工号")
    private String userNo;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "学院专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "技能标签，逗号分隔")
    private String skillTags;

    @Schema(description = "个人简介/研究方向")
    private String bio;
}
