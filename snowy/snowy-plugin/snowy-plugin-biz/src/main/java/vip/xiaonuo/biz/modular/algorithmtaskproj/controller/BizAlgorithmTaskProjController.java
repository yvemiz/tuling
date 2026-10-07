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
package vip.xiaonuo.biz.modular.algorithmtaskproj.controller;

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
import vip.xiaonuo.biz.modular.algorithmtaskproj.entity.BizAlgorithmTaskProj;
import vip.xiaonuo.biz.modular.algorithmtaskproj.param.BizAlgorithmTaskProjAddParam;
import vip.xiaonuo.biz.modular.algorithmtaskproj.param.BizAlgorithmTaskProjEditParam;
import vip.xiaonuo.biz.modular.algorithmtaskproj.param.BizAlgorithmTaskProjIdParam;
import vip.xiaonuo.biz.modular.algorithmtaskproj.param.BizAlgorithmTaskProjPageParam;
import vip.xiaonuo.biz.modular.algorithmtaskproj.service.BizAlgorithmTaskProjService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 算法任务指派控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "算法任务指派控制器")
@RestController
@Validated
public class BizAlgorithmTaskProjController {

    @Resource
    private BizAlgorithmTaskProjService bizAlgorithmTaskProjService;

    /**
     * 获取算法任务指派分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法任务指派分页")
    @SaCheckPermission("/biz/algorithmtaskproj/page")
    @GetMapping("/biz/algorithmtaskproj/page")
    public CommonResult<Page<BizAlgorithmTaskProj>> page(BizAlgorithmTaskProjPageParam bizAlgorithmTaskProjPageParam) {
        return CommonResult.data(bizAlgorithmTaskProjService.page(bizAlgorithmTaskProjPageParam));
    }

    /**
     * 添加算法任务指派
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加算法任务指派")
    @CommonLog("添加算法任务指派")
    @SaCheckPermission("/biz/algorithmtaskproj/add")
    @PostMapping("/biz/algorithmtaskproj/add")
    public CommonResult<String> add(@RequestBody @Valid BizAlgorithmTaskProjAddParam bizAlgorithmTaskProjAddParam) {
        bizAlgorithmTaskProjService.add(bizAlgorithmTaskProjAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑算法任务指派
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑算法任务指派")
    @CommonLog("编辑算法任务指派")
    @SaCheckPermission("/biz/algorithmtaskproj/edit")
    @PostMapping("/biz/algorithmtaskproj/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizAlgorithmTaskProjEditParam bizAlgorithmTaskProjEditParam) {
        bizAlgorithmTaskProjService.edit(bizAlgorithmTaskProjEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除算法任务指派
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除算法任务指派")
    @CommonLog("删除算法任务指派")
    @SaCheckPermission("/biz/algorithmtaskproj/delete")
    @PostMapping("/biz/algorithmtaskproj/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizAlgorithmTaskProjIdParam> bizAlgorithmTaskProjIdParamList) {
        bizAlgorithmTaskProjService.delete(bizAlgorithmTaskProjIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取算法任务指派详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法任务指派详情")
    @SaCheckPermission("/biz/algorithmtaskproj/detail")
    @GetMapping("/biz/algorithmtaskproj/detail")
    public CommonResult<BizAlgorithmTaskProj> detail(@Valid BizAlgorithmTaskProjIdParam bizAlgorithmTaskProjIdParam) {
        return CommonResult.data(bizAlgorithmTaskProjService.detail(bizAlgorithmTaskProjIdParam));
    }

    /**
     * 下载算法任务指派导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载算法任务指派导入模板")
    @SaCheckPermission("/biz/algorithmtaskproj/downloadImportTemplate")
    @GetMapping(value = "/biz/algorithmtaskproj/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizAlgorithmTaskProjService.downloadImportTemplate(response);
    }

    /**
     * 导入算法任务指派
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入算法任务指派")
    @CommonLog("导入算法任务指派")
    @SaCheckPermission("/biz/algorithmtaskproj/importData")
    @PostMapping("/biz/algorithmtaskproj/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizAlgorithmTaskProjService.importData(file));
    }

    /**
     * 导出算法任务指派
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出算法任务指派")
    @SaCheckPermission("/biz/algorithmtaskproj/exportData")
    @PostMapping(value = "/biz/algorithmtaskproj/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizAlgorithmTaskProjIdParam> bizAlgorithmTaskProjIdParamList, HttpServletResponse response) throws IOException {
        bizAlgorithmTaskProjService.exportData(bizAlgorithmTaskProjIdParamList, response);
    }
}
