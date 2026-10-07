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
package vip.xiaonuo.biz.modular.projectmember.controller;

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
import vip.xiaonuo.biz.modular.projectmember.entity.BizProjectMember;
import vip.xiaonuo.biz.modular.projectmember.param.BizProjectMemberAddParam;
import vip.xiaonuo.biz.modular.projectmember.param.BizProjectMemberEditParam;
import vip.xiaonuo.biz.modular.projectmember.param.BizProjectMemberIdParam;
import vip.xiaonuo.biz.modular.projectmember.param.BizProjectMemberPageParam;
import vip.xiaonuo.biz.modular.projectmember.service.BizProjectMemberService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 项目成员管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "项目成员管理控制器")
@RestController
@Validated
public class BizProjectMemberController {

    @Resource
    private BizProjectMemberService bizProjectMemberService;

    /**
     * 获取项目成员管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目成员管理分页")
    @SaCheckPermission("/biz/projectmember/page")
    @GetMapping("/biz/projectmember/page")
    public CommonResult<Page<BizProjectMember>> page(BizProjectMemberPageParam bizProjectMemberPageParam) {
        return CommonResult.data(bizProjectMemberService.page(bizProjectMemberPageParam));
    }

    /**
     * 添加项目成员管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加项目成员管理")
    @CommonLog("添加项目成员管理")
    @SaCheckPermission("/biz/projectmember/add")
    @PostMapping("/biz/projectmember/add")
    public CommonResult<String> add(@RequestBody @Valid BizProjectMemberAddParam bizProjectMemberAddParam) {
        bizProjectMemberService.add(bizProjectMemberAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目成员管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑项目成员管理")
    @CommonLog("编辑项目成员管理")
    @SaCheckPermission("/biz/projectmember/edit")
    @PostMapping("/biz/projectmember/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizProjectMemberEditParam bizProjectMemberEditParam) {
        bizProjectMemberService.edit(bizProjectMemberEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目成员管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除项目成员管理")
    @CommonLog("删除项目成员管理")
    @SaCheckPermission("/biz/projectmember/delete")
    @PostMapping("/biz/projectmember/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizProjectMemberIdParam> bizProjectMemberIdParamList) {
        bizProjectMemberService.delete(bizProjectMemberIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目成员管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取项目成员管理详情")
    @SaCheckPermission("/biz/projectmember/detail")
    @GetMapping("/biz/projectmember/detail")
    public CommonResult<BizProjectMember> detail(@Valid BizProjectMemberIdParam bizProjectMemberIdParam) {
        return CommonResult.data(bizProjectMemberService.detail(bizProjectMemberIdParam));
    }

    /**
     * 下载项目成员管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载项目成员管理导入模板")
    @SaCheckPermission("/biz/projectmember/downloadImportTemplate")
    @GetMapping(value = "/biz/projectmember/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizProjectMemberService.downloadImportTemplate(response);
    }

    /**
     * 导入项目成员管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入项目成员管理")
    @CommonLog("导入项目成员管理")
    @SaCheckPermission("/biz/projectmember/importData")
    @PostMapping("/biz/projectmember/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizProjectMemberService.importData(file));
    }

    /**
     * 导出项目成员管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出项目成员管理")
    @SaCheckPermission("/biz/projectmember/exportData")
    @PostMapping(value = "/biz/projectmember/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizProjectMemberIdParam> bizProjectMemberIdParamList, HttpServletResponse response) throws IOException {
        bizProjectMemberService.exportData(bizProjectMemberIdParamList, response);
    }
}
