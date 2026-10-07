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
package vip.xiaonuo.biz.modular.projectrecruit.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目招募需求添加参数
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
public class BizProjectRecruitAddParam {

    /** 所属项目ID（关联BIZ_PROJECT.ID） */
    @Schema(description = "所属项目ID（关联BIZ_PROJECT.ID）")
    private String projectId;

    /** 发布组长用户ID（关联SYS_USER.ID） */
    @Schema(description = "发布组长用户ID（关联SYS_USER.ID）")
    private String leaderId;

    /** 招聘标题 */
    @Schema(description = "招聘标题")
    private String title;

    /** 拟招募人数 */
    @Schema(description = "拟招募人数")
    private Integer recruitCount;

    /** 招募技能要求与详细说明 */
    @Schema(description = "招募技能要求与详细说明")
    private String requirementDesc;

    /** 状态：OPEN-招募中, CLOSED-已招满/关闭 */
    @Schema(description = "状态：OPEN-招募中, CLOSED-已招满/关闭")
    private String status;

    /** 招募截止时间 */
    @Schema(description = "招募截止时间")
    private Date deadline;

    /** 联动发布的论坛帖子ID（关联BIZ_FORUM_POST.ID） */
    @Schema(description = "联动发布的论坛帖子ID（关联BIZ_FORUM_POST.ID）")
    private String forumPostId;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

}
