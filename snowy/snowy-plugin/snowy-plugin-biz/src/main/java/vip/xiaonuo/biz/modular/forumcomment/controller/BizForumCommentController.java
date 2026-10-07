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
package vip.xiaonuo.biz.modular.forumcomment.controller;

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
import vip.xiaonuo.biz.modular.forumcomment.entity.BizForumComment;
import vip.xiaonuo.biz.modular.forumcomment.param.BizForumCommentAddParam;
import vip.xiaonuo.biz.modular.forumcomment.param.BizForumCommentEditParam;
import vip.xiaonuo.biz.modular.forumcomment.param.BizForumCommentIdParam;
import vip.xiaonuo.biz.modular.forumcomment.param.BizForumCommentPageParam;
import vip.xiaonuo.biz.modular.forumcomment.service.BizForumCommentService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 论坛评论管理控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "论坛评论管理控制器")
@RestController
@Validated
public class BizForumCommentController {

    @Resource
    private BizForumCommentService bizForumCommentService;

    /**
     * 获取论坛评论管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛评论管理分页")
    @SaCheckPermission("/biz/forumcomment/page")
    @GetMapping("/biz/forumcomment/page")
    public CommonResult<Page<BizForumComment>> page(BizForumCommentPageParam bizForumCommentPageParam) {
        return CommonResult.data(bizForumCommentService.page(bizForumCommentPageParam));
    }

    /**
     * 添加论坛评论管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加论坛评论管理")
    @CommonLog("添加论坛评论管理")
    @SaCheckPermission("/biz/forumcomment/add")
    @PostMapping("/biz/forumcomment/add")
    public CommonResult<String> add(@RequestBody @Valid BizForumCommentAddParam bizForumCommentAddParam) {
        bizForumCommentService.add(bizForumCommentAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑论坛评论管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑论坛评论管理")
    @CommonLog("编辑论坛评论管理")
    @SaCheckPermission("/biz/forumcomment/edit")
    @PostMapping("/biz/forumcomment/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizForumCommentEditParam bizForumCommentEditParam) {
        bizForumCommentService.edit(bizForumCommentEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除论坛评论管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除论坛评论管理")
    @CommonLog("删除论坛评论管理")
    @SaCheckPermission("/biz/forumcomment/delete")
    @PostMapping("/biz/forumcomment/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizForumCommentIdParam> bizForumCommentIdParamList) {
        bizForumCommentService.delete(bizForumCommentIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取论坛评论管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取论坛评论管理详情")
    @SaCheckPermission("/biz/forumcomment/detail")
    @GetMapping("/biz/forumcomment/detail")
    public CommonResult<BizForumComment> detail(@Valid BizForumCommentIdParam bizForumCommentIdParam) {
        return CommonResult.data(bizForumCommentService.detail(bizForumCommentIdParam));
    }

    /**
     * 下载论坛评论管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载论坛评论管理导入模板")
    @SaCheckPermission("/biz/forumcomment/downloadImportTemplate")
    @GetMapping(value = "/biz/forumcomment/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizForumCommentService.downloadImportTemplate(response);
    }

    /**
     * 导入论坛评论管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入论坛评论管理")
    @CommonLog("导入论坛评论管理")
    @SaCheckPermission("/biz/forumcomment/importData")
    @PostMapping("/biz/forumcomment/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizForumCommentService.importData(file));
    }

    /**
     * 导出论坛评论管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出论坛评论管理")
    @SaCheckPermission("/biz/forumcomment/exportData")
    @PostMapping(value = "/biz/forumcomment/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizForumCommentIdParam> bizForumCommentIdParamList, HttpServletResponse response) throws IOException {
        bizForumCommentService.exportData(bizForumCommentIdParamList, response);
    }
}
