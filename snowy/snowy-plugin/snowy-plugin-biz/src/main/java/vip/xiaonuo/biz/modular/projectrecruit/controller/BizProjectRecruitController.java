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
package vip.xiaonuo.biz.modular.projectrecruit.controller;

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
import vip.xiaonuo.biz.modular.projectrecruit.entity.BizProjectRecruit;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitAddParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitEditParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitIdParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitPageParam;
import vip.xiaonuo.biz.modular.projectrecruit.service.BizProjectRecruitService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 项目招募需求控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "项目招募需求控制器")
@RestController
@Validated
public class BizProjectRecruitController {

    @Resource
    private BizProjectRecruitService bizProjectRecruitService;

    /**
     * 获取项目招募需求分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目招募需求分页")
    @SaCheckPermission("/biz/projectrecruit/page")
    @GetMapping("/biz/projectrecruit/page")
    public CommonResult<Page<BizProjectRecruit>> page(BizProjectRecruitPageParam bizProjectRecruitPageParam) {
        return CommonResult.data(bizProjectRecruitService.page(bizProjectRecruitPageParam));
    }

    /**
     * 添加项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加项目招募需求")
    @CommonLog("添加项目招募需求")
    @SaCheckPermission("/biz/projectrecruit/add")
    @PostMapping("/biz/projectrecruit/add")
    public CommonResult<String> add(@RequestBody @Valid BizProjectRecruitAddParam bizProjectRecruitAddParam) {
        bizProjectRecruitService.add(bizProjectRecruitAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑项目招募需求")
    @CommonLog("编辑项目招募需求")
    @SaCheckPermission("/biz/projectrecruit/edit")
    @PostMapping("/biz/projectrecruit/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizProjectRecruitEditParam bizProjectRecruitEditParam) {
        bizProjectRecruitService.edit(bizProjectRecruitEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除项目招募需求")
    @CommonLog("删除项目招募需求")
    @SaCheckPermission("/biz/projectrecruit/delete")
    @PostMapping("/biz/projectrecruit/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList) {
        bizProjectRecruitService.delete(bizProjectRecruitIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目招募需求详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目招募需求详情")
    @SaCheckPermission("/biz/projectrecruit/detail")
    @GetMapping("/biz/projectrecruit/detail")
    public CommonResult<BizProjectRecruit> detail(@Valid BizProjectRecruitIdParam bizProjectRecruitIdParam) {
        return CommonResult.data(bizProjectRecruitService.detail(bizProjectRecruitIdParam));
    }

    /**
     * 下载项目招募需求导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载项目招募需求导入模板")
    @SaCheckPermission("/biz/projectrecruit/downloadImportTemplate")
    @GetMapping(value = "/biz/projectrecruit/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizProjectRecruitService.downloadImportTemplate(response);
    }

    /**
     * 导入项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入项目招募需求")
    @CommonLog("导入项目招募需求")
    @SaCheckPermission("/biz/projectrecruit/importData")
    @PostMapping("/biz/projectrecruit/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizProjectRecruitService.importData(file));
    }

    /**
     * 导出项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出项目招募需求")
    @SaCheckPermission("/biz/projectrecruit/exportData")
    @PostMapping(value = "/biz/projectrecruit/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList, HttpServletResponse response) throws IOException {
        bizProjectRecruitService.exportData(bizProjectRecruitIdParamList, response);
    }
}
