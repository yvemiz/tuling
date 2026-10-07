package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.portal.param.PortalAlgorithmSubmitParam;
import vip.xiaonuo.biz.modular.portal.param.PortalAlgorithmTaskParam;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前台算法练习与任务督促控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-算法中心控制器")
@RestController
@Validated
@RequestMapping("/client/portal/algorithm")
public class PortalAlgorithmController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "老师发布算法练习题并分发到各组")
    @SaClientCheckLogin
    @PostMapping("/publish")
    public CommonResult<String> publish(@RequestBody @Valid PortalAlgorithmTaskParam param) {
        return portalService.publishAlgorithmTask(param);
    }

    @Operation(summary = "学生查看所加入各组分配的算法任务列表")
    @SaClientCheckLogin
    @GetMapping("/myTasks")
    public CommonResult<List<JSONObject>> myTasks() {
        return portalService.getMyAlgorithmTasks();
    }

    @Operation(summary = "组长/老师查看团队算法完成进度与成员状态")
    @SaClientCheckLogin
    @GetMapping("/teamProgress")
    public CommonResult<List<JSONObject>> teamProgress(@RequestParam String projectId, @RequestParam String taskId) {
        return portalService.getTeamTaskProgress(projectId, taskId);
    }

    @Operation(summary = "组长履行监督督促职责-催促组员按时提交算法")
    @SaClientCheckLogin
    @PostMapping("/urge")
    public CommonResult<String> urge(@RequestParam String projectId, @RequestParam String studentId, @RequestParam String taskId, @RequestParam(required = false) String message) {
        return portalService.urgeMember(projectId, studentId, taskId, message);
    }

    @Operation(summary = "组员提交算法作业（多组进组仅需提交一次，多模式支持）")
    @SaClientCheckLogin
    @PostMapping("/submit")
    public CommonResult<String> submit(@RequestBody @Valid PortalAlgorithmSubmitParam param) {
        return portalService.submitAlgorithm(param);
    }

    @Operation(summary = "查询我的算法提交记录")
    @SaClientCheckLogin
    @GetMapping("/mySubmissions")
    public CommonResult<List<JSONObject>> mySubmissions(@RequestParam(required = false) String taskId) {
        return portalService.getMySubmissions(taskId);
    }

    @Operation(summary = "老师对算法提交进行评分与评语批改")
    @SaClientCheckLogin
    @PostMapping("/grade")
    public CommonResult<String> grade(@RequestParam String submissionId, @RequestParam BigDecimal score, @RequestParam(required = false) String feedback) {
        return portalService.gradeAlgorithmSubmission(submissionId, score, feedback);
    }

    @Operation(summary = "学生查看所收到的组长催交提醒")
    @SaClientCheckLogin
    @GetMapping("/myUrges")
    public CommonResult<List<JSONObject>> myUrges() {
        return portalService.getMyUrges();
    }

    @Operation(summary = "算法题库列表查询（教师/学生均可查阅）")
    @SaClientCheckLogin
    @GetMapping("/library/list")
    public CommonResult<List<JSONObject>> libraryList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String tag) {
        return portalService.getAlgorithmLibrary(keyword, difficulty, tag);
    }

    @Operation(summary = "教师向题库录入新算法题目")
    @SaClientCheckLogin
    @PostMapping("/library/save")
    public CommonResult<String> librarySave(@RequestBody JSONObject param) {
        return portalService.saveAlgorithmLibraryProblem(param);
    }

    @Operation(summary = "教师修改算法题库中的题目")
    @SaClientCheckLogin
    @PostMapping("/library/update")
    public CommonResult<String> libraryUpdate(@RequestBody JSONObject param) {
        return portalService.updateAlgorithmLibraryProblem(param);
    }

    @Operation(summary = "教师删除题库中的算法题目")
    @SaClientCheckLogin
    @PostMapping("/library/delete")
    public CommonResult<String> libraryDelete(@RequestBody JSONObject param) {
        String id = param.getStr("id");
        return portalService.deleteAlgorithmLibraryProblem(id);
    }

    @Operation(summary = "教师将题库题目发布分发至指定项目团队")
    @SaClientCheckLogin
    @PostMapping("/library/dispatch")
    public CommonResult<String> libraryDispatch(@RequestBody JSONObject param) {
        return portalService.dispatchAlgorithmTask(param);
    }

    @Operation(summary = "教师查看所有小组的算法完成概览看板")
    @SaClientCheckLogin
    @GetMapping("/teacher/groupProgress")
    public CommonResult<List<JSONObject>> teacherGroupProgress() {
        return portalService.getTeacherGroupProgress();
    }

    @Operation(summary = "教师查看各小组所有学生的算法具体提交信息与代码")
    @SaClientCheckLogin
    @GetMapping("/teacher/allSubmissions")
    public CommonResult<List<JSONObject>> teacherAllSubmissions(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return portalService.getTeacherAllSubmissions(projectId, taskId, keyword, status);
    }

    @Operation(summary = "组长获取自己担任组长的所有项目小组列表")
    @SaClientCheckLogin
    @GetMapping("/leader/myTeams")
    public CommonResult<List<JSONObject>> leaderMyTeams() {
        return portalService.getLeaderMyTeams();
    }

    @Operation(summary = "组长查看指定管辖小组的算法督训全景矩阵")
    @SaClientCheckLogin
    @GetMapping("/leader/teamOverview")
    public CommonResult<JSONObject> leaderTeamOverview(@RequestParam String projectId) {
        return portalService.getLeaderTeamOverview(projectId);
    }

    @Operation(summary = "组长一键催交管辖小组内未完成成员")
    @SaClientCheckLogin
    @PostMapping("/leader/batchUrge")
    public CommonResult<JSONObject> leaderBatchUrge(@RequestBody JSONObject param) {
        String projectId = param.getStr("projectId");
        String taskId = param.getStr("taskId");
        String message = param.getStr("message");
        return portalService.batchUrgeTeamMembers(projectId, taskId, message);
    }
}
