package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 前台项目团队专属管理控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-团队管理控制器")
@RestController
@Validated
@RequestMapping("/client/portal/project")
public class PortalProjectController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "获取项目团队详情与成员阵容")
    @GetMapping("/detail")
    public CommonResult<JSONObject> detail(@RequestParam String projectId) {
        return portalService.getProjectDetail(projectId);
    }

    @Operation(summary = "组长或发起教师切换项目私密状态（设为私密后不可被外界查看）")
    @SaClientCheckLogin
    @PostMapping("/togglePrivacy")
    public CommonResult<String> togglePrivacy(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String isPrivate) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String priv = body != null && StrUtil.isNotBlank(body.getStr("isPrivate")) ? body.getStr("isPrivate") : isPrivate;
        return portalService.toggleProjectPrivacy(pId, priv);
    }

    @Operation(summary = "组长或指导教师移除组员")
    @SaClientCheckLogin
    @PostMapping("/removeMember")
    public CommonResult<String> removeMember(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String studentId) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String sId = body != null && StrUtil.isNotBlank(body.getStr("studentId")) ? body.getStr("studentId") : studentId;
        return portalService.removeMember(pId, sId);
    }

    @Operation(summary = "组员主动退出项目组")
    @SaClientCheckLogin
    @PostMapping("/quit")
    public CommonResult<String> quit(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        return portalService.quitProject(pId);
    }

    @Operation(summary = "组长或发起教师解散项目组")
    @SaClientCheckLogin
    @PostMapping("/disband")
    public CommonResult<String> disband(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        return portalService.disbandProject(pId);
    }

    @Operation(summary = "组长或发起教师更新组内职务分工")
    @SaClientCheckLogin
    @PostMapping("/updateDuty")
    public CommonResult<String> updateDuty(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String dutyRemark,
            @RequestParam(required = false) String duty) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String sId = body != null && StrUtil.isNotBlank(body.getStr("studentId")) ? body.getStr("studentId") : studentId;
        String dRem = body != null && StrUtil.isNotBlank(body.getStr("dutyRemark")) ? body.getStr("dutyRemark") : dutyRemark;
        String d = body != null && StrUtil.isNotBlank(body.getStr("duty")) ? body.getStr("duty") : duty;
        String finalDuty = StrUtil.isNotBlank(dRem) ? dRem : d;
        return portalService.updateMemberDuty(pId, sId, finalDuty);
    }

    @Operation(summary = "学生申请报名竞聘组长或申请加入组员")
    @SaClientCheckLogin
    @PostMapping("/apply")
    public CommonResult<String> apply(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false, defaultValue = "MEMBER") String applyRole,
            @RequestParam(required = false) String message) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String role = body != null && StrUtil.isNotBlank(body.getStr("applyRole")) ? body.getStr("applyRole") : applyRole;
        String msg = body != null && StrUtil.isNotBlank(body.getStr("message")) ? body.getStr("message") : message;
        return portalService.applyToProject(pId, role, msg);
    }

    @Operation(summary = "老师或组长获取项目组报名申请筛选名单")
    @SaClientCheckLogin
    @GetMapping("/applicants")
    public CommonResult<List<JSONObject>> applicants(@RequestParam String projectId) {
        return portalService.getProjectApplicants(projectId);
    }

    @Operation(summary = "老师或组长筛选录取/淘汰报名学生")
    @SaClientCheckLogin
    @PostMapping("/handleApplicant")
    public CommonResult<String> handleApplicant(
            @RequestParam String flowId,
            @RequestParam boolean accept,
            @RequestParam(required = false) String remark) {
        return portalService.handleProjectApplicant(flowId, accept, remark);
    }

    @Operation(summary = "获取团队获奖荣誉证书")
    @SaClientCheckLogin
    @GetMapping("/certificates")
    public CommonResult<List<JSONObject>> certificates(@RequestParam String projectId) {
        return portalService.getProjectCertificates(projectId);
    }

    @Operation(summary = "绑定/修改项目组参赛竞赛（支持<暂未参与>转为具体比赛）")
    @SaClientCheckLogin
    @PostMapping("/bindCompetition")
    public CommonResult<String> bindCompetition(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String competitionId) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String cId = body != null && StrUtil.isNotBlank(body.getStr("competitionId")) ? body.getStr("competitionId") : competitionId;
        return portalService.bindProjectCompetition(pId, cId);
    }

    @Operation(summary = "确认完赛晋级（自动将项目组升级至更高一级比赛）")
    @SaClientCheckLogin
    @PostMapping("/confirmPromotion")
    public CommonResult<String> confirmPromotion(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String targetCompetitionId) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String tId = body != null && StrUtil.isNotBlank(body.getStr("targetCompetitionId")) ? body.getStr("targetCompetitionId") : targetCompetitionId;
        return portalService.confirmPromotion(pId, tId);
    }

    @Operation(summary = "设定项目晋级状态（测试与导师评定）")
    @SaClientCheckLogin
    @PostMapping("/setPromotion")
    public CommonResult<String> setPromotion(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String competitionId,
            @RequestParam(required = false, defaultValue = "false") Boolean isPromoted,
            @RequestParam(required = false) String remark) {
        String pId = body != null && StrUtil.isNotBlank(body.getStr("projectId")) ? body.getStr("projectId") : projectId;
        String cId = body != null && StrUtil.isNotBlank(body.getStr("competitionId")) ? body.getStr("competitionId") : competitionId;
        Boolean promo = body != null && body.getBool("isPromoted") != null ? body.getBool("isPromoted") : isPromoted;
        String rem = body != null && StrUtil.isNotBlank(body.getStr("remark")) ? body.getStr("remark") : remark;
        return portalService.setProjectPromotion(pId, cId, Boolean.TRUE.equals(promo), rem);
    }
}
