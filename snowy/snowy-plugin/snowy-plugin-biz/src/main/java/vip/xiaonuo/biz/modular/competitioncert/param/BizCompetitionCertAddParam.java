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
package vip.xiaonuo.biz.modular.competitioncert.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞赛证书管理添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizCompetitionCertAddParam {

    /** 所属竞赛ID（关联BIZ_COMPETITION.ID） */
    @Schema(description = "所属竞赛ID（关联BIZ_COMPETITION.ID）")
    private String competitionId;

    /** 所属项目ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "所属项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 获得证书学生ID（关联SYS_USER.ID） */
    @Schema(description = "获得证书学生ID（关联SYS_USER.ID）")
    private String studentId;

    /** 证书名称 */
    @Schema(description = "证书名称")
    private String certificateName;

    /** 获奖级别（国家一等奖/省级二等奖/优秀奖等） */
    @Schema(description = "获奖级别（国家一等奖/省级二等奖/优秀奖等）")
    private String awardLevel;

    /** 获奖日期 */
    @Schema(description = "获奖日期")
    private Date awardDate;

    /** 证书扫描件/照片文件ID（关联DEV_FILE.ID） */
    @Schema(description = "证书扫描件/照片文件ID（关联DEV_FILE.ID）")
    private String fileId;

    /** 证书图片URL或路径 */
    @Schema(description = "证书图片URL或路径")
    private String certificateImage;

    /** 获奖小组成员名单（以小组为单位，多个学生） */
    @Schema(description = "获奖小组成员名单（以小组为单位，多个学生）")
    private String studentNames;

    /** 获奖小组成员ID列表 */
    @Schema(description = "获奖小组成员ID列表")
    private String studentIds;

    /** 上传人用户ID（组员或组长皆可上传） */
    @Schema(description = "上传人用户ID（组员或组长皆可上传）")
    private String uploadUserId;

    /** 教师认证状态：PENDING-待认证, VERIFIED-已认证, REJECTED-已驳回 */
    @Schema(description = "教师认证状态：PENDING-待认证, VERIFIED-已认证, REJECTED-已驳回")
    private String verifyStatus;

    /** 认证教师ID（关联SYS_USER.ID） */
    @Schema(description = "认证教师ID（关联SYS_USER.ID）")
    private String verifyTeacherId;

    /** 审核评语 */
    @Schema(description = "审核评语")
    private String verifyRemark;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
