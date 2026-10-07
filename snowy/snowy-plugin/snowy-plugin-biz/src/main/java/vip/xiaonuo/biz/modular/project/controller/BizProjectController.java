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
package vip.xiaonuo.biz.modular.project.controller;

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
import vip.xiaonuo.biz.modular.project.entity.BizProject;
import vip.xiaonuo.biz.modular.project.param.BizProjectAddParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectEditParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectIdParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectPageParam;
import vip.xiaonuo.biz.modular.project.service.BizProjectService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 项目团队管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "项目团队管理控制器")
@RestController
@Validated
public class BizProjectController {

    @Resource
    private BizProjectService bizProjectService;

    /**
     * 获取项目团队管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目团队管理分页")
    @SaCheckPermission("/biz/project/page")
    @GetMapping("/biz/project/page")
    public CommonResult<Page<BizProject>> page(BizProjectPageParam bizProjectPageParam) {
        return CommonResult.data(bizProjectService.page(bizProjectPageParam));
    }

    /**
     * 添加项目团队管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加项目团队管理")
    @CommonLog("添加项目团队管理")
    @SaCheckPermission("/biz/project/add")
    @PostMapping("/biz/project/add")
    public CommonResult<String> add(@RequestBody @Valid BizProjectAddParam bizProjectAddParam) {
        bizProjectService.add(bizProjectAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目团队管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑项目团队管理")
    @CommonLog("编辑项目团队管理")
    @SaCheckPermission("/biz/project/edit")
    @PostMapping("/biz/project/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizProjectEditParam bizProjectEditParam) {
        bizProjectService.edit(bizProjectEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目团队管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除项目团队管理")
    @CommonLog("删除项目团队管理")
    @SaCheckPermission("/biz/project/delete")
    @PostMapping("/biz/project/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizProjectIdParam> bizProjectIdParamList) {
        bizProjectService.delete(bizProjectIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目团队管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目团队管理详情")
    @SaCheckPermission("/biz/project/detail")
    @GetMapping("/biz/project/detail")
    public CommonResult<BizProject> detail(@Valid BizProjectIdParam bizProjectIdParam) {
        return CommonResult.data(bizProjectService.detail(bizProjectIdParam));
    }

    /**
     * 下载项目团队管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载项目团队管理导入模板")
    @SaCheckPermission("/biz/project/downloadImportTemplate")
    @GetMapping(value = "/biz/project/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizProjectService.downloadImportTemplate(response);
    }

    /**
     * 导入项目团队管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入项目团队管理")
    @CommonLog("导入项目团队管理")
    @SaCheckPermission("/biz/project/importData")
    @PostMapping("/biz/project/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizProjectService.importData(file));
    }

    /**
     * 导出项目团队管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出项目团队管理")
    @SaCheckPermission("/biz/project/exportData")
    @PostMapping(value = "/biz/project/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizProjectIdParam> bizProjectIdParamList, HttpServletResponse response) throws IOException {
        bizProjectService.exportData(bizProjectIdParamList, response);
    }
}
