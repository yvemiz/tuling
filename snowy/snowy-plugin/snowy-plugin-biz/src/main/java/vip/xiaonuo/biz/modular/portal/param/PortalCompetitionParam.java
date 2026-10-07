package vip.xiaonuo.biz.modular.portal.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 老师发布与编辑竞赛参数
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Getter
@Setter
public class PortalCompetitionParam {

    @Schema(description = "竞赛ID（编辑时传）")
    private String id;

    @Schema(description = "竞赛名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "竞赛名称不能为空")
    private String title;

    @Schema(description = "竞赛级别：SCHOOL-校级, PROVINCE-省级, NATIONAL-国家级, INTERNATIONAL-国际级")
    private String competitionLevel = "SCHOOL";

    @Schema(description = "主办单位/承办方")
    private String organizer;

    @Schema(description = "竞赛类别（计算机/算法/创客/数学建模等）")
    private String category;

    @Schema(description = "报名开始时间")
    private Date signupStartTime;

    @Schema(description = "报名截止时间")
    private Date signupEndTime;

    @Schema(description = "竞赛开始时间")
    private Date competitionStartTime;

    @Schema(description = "竞赛结束时间")
    private Date competitionEndTime;

    @Schema(description = "最少参赛人数，1表示允许单人参赛")
    private Integer minMembers = 1;

    @Schema(description = "最多参赛人数，1表示单人，大于1须以组为单位")
    private Integer maxMembers = 5;

    @Schema(description = "竞赛要求与参赛规程")
    private String requirements;

    @Schema(description = "规程附件文件ID")
    private String attachmentFileId;

    @Schema(description = "竞赛状态：PENDING-未开始, SIGNUP-报名中, ONGOING-进行中, REVIEWING-评审中, FINISHED-已结束")
    private String status = "SIGNUP";
}
