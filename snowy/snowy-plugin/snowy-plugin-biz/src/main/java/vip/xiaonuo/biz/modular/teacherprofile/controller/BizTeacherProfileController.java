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
package vip.xiaonuo.biz.modular.teacherprofile.controller;

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
import vip.xiaonuo.biz.modular.teacherprofile.entity.BizTeacherProfile;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileAddParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileEditParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileIdParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfilePageParam;
import vip.xiaonuo.biz.modular.teacherprofile.service.BizTeacherProfileService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 教师档案管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "教师档案管理控制器")
@RestController
@Validated
public class BizTeacherProfileController {

    @Resource
    private BizTeacherProfileService bizTeacherProfileService;

    /**
     * 获取教师档案管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取教师档案管理分页")
    @SaCheckPermission("/biz/teacherprofile/page")
    @GetMapping("/biz/teacherprofile/page")
    public CommonResult<Page<BizTeacherProfile>> page(BizTeacherProfilePageParam bizTeacherProfilePageParam) {
        return CommonResult.data(bizTeacherProfileService.page(bizTeacherProfilePageParam));
    }

    /**
     * 添加教师档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加教师档案管理")
    @CommonLog("添加教师档案管理")
    @SaCheckPermission("/biz/teacherprofile/add")
    @PostMapping("/biz/teacherprofile/add")
    public CommonResult<String> add(@RequestBody @Valid BizTeacherProfileAddParam bizTeacherProfileAddParam) {
        bizTeacherProfileService.add(bizTeacherProfileAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑教师档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑教师档案管理")
    @CommonLog("编辑教师档案管理")
    @SaCheckPermission("/biz/teacherprofile/edit")
    @PostMapping("/biz/teacherprofile/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizTeacherProfileEditParam bizTeacherProfileEditParam) {
        bizTeacherProfileService.edit(bizTeacherProfileEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除教师档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除教师档案管理")
    @CommonLog("删除教师档案管理")
    @SaCheckPermission("/biz/teacherprofile/delete")
    @PostMapping("/biz/teacherprofile/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizTeacherProfileIdParam> bizTeacherProfileIdParamList) {
        bizTeacherProfileService.delete(bizTeacherProfileIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取教师档案管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取教师档案管理详情")
    @SaCheckPermission("/biz/teacherprofile/detail")
    @GetMapping("/biz/teacherprofile/detail")
    public CommonResult<BizTeacherProfile> detail(@Valid BizTeacherProfileIdParam bizTeacherProfileIdParam) {
        return CommonResult.data(bizTeacherProfileService.detail(bizTeacherProfileIdParam));
    }

    /**
     * 下载教师档案管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载教师档案管理导入模板")
    @SaCheckPermission("/biz/teacherprofile/downloadImportTemplate")
    @GetMapping(value = "/biz/teacherprofile/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizTeacherProfileService.downloadImportTemplate(response);
    }

    /**
     * 导入教师档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入教师档案管理")
    @CommonLog("导入教师档案管理")
    @SaCheckPermission("/biz/teacherprofile/importData")
    @PostMapping("/biz/teacherprofile/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizTeacherProfileService.importData(file));
    }

    /**
     * 导出教师档案管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出教师档案管理")
    @SaCheckPermission("/biz/teacherprofile/exportData")
    @PostMapping(value = "/biz/teacherprofile/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizTeacherProfileIdParam> bizTeacherProfileIdParamList, HttpServletResponse response) throws IOException {
        bizTeacherProfileService.exportData(bizTeacherProfileIdParamList, response);
    }
}
