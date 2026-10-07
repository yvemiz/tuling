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
package vip.xiaonuo.biz.modular.studentprofile.controller;

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
import vip.xiaonuo.biz.modular.studentprofile.entity.BizStudentProfile;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileAddParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileEditParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileIdParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfilePageParam;
import vip.xiaonuo.biz.modular.studentprofile.service.BizStudentProfileService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 学生档案管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "学生档案管理控制器")
@RestController
@Validated
public class BizStudentProfileController {

    @Resource
    private BizStudentProfileService bizStudentProfileService;

    /**
     * 获取学生档案管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取学生档案管理分页")
    @SaCheckPermission("/biz/studentprofile/page")
    @GetMapping("/biz/studentprofile/page")
    public CommonResult<Page<BizStudentProfile>> page(BizStudentProfilePageParam bizStudentProfilePageParam) {
        return CommonResult.data(bizStudentProfileService.page(bizStudentProfilePageParam));
    }

    /**
     * 添加学生档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加学生档案管理")
    @CommonLog("添加学生档案管理")
    @SaCheckPermission("/biz/studentprofile/add")
    @PostMapping("/biz/studentprofile/add")
    public CommonResult<String> add(@RequestBody @Valid BizStudentProfileAddParam bizStudentProfileAddParam) {
        bizStudentProfileService.add(bizStudentProfileAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑学生档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑学生档案管理")
    @CommonLog("编辑学生档案管理")
    @SaCheckPermission("/biz/studentprofile/edit")
    @PostMapping("/biz/studentprofile/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizStudentProfileEditParam bizStudentProfileEditParam) {
        bizStudentProfileService.edit(bizStudentProfileEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除学生档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除学生档案管理")
    @CommonLog("删除学生档案管理")
    @SaCheckPermission("/biz/studentprofile/delete")
    @PostMapping("/biz/studentprofile/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizStudentProfileIdParam> bizStudentProfileIdParamList) {
        bizStudentProfileService.delete(bizStudentProfileIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取学生档案管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取学生档案管理详情")
    @SaCheckPermission("/biz/studentprofile/detail")
    @GetMapping("/biz/studentprofile/detail")
    public CommonResult<BizStudentProfile> detail(@Valid BizStudentProfileIdParam bizStudentProfileIdParam) {
        return CommonResult.data(bizStudentProfileService.detail(bizStudentProfileIdParam));
    }

    /**
     * 下载学生档案管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载学生档案管理导入模板")
    @SaCheckPermission("/biz/studentprofile/downloadImportTemplate")
    @GetMapping(value = "/biz/studentprofile/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizStudentProfileService.downloadImportTemplate(response);
    }

    /**
     * 导入学生档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入学生档案管理")
    @CommonLog("导入学生档案管理")
    @SaCheckPermission("/biz/studentprofile/importData")
    @PostMapping("/biz/studentprofile/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizStudentProfileService.importData(file));
    }

    /**
     * 导出学生档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出学生档案管理")
    @SaCheckPermission("/biz/studentprofile/exportData")
    @PostMapping(value = "/biz/studentprofile/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizStudentProfileIdParam> bizStudentProfileIdParamList, HttpServletResponse response) throws IOException {
        bizStudentProfileService.exportData(bizStudentProfileIdParamList, response);
    }
}
