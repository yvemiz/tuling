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
package vip.xiaonuo.biz.modular.algorithmsuffix.controller;

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
import vip.xiaonuo.biz.modular.algorithmsuffix.entity.BizAlgorithmSubmission;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionAddParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionEditParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionIdParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionPageParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.service.BizAlgorithmSubmissionService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 算法作业提交控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "算法作业提交控制器")
@RestController
@Validated
public class BizAlgorithmSubmissionController {

    @Resource
    private BizAlgorithmSubmissionService bizAlgorithmSubmissionService;

    /**
     * 获取算法作业提交分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法作业提交分页")
    @SaCheckPermission("/biz/algorithmsuffix/page")
    @GetMapping("/biz/algorithmsuffix/page")
    public CommonResult<Page<BizAlgorithmSubmission>> page(BizAlgorithmSubmissionPageParam bizAlgorithmSubmissionPageParam) {
        return CommonResult.data(bizAlgorithmSubmissionService.page(bizAlgorithmSubmissionPageParam));
    }

    /**
     * 添加算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加算法作业提交")
    @CommonLog("添加算法作业提交")
    @SaCheckPermission("/biz/algorithmsuffix/add")
    @PostMapping("/biz/algorithmsuffix/add")
    public CommonResult<String> add(@RequestBody @Valid BizAlgorithmSubmissionAddParam bizAlgorithmSubmissionAddParam) {
        bizAlgorithmSubmissionService.add(bizAlgorithmSubmissionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑算法作业提交")
    @CommonLog("编辑算法作业提交")
    @SaCheckPermission("/biz/algorithmsuffix/edit")
    @PostMapping("/biz/algorithmsuffix/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizAlgorithmSubmissionEditParam bizAlgorithmSubmissionEditParam) {
        bizAlgorithmSubmissionService.edit(bizAlgorithmSubmissionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除算法作业提交")
    @CommonLog("删除算法作业提交")
    @SaCheckPermission("/biz/algorithmsuffix/delete")
    @PostMapping("/biz/algorithmsuffix/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList) {
        bizAlgorithmSubmissionService.delete(bizAlgorithmSubmissionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取算法作业提交详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法作业提交详情")
    @SaCheckPermission("/biz/algorithmsuffix/detail")
    @GetMapping("/biz/algorithmsuffix/detail")
    public CommonResult<BizAlgorithmSubmission> detail(@Valid BizAlgorithmSubmissionIdParam bizAlgorithmSubmissionIdParam) {
        return CommonResult.data(bizAlgorithmSubmissionService.detail(bizAlgorithmSubmissionIdParam));
    }

    /**
     * 下载算法作业提交导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载算法作业提交导入模板")
    @SaCheckPermission("/biz/algorithmsuffix/downloadImportTemplate")
    @GetMapping(value = "/biz/algorithmsuffix/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizAlgorithmSubmissionService.downloadImportTemplate(response);
    }

    /**
     * 导入算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入算法作业提交")
    @CommonLog("导入算法作业提交")
    @SaCheckPermission("/biz/algorithmsuffix/importData")
    @PostMapping("/biz/algorithmsuffix/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizAlgorithmSubmissionService.importData(file));
    }

    /**
     * 导出算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出算法作业提交")
    @SaCheckPermission("/biz/algorithmsuffix/exportData")
    @PostMapping(value = "/biz/algorithmsuffix/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList, HttpServletResponse response) throws IOException {
        bizAlgorithmSubmissionService.exportData(bizAlgorithmSubmissionIdParamList, response);
    }
}
