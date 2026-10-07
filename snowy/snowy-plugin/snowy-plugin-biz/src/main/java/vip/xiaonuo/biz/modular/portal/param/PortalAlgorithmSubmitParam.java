package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 组员提交算法作业参数（多组仅需提交一次）
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalAlgorithmSubmitParam {

    @Schema(description = "算法任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "算法任务ID不能为空")
    private String taskId;

    @Schema(description = "提交形式：CODE_TEXT-在线代码, FILE-文件, GIT_URL-Git链接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "提交类型不能为空")
    private String submissionType;

    @Schema(description = "编程语言（Java, Python, C++, Go等）")
    private String codeLanguage;

    @Schema(description = "代码文本内容")
    private String codeContent;

    @Schema(description = "提交的文件附件ID")
    private String fileId;

    @Schema(description = "Git仓库地址/分支说明")
    private String gitUrl;
}
