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
package vip.xiaonuo.biz.modular.follow.controller;

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
import vip.xiaonuo.biz.modular.follow.entity.BizFollow;
import vip.xiaonuo.biz.modular.follow.param.BizFollowAddParam;
import vip.xiaonuo.biz.modular.follow.param.BizFollowEditParam;
import vip.xiaonuo.biz.modular.follow.param.BizFollowIdParam;
import vip.xiaonuo.biz.modular.follow.param.BizFollowPageParam;
import vip.xiaonuo.biz.modular.follow.service.BizFollowService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 学生关注管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "学生关注管理控制器")
@RestController
@Validated
public class BizFollowController {

    @Resource
    private BizFollowService bizFollowService;

    /**
     * 获取学生关注管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取学生关注管理分页")
    @SaCheckPermission("/biz/follow/page")
    @GetMapping("/biz/follow/page")
    public CommonResult<Page<BizFollow>> page(BizFollowPageParam bizFollowPageParam) {
        return CommonResult.data(bizFollowService.page(bizFollowPageParam));
    }

    /**
     * 添加学生关注管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加学生关注管理")
    @CommonLog("添加学生关注管理")
    @SaCheckPermission("/biz/follow/add")
    @PostMapping("/biz/follow/add")
    public CommonResult<String> add(@RequestBody @Valid BizFollowAddParam bizFollowAddParam) {
        bizFollowService.add(bizFollowAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑学生关注管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑学生关注管理")
    @CommonLog("编辑学生关注管理")
    @SaCheckPermission("/biz/follow/edit")
    @PostMapping("/biz/follow/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizFollowEditParam bizFollowEditParam) {
        bizFollowService.edit(bizFollowEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除学生关注管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除学生关注管理")
    @CommonLog("删除学生关注管理")
    @SaCheckPermission("/biz/follow/delete")
    @PostMapping("/biz/follow/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizFollowIdParam> bizFollowIdParamList) {
        bizFollowService.delete(bizFollowIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取学生关注管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取学生关注管理详情")
    @SaCheckPermission("/biz/follow/detail")
    @GetMapping("/biz/follow/detail")
    public CommonResult<BizFollow> detail(@Valid BizFollowIdParam bizFollowIdParam) {
        return CommonResult.data(bizFollowService.detail(bizFollowIdParam));
    }

    /**
     * 下载学生关注管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载学生关注管理导入模板")
    @SaCheckPermission("/biz/follow/downloadImportTemplate")
    @GetMapping(value = "/biz/follow/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizFollowService.downloadImportTemplate(response);
    }

    /**
     * 导入学生关注管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入学生关注管理")
    @CommonLog("导入学生关注管理")
    @SaCheckPermission("/biz/follow/importData")
    @PostMapping("/biz/follow/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizFollowService.importData(file));
    }

    /**
     * 导出学生关注管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出学生关注管理")
    @SaCheckPermission("/biz/follow/exportData")
    @PostMapping(value = "/biz/follow/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizFollowIdParam> bizFollowIdParamList, HttpServletResponse response) throws IOException {
        bizFollowService.exportData(bizFollowIdParamList, response);
    }
}
