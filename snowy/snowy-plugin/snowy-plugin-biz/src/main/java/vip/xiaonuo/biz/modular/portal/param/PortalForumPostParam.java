package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 论坛发帖参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalForumPostParam {

    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "帖子标题不能为空")
    private String title;

    @Schema(description = "帖子内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "帖子内容不能为空")
    private String content;

    @Schema(description = "帖子分类：GENERAL-普通交流, RECRUITMENT-组长招聘, FOLLOW_EVENT-关注动态, COMPETITION_EVENT-竞赛发布")
    private String postType = "GENERAL";

    @Schema(description = "关联实体类型：RECRUIT, COMPETITION, PROJECT, NONE")
    private String refType = "NONE";

    @Schema(description = "关联实体ID")
    private String refId;

    @Schema(description = "扩展数据JSON (多模态内容：代码片段、附件列表、标签列表等)")
    private String extJson;
}
