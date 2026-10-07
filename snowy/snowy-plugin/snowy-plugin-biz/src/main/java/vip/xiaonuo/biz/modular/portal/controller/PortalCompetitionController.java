package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.competition.entity.BizCompetition;
import vip.xiaonuo.biz.modular.portal.param.PortalCompetitionParam;
import vip.xiaonuo.biz.modular.portal.param.PortalProjectCreateParam;
import vip.xiaonuo.biz.modular.portal.param.PortalRecruitParam;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 前台竞赛与团队报名控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-竞赛管理控制器")
@RestController
@Validated
@RequestMapping("/client/portal/competition")
public class PortalCompetitionController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "分页获取竞赛列表")
    @GetMapping("/page")
    public CommonResult<Page<BizCompetition>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return portalService.pageCompetitions(keyword, level, status, current, size);
    }

    @Operation(summary = "获取竞赛详情")
    @GetMapping("/detail")
    public CommonResult<JSONObject> detail(@RequestParam String competitionId) {
        return portalService.getCompetitionDetail(competitionId);
    }

    @Operation(summary = "老师发布新竞赛")
    @SaClientCheckLogin
    @PostMapping("/publish")
    public CommonResult<String> publish(@RequestBody @Valid PortalCompetitionParam param) {
        return portalService.publishCompetition(param);
    }

    @Operation(summary = "老师编辑竞赛")
    @SaClientCheckLogin
    @PostMapping("/edit")
    public CommonResult<String> edit(@RequestBody @Valid PortalCompetitionParam param) {
        return portalService.editCompetition(param);
    }

    @Operation(summary = "老师删除竞赛")
    @SaClientCheckLogin
    @PostMapping("/delete")
    public CommonResult<String> delete(@RequestParam String id) {
        return portalService.deleteCompetition(id);
    }

    @Operation(summary = "获取当前竞赛下的所有公开项目组")
    @GetMapping("/projects")
    public CommonResult<List<JSONObject>> projects(@RequestParam String competitionId) {
        return portalService.getCompetitionProjects(competitionId);
    }

    @Operation(summary = "学生创建项目组并成为组长，报名该竞赛")
    @SaClientCheckLogin
    @PostMapping("/createProject")
    public CommonResult<String> createProject(@RequestBody @Valid PortalProjectCreateParam param) {
        return portalService.createProjectAndBecomeLeader(param);
    }

    @Operation(summary = "单人竞赛直接报名（限单人赛）")
    @SaClientCheckLogin
    @PostMapping("/individualSignup")
    public CommonResult<String> individualSignup(@RequestParam String competitionId) {
        return portalService.individualSignup(competitionId);
    }

    @Operation(summary = "学生对竞赛或项目组点击关注/取消关注")
    @SaClientCheckLogin
    @PostMapping("/toggleFollow")
    public CommonResult<Boolean> toggleFollow(@RequestParam String targetType, @RequestParam String targetId) {
        return portalService.toggleFollow(targetType, targetId);
    }

    @Operation(summary = "组长专享-查看对本竞赛感兴趣的学生名单")
    @SaClientCheckLogin
    @GetMapping("/interestedStudents")
    public CommonResult<List<JSONObject>> interestedStudents(@RequestParam String competitionId) {
        return portalService.getInterestedStudents(competitionId);
    }

    @Operation(summary = "组长向感兴趣的学生发起入组邀请")
    @SaClientCheckLogin
    @PostMapping("/inviteStudent")
    public CommonResult<String> inviteStudent(@RequestParam String projectId,
                                              @RequestParam(required = false) String studentId,
                                              @RequestParam(required = false) String targetStudentId,
                                              @RequestParam(required = false) String message) {
        String finalStudentId = StrUtil.isNotBlank(studentId) ? studentId : targetStudentId;
        return portalService.inviteStudent(projectId, finalStudentId, message);
    }

    @Operation(summary = "组长发布团队招募令")
    @SaClientCheckLogin
    @PostMapping("/publishRecruit")
    public CommonResult<String> publishRecruit(@RequestBody @Valid PortalRecruitParam param) {
        return portalService.publishRecruit(param);
    }

    @Operation(summary = "获取全网招募令列表")
    @GetMapping("/listRecruits")
    public CommonResult<List<JSONObject>> listRecruits(@RequestParam(required = false) String keyword) {
        return portalService.listRecruits(keyword);
    }

    @Operation(summary = "组员/组长上传竞赛获奖证书")
    @SaClientCheckLogin
    @PostMapping("/uploadCertificate")
    public CommonResult<String> uploadCertificate(
            @RequestBody(required = false) JSONObject body,
            @RequestParam(required = false) String competitionId,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String certName,
            @RequestParam(required = false) String certificateName,
            @RequestParam(required = false) String awardLevel,
            @RequestParam(required = false) String awardDate,
            @RequestParam(required = false) String fileId,
            @RequestParam(required = false) String certificateUrl,
            @RequestParam(required = false) String certificateImage,
            @RequestParam(required = false) String studentNames,
            @RequestParam(required = false) String studentIds) {
        if (body == null) {
            body = new JSONObject();
        }
        if (StrUtil.isNotBlank(competitionId)) body.set("competitionId", competitionId);
        if (StrUtil.isNotBlank(projectId)) body.set("projectId", projectId);
        if (StrUtil.isNotBlank(certName)) body.set("certificateName", certName);
        if (StrUtil.isNotBlank(certificateName)) body.set("certificateName", certificateName);
        if (StrUtil.isNotBlank(awardLevel)) body.set("awardLevel", awardLevel);
        if (StrUtil.isNotBlank(awardDate)) body.set("awardDate", awardDate);
        if (StrUtil.isNotBlank(fileId)) body.set("fileId", fileId);
        if (StrUtil.isNotBlank(certificateUrl)) body.set("certificateUrl", certificateUrl);
        if (StrUtil.isNotBlank(certificateImage)) body.set("certificateImage", certificateImage);
        if (StrUtil.isNotBlank(studentNames)) body.set("studentNames", studentNames);
        if (StrUtil.isNotBlank(studentIds)) body.set("studentIds", studentIds);

        return portalService.uploadCertificate(body);
    }

    @Operation(summary = "查看某项目团队的竞赛证书与荣誉")
    @SaClientCheckLogin
    @GetMapping("/projectCertificates")
    public CommonResult<List<JSONObject>> projectCertificates(@RequestParam String projectId) {
        return portalService.getProjectCertificates(projectId);
    }

    @Operation(summary = "查看我获得的竞赛证书")
    @SaClientCheckLogin
    @GetMapping("/myCertificates")
    public CommonResult<List<JSONObject>> myCertificates(@RequestParam(required = false) String competitionId) {
        return portalService.getMyCertificates(competitionId);
    }

    @Operation(summary = "老师认证审核竞赛证书")
    @SaClientCheckLogin
    @PostMapping("/verifyCertificate")
    public CommonResult<String> verifyCertificate(@RequestParam String certId, @RequestParam String status, @RequestParam(required = false) String remark) {
        return portalService.verifyCertificate(certId, status, remark);
    }

    @Operation(summary = "设定项目组完赛晋级状态（老师或组长确认/测试使用）")
    @SaClientCheckLogin
    @PostMapping("/setProjectPromotion")
    public CommonResult<String> setProjectPromotion(
            @RequestParam String projectId,
            @RequestParam(required = false) String competitionId,
            @RequestParam boolean isPromoted,
            @RequestParam(required = false) String remark) {
        return portalService.setProjectPromotion(projectId, competitionId, isPromoted, remark);
    }
}
