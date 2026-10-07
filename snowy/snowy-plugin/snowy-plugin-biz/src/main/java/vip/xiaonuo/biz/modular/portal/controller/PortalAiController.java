package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.portal.param.PortalMemberScoreParam;
import vip.xiaonuo.biz.modular.portal.param.PortalTeacherEvaluateParam;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 前台AI智能评测、组员私密打分与排行榜发布控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-AI机制与排行榜控制器")
@RestController
@Validated
@RequestMapping("/client/portal/ai")
public class PortalAiController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "组长/老师使用AI对项目组进行综合评估分析打分与诊断报告")
    @SaClientCheckLogin
    @PostMapping("/evaluateTeam")
    public CommonResult<JSONObject> evaluateTeam(@RequestParam String projectId, @RequestParam(required = false) String batch) {
        return portalService.evaluateTeamWithAi(projectId, batch);
    }

    @Operation(summary = "组长/老师对组员表现进行手动打分（保密：仅老师与该组组长能看到）")
    @SaClientCheckLogin
    @PostMapping("/saveMemberScore")
    public CommonResult<String> saveMemberScore(@RequestBody @Valid PortalMemberScoreParam param) {
        return portalService.saveMemberScore(param);
    }

    @Operation(summary = "查询项目组的组员表现打分表（保密受限：仅老师与本组组长可见）")
    @SaClientCheckLogin
    @GetMapping("/memberScores")
    public CommonResult<List<JSONObject>> memberScores(@RequestParam String projectId) {
        return portalService.getMemberScores(projectId);
    }

    @Operation(summary = "老师对各项目组综合评估评定得分（结合AI得分）")
    @SaClientCheckLogin
    @PostMapping("/teacherEvaluateTeam")
    public CommonResult<String> teacherEvaluateTeam(@RequestBody @Valid PortalTeacherEvaluateParam param) {
        return portalService.teacherEvaluateTeam(param);
    }

    @Operation(summary = "老师正式发布项目组排行榜")
    @SaClientCheckLogin
    @PostMapping("/publishRanking")
    public CommonResult<String> publishRanking(@RequestParam String rankingId) {
        return portalService.publishProjectRanking(rankingId);
    }

    @Operation(summary = "全体师生公开查看已发布的项目综合排行榜")
    @GetMapping("/rankings")
    public CommonResult<List<JSONObject>> rankings(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String competitionId) {
        return portalService.getPublishedRankings(period, competitionId);
    }
}
