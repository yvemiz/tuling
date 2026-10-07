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
package vip.xiaonuo.biz.modular.competitionproject.controller;

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
import vip.xiaonuo.biz.modular.competitionproject.entity.BizCompetitionProject;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectAddParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectEditParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectIdParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectPageParam;
import vip.xiaonuo.biz.modular.competitionproject.service.BizCompetitionProjectService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 竞赛报名关联控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "竞赛报名关联控制器")
@RestController
@Validated
public class BizCompetitionProjectController {

    @Resource
    private BizCompetitionProjectService bizCompetitionProjectService;

    /**
     * 获取竞赛报名关联分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取竞赛报名关联分页")
    @SaCheckPermission("/biz/competitionproject/page")
    @GetMapping("/biz/competitionproject/page")
    public CommonResult<Page<BizCompetitionProject>> page(BizCompetitionProjectPageParam bizCompetitionProjectPageParam) {
        return CommonResult.data(bizCompetitionProjectService.page(bizCompetitionProjectPageParam));
    }

    /**
     * 添加竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加竞赛报名关联")
    @CommonLog("添加竞赛报名关联")
    @SaCheckPermission("/biz/competitionproject/add")
    @PostMapping("/biz/competitionproject/add")
    public CommonResult<String> add(@RequestBody @Valid BizCompetitionProjectAddParam bizCompetitionProjectAddParam) {
        bizCompetitionProjectService.add(bizCompetitionProjectAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑竞赛报名关联")
    @CommonLog("编辑竞赛报名关联")
    @SaCheckPermission("/biz/competitionproject/edit")
    @PostMapping("/biz/competitionproject/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizCompetitionProjectEditParam bizCompetitionProjectEditParam) {
        bizCompetitionProjectService.edit(bizCompetitionProjectEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除竞赛报名关联")
    @CommonLog("删除竞赛报名关联")
    @SaCheckPermission("/biz/competitionproject/delete")
    @PostMapping("/biz/competitionproject/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizCompetitionProjectIdParam> bizCompetitionProjectIdParamList) {
        bizCompetitionProjectService.delete(bizCompetitionProjectIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取竞赛报名关联详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取竞赛报名关联详情")
    @SaCheckPermission("/biz/competitionproject/detail")
    @GetMapping("/biz/competitionproject/detail")
    public CommonResult<BizCompetitionProject> detail(@Valid BizCompetitionProjectIdParam bizCompetitionProjectIdParam) {
        return CommonResult.data(bizCompetitionProjectService.detail(bizCompetitionProjectIdParam));
    }

    /**
     * 下载竞赛报名关联导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载竞赛报名关联导入模板")
    @SaCheckPermission("/biz/competitionproject/downloadImportTemplate")
    @GetMapping(value = "/biz/competitionproject/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizCompetitionProjectService.downloadImportTemplate(response);
    }

    /**
     * 导入竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入竞赛报名关联")
    @CommonLog("导入竞赛报名关联")
    @SaCheckPermission("/biz/competitionproject/importData")
    @PostMapping("/biz/competitionproject/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizCompetitionProjectService.importData(file));
    }

    /**
     * 导出竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出竞赛报名关联")
    @SaCheckPermission("/biz/competitionproject/exportData")
    @PostMapping(value = "/biz/competitionproject/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizCompetitionProjectIdParam> bizCompetitionProjectIdParamList, HttpServletResponse response) throws IOException {
        bizCompetitionProjectService.exportData(bizCompetitionProjectIdParamList, response);
    }
}
