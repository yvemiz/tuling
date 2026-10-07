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
package vip.xiaonuo.biz.modular.algorithmurge.controller;

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
import vip.xiaonuo.biz.modular.algorithmurge.entity.BizAlgorithmUrge;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeAddParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeEditParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeIdParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgePageParam;
import vip.xiaonuo.biz.modular.algorithmurge.service.BizAlgorithmUrgeService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 算法催交记录控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "算法催交记录控制器")
@RestController
@Validated
public class BizAlgorithmUrgeController {

    @Resource
    private BizAlgorithmUrgeService bizAlgorithmUrgeService;

    /**
     * 获取算法催交记录分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法催交记录分页")
    @SaCheckPermission("/biz/algorithmurge/page")
    @GetMapping("/biz/algorithmurge/page")
    public CommonResult<Page<BizAlgorithmUrge>> page(BizAlgorithmUrgePageParam bizAlgorithmUrgePageParam) {
        return CommonResult.data(bizAlgorithmUrgeService.page(bizAlgorithmUrgePageParam));
    }

    /**
     * 添加算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加算法催交记录")
    @CommonLog("添加算法催交记录")
    @SaCheckPermission("/biz/algorithmurge/add")
    @PostMapping("/biz/algorithmurge/add")
    public CommonResult<String> add(@RequestBody @Valid BizAlgorithmUrgeAddParam bizAlgorithmUrgeAddParam) {
        bizAlgorithmUrgeService.add(bizAlgorithmUrgeAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑算法催交记录")
    @CommonLog("编辑算法催交记录")
    @SaCheckPermission("/biz/algorithmurge/edit")
    @PostMapping("/biz/algorithmurge/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizAlgorithmUrgeEditParam bizAlgorithmUrgeEditParam) {
        bizAlgorithmUrgeService.edit(bizAlgorithmUrgeEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除算法催交记录")
    @CommonLog("删除算法催交记录")
    @SaCheckPermission("/biz/algorithmurge/delete")
    @PostMapping("/biz/algorithmurge/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList) {
        bizAlgorithmUrgeService.delete(bizAlgorithmUrgeIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取算法催交记录详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取算法催交记录详情")
    @SaCheckPermission("/biz/algorithmurge/detail")
    @GetMapping("/biz/algorithmurge/detail")
    public CommonResult<BizAlgorithmUrge> detail(@Valid BizAlgorithmUrgeIdParam bizAlgorithmUrgeIdParam) {
        return CommonResult.data(bizAlgorithmUrgeService.detail(bizAlgorithmUrgeIdParam));
    }

    /**
     * 下载算法催交记录导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载算法催交记录导入模板")
    @SaCheckPermission("/biz/algorithmurge/downloadImportTemplate")
    @GetMapping(value = "/biz/algorithmurge/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizAlgorithmUrgeService.downloadImportTemplate(response);
    }

    /**
     * 导入算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入算法催交记录")
    @CommonLog("导入算法催交记录")
    @SaCheckPermission("/biz/algorithmurge/importData")
    @PostMapping("/biz/algorithmurge/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizAlgorithmUrgeService.importData(file));
    }

    /**
     * 导出算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出算法催交记录")
    @SaCheckPermission("/biz/algorithmurge/exportData")
    @PostMapping(value = "/biz/algorithmurge/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList, HttpServletResponse response) throws IOException {
        bizAlgorithmUrgeService.exportData(bizAlgorithmUrgeIdParamList, response);
    }
}
