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
package vip.xiaonuo.biz.modular.memberscore.param;

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
 * 组员表现打分编辑参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizMemberScoreEditParam {

    /** 主键ID（雪花ID） */
    @ExcelProperty("主键ID（雪花ID）")
    @Schema(description = "主键ID（雪花ID）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 所属项目ID（关联BIZ_PROJECT.ID） */
    @ExcelProperty("所属项目ID（关联BIZ_PROJECT.ID）")
    @Schema(description = "所属项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 被评价的组员学生ID（关联SYS_USER.ID） */
    @ExcelProperty("被评价的组员学生ID（关联SYS_USER.ID）")
    @Schema(description = "被评价的组员学生ID（关联SYS_USER.ID）")
    private String studentId;

    /** 打分人用户ID（组长或老师，关联SYS_USER.ID） */
    @ExcelProperty("打分人用户ID（组长或老师，关联SYS_USER.ID）")
    @Schema(description = "打分人用户ID（组长或老师，关联SYS_USER.ID）")
    private String evaluatorId;

    /** 打分人身份：LEADER-组长手动评分, TEACHER-老师评分 */
    @ExcelProperty("打分人身份：LEADER-组长手动评分, TEACHER-老师评分")
    @Schema(description = "打分人身份：LEADER-组长手动评分, TEACHER-老师评分")
    private String evaluatorRole;

    /** 表现评分（0-100） */
    @ExcelProperty("表现评分（0-100）")
    @Schema(description = "表现评分（0-100）")
    private BigDecimal score;

    /** 表现评价与评语（保密限制：仅老师和该组组长有权限查看） */
    @ExcelProperty("表现评价与评语（保密限制：仅老师和该组组长有权限查看）")
    @Schema(description = "表现评价与评语（保密限制：仅老师和该组组长有权限查看）")
    private String remark;

    /** 打分考核周期/阶段（如第3周、中期考核） */
    @ExcelProperty("打分考核周期/阶段（如第3周、中期考核）")
    @Schema(description = "打分考核周期/阶段（如第3周、中期考核）")
    private String scorePeriod;

    /** 排序码 */
    @ExcelProperty("排序码")
    @Schema(description = "排序码")
    private Integer sortCode;

}
