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
package vip.xiaonuo.biz.modular.studentprofile.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 学生档案管理添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizStudentProfileAddParam {

    /** 系统用户ID（关联SYS_USER.ID） */
    @Schema(description = "系统用户ID（关联SYS_USER.ID）")
    private String userId;

    /** 学号 */
    @Schema(description = "学号")
    private String studentNo;

    /** 所属学院机构ID（关联SYS_ORG.ID） */
    @Schema(description = "所属学院机构ID（关联SYS_ORG.ID）")
    private String collegeId;

    /** 专业名称 */
    @Schema(description = "专业名称")
    private String major;

    /** 年级（如2023级） */
    @Schema(description = "年级（如2023级）")
    private String grade;

    /** 班级名称 */
    @Schema(description = "班级名称")
    private String className;

    /** 技能标签（逗号分隔，如: Java,Python,算法,前端） */
    @Schema(description = "技能标签（逗号分隔，如: Java,Python,算法,前端）")
    private String skillTags;

    /** 个人简介/技术特长 */
    @Schema(description = "个人简介/技术特长")
    private String bio;

    /** 扩展信息(JSON) */
    @Schema(description = "扩展信息(JSON)")
    private String extJson;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

    /** 账号 */
    @Schema(description = "账号")
    private String account;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;
}
