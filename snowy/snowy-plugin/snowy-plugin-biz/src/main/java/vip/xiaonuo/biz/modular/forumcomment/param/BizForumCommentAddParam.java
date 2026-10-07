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
package vip.xiaonuo.biz.modular.forumcomment.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 论坛评论管理添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizForumCommentAddParam {

    /** 所属帖子ID（关联BIZ_FORUM_POST.ID） */
    @Schema(description = "所属帖子ID（关联BIZ_FORUM_POST.ID）")
    private String postId;

    /** 评论人用户ID（关联SYS_USER.ID） */
    @Schema(description = "评论人用户ID（关联SYS_USER.ID）")
    private String userId;

    /** 评论内容 */
    @Schema(description = "评论内容")
    private String content;

    /** 上级父评论ID（支持盖楼回复，顶层为NULL） */
    @Schema(description = "上级父评论ID（支持盖楼回复，顶层为NULL）")
    private String parentId;

    /** 被回复人用户ID（关联SYS_USER.ID） */
    @Schema(description = "被回复人用户ID（关联SYS_USER.ID）")
    private String replyToUserId;

    /** 点赞数 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
