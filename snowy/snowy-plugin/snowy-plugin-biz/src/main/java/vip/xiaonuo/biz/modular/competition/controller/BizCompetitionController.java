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
package vip.xiaonuo.biz.modular.competition.controller;

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
import vip.xiaonuo.biz.modular.competition.entity.BizCompetition;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionAddParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionEditParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionIdParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionPageParam;
import vip.xiaonuo.biz.modular.competition.service.BizCompetitionService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 竞赛信息管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "竞赛信息管理控制器")
@RestController
@Validated
public class BizCompetitionController {

    @Resource
    private BizCompetitionService bizCompetitionService;

    /**
     * 获取竞赛信息管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取竞赛信息管理分页")
    @SaCheckPermission("/biz/competition/page")
    @GetMapping("/biz/competition/page")
    public CommonResult<Page<BizCompetition>> page(BizCompetitionPageParam bizCompetitionPageParam) {
        return CommonResult.data(bizCompetitionService.page(bizCompetitionPageParam));
    }

    /**
     * 添加竞赛信息管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加竞赛信息管理")
    @CommonLog("添加竞赛信息管理")
    @SaCheckPermission("/biz/competition/add")
    @PostMapping("/biz/competition/add")
    public CommonResult<String> add(@RequestBody @Valid BizCompetitionAddParam bizCompetitionAddParam) {
        bizCompetitionService.add(bizCompetitionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑竞赛信息管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑竞赛信息管理")
    @CommonLog("编辑竞赛信息管理")
    @SaCheckPermission("/biz/competition/edit")
    @PostMapping("/biz/competition/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizCompetitionEditParam bizCompetitionEditParam) {
        bizCompetitionService.edit(bizCompetitionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除竞赛信息管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除竞赛信息管理")
    @CommonLog("删除竞赛信息管理")
    @SaCheckPermission("/biz/competition/delete")
    @PostMapping("/biz/competition/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizCompetitionIdParam> bizCompetitionIdParamList) {
        bizCompetitionService.delete(bizCompetitionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取竞赛信息管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取竞赛信息管理详情")
    @SaCheckPermission("/biz/competition/detail")
    @GetMapping("/biz/competition/detail")
    public CommonResult<BizCompetition> detail(@Valid BizCompetitionIdParam bizCompetitionIdParam) {
        return CommonResult.data(bizCompetitionService.detail(bizCompetitionIdParam));
    }

    /**
     * 下载竞赛信息管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载竞赛信息管理导入模板")
    @SaCheckPermission("/biz/competition/downloadImportTemplate")
    @GetMapping(value = "/biz/competition/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizCompetitionService.downloadImportTemplate(response);
    }

    /**
     * 导入竞赛信息管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入竞赛信息管理")
    @CommonLog("导入竞赛信息管理")
    @SaCheckPermission("/biz/competition/importData")
    @PostMapping("/biz/competition/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizCompetitionService.importData(file));
    }

    /**
     * 导出竞赛信息管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出竞赛信息管理")
    @SaCheckPermission("/biz/competition/exportData")
    @PostMapping(value = "/biz/competition/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizCompetitionIdParam> bizCompetitionIdParamList, HttpServletResponse response) throws IOException {
        bizCompetitionService.exportData(bizCompetitionIdParamList, response);
    }
}
