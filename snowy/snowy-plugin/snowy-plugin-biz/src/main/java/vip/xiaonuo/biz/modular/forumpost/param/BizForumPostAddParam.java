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
package vip.xiaonuo.biz.modular.forumpost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 论坛帖子管理添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizForumPostAddParam {

    /** 发布人用户ID（关联SYS_USER.ID，所有注册用户均可发布） */
    @Schema(description = "发布人用户ID（关联SYS_USER.ID，所有注册用户均可发布）")
    private String authorId;

    /** 帖子标题 */
    @Schema(description = "帖子标题")
    private String title;

    /** 帖子正文内容(Markdown/富文本) */
    @Schema(description = "帖子正文内容(Markdown/富文本)")
    private String content;

    /** 帖子类别：GENERAL-普通交流, RECRUITMENT-组长招聘, FOLLOW_EVENT-关注动态, COMPETITION_EVENT-竞赛发布 */
    @Schema(description = "帖子类别：GENERAL-普通交流, RECRUITMENT-组长招聘, FOLLOW_EVENT-关注动态, COMPETITION_EVENT-竞赛发布")
    private String postType;

    /** 关联实体类型：RECRUIT-招聘, COMPETITION-竞赛, PROJECT-项目组, NONE-无 */
    @Schema(description = "关联实体类型：RECRUIT-招聘, COMPETITION-竞赛, PROJECT-项目组, NONE-无")
    private String refType;

    /** 关联实体主键ID */
    @Schema(description = "关联实体主键ID")
    private String refId;

    /** 是否置顶：YES-置顶（仅老师可置顶）, NO-普通 */
    @Schema(description = "是否置顶：YES-置顶（仅老师可置顶）, NO-普通")
    private String isPinned;

    /** 置顶时间 */
    @Schema(description = "置顶时间")
    private Date pinTime;

    /** 审核状态：AI_PENDING-待AI审查, PUBLISHED-已发布(通过), MANUAL_PENDING-转人工审核, REJECTED-已驳回 */
    @Schema(description = "审核状态：AI_PENDING-待AI审查, PUBLISHED-已发布(通过), MANUAL_PENDING-转人工审核, REJECTED-已驳回")
    private String auditStatus;

    /** AI审查结论：PASS-正常通过, RISK-有风险/违规, ERROR-AI配置/调用异常 */
    @Schema(description = "AI审查结论：PASS-正常通过, RISK-有风险/违规, ERROR-AI配置/调用异常")
    private String aiAuditResult;

    /** AI审查不通过或存疑原因 */
    @Schema(description = "AI审查不通过或存疑原因")
    private String aiAuditReason;

    /** 组长通过票数计数（超过阈值后自动正式发布） */
    @Schema(description = "组长通过票数计数（超过阈值后自动正式发布）")
    private Integer leaderPassCount;

    /** 组长驳回票数计数 */
    @Schema(description = "组长驳回票数计数")
    private Integer leaderRejectCount;

    /** 浏览数 */
    @Schema(description = "浏览数")
    private Integer viewCount;

    /** 点赞数 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 评论总数 */
    @Schema(description = "评论总数")
    private Integer commentCount;

    /** 扩展信息(JSON) */
    @Schema(description = "扩展信息(JSON)")
    private String extJson;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
