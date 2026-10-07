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
package vip.xiaonuo.biz.modular.forumaudit.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 论坛审核记录编辑参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizForumAuditEditParam {

    /** 主键ID（雪花ID） */
    @ExcelProperty("主键ID（雪花ID）")
    @Schema(description = "主键ID（雪花ID）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 审核的帖子ID（关联BIZ_FORUM_POST.ID） */
    @ExcelProperty("审核的帖子ID（关联BIZ_FORUM_POST.ID）")
    @Schema(description = "审核的帖子ID（关联BIZ_FORUM_POST.ID）")
    private String postId;

    /** 审核主体类型：AI-智能审查, TEACHER-老师直接审核, LEADER-组长联审投票 */
    @ExcelProperty("审核主体类型：AI-智能审查, TEACHER-老师直接审核, LEADER-组长联审投票")
    @Schema(description = "审核主体类型：AI-智能审查, TEACHER-老师直接审核, LEADER-组长联审投票")
    private String auditorType;

    /** 审核人用户ID（关联SYS_USER.ID，AI审核时可填AI_SYSTEM） */
    @ExcelProperty("审核人用户ID（关联SYS_USER.ID，AI审核时可填AI_SYSTEM）")
    @Schema(description = "审核人用户ID（关联SYS_USER.ID，AI审核时可填AI_SYSTEM）")
    private String auditorId;

    /** 审核决议：PASS-通过, REJECT-驳回/存疑 */
    @ExcelProperty("审核决议：PASS-通过, REJECT-驳回/存疑")
    @Schema(description = "审核决议：PASS-通过, REJECT-驳回/存疑")
    private String auditResult;

    /** 审核意见与处理说明 */
    @ExcelProperty("审核意见与处理说明")
    @Schema(description = "审核意见与处理说明")
    private String auditReason;

    /** 审核时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("审核时间")
    @Schema(description = "审核时间")
    private Date auditTime;

    /** 排序码 */
    @ExcelProperty("排序码")
    @Schema(description = "排序码")
    private Integer sortCode;

}
