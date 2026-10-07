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
package vip.xiaonuo.biz.modular.algorithmsuffix.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 算法作业提交添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizAlgorithmSubmissionAddParam {

    /** 算法任务ID（关联BIZ_ALGORITHM_TASK.ID） */
    @Schema(description = "算法任务ID（关联BIZ_ALGORITHM_TASK.ID）")
    private String taskId;

    /** 提交学生ID（关联SYS_USER.ID。核心规则：学生进多组只要提交一次，各组统计自动生效） */
    @Schema(description = "提交学生ID（关联SYS_USER.ID。核心规则：学生进多组只要提交一次，各组统计自动生效）")
    private String studentId;

    /** 提交形式：CODE_TEXT-在线代码, FILE-文件, GIT_URL-Git链接 */
    @Schema(description = "提交形式：CODE_TEXT-在线代码, FILE-文件, GIT_URL-Git链接")
    private String submissionType;

    /** 编程语言（Java, C++, Python, Go等） */
    @Schema(description = "编程语言（Java, C++, Python, Go等）")
    private String codeLanguage;

    /** 代码文本内容 */
    @Schema(description = "代码文本内容")
    private String codeContent;

    /** 提交的文件附件ID（关联DEV_FILE.ID） */
    @Schema(description = "提交的文件附件ID（关联DEV_FILE.ID）")
    private String fileId;

    /** Git仓库地址/分支说明 */
    @Schema(description = "Git仓库地址/分支说明")
    private String gitUrl;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private Date submitTime;

    /** 得分（0-100） */
    @Schema(description = "得分（0-100）")
    private BigDecimal score;

    /** 状态：SUBMITTED-已提交, GRADED-已评分, REWORK-需重做 */
    @Schema(description = "状态：SUBMITTED-已提交, GRADED-已评分, REWORK-需重做")
    private String status;

    /** 教师批改评语 */
    @Schema(description = "教师批改评语")
    private String teacherFeedback;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
