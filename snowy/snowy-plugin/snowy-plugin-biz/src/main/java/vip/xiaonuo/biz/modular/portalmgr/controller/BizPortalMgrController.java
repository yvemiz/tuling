package vip.xiaonuo.biz.modular.portalmgr.controller;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.portalmgr.entity.BizForumBoard;
import vip.xiaonuo.biz.modular.portalmgr.service.BizForumBoardService;
import vip.xiaonuo.biz.modular.portalmgr.service.BizPortalConfigService;
import vip.xiaonuo.biz.modular.project.entity.BizProject;
import vip.xiaonuo.biz.modular.project.service.BizProjectService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台门户管控控制器
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Tag(name = "前台门户管控控制器")
@RestController
@Validated
public class BizPortalMgrController {

    @Resource
    private BizPortalConfigService bizPortalConfigService;

    @Resource
    private BizForumBoardService bizForumBoardService;

    @Resource
    private BizProjectService bizProjectService;

    @Resource
    private ClientUserService clientUserService;

    /* ---------------- 1. 门户配置管理 ---------------- */

    @Operation(summary = "获取全部前台配置")
    @GetMapping("/portal/config/getAll")
    public CommonResult<JSONObject> getAllConfigs() {
        return CommonResult.data(bizPortalConfigService.getAllConfigs());
    }

    @Operation(summary = "保存前台配置")
    @PostMapping("/portal/config/save")
    public CommonResult<String> saveConfig(@RequestBody JSONObject param) {
        String configKey = param.getStr("configKey");
        String configName = param.getStr("configName");
        Object valObj = param.get("configValue");
        if (StrUtil.isBlank(configKey)) {
            throw new CommonException("配置键不能为空");
        }
        String configValueStr = (valObj instanceof String) ? (String) valObj : JSONUtil.toJsonStr(valObj);
        bizPortalConfigService.saveConfig(configKey, configName, configValueStr);
        return CommonResult.ok();
    }

    /* ---------------- 2. 社区子论坛板块管理 ---------------- */

    @Operation(summary = "分页查询社区板块")
    @GetMapping("/portal/board/page")
    public CommonResult<Page<BizForumBoard>> boardPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey) {
        return CommonResult.data(bizForumBoardService.page(current, size, searchKey));
    }

    @Operation(summary = "获取所有可用板块列表(供前台或下拉使用)")
    @GetMapping("/portal/board/list")
    public CommonResult<List<BizForumBoard>> boardList() {
        return CommonResult.data(bizForumBoardService.listEnabledBoards());
    }

    @Operation(summary = "新增社区板块")
    @PostMapping("/portal/board/add")
    public CommonResult<String> addBoard(@RequestBody BizForumBoard board) {
        bizForumBoardService.add(board);
        return CommonResult.ok();
    }

    @Operation(summary = "编辑社区板块")
    @PostMapping("/portal/board/edit")
    public CommonResult<String> editBoard(@RequestBody BizForumBoard board) {
        bizForumBoardService.edit(board);
        return CommonResult.ok();
    }

    @Operation(summary = "删除社区板块")
    @PostMapping("/portal/board/delete")
    public CommonResult<String> deleteBoard(@RequestBody List<String> ids) {
        bizForumBoardService.delete(ids);
        return CommonResult.ok();
    }

    @Operation(summary = "切换板块状态")
    @PostMapping("/portal/board/changeStatus")
    public CommonResult<String> changeBoardStatus(@RequestBody JSONObject param) {
        String id = param.getStr("id");
        String status = param.getStr("status");
        if (StrUtil.isBlank(id) || StrUtil.isBlank(status)) {
            throw new CommonException("参数不完整");
        }
        BizForumBoard board = bizForumBoardService.getById(id);
        if (board != null) {
            board.setStatus(status);
            bizForumBoardService.updateById(board);
        }
        return CommonResult.ok();
    }

    /* ---------------- 3. 前台展示项目与图片推荐管控 ---------------- */

    @Operation(summary = "分页查询前台项目与推荐状态")
    @GetMapping("/portal/project/page")
    public CommonResult<Page<JSONObject>> projectPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String isFeatured) {

        Page<BizProject> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<BizProject> qw = new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getDeleteFlag, "NOT_DELETE");

        if (StrUtil.isNotBlank(searchKey)) {
            qw.and(w -> w.like(BizProject::getProjectName, searchKey)
                    .or().like(BizProject::getProjectDesc, searchKey));
        }
        if (StrUtil.isNotBlank(isFeatured)) {
            if ("YES".equalsIgnoreCase(isFeatured)) {
                qw.like(BizProject::getExtJson, "\"isFeatured\":\"YES\"");
            }
        }
        qw.orderByDesc(BizProject::getSortCode).orderByDesc(BizProject::getCreateTime);

        Page<BizProject> projPage = bizProjectService.page(pageParam, qw);
        List<BizProject> records = projPage.getRecords();

        Set<String> leaderIds = records.stream().map(BizProject::getLeaderId)
                .filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        Map<String, ClientUser> leaderMap = Collections.emptyMap();
        if (!leaderIds.isEmpty()) {
            List<ClientUser> leaders = clientUserService.listByIds(leaderIds);
            leaderMap = CollStreamUtil.toIdentityMap(leaders, ClientUser::getId);
        }

        List<JSONObject> list = new ArrayList<>();
        for (BizProject p : records) {
            JSONObject obj = JSONUtil.parseObj(p);
            ClientUser leader = leaderMap.get(p.getLeaderId());
            obj.set("leaderName", leader != null ? leader.getName() : "未指定组长");
            obj.set("leaderAccount", leader != null ? leader.getAccount() : "-");

            JSONObject ext = StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())
                    ? JSONUtil.parseObj(p.getExtJson()) : new JSONObject();
            obj.set("coverUrl", ext.getStr("coverUrl", p.getAvatarFileId()));
            obj.set("isFeatured", ext.getStr("isFeatured", "NO"));
            obj.set("isPinned", ext.getStr("isPinned", "NO"));
            obj.set("badgeTag", ext.getStr("badgeTag", ""));
            list.add(obj);
        }

        Page<JSONObject> result = new Page<>(projPage.getCurrent(), projPage.getSize(), projPage.getTotal());
        result.setRecords(list);
        return CommonResult.data(result);
    }

    @Operation(summary = "修改前台项目展示信息与推荐")
    @PostMapping("/portal/project/editShowcase")
    public CommonResult<String> editProjectShowcase(@RequestBody JSONObject param) {
        String projectId = param.getStr("projectId");
        if (StrUtil.isBlank(projectId)) {
            throw new CommonException("项目ID不能为空");
        }
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        // 修改项目名称
        if (param.containsKey("projectName") && StrUtil.isNotBlank(param.getStr("projectName"))) {
            project.setProjectName(param.getStr("projectName"));
        }
        // 修改项目描述
        if (param.containsKey("projectDesc")) {
            project.setProjectDesc(param.getStr("projectDesc"));
        }
        // 封面图片
        if (param.containsKey("coverUrl")) {
            project.setAvatarFileId(param.getStr("coverUrl"));
        }
        // 排序码
        if (param.containsKey("sortCode") && param.getInt("sortCode") != null) {
            project.setSortCode(param.getInt("sortCode"));
        }

        // 扩展字段更新（isFeatured, isPinned, coverUrl, badgeTag）
        JSONObject ext = StrUtil.isNotBlank(project.getExtJson()) && JSONUtil.isTypeJSONObject(project.getExtJson())
                ? JSONUtil.parseObj(project.getExtJson()) : new JSONObject();

        if (param.containsKey("coverUrl")) {
            ext.set("coverUrl", param.getStr("coverUrl"));
        }
        if (param.containsKey("isFeatured")) {
            ext.set("isFeatured", param.getStr("isFeatured"));
        }
        if (param.containsKey("isPinned")) {
            ext.set("isPinned", param.getStr("isPinned"));
        }
        if (param.containsKey("badgeTag")) {
            ext.set("badgeTag", param.getStr("badgeTag"));
        }
        project.setExtJson(ext.toString());

        bizProjectService.updateById(project);
        return CommonResult.ok();
    }

    @Operation(summary = "获取前台首页精选推荐项目")
    @GetMapping("/portal/project/featuredList")
    public CommonResult<List<JSONObject>> featuredProjectList() {
        List<BizProject> list = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getDeleteFlag, "NOT_DELETE")
                .like(BizProject::getExtJson, "\"isFeatured\":\"YES\"")
                .orderByDesc(BizProject::getSortCode)
                .orderByDesc(BizProject::getCreateTime));

        Set<String> leaderIds = list.stream().map(BizProject::getLeaderId)
                .filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        Map<String, ClientUser> leaderMap = Collections.emptyMap();
        if (!leaderIds.isEmpty()) {
            List<ClientUser> leaders = clientUserService.listByIds(leaderIds);
            leaderMap = CollStreamUtil.toIdentityMap(leaders, ClientUser::getId);
        }

        List<JSONObject> res = new ArrayList<>();
        for (BizProject p : list) {
            JSONObject obj = JSONUtil.parseObj(p);
            ClientUser leader = leaderMap.get(p.getLeaderId());
            obj.set("leaderName", leader != null ? leader.getName() : "未指定组长");
            JSONObject ext = StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())
                    ? JSONUtil.parseObj(p.getExtJson()) : new JSONObject();
            obj.set("coverUrl", ext.getStr("coverUrl", p.getAvatarFileId()));
            obj.set("badgeTag", ext.getStr("badgeTag", "精选推荐"));
            res.add(obj);
        }
        return CommonResult.data(res);
    }
}
