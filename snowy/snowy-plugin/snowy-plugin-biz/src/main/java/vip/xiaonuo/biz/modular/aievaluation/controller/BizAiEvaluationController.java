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
package vip.xiaonuo.biz.modular.aievaluation.controller;

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
import vip.xiaonuo.biz.modular.aievaluation.entity.BizAiEvaluation;
import vip.xiaonuo.biz.modular.aievaluation.param.BizAiEvaluationAddParam;
import vip.xiaonuo.biz.modular.aievaluation.param.BizAiEvaluationEditParam;
import vip.xiaonuo.biz.modular.aievaluation.param.BizAiEvaluationIdParam;
import vip.xiaonuo.biz.modular.aievaluation.param.BizAiEvaluationPageParam;
import vip.xiaonuo.biz.modular.aievaluation.service.BizAiEvaluationService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 团队AI评估控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "团队AI评估控制器")
@RestController
@Validated
public class BizAiEvaluationController {

    @Resource
    private BizAiEvaluationService bizAiEvaluationService;

    /**
     * 获取团队AI评估分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取团队AI评估分页")
    @SaCheckPermission("/biz/aievaluation/page")
    @GetMapping("/biz/aievaluation/page")
    public CommonResult<Page<BizAiEvaluation>> page(BizAiEvaluationPageParam bizAiEvaluationPageParam) {
        return CommonResult.data(bizAiEvaluationService.page(bizAiEvaluationPageParam));
    }

    /**
     * 添加团队AI评估
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加团队AI评估")
    @CommonLog("添加团队AI评估")
    @SaCheckPermission("/biz/aievaluation/add")
    @PostMapping("/biz/aievaluation/add")
    public CommonResult<String> add(@RequestBody @Valid BizAiEvaluationAddParam bizAiEvaluationAddParam) {
        bizAiEvaluationService.add(bizAiEvaluationAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑团队AI评估
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑团队AI评估")
    @CommonLog("编辑团队AI评估")
    @SaCheckPermission("/biz/aievaluation/edit")
    @PostMapping("/biz/aievaluation/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizAiEvaluationEditParam bizAiEvaluationEditParam) {
        bizAiEvaluationService.edit(bizAiEvaluationEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除团队AI评估
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除团队AI评估")
    @CommonLog("删除团队AI评估")
    @SaCheckPermission("/biz/aievaluation/delete")
    @PostMapping("/biz/aievaluation/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizAiEvaluationIdParam> bizAiEvaluationIdParamList) {
        bizAiEvaluationService.delete(bizAiEvaluationIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取团队AI评估详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取团队AI评估详情")
    @SaCheckPermission("/biz/aievaluation/detail")
    @GetMapping("/biz/aievaluation/detail")
    public CommonResult<BizAiEvaluation> detail(@Valid BizAiEvaluationIdParam bizAiEvaluationIdParam) {
        return CommonResult.data(bizAiEvaluationService.detail(bizAiEvaluationIdParam));
    }

    /**
     * 下载团队AI评估导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载团队AI评估导入模板")
    @SaCheckPermission("/biz/aievaluation/downloadImportTemplate")
    @GetMapping(value = "/biz/aievaluation/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizAiEvaluationService.downloadImportTemplate(response);
    }

    /**
     * 导入团队AI评估
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入团队AI评估")
    @CommonLog("导入团队AI评估")
    @SaCheckPermission("/biz/aievaluation/importData")
    @PostMapping("/biz/aievaluation/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizAiEvaluationService.importData(file));
    }

    /**
     * 导出团队AI评估
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出团队AI评估")
    @SaCheckPermission("/biz/aievaluation/exportData")
    @PostMapping(value = "/biz/aievaluation/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizAiEvaluationIdParam> bizAiEvaluationIdParamList, HttpServletResponse response) throws IOException {
        bizAiEvaluationService.exportData(bizAiEvaluationIdParamList, response);
    }
}
