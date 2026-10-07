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
package vip.xiaonuo.biz.modular.forumpost.controller;

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
import vip.xiaonuo.biz.modular.forumpost.entity.BizForumPost;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostAddParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostEditParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostIdParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostPageParam;
import vip.xiaonuo.biz.modular.forumpost.service.BizForumPostService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 论坛帖子管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "论坛帖子管理控制器")
@RestController
@Validated
public class BizForumPostController {

    @Resource
    private BizForumPostService bizForumPostService;

    /**
     * 获取论坛帖子管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛帖子管理分页")
    @SaCheckPermission("/biz/forumpost/page")
    @GetMapping("/biz/forumpost/page")
    public CommonResult<Page<BizForumPost>> page(BizForumPostPageParam bizForumPostPageParam) {
        return CommonResult.data(bizForumPostService.page(bizForumPostPageParam));
    }

    /**
     * 添加论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加论坛帖子管理")
    @CommonLog("添加论坛帖子管理")
    @SaCheckPermission("/biz/forumpost/add")
    @PostMapping("/biz/forumpost/add")
    public CommonResult<String> add(@RequestBody @Valid BizForumPostAddParam bizForumPostAddParam) {
        bizForumPostService.add(bizForumPostAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑论坛帖子管理")
    @CommonLog("编辑论坛帖子管理")
    @SaCheckPermission("/biz/forumpost/edit")
    @PostMapping("/biz/forumpost/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizForumPostEditParam bizForumPostEditParam) {
        bizForumPostService.edit(bizForumPostEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除论坛帖子管理")
    @CommonLog("删除论坛帖子管理")
    @SaCheckPermission("/biz/forumpost/delete")
    @PostMapping("/biz/forumpost/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizForumPostIdParam> bizForumPostIdParamList) {
        bizForumPostService.delete(bizForumPostIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取论坛帖子管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛帖子管理详情")
    @SaCheckPermission("/biz/forumpost/detail")
    @GetMapping("/biz/forumpost/detail")
    public CommonResult<BizForumPost> detail(@Valid BizForumPostIdParam bizForumPostIdParam) {
        return CommonResult.data(bizForumPostService.detail(bizForumPostIdParam));
    }

    /**
     * 下载论坛帖子管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载论坛帖子管理导入模板")
    @SaCheckPermission("/biz/forumpost/downloadImportTemplate")
    @GetMapping(value = "/biz/forumpost/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizForumPostService.downloadImportTemplate(response);
    }

    /**
     * 导入论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入论坛帖子管理")
    @CommonLog("导入论坛帖子管理")
    @SaCheckPermission("/biz/forumpost/importData")
    @PostMapping("/biz/forumpost/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizForumPostService.importData(file));
    }

    /**
     * 导出论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出论坛帖子管理")
    @SaCheckPermission("/biz/forumpost/exportData")
    @PostMapping(value = "/biz/forumpost/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizForumPostIdParam> bizForumPostIdParamList, HttpServletResponse response) throws IOException {
        bizForumPostService.exportData(bizForumPostIdParamList, response);
    }
}
