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
package vip.xiaonuo.biz.modular.memberscore.controller;

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
import vip.xiaonuo.biz.modular.memberscore.entity.BizMemberScore;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreAddParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreEditParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreIdParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScorePageParam;
import vip.xiaonuo.biz.modular.memberscore.service.BizMemberScoreService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 组员表现打分控制器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 */
@Tag(name = "组员表现打分控制器")
@RestController
@Validated
public class BizMemberScoreController {

    @Resource
    private BizMemberScoreService bizMemberScoreService;

    /**
     * 获取组员表现打分分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取组员表现打分分页")
    @SaCheckPermission("/biz/memberscore/page")
    @GetMapping("/biz/memberscore/page")
    public CommonResult<Page<BizMemberScore>> page(BizMemberScorePageParam bizMemberScorePageParam) {
        return CommonResult.data(bizMemberScoreService.page(bizMemberScorePageParam));
    }

    /**
     * 添加组员表现打分
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "添加组员表现打分")
    @CommonLog("添加组员表现打分")
    @SaCheckPermission("/biz/memberscore/add")
    @PostMapping("/biz/memberscore/add")
    public CommonResult<String> add(@RequestBody @Valid BizMemberScoreAddParam bizMemberScoreAddParam) {
        bizMemberScoreService.add(bizMemberScoreAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑组员表现打分
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "编辑组员表现打分")
    @CommonLog("编辑组员表现打分")
    @SaCheckPermission("/biz/memberscore/edit")
    @PostMapping("/biz/memberscore/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizMemberScoreEditParam bizMemberScoreEditParam) {
        bizMemberScoreService.edit(bizMemberScoreEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除组员表现打分
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "删除组员表现打分")
    @CommonLog("删除组员表现打分")
    @SaCheckPermission("/biz/memberscore/delete")
    @PostMapping("/biz/memberscore/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizMemberScoreIdParam> bizMemberScoreIdParamList) {
        bizMemberScoreService.delete(bizMemberScoreIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取组员表现打分详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "获取组员表现打分详情")
    @SaCheckPermission("/biz/memberscore/detail")
    @GetMapping("/biz/memberscore/detail")
    public CommonResult<BizMemberScore> detail(@Valid BizMemberScoreIdParam bizMemberScoreIdParam) {
        return CommonResult.data(bizMemberScoreService.detail(bizMemberScoreIdParam));
    }

    /**
     * 下载组员表现打分导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "下载组员表现打分导入模板")
    @SaCheckPermission("/biz/memberscore/downloadImportTemplate")
    @GetMapping(value = "/biz/memberscore/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        bizMemberScoreService.downloadImportTemplate(response);
    }

    /**
     * 导入组员表现打分
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导入组员表现打分")
    @CommonLog("导入组员表现打分")
    @SaCheckPermission("/biz/memberscore/importData")
    @PostMapping("/biz/memberscore/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(bizMemberScoreService.importData(file));
    }

    /**
     * 导出组员表现打分
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    @Operation(summary = "导出组员表现打分")
    @SaCheckPermission("/biz/memberscore/exportData")
    @PostMapping(value = "/biz/memberscore/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<BizMemberScoreIdParam> bizMemberScoreIdParamList, HttpServletResponse response) throws IOException {
        bizMemberScoreService.exportData(bizMemberScoreIdParamList, response);
    }
}
