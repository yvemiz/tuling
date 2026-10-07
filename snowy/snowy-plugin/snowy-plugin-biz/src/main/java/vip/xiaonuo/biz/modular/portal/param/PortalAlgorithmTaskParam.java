package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 老师发布算法练习题参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalAlgorithmTaskParam {

    @Schema(description = "算法练习题目", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目名称不能为空")
    private String title;

    @Schema(description = "任务详情与题目描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目描述不能为空")
    private String contentDesc;

    @Schema(description = "题目难度：EASY-简单, MEDIUM-中等, HARD-困难")
    private String difficulty = "MEDIUM";

    @Schema(description = "题目分类标签（如：动态规划,贪心,图论）")
    private String tags;

    @Schema(description = "允许的提交形式（逗号分隔：CODE_TEXT,FILE,GIT_URL）")
    private String allowedSubmissionTypes = "CODE_TEXT,FILE,GIT_URL";

    @Schema(description = "截止提交时间")
    private Date deadline;

    @Schema(description = "输入样例")
    private String sampleInput;

    @Schema(description = "输出样例")
    private String sampleOutput;

    @Schema(description = "指派的项目组ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "指派项目组不能为空")
    private List<String> assignedProjectIds;
}
