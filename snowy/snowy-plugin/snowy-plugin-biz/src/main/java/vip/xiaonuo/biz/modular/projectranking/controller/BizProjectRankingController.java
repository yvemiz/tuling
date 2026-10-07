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
package vip.xiaonuo.biz.modular.projectranking.controller;

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
import vip.xiaonuo.biz.modular.projectranking.entity.BizProjectRanking;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingAddParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingEditParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingIdParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingPageParam;
import vip.xiaonuo.biz.modular.projectranking.service.BizProjectRankingService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 项目综合排行榜控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "项目综合排行榜控制器")
@RestController
@Validated
public class BizProjectRankingController {

    @Resource
    private BizProjectRankingService bizProjectRankingService;

    /**
     * 获取项目综合排行榜分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目综合排行榜分页")
    @SaCheckPermission("/biz/projectranking/page")
    @GetMapping("/biz/projectranking/page")
    public CommonResult<Page<BizProjectRanking>> page(BizProjectRankingPageParam bizProjectRankingPageParam) {
        return CommonResult.data(bizProjectRankingService.page(bizProjectRankingPageParam));
    }

    /**
     * 添加项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加项目综合排行榜")
    @CommonLog("添加项目综合排行榜")
    @SaCheckPermission("/biz/projectranking/add")
    @PostMapping("/biz/projectranking/add")
    public CommonResult<String> add(@RequestBody @Valid BizProjectRankingAddParam bizProjectRankingAddParam) {
        bizProjectRankingService.add(bizProjectRankingAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑项目综合排行榜")
    @CommonLog("编辑项目综合排行榜")
    @SaCheckPermission("/biz/projectranking/edit")
    @PostMapping("/biz/projectranking/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizProjectRankingEditParam bizProjectRankingEditParam) {
        bizProjectRankingService.edit(bizProjectRankingEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除项目综合排行榜")
    @CommonLog("删除项目综合排行榜")
    @SaCheckPermission("/biz/projectranking/delete")
    @PostMapping("/biz/projectranking/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizProjectRankingIdParam> bizProjectRankingIdParamList) {
        bizProjectRankingService.delete(bizProjectRankingIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目综合排行榜详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目综合排行榜详情")
    @SaCheckPermission("/biz/projectranking/detail")
    @GetMapping("/biz/projectranking/detail")
    public CommonResult<BizProjectRanking> detail(@Valid BizProjectRankingIdParam bizProjectRankingIdParam) {
        return CommonResult.data(bizProjectRankingService.detail(bizProjectRankingIdParam));
    }

    /**
     * 下载项目综合排行榜导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载项目综合排行榜导入模板")
    @SaCheckPermission("/biz/projectranking/downloadImportTemplate")
    @GetMapping(value = "/biz/projectranking/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizProjectRankingService.downloadImportTemplate(response);
    }

    /**
     * 导入项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入项目综合排行榜")
    @CommonLog("导入项目综合排行榜")
    @SaCheckPermission("/biz/projectranking/importData")
    @PostMapping("/biz/projectranking/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizProjectRankingService.importData(file));
    }

    /**
     * 导出项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出项目综合排行榜")
    @SaCheckPermission("/biz/projectranking/exportData")
    @PostMapping(value = "/biz/projectranking/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizProjectRankingIdParam> bizProjectRankingIdParamList, HttpServletResponse response) throws IOException {
        bizProjectRankingService.exportData(bizProjectRankingIdParamList, response);
    }
}
