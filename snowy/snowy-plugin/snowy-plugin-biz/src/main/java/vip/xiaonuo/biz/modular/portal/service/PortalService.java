package vip.xiaonuo.biz.modular.portal.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.competition.entity.BizCompetition;
import vip.xiaonuo.biz.modular.portal.param.*;
import vip.xiaonuo.common.pojo.CommonResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前台门户综合服务接口
 *
 * @author antigravity
 * @date 2026/10/04
 */
public interface PortalService {

    // ========== 账号与个人中心 ==========
    CommonResult<JSONObject> login(PortalLoginParam param);

    CommonResult<String> register(PortalRegisterParam param);

    CommonResult<JSONObject> demoLogin(String role);

    CommonResult<JSONObject> getMyProfile();

    CommonResult<String> updateMyProfile(JSONObject param);

    CommonResult<List<JSONObject>> getAllStudents(String keyword);

    CommonResult<List<JSONObject>> getMyProjects();

    // ========== 竞赛与参赛团队 ==========
    CommonResult<Page<BizCompetition>> pageCompetitions(String keyword, String level, String status, Integer current, Integer size);

    CommonResult<JSONObject> getCompetitionDetail(String competitionId);

    CommonResult<String> publishCompetition(PortalCompetitionParam param);

    CommonResult<String> editCompetition(PortalCompetitionParam param);

    CommonResult<String> deleteCompetition(String id);

    CommonResult<List<JSONObject>> getCompetitionProjects(String competitionId);

    CommonResult<String> createProjectAndBecomeLeader(PortalProjectCreateParam param);

    CommonResult<String> individualSignup(String competitionId);

    CommonResult<Boolean> toggleFollow(String targetType, String targetId);

    CommonResult<List<JSONObject>> getInterestedStudents(String competitionId);

    CommonResult<String> inviteStudent(String projectId, String studentId, String message);

    CommonResult<List<JSONObject>> getMyInvitations();

    CommonResult<String> handleInvitation(String inviteId, boolean accept);

    CommonResult<String> publishRecruit(PortalRecruitParam param);

    CommonResult<List<JSONObject>> listRecruits(String keyword);

    CommonResult<String> uploadCertificate(String competitionId, String projectId, String certName, String awardLevel, String awardDate, String fileId);

    CommonResult<String> uploadCertificate(JSONObject body);

    CommonResult<List<JSONObject>> getMyCertificates(String competitionId);

    CommonResult<List<JSONObject>> getProjectCertificates(String projectId);

    CommonResult<String> verifyCertificate(String certId, String status, String remark);

    // ========== 项目团队管理 ==========
    CommonResult<JSONObject> getProjectDetail(String projectId);

    CommonResult<String> toggleProjectPrivacy(String projectId, String isPrivate);

    CommonResult<String> removeMember(String projectId, String studentId);

    CommonResult<String> quitProject(String projectId);

    CommonResult<String> disbandProject(String projectId);

    CommonResult<String> updateMemberDuty(String projectId, String studentId, String dutyRemark);

    CommonResult<String> applyToProject(String projectId, String applyRole, String message);

    CommonResult<List<JSONObject>> getProjectApplicants(String projectId);

    CommonResult<String> handleProjectApplicant(String flowId, boolean accept, String remark);

    // ========== 算法训练 ==========
    CommonResult<String> publishAlgorithmTask(PortalAlgorithmTaskParam param);

    CommonResult<List<JSONObject>> getMyAlgorithmTasks();

    CommonResult<List<JSONObject>> getTeamTaskProgress(String projectId, String taskId);

    CommonResult<String> urgeMember(String projectId, String studentId, String taskId, String message);

    CommonResult<String> submitAlgorithm(PortalAlgorithmSubmitParam param);

    CommonResult<List<JSONObject>> getMySubmissions(String taskId);

    CommonResult<String> gradeAlgorithmSubmission(String submissionId, BigDecimal score, String feedback);

    // 算法题库 CRUD 与发布 (教师)
    CommonResult<List<JSONObject>> getAlgorithmLibrary(String keyword, String difficulty, String tag);

    CommonResult<String> saveAlgorithmLibraryProblem(JSONObject param);

    CommonResult<String> updateAlgorithmLibraryProblem(JSONObject param);

    CommonResult<String> deleteAlgorithmLibraryProblem(String id);

    CommonResult<String> dispatchAlgorithmTask(JSONObject param);

    // 教师多小组算法监控与具体提交明细
    CommonResult<List<JSONObject>> getTeacherGroupProgress();

    CommonResult<List<JSONObject>> getTeacherAllSubmissions(String projectId, String taskId, String keyword, String status);

    // 组长多团队督训
    CommonResult<List<JSONObject>> getLeaderMyTeams();

    CommonResult<JSONObject> getLeaderTeamOverview(String projectId);

    CommonResult<JSONObject> batchUrgeTeamMembers(String projectId, String taskId, String message);

    // ========== 论坛社区与多级智能审核 ==========
    CommonResult<Page<JSONObject>> pageForumPosts(String postType, Integer current, Integer size);

    CommonResult<JSONObject> getPostDetail(String postId);

    CommonResult<String> createForumPost(PortalForumPostParam param);

    CommonResult<Page<JSONObject>> pagePendingAuditPosts(Integer current, Integer size);

    CommonResult<String> teacherAuditPost(PortalAuditParam param);

    CommonResult<String> leaderAuditPost(PortalAuditParam param);

    CommonResult<String> togglePinPost(String postId, String isPinned);

    CommonResult<String> addComment(String postId, String content, String parentId, String replyToUserId);

    CommonResult<List<JSONObject>> getPostComments(String postId);

    // ========== AI评测、打分与排行榜 ==========
    CommonResult<JSONObject> evaluateTeamWithAi(String projectId, String batch);

    CommonResult<String> saveMemberScore(PortalMemberScoreParam param);

    CommonResult<List<JSONObject>> getMemberScores(String projectId);

    CommonResult<String> teacherEvaluateTeam(PortalTeacherEvaluateParam param);

    CommonResult<String> publishProjectRanking(String rankingId);

    CommonResult<List<JSONObject>> getPublishedRankings(String period, String competitionId);

    // ========== 催促通知与通用文件上传 ==========
    CommonResult<List<JSONObject>> getMyUrges();

    // ========== 待办中心、赛事绑定与晋级流转 ==========
    CommonResult<String> bindProjectCompetition(String projectId, String competitionId);

    CommonResult<String> confirmPromotion(String projectId, String targetCompetitionId);

    CommonResult<String> setProjectPromotion(String projectId, String competitionId, boolean isPromoted, String remark);

    CommonResult<List<JSONObject>> getMyTodoList();

    CommonResult<String> uploadFile(org.springframework.web.multipart.MultipartFile file);
}
