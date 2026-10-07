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
package vip.xiaonuo.biz.modular.projectflow.controller;

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
import vip.xiaonuo.biz.modular.projectflow.entity.BizProjectFlow;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowAddParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowEditParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowIdParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowPageParam;
import vip.xiaonuo.biz.modular.projectflow.service.BizProjectFlowService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 组队申请流转控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "组队申请流转控制器")
@RestController
@Validated
public class BizProjectFlowController {

    @Resource
    private BizProjectFlowService bizProjectFlowService;

    /**
     * 获取组队申请流转分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取组队申请流转分页")
    @SaCheckPermission("/biz/projectflow/page")
    @GetMapping("/biz/projectflow/page")
    public CommonResult<Page<BizProjectFlow>> page(BizProjectFlowPageParam bizProjectFlowPageParam) {
        return CommonResult.data(bizProjectFlowService.page(bizProjectFlowPageParam));
    }

    /**
     * 添加组队申请流转
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加组队申请流转")
    @CommonLog("添加组队申请流转")
    @SaCheckPermission("/biz/projectflow/add")
    @PostMapping("/biz/projectflow/add")
    public CommonResult<String> add(@RequestBody @Valid BizProjectFlowAddParam bizProjectFlowAddParam) {
        bizProjectFlowService.add(bizProjectFlowAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑组队申请流转
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑组队申请流转")
    @CommonLog("编辑组队申请流转")
    @SaCheckPermission("/biz/projectflow/edit")
    @PostMapping("/biz/projectflow/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizProjectFlowEditParam bizProjectFlowEditParam) {
        bizProjectFlowService.edit(bizProjectFlowEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除组队申请流转
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除组队申请流转")
    @CommonLog("删除组队申请流转")
    @SaCheckPermission("/biz/projectflow/delete")
    @PostMapping("/biz/projectflow/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizProjectFlowIdParam> bizProjectFlowIdParamList) {
        bizProjectFlowService.delete(bizProjectFlowIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取组队申请流转详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取组队申请流转详情")
    @SaCheckPermission("/biz/projectflow/detail")
    @GetMapping("/biz/projectflow/detail")
    public CommonResult<BizProjectFlow> detail(@Valid BizProjectFlowIdParam bizProjectFlowIdParam) {
        return CommonResult.data(bizProjectFlowService.detail(bizProjectFlowIdParam));
    }

    /**
     * 下载组队申请流转导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载组队申请流转导入模板")
    @SaCheckPermission("/biz/projectflow/downloadImportTemplate")
    @GetMapping(value = "/biz/projectflow/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizProjectFlowService.downloadImportTemplate(response);
    }

    /**
     * 导入组队申请流转
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入组队申请流转")
    @CommonLog("导入组队申请流转")
    @SaCheckPermission("/biz/projectflow/importData")
    @PostMapping("/biz/projectflow/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizProjectFlowService.importData(file));
    }

    /**
     * 导出组队申请流转
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出组队申请流转")
    @SaCheckPermission("/biz/projectflow/exportData")
    @PostMapping(value = "/biz/projectflow/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizProjectFlowIdParam> bizProjectFlowIdParamList, HttpServletResponse response) throws IOException {
        bizProjectFlowService.exportData(bizProjectFlowIdParamList, response);
    }
}
