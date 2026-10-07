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
package vip.xiaonuo.biz.modular.forumaudit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.biz.modular.forumaudit.entity.BizForumAudit;
import vip.xiaonuo.biz.modular.forumaudit.param.BizForumAuditAddParam;
import vip.xiaonuo.biz.modular.forumaudit.param.BizForumAuditEditParam;
import vip.xiaonuo.biz.modular.forumaudit.param.BizForumAuditIdParam;
import vip.xiaonuo.biz.modular.forumaudit.param.BizForumAuditPageParam;
import vip.xiaonuo.biz.modular.forumaudit.service.BizForumAuditService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 论坛审核记录控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "论坛审核记录控制器")
@RestController
@Validated
public class BizForumAuditController {

    @Resource
    private BizForumAuditService bizForumAuditService;

    /**
     * 获取论坛审核记录分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛审核记录分页")
    @SaCheckPermission("/biz/forumaudit/page")
    @GetMapping("/biz/forumaudit/page")
    public CommonResult<Page<BizForumAudit>> page(BizForumAuditPageParam bizForumAuditPageParam) {
        return CommonResult.data(bizForumAuditService.page(bizForumAuditPageParam));
    }

    /**
     * 添加论坛审核记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加论坛审核记录")
    @CommonLog("添加论坛审核记录")
    @SaCheckPermission("/biz/forumaudit/add")
    @PostMapping("/biz/forumaudit/add")
    public CommonResult<String> add(@RequestBody @Valid BizForumAuditAddParam bizForumAuditAddParam) {
        bizForumAuditService.add(bizForumAuditAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑论坛审核记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑论坛审核记录")
    @CommonLog("编辑论坛审核记录")
    @SaCheckPermission("/biz/forumaudit/edit")
    @PostMapping("/biz/forumaudit/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizForumAuditEditParam bizForumAuditEditParam) {
        bizForumAuditService.edit(bizForumAuditEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除论坛审核记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除论坛审核记录")
    @CommonLog("删除论坛审核记录")
    @SaCheckPermission("/biz/forumaudit/delete")
    @PostMapping("/biz/forumaudit/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizForumAuditIdParam> bizForumAuditIdParamList) {
        bizForumAuditService.delete(bizForumAuditIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取论坛审核记录详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛审核记录详情")
    @SaCheckPermission("/biz/forumaudit/detail")
    @GetMapping("/biz/forumaudit/detail")
    public CommonResult<BizForumAudit> detail(@Valid BizForumAuditIdParam bizForumAuditIdParam) {
        return CommonResult.data(bizForumAuditService.detail(bizForumAuditIdParam));
    }

    /**
     * 下载论坛审核记录导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载论坛审核记录导入模板")
    @SaCheckPermission("/biz/forumaudit/downloadImportTemplate")
    @GetMapping(value = "/biz/forumaudit/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizForumAuditService.downloadImportTemplate(response);
    }

    /**
     * 导入论坛审核记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入论坛审核记录")
    @CommonLog("导入论坛审核记录")
    @SaCheckPermission("/biz/forumaudit/importData")
    @PostMapping("/biz/forumaudit/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizForumAuditService.importData(file));
    }

    /**
     * 导出论坛审核记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出论坛审核记录")
    @SaCheckPermission("/biz/forumaudit/exportData")
    @PostMapping(value = "/biz/forumaudit/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizForumAuditIdParam> bizForumAuditIdParamList, HttpServletResponse response) throws IOException {
        bizForumAuditService.exportData(bizForumAuditIdParamList, response);
    }
}
