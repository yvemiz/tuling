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
package vip.xiaonuo.biz.modular.algorithmtask.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 算法题目任务实体
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Getter
@Setter
@TableName("biz_algorithm_task")
public class BizAlgorithmTask {

    /** 主键ID（雪花ID） */
    @TableId
    @Schema(description = "主键ID（雪花ID）")
    private String id;

    /** 算法题目/练习任务名称 */
    @Schema(description = "算法题目/练习任务名称")
    private String title;

    /** 任务详情与题目描述 */
    @Schema(description = "任务详情与题目描述")
    private String contentDesc;

    /** 题目难度：EASY-简单, MEDIUM-中等, HARD-困难 */
    @Schema(description = "题目难度：EASY-简单, MEDIUM-中等, HARD-困难")
    private String difficulty;

    /** 题目分类标签（如：动态规划,贪心,图论） */
    @Schema(description = "题目分类标签（如：动态规划,贪心,图论）")
    private String tags;

    /** 允许的提交形式（逗号分隔：CODE_TEXT-代码文本, FILE-附件文件, GIT_URL-Git仓库链接） */
    @Schema(description = "允许的提交形式（逗号分隔：CODE_TEXT-代码文本, FILE-附件文件, GIT_URL-Git仓库链接）")
    private String allowedSubmissionTypes;

    /** 截止提交时间 */
    @Schema(description = "截止提交时间")
    private Date deadline;

    /** 发布教师ID（关联SYS_USER.ID） */
    @Schema(description = "发布教师ID（关联SYS_USER.ID）")
    private String teacherId;

    /** 输入样例 */
    @Schema(description = "输入样例")
    private String sampleInput;

    /** 输出样例 */
    @Schema(description = "输出样例")
    private String sampleOutput;

    /** 扩展配置(JSON) */
    @Schema(description = "扩展配置(JSON)")
    private String extJson;

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @Schema(description = "修改用户")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;
}
