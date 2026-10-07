package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 前台个人中心与学生管理控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-个人中心控制器")
@RestController
@Validated
@RequestMapping("/client/portal/user")
public class PortalUserController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "获取当前登录用户的全量档案与角色画像")
    @SaClientCheckLogin
    @GetMapping("/myProfile")
    public CommonResult<JSONObject> getMyProfile() {
        return portalService.getMyProfile();
    }

    @Operation(summary = "修改个人资料")
    @SaClientCheckLogin
    @PostMapping("/updateMyProfile")
    public CommonResult<String> updateMyProfile(@RequestBody JSONObject param) {
        return portalService.updateMyProfile(param);
    }

    @Operation(summary = "查询我的项目组（作为组长或组员）")
    @SaClientCheckLogin
    @GetMapping("/myProjects")
    public CommonResult<List<JSONObject>> getMyProjects() {
        return portalService.getMyProjects();
    }

    @Operation(summary = "查看我收到的入组邀请")
    @SaClientCheckLogin
    @GetMapping("/myInvitations")
    public CommonResult<List<JSONObject>> getMyInvitations() {
        return portalService.getMyInvitations();
    }

    @Operation(summary = "同意或拒绝入组邀请")
    @SaClientCheckLogin
    @PostMapping("/handleInvitation")
    public CommonResult<String> handleInvitation(@RequestParam String inviteId, @RequestParam boolean accept) {
        return portalService.handleInvitation(inviteId, accept);
    }

    @Operation(summary = "老师管理端-检索所有学生档案与动态")
    @SaClientCheckLogin
    @GetMapping("/allStudents")
    public CommonResult<List<JSONObject>> getAllStudents(@RequestParam(required = false) String keyword) {
        return portalService.getAllStudents(keyword);
    }

    @Operation(summary = "前台通用文件上传接口")
    @SaClientCheckLogin
    @PostMapping("/uploadFile")
    public CommonResult<String> uploadFile(@RequestPart("file") MultipartFile file) {
        return portalService.uploadFile(file);
    }

    @Operation(summary = "获取当前用户的全量待办任务列表")
    @SaClientCheckLogin
    @GetMapping("/todoList")
    public CommonResult<List<JSONObject>> getMyTodoList() {
        return portalService.getMyTodoList();
    }
}
