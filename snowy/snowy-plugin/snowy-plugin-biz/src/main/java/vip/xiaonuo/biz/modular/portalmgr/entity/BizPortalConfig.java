package vip.xiaonuo.biz.modular.portalmgr.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 前台门户配置实体
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Getter
@Setter
@TableName("biz_portal_config")
public class BizPortalConfig {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 配置键 */
    @Schema(description = "配置键")
    private String configKey;

    /** 配置名称 */
    @Schema(description = "配置名称")
    private String configName;

    /** 配置值(JSON/Text) */
    @Schema(description = "配置值(JSON/Text)")
    private String configValue;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 删除标志 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;
}
