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
package vip.xiaonuo.biz.modular.algorithmtask.controller;

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
import vip.xiaonuo.biz.modular.algorithmtask.entity.BizAlgorithmTask;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskAddParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskEditParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskIdParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskPageParam;
import vip.xiaonuo.biz.modular.algorithmtask.service.BizAlgorithmTaskService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 算法题目任务控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "算法题目任务控制器")
@RestController
@Validated
public class BizAlgorithmTaskController {

    @Resource
    private BizAlgorithmTaskService bizAlgorithmTaskService;

    /**
     * 获取算法题目任务分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法题目任务分页")
    @SaCheckPermission("/biz/algorithmtask/page")
    @GetMapping("/biz/algorithmtask/page")
    public CommonResult<Page<BizAlgorithmTask>> page(BizAlgorithmTaskPageParam bizAlgorithmTaskPageParam) {
        return CommonResult.data(bizAlgorithmTaskService.page(bizAlgorithmTaskPageParam));
    }

    /**
     * 添加算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加算法题目任务")
    @CommonLog("添加算法题目任务")
    @SaCheckPermission("/biz/algorithmtask/add")
    @PostMapping("/biz/algorithmtask/add")
    public CommonResult<String> add(@RequestBody @Valid BizAlgorithmTaskAddParam bizAlgorithmTaskAddParam) {
        bizAlgorithmTaskService.add(bizAlgorithmTaskAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑算法题目任务")
    @CommonLog("编辑算法题目任务")
    @SaCheckPermission("/biz/algorithmtask/edit")
    @PostMapping("/biz/algorithmtask/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizAlgorithmTaskEditParam bizAlgorithmTaskEditParam) {
        bizAlgorithmTaskService.edit(bizAlgorithmTaskEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除算法题目任务")
    @CommonLog("删除算法题目任务")
    @SaCheckPermission("/biz/algorithmtask/delete")
    @PostMapping("/biz/algorithmtask/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizAlgorithmTaskIdParam> bizAlgorithmTaskIdParamList) {
        bizAlgorithmTaskService.delete(bizAlgorithmTaskIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取算法题目任务详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法题目任务详情")
    @SaCheckPermission("/biz/algorithmtask/detail")
    @GetMapping("/biz/algorithmtask/detail")
    public CommonResult<BizAlgorithmTask> detail(@Valid BizAlgorithmTaskIdParam bizAlgorithmTaskIdParam) {
        return CommonResult.data(bizAlgorithmTaskService.detail(bizAlgorithmTaskIdParam));
    }

    /**
     * 下载算法题目任务导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载算法题目任务导入模板")
    @SaCheckPermission("/biz/algorithmtask/downloadImportTemplate")
    @GetMapping(value = "/biz/algorithmtask/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizAlgorithmTaskService.downloadImportTemplate(response);
    }

    /**
     * 导入算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入算法题目任务")
    @CommonLog("导入算法题目任务")
    @SaCheckPermission("/biz/algorithmtask/importData")
    @PostMapping("/biz/algorithmtask/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizAlgorithmTaskService.importData(file));
    }

    /**
     * 导出算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出算法题目任务")
    @SaCheckPermission("/biz/algorithmtask/exportData")
    @PostMapping(value = "/biz/algorithmtask/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizAlgorithmTaskIdParam> bizAlgorithmTaskIdParamList, HttpServletResponse response) throws IOException {
        bizAlgorithmTaskService.exportData(bizAlgorithmTaskIdParamList, response);
    }
}
