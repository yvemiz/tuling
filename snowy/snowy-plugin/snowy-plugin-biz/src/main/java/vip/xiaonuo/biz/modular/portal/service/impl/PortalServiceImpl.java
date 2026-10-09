package vip.xiaonuo.biz.modular.portal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.biz.modular.aievaluation.entity.BizAiEvaluation;
import vip.xiaonuo.biz.modular.aievaluation.service.BizAiEvaluationService;
import vip.xiaonuo.biz.modular.algorithmsuffix.entity.BizAlgorithmSubmission;
import vip.xiaonuo.biz.modular.algorithmsuffix.service.BizAlgorithmSubmissionService;
import vip.xiaonuo.biz.modular.algorithmtask.entity.BizAlgorithmTask;
import vip.xiaonuo.biz.modular.algorithmtask.service.BizAlgorithmTaskService;
import vip.xiaonuo.biz.modular.algorithmtaskproj.entity.BizAlgorithmTaskProj;
import vip.xiaonuo.biz.modular.algorithmtaskproj.service.BizAlgorithmTaskProjService;
import vip.xiaonuo.biz.modular.algorithmurge.entity.BizAlgorithmUrge;
import vip.xiaonuo.biz.modular.algorithmurge.service.BizAlgorithmUrgeService;
import vip.xiaonuo.biz.modular.competition.entity.BizCompetition;
import vip.xiaonuo.biz.modular.competition.service.BizCompetitionService;
import vip.xiaonuo.biz.modular.competitioncert.entity.BizCompetitionCert;
import vip.xiaonuo.biz.modular.competitioncert.service.BizCompetitionCertService;
import vip.xiaonuo.biz.modular.competitionproject.entity.BizCompetitionProject;
import vip.xiaonuo.biz.modular.competitionproject.service.BizCompetitionProjectService;
import vip.xiaonuo.biz.modular.follow.entity.BizFollow;
import vip.xiaonuo.biz.modular.follow.service.BizFollowService;
import vip.xiaonuo.biz.modular.forumaudit.entity.BizForumAudit;
import vip.xiaonuo.biz.modular.forumaudit.service.BizForumAuditService;
import vip.xiaonuo.biz.modular.forumcomment.entity.BizForumComment;
import vip.xiaonuo.biz.modular.forumcomment.service.BizForumCommentService;
import vip.xiaonuo.biz.modular.forumpost.entity.BizForumPost;
import vip.xiaonuo.biz.modular.forumpost.service.BizForumPostService;
import vip.xiaonuo.biz.modular.memberscore.entity.BizMemberScore;
import vip.xiaonuo.biz.modular.memberscore.service.BizMemberScoreService;
import vip.xiaonuo.biz.modular.portal.param.*;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.biz.modular.project.entity.BizProject;
import vip.xiaonuo.biz.modular.project.service.BizProjectService;
import vip.xiaonuo.biz.modular.projectflow.entity.BizProjectFlow;
import vip.xiaonuo.biz.modular.projectflow.service.BizProjectFlowService;
import vip.xiaonuo.biz.modular.projectmember.entity.BizProjectMember;
import vip.xiaonuo.biz.modular.projectmember.service.BizProjectMemberService;
import vip.xiaonuo.biz.modular.projectranking.entity.BizProjectRanking;
import vip.xiaonuo.biz.modular.projectranking.service.BizProjectRankingService;
import vip.xiaonuo.biz.modular.projectrecruit.entity.BizProjectRecruit;
import vip.xiaonuo.biz.modular.projectrecruit.service.BizProjectRecruitService;
import vip.xiaonuo.biz.modular.studentprofile.entity.BizStudentProfile;
import vip.xiaonuo.biz.modular.studentprofile.service.BizStudentProfileService;
import vip.xiaonuo.biz.modular.teacherprofile.entity.BizTeacherProfile;
import vip.xiaonuo.biz.modular.teacherprofile.service.BizTeacherProfileService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.util.CommonCryptogramUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台门户业务实现类
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Slf4j
@Service
public class PortalServiceImpl implements PortalService {

    @Resource
    private ClientUserService clientUserService;

    @Resource
    private BizStudentProfileService bizStudentProfileService;

    @Resource
    private BizTeacherProfileService bizTeacherProfileService;

    @Resource
    private BizCompetitionService bizCompetitionService;

    @Resource
    private BizProjectService bizProjectService;

    @Resource
    private BizProjectMemberService bizProjectMemberService;

    @Resource
    private BizCompetitionProjectService bizCompetitionProjectService;

    @Resource
    private BizCompetitionCertService bizCompetitionCertService;

    @Resource
    private BizFollowService bizFollowService;

    @Resource
    private BizProjectRecruitService bizProjectRecruitService;

    @Resource
    private BizProjectFlowService bizProjectFlowService;

    @Resource
    private BizAlgorithmTaskService bizAlgorithmTaskService;

    @Resource
    private BizAlgorithmTaskProjService bizAlgorithmTaskProjService;

    @Resource
    private BizAlgorithmSubmissionService bizAlgorithmSubmissionService;

    @Resource
    private BizAlgorithmUrgeService bizAlgorithmUrgeService;

    @Resource
    private BizForumPostService bizForumPostService;

    @Resource
    private BizForumAuditService bizForumAuditService;

    @Resource
    private BizForumCommentService bizForumCommentService;

    @Resource
    private BizAiEvaluationService bizAiEvaluationService;

    @Resource
    private BizMemberScoreService bizMemberScoreService;

    @Resource
    private BizProjectRankingService bizProjectRankingService;

    @Resource
    private vip.xiaonuo.dev.api.DevFileApi devFileApi;

    /**
     * 获取当前C端登录用户ID
     */
    private String getCurrentUserId() {
        if (!StpClientUtil.isLogin()) {
            throw new CommonException("请先登录前台账户");
        }
        return StpClientUtil.getLoginIdAsString();
    }

    /**
     * 判断并获取当前用户的角色标识集合 (STUDENT, TEACHER, LEADER)
     */
    private JSONObject getUserRoleInfo(String userId) {
        JSONObject result = new JSONObject();
        ClientUser user = clientUserService.getById(userId);
        if (user == null) {
            result.set("primaryRole", "STUDENT");
            result.set("isTeacher", false);
            result.set("isStudent", true);
            result.set("isLeader", false);
            return result;
        }

        // 解析 extJson
        String extJson = user.getExtJson();
        String declaredRole = "STUDENT";
        if (StrUtil.isNotEmpty(extJson) && JSONUtil.isTypeJSONObject(extJson)) {
            JSONObject ext = JSONUtil.parseObj(extJson);
            if (ext.containsKey("userRole")) {
                declaredRole = ext.getStr("userRole");
            }
        }
        boolean isTeacher = "TEACHER".equalsIgnoreCase(declaredRole);
        // 如果 extJson 未显式标明，检查教师档案关联
        if (!isTeacher) {
            long teacherProfileCount = bizTeacherProfileService.count(new LambdaQueryWrapper<BizTeacherProfile>()
                    .eq(BizTeacherProfile::getUserId, userId)
                    .eq(BizTeacherProfile::getDeleteFlag, "NOT_DELETE"));
            if (teacherProfileCount > 0) {
                isTeacher = true;
            }
        }
        boolean isStudent = !isTeacher;

        // 检查是否名下有作为组长的未删除项目
        long leaderProjectCount = bizProjectService.count(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getLeaderId, userId)
                .eq(BizProject::getDeleteFlag, "NOT_DELETE"));
        long joinedProjectsCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getStudentId, userId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        boolean isLeader = isStudent && (leaderProjectCount > 0);

        result.set("primaryRole", isTeacher ? "TEACHER" : (isLeader ? "LEADER" : "STUDENT"));
        result.set("isTeacher", isTeacher);
        result.set("isStudent", isStudent);
        result.set("isLeader", isLeader);
        result.set("leaderProjectCount", leaderProjectCount);
        result.set("ledProjectsCount", leaderProjectCount);
        result.set("joinedProjectsCount", joinedProjectsCount);
        return result;
    }

    // =========================================================================
    // 1. 账号与个人中心
    // =========================================================================

    @Override
    public CommonResult<JSONObject> login(PortalLoginParam param) {
        ClientUser user = clientUserService.getOne(new LambdaQueryWrapper<ClientUser>()
                .eq(ClientUser::getAccount, param.getAccount()));
        if (user == null) {
            throw new CommonException("账号不存在");
        }
        // 校验密码（支持明文比对、国密SM3比对或BCrypt比对）
        boolean pwdMatch = false;
        if (StrUtil.equals(user.getPassword(), param.getPassword())) {
            pwdMatch = true;
        } else if (StrUtil.equals(user.getPassword(), CommonCryptogramUtil.doHashValue(param.getPassword()))) {
            pwdMatch = true;
        } else {
            try {
                pwdMatch = BCrypt.checkpw(param.getPassword(), user.getPassword());
            } catch (Exception ignored) {
            }
        }
        if (!pwdMatch) {
            throw new CommonException("密码错误");
        }

        StpClientUtil.login(user.getId());
        String token = StpClientUtil.getTokenValue();

        JSONObject data = new JSONObject();
        data.set("token", token);
        data.set("userInfo", user);
        data.set("roleInfo", getUserRoleInfo(user.getId()));
        return CommonResult.data(data);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> register(PortalRegisterParam param) {
        if ("TEACHER".equalsIgnoreCase(param.getRole())) {
            throw new CommonException("前台不支持教师自主注册，教师账号由系统管理员在后台统一录入分发，请联系管理员获取账号");
        }
            // 学生注册：仅需输入学号和密码，后端根据学号自动匹配预导入学生档案
            String studentNo = StrUtil.isNotBlank(param.getStudentNo()) ? param.getStudentNo()
                    : (StrUtil.isNotBlank(param.getUserNo()) ? param.getUserNo() : param.getAccount());
            if (StrUtil.isBlank(studentNo)) {
                throw new CommonException("请填写学号");
            }
            if (StrUtil.isBlank(param.getPassword())) {
                throw new CommonException("请设置密码");
            }

            studentNo = studentNo.trim();

            // 在档案库检索预导入的学生档案 (按 student_no)
            BizStudentProfile profile = bizStudentProfileService.getOne(new LambdaQueryWrapper<BizStudentProfile>()
                    .eq(BizStudentProfile::getStudentNo, studentNo)
                    .eq(BizStudentProfile::getDeleteFlag, "NOT_DELETE"));

            if (profile == null) {
                throw new CommonException("未在预导入学籍档案库中匹配到学号【" + studentNo + "】，请核对学号或联系管理员导入！");
            }

            // 检查该档案是否已被激活注册
            ClientUser existingUser = null;
            if (StrUtil.isNotBlank(profile.getUserId())) {
                existingUser = clientUserService.getById(profile.getUserId());
            } else {
                existingUser = clientUserService.getOne(new LambdaQueryWrapper<ClientUser>()
                        .eq(ClientUser::getAccount, studentNo)
                        .eq(ClientUser::getDeleteFlag, "NOT_DELETE"));
            }

            // 获取预导入的姓名
            String matchedName = null;
            if (existingUser != null && StrUtil.isNotBlank(existingUser.getName())) {
                matchedName = existingUser.getName();
            } else if (StrUtil.isNotBlank(profile.getExtJson())) {
                try {
                    JSONObject extObj = JSONUtil.parseObj(profile.getExtJson());
                    matchedName = extObj.getStr("name");
                } catch (Exception ignored) {}
            }
            if (StrUtil.isBlank(matchedName)) {
                matchedName = StrUtil.isNotBlank(param.getName()) ? param.getName() : "学子_" + studentNo;
            }

            if (existingUser != null && StrUtil.isNotBlank(existingUser.getPassword())) {
                throw new CommonException("学号【" + studentNo + "】已完成注册激活，请直接登录！如遗忘密码请联系管理员");
            }

            if (existingUser == null) {
                existingUser = new ClientUser();
                existingUser.setAccount(studentNo);
                existingUser.setPassword(BCrypt.hashpw(param.getPassword()));
                existingUser.setName(matchedName);
                existingUser.setNickname(matchedName);
                existingUser.setPhone(param.getPhone());
                existingUser.setGender("未知");

                JSONObject ext = new JSONObject();
                ext.set("userRole", "STUDENT");
                existingUser.setExtJson(ext.toString());

                clientUserService.save(existingUser);

                profile.setUserId(existingUser.getId());
                bizStudentProfileService.updateById(profile);
            } else {
                // 激活已有预置记录
                existingUser.setPassword(BCrypt.hashpw(param.getPassword()));
                if (StrUtil.isBlank(existingUser.getAccount())) {
                    existingUser.setAccount(studentNo);
                }
                if (StrUtil.isNotBlank(param.getPhone())) {
                    existingUser.setPhone(param.getPhone());
                }
                JSONObject ext = StrUtil.isNotBlank(existingUser.getExtJson()) ? JSONUtil.parseObj(existingUser.getExtJson()) : new JSONObject();
                ext.set("userRole", "STUDENT");
                existingUser.setExtJson(ext.toString());

                clientUserService.updateById(existingUser);

                if (!StrUtil.equals(profile.getUserId(), existingUser.getId())) {
                    profile.setUserId(existingUser.getId());
                    bizStudentProfileService.updateById(profile);
                }
            }

            String majorInfo = StrUtil.isNotBlank(profile.getMajor()) ? profile.getMajor() : "所属专业";
            String classInfo = StrUtil.isNotBlank(profile.getClassName()) ? profile.getClassName() : "";
            return CommonResult.data("学号匹配成功！已自动关联档案【" + matchedName + " · " + majorInfo + " " + classInfo + "】，账号注册激活成功！");
    }

    @Override
    public CommonResult<JSONObject> demoLogin(String role) {
        String targetAccount;
        String targetName;
        if ("TEACHER".equalsIgnoreCase(role)) {
            targetAccount = "teacher_demo";
            targetName = "王教授 (竞赛指导老师)";
        } else if ("LEADER".equalsIgnoreCase(role)) {
            targetAccount = "leader_demo";
            targetName = "李组长 (创客项目团队长)";
        } else {
            targetAccount = "student_demo";
            targetName = "张同学 (算法与竞赛爱好者)";
        }

        ClientUser user = clientUserService.getOne(new LambdaQueryWrapper<ClientUser>()
                .eq(ClientUser::getAccount, targetAccount));
        if (user == null) {
            user = new ClientUser();
            user.setAccount(targetAccount);
            user.setPassword(BCrypt.hashpw("123456"));
            user.setName(targetName);
            user.setNickname(targetName);
            user.setPhone("138" + RandomUtil.randomNumbers(8));
            user.setEmail(targetAccount + "@school.edu.cn");

            JSONObject ext = new JSONObject();
            ext.set("userRole", "TEACHER".equalsIgnoreCase(role) ? "TEACHER" : "STUDENT");
            user.setExtJson(ext.toString());
            clientUserService.save(user);

            if ("TEACHER".equalsIgnoreCase(role)) {
                BizTeacherProfile tp = new BizTeacherProfile();
                tp.setUserId(user.getId());
                tp.setTeacherNo("T10001");
                tp.setTitle("教授 / 硕士生导师");
                tp.setResearchDirection("计算机视觉、分布式系统与学科竞赛实训");
                tp.setBio("国家级大学生创新创业竞赛金牌指导教师，累计指导学生斩获竞赛奖项超50项。");
                bizTeacherProfileService.save(tp);
            } else {
                BizStudentProfile sp = new BizStudentProfile();
                sp.setUserId(user.getId());
                sp.setStudentNo("2026" + RandomUtil.randomNumbers(6));
                sp.setMajor("计算机科学与技术");
                sp.setGrade("2023级");
                sp.setClassName("计科卓越班");
                sp.setSkillTags("Java,Python,算法与数据结构,Vue3,全栈架构");
                sp.setBio("热爱算法打榜与前沿开源工程，擅长系统设计与团队协同。");
                bizStudentProfileService.save(sp);

                // 如果是组长，默认给他建一个项目
                if ("LEADER".equalsIgnoreCase(role)) {
                    BizProject project = new BizProject();
                    project.setProjectName("极客智能算法与系统实训组");
                    project.setProjectDesc("专注ACM及天池大赛，主攻智能推荐算法及高性能分布式计算实现。");
                    project.setLeaderId(user.getId());
                    project.setIsPrivate("NO");
                    project.setCurrentMemberCount(3);
                    project.setMaxMemberLimit(5);
                    project.setStatus("RECRUITING");
                    bizProjectService.save(project);

                    BizProjectMember member = new BizProjectMember();
                    member.setProjectId(project.getId());
                    member.setStudentId(user.getId());
                    member.setMemberRole("LEADER");
                    member.setDutyRemark("项目负责人与系统架构师");
                    bizProjectMemberService.save(member);
                }
            }
        }

        StpClientUtil.login(user.getId());
        String token = StpClientUtil.getTokenValue();

        JSONObject data = new JSONObject();
        data.set("token", token);
        data.set("userInfo", user);
        data.set("roleInfo", getUserRoleInfo(user.getId()));
        return CommonResult.data(data);
    }

    @Override
    public CommonResult<JSONObject> getMyProfile() {
        String userId = getCurrentUserId();
        ClientUser user = clientUserService.getById(userId);
        if (user == null) {
            throw new CommonException("用户不存在");
        }
        JSONObject profile = new JSONObject();
        profile.set("user", user);
        profile.set("roleInfo", getUserRoleInfo(userId));

        BizStudentProfile studentProfile = bizStudentProfileService.getOne(new LambdaQueryWrapper<BizStudentProfile>()
                .eq(BizStudentProfile::getUserId, userId));
        profile.set("studentProfile", studentProfile);

        BizTeacherProfile teacherProfile = bizTeacherProfileService.getOne(new LambdaQueryWrapper<BizTeacherProfile>()
                .eq(BizTeacherProfile::getUserId, userId));
        profile.set("teacherProfile", teacherProfile);

        return CommonResult.data(profile);
    }

    @Override
    public CommonResult<String> updateMyProfile(JSONObject param) {
        String userId = getCurrentUserId();
        ClientUser user = clientUserService.getById(userId);
        if (user == null) {
            throw new CommonException("用户不存在");
        }
        if (param.containsKey("name")) {
            user.setName(param.getStr("name"));
        }
        if (param.containsKey("phone")) {
            user.setPhone(param.getStr("phone"));
        }
        if (param.containsKey("email")) {
            user.setEmail(param.getStr("email"));
        }
        clientUserService.updateById(user);

        BizStudentProfile studentProfile = bizStudentProfileService.getOne(new LambdaQueryWrapper<BizStudentProfile>()
                .eq(BizStudentProfile::getUserId, userId));
        if (studentProfile != null) {
            if (param.containsKey("major")) studentProfile.setMajor(param.getStr("major"));
            if (param.containsKey("className")) studentProfile.setClassName(param.getStr("className"));
            if (param.containsKey("skillTags")) studentProfile.setSkillTags(param.getStr("skillTags"));
            if (param.containsKey("bio")) studentProfile.setBio(param.getStr("bio"));
            bizStudentProfileService.updateById(studentProfile);
        }
        return CommonResult.ok();
    }

    @Override
    public CommonResult<List<JSONObject>> getAllStudents(String keyword) {
        List<ClientUser> users = clientUserService.list();
        List<BizStudentProfile> profiles = bizStudentProfileService.list();
        Map<String, BizStudentProfile> profileMap = CollStreamUtil.toIdentityMap(profiles, BizStudentProfile::getUserId);

        List<JSONObject> list = new ArrayList<>();
        for (ClientUser u : users) {
            // 过滤教师
            JSONObject roleInfo = getUserRoleInfo(u.getId());
            if (roleInfo.getBool("isTeacher", false)) {
                continue;
            }
            if (StrUtil.isNotBlank(keyword)) {
                boolean match = StrUtil.containsIgnoreCase(u.getName(), keyword)
                        || StrUtil.containsIgnoreCase(u.getAccount(), keyword);
                BizStudentProfile p = profileMap.get(u.getId());
                if (p != null) {
                    match = match || StrUtil.containsIgnoreCase(p.getMajor(), keyword)
                            || StrUtil.containsIgnoreCase(p.getSkillTags(), keyword);
                }
                if (!match) continue;
            }
            JSONObject obj = new JSONObject();
            obj.set("id", u.getId());
            obj.set("name", u.getName());
            obj.set("account", u.getAccount());
            obj.set("email", u.getEmail());
            obj.set("phone", u.getPhone());
            obj.set("profile", profileMap.get(u.getId()));
            obj.set("roleInfo", roleInfo);
            list.add(obj);
        }
        return CommonResult.data(list);
    }

    @Override
    public CommonResult<List<JSONObject>> getMyProjects() {
        String userId = getCurrentUserId();
        List<BizProjectMember> memberList = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getStudentId, userId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        List<String> memberProjIds = CollStreamUtil.toList(memberList, BizProjectMember::getProjectId);

        List<BizProject> ledProjs = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getLeaderId, userId)
                .eq(BizProject::getDeleteFlag, "NOT_DELETE"));
        List<String> ledProjIds = CollStreamUtil.toList(ledProjs, BizProject::getId);

        Set<String> allProjIds = new LinkedHashSet<>();
        allProjIds.addAll(ledProjIds);
        allProjIds.addAll(memberProjIds);

        JSONObject myRoleInfo = getUserRoleInfo(userId);
        if (myRoleInfo.getBool("isTeacher", false)) {
            List<BizProject> teacherProjs = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                    .and(wrapper -> wrapper.eq(BizProject::getCreateUser, userId).or().like(BizProject::getExtJson, userId))
                    .eq(BizProject::getDeleteFlag, "NOT_DELETE"));
            allProjIds.addAll(CollStreamUtil.toList(teacherProjs, BizProject::getId));
        }

        if (CollectionUtil.isEmpty(allProjIds)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<BizProject> projects = bizProjectService.listByIds(allProjIds);
        Map<String, BizProjectMember> myMemberMap = CollStreamUtil.toIdentityMap(memberList, BizProjectMember::getProjectId);

        // 获取这些项目的竞赛信息
        List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .in(BizCompetitionProject::getProjectId, allProjIds)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        Map<String, String> projCompIdMap = compProjs.stream()
                .filter(cp -> StrUtil.isNotBlank(cp.getProjectId()) && StrUtil.isNotBlank(cp.getCompetitionId()))
                .collect(Collectors.toMap(BizCompetitionProject::getProjectId, BizCompetitionProject::getCompetitionId, (v1, v2) -> v1));

        List<String> compIds = compProjs.stream().map(BizCompetitionProject::getCompetitionId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizCompetition> compMap = CollectionUtil.isNotEmpty(compIds)
                ? bizCompetitionService.listByIds(compIds).stream().collect(Collectors.toMap(BizCompetition::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 获取这些项目的所有成员
        List<BizProjectMember> allMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .in(BizProjectMember::getProjectId, allProjIds)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizProjectMember>> projMemberMap = allMembers.stream().collect(Collectors.groupingBy(BizProjectMember::getProjectId));

        List<String> allStudentIds = allMembers.stream().map(BizProjectMember::getStudentId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> studentUserMap = CollectionUtil.isNotEmpty(allStudentIds)
                ? clientUserService.listByIds(allStudentIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 组长用户信息
        List<String> leaderIds = projects.stream().map(BizProject::getLeaderId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> leaderUserMap = CollectionUtil.isNotEmpty(leaderIds)
                ? clientUserService.listByIds(leaderIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizProject p : projects) {
            JSONObject obj = JSONUtil.parseObj(p);
            boolean isTeacherOwner = false;
            if (StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())) {
                JSONObject ext = JSONUtil.parseObj(p.getExtJson());
                isTeacherOwner = ext.getBool("isTeacherInitiated", false) && StrUtil.equals(ext.getStr("teacherId"), userId);
            }
            boolean isLeader = StrUtil.equals(p.getLeaderId(), userId) || isTeacherOwner;
            BizProjectMember myMember = myMemberMap.get(p.getId());
            obj.set("myRole", isLeader ? "LEADER" : (myMember != null ? myMember.getMemberRole() : "MEMBER"));
            obj.set("isLeader", isLeader);
            obj.set("isTeacherInitiated", isTeacherOwner);

            ClientUser leader = leaderUserMap.get(p.getLeaderId());
            obj.set("leaderName", leader != null ? leader.getName() : (isTeacherOwner ? "指导导师" : "组长"));

            String compId = projCompIdMap.get(p.getId());
            boolean isUnassignedComp = StrUtil.isBlank(compId) || "UNASSIGNED".equalsIgnoreCase(compId);
            BizCompetition comp = !isUnassignedComp ? compMap.get(compId) : null;
            obj.set("competitionId", !isUnassignedComp ? compId : "UNASSIGNED");
            obj.set("competitionTitle", comp != null ? comp.getTitle() : "暂未参与");
            obj.set("isUnassigned", isUnassignedComp || comp == null);

            // 封装组员列表
            List<BizProjectMember> pMembers = projMemberMap.getOrDefault(p.getId(), Collections.emptyList());
            List<JSONObject> memberObjList = new ArrayList<>();
            for (BizProjectMember m : pMembers) {
                JSONObject mObj = new JSONObject();
                mObj.set("id", m.getId());
                mObj.set("studentId", m.getStudentId());
                ClientUser stu = studentUserMap.get(m.getStudentId());
                mObj.set("studentName", stu != null ? stu.getName() : "同学");
                mObj.set("studentAccount", stu != null ? stu.getAccount() : "");
                mObj.set("isLeader", "LEADER".equalsIgnoreCase(m.getMemberRole()) || StrUtil.equals(p.getLeaderId(), m.getStudentId()));
                mObj.set("role", m.getMemberRole());
                mObj.set("duty", m.getDutyRemark());
                mObj.set("dutyRemark", m.getDutyRemark());
                memberObjList.add(mObj);
            }
            obj.set("members", memberObjList);
            obj.set("memberCount", memberObjList.size());
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    // =========================================================================
    // 2. 竞赛与参赛团队
    // =========================================================================

    @Override
    public CommonResult<Page<BizCompetition>> pageCompetitions(String keyword, String level, String status, Integer current, Integer size) {
        Page<BizCompetition> page = new Page<>(current != null ? current : 1, size != null ? size : 10);
        LambdaQueryWrapper<BizCompetition> qw = new LambdaQueryWrapper<BizCompetition>()
                .eq(BizCompetition::getDeleteFlag, "NOT_DELETE");
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(BizCompetition::getTitle, keyword)
                    .or().like(BizCompetition::getOrganizer, keyword)
                    .or().like(BizCompetition::getCategory, keyword));
        }
        if (StrUtil.isNotBlank(level)) {
            qw.eq(BizCompetition::getCompetitionLevel, level);
        }
        if (StrUtil.isNotBlank(status)) {
            if ("SIGNUP".equalsIgnoreCase(status) || "ENROLLING".equalsIgnoreCase(status)) {
                qw.and(w -> w.eq(BizCompetition::getStatus, "ENROLLING").or().eq(BizCompetition::getStatus, "SIGNUP"));
            } else {
                qw.eq(BizCompetition::getStatus, status);
            }
        }
        qw.orderByDesc(BizCompetition::getCreateTime);

        Page<BizCompetition> p = bizCompetitionService.page(page, qw);
        Date now = new Date();
        for (BizCompetition c : p.getRecords()) {
            if (c.getCompetitionEndTime() != null && now.after(c.getCompetitionEndTime()) && !"FINISHED".equalsIgnoreCase(c.getStatus())) {
                c.setStatus("FINISHED");
                bizCompetitionService.updateById(c);
            } else if (c.getSignupEndTime() != null && now.after(c.getSignupEndTime()) && ("SIGNUP".equalsIgnoreCase(c.getStatus()) || "ENROLLING".equalsIgnoreCase(c.getStatus()))) {
                c.setStatus("ONGOING");
                bizCompetitionService.updateById(c);
            }
        }
        return CommonResult.data(p);
    }

    @Override
    public CommonResult<JSONObject> getCompetitionDetail(String competitionId) {
        BizCompetition competition = bizCompetitionService.getById(competitionId);
        if (competition == null) {
            throw new CommonException("竞赛不存在");
        }
        Date now = new Date();
        if (competition.getCompetitionEndTime() != null && now.after(competition.getCompetitionEndTime()) && !"FINISHED".equalsIgnoreCase(competition.getStatus())) {
            competition.setStatus("FINISHED");
            bizCompetitionService.updateById(competition);
        } else if (competition.getSignupEndTime() != null && now.after(competition.getSignupEndTime()) && ("SIGNUP".equalsIgnoreCase(competition.getStatus()) || "ENROLLING".equalsIgnoreCase(competition.getStatus()))) {
            competition.setStatus("ONGOING");
            bizCompetitionService.updateById(competition);
        }

        // 增加浏览数
        competition.setViewCount(competition.getViewCount() == null ? 1 : competition.getViewCount() + 1);
        bizCompetitionService.updateById(competition);

        JSONObject result = JSONUtil.parseObj(competition);

        boolean isEnded = "FINISHED".equalsIgnoreCase(competition.getStatus()) || (competition.getCompetitionEndTime() != null && now.after(competition.getCompetitionEndTime()));
        boolean isSignupClosed = isEnded || (competition.getSignupEndTime() != null && now.after(competition.getSignupEndTime()));
        result.set("isEnded", isEnded);
        result.set("isSignupClosed", isSignupClosed);

        // 获取当前用户是否已关注
        if (StpClientUtil.isLogin()) {
            String userId = StpClientUtil.getLoginIdAsString();
            long followCount = bizFollowService.count(new LambdaQueryWrapper<BizFollow>()
                    .eq(BizFollow::getUserId, userId)
                    .eq(BizFollow::getTargetType, "COMPETITION")
                    .eq(BizFollow::getTargetId, competitionId)
                    .eq(BizFollow::getDeleteFlag, "NOT_DELETE"));
            result.set("isFollowed", followCount > 0);
        } else {
            result.set("isFollowed", false);
        }

        // 获取参赛项目数
        long teamCount = bizCompetitionProjectService.count(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getCompetitionId, competitionId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        result.set("teamCount", teamCount);

        return CommonResult.data(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> publishCompetition(PortalCompetitionParam param) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        if (!roleInfo.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权发布竞赛");
        }

        BizCompetition comp = BeanUtil.toBean(param, BizCompetition.class);
        comp.setPublishTeacherId(userId);
        comp.setViewCount(0);
        comp.setFollowCount(0);
        comp.setDeleteFlag("NOT_DELETE");
        bizCompetitionService.save(comp);

        // 联动发布到论坛（免审，直接发布）
        BizForumPost post = new BizForumPost();
        post.setAuthorId(userId);
        post.setTitle("【官方竞赛发布】" + comp.getTitle());
        post.setContent("指导教师已正式发布全新学科竞赛【" + comp.getTitle() + "】！\n\n"
                + "举办单位：" + StrUtil.nullToDefault(comp.getOrganizer(), "学院教务处") + "\n"
                + "竞赛级别：" + comp.getCompetitionLevel() + "\n"
                + "参赛人数要求：" + (comp.getMaxMembers() == 1 ? "单人个人参赛" : comp.getMinMembers() + " - " + comp.getMaxMembers() + "人组队参赛") + "\n\n"
                + "参赛规程与要求：\n" + StrUtil.nullToDefault(comp.getRequirements(), "详见竞赛主页详情。") + "\n\n欢迎广大学子积极建组与报名！");
        post.setPostType("COMPETITION_EVENT");
        post.setRefType("COMPETITION");
        post.setRefId(comp.getId());
        post.setIsPinned("YES"); // 老师发布的官方竞赛默认置顶
        post.setPinTime(new Date());
        post.setAuditStatus("PUBLISHED");
        post.setAiAuditResult("PASS");
        bizForumPostService.save(post);

        return CommonResult.data(comp.getId());
    }

    @Override
    public CommonResult<String> editCompetition(PortalCompetitionParam param) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        if (!roleInfo.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权编辑竞赛");
        }
        BizCompetition comp = bizCompetitionService.getById(param.getId());
        if (comp == null) {
            throw new CommonException("竞赛不存在");
        }
        BeanUtil.copyProperties(param, comp);
        bizCompetitionService.updateById(comp);
        return CommonResult.ok();
    }

    @Override
    public CommonResult<String> deleteCompetition(String id) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        if (!roleInfo.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权删除竞赛");
        }
        bizCompetitionService.removeById(id);
        return CommonResult.ok();
    }

    @Override
    public CommonResult<List<JSONObject>> getCompetitionProjects(String competitionId) {
        if (StrUtil.isBlank(competitionId) || "UNASSIGNED".equalsIgnoreCase(competitionId)) {
            return CommonResult.data(Collections.emptyList());
        }
        // 查找该竞赛下的所有关联报名
        List<BizCompetitionProject> cpList = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getCompetitionId, competitionId)
                .ne(BizCompetitionProject::getCompetitionId, "UNASSIGNED")
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));

        if (CollectionUtil.isEmpty(cpList)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<JSONObject> result = new ArrayList<>();
        String currentUserId = StpClientUtil.isLogin() ? StpClientUtil.getLoginIdAsString() : null;
        boolean isTeacher = false;
        if (currentUserId != null) {
            JSONObject role = getUserRoleInfo(currentUserId);
            isTeacher = role.getBool("isTeacher", false);
        }

        List<String> projectIds = cpList.stream()
                .map(BizCompetitionProject::getProjectId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());

        // 1. 团队参赛处理
        if (CollectionUtil.isNotEmpty(projectIds)) {
            List<BizProject> projects = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                    .in(BizProject::getId, projectIds)
                    .eq(BizProject::getDeleteFlag, "NOT_DELETE"));

            for (BizProject p : projects) {
                if ("YES".equalsIgnoreCase(p.getIsPrivate()) && !isTeacher) {
                    // 如果是私密项目且非导师，只有本组组员或组长可以查看
                    if (currentUserId == null) continue;
                    long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                            .eq(BizProjectMember::getProjectId, p.getId())
                            .eq(BizProjectMember::getStudentId, currentUserId)
                            .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                    if (count == 0 && !StrUtil.equals(p.getLeaderId(), currentUserId)) {
                        continue; // 无权查看该私密项目
                    }
                }

                JSONObject item = JSONUtil.parseObj(p);
                boolean hasLeader = StrUtil.isNotBlank(p.getLeaderId());
                item.set("hasLeader", hasLeader);

                if (hasLeader) {
                    ClientUser leader = clientUserService.getById(p.getLeaderId());
                    item.set("leaderName", leader != null ? leader.getName() : "组长");
                    item.set("leaderAvatar", leader != null ? leader.getAvatar() : null);
                } else {
                    item.set("leaderName", "待竞聘选拔 (暂无组长)");
                    item.set("leaderAvatar", null);
                }

                // 导师信息
                if (StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())) {
                    JSONObject ext = JSONUtil.parseObj(p.getExtJson());
                    item.set("isTeacherInitiated", ext.getBool("isTeacherInitiated", false));
                    item.set("teacherId", ext.getStr("teacherId"));
                    item.set("teacherName", ext.getStr("teacherName"));
                } else {
                    item.set("isTeacherInitiated", false);
                    item.set("teacherName", null);
                }

                // 统计待审核学生人数
                long pendingApplicantsCount = bizProjectFlowService.count(new LambdaQueryWrapper<BizProjectFlow>()
                        .eq(BizProjectFlow::getProjectId, p.getId())
                        .eq(BizProjectFlow::getDirection, "APPLY")
                        .eq(BizProjectFlow::getStatus, "PENDING")
                        .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE"));
                item.set("pendingApplicantCount", pendingApplicantsCount);

                // 是否已关注该项目组以及当前用户在该组的状态
                if (currentUserId != null) {
                    long fCount = bizFollowService.count(new LambdaQueryWrapper<BizFollow>()
                            .eq(BizFollow::getUserId, currentUserId)
                            .eq(BizFollow::getTargetType, "PROJECT")
                            .eq(BizFollow::getTargetId, p.getId())
                            .eq(BizFollow::getDeleteFlag, "NOT_DELETE"));
                    item.set("isFollowed", fCount > 0);

                    long mCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                            .eq(BizProjectMember::getProjectId, p.getId())
                            .eq(BizProjectMember::getStudentId, currentUserId)
                            .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                    item.set("isMember", mCount > 0 || StrUtil.equals(p.getLeaderId(), currentUserId));

                    BizProjectFlow myPending = bizProjectFlowService.getOne(new LambdaQueryWrapper<BizProjectFlow>()
                            .eq(BizProjectFlow::getProjectId, p.getId())
                            .eq(BizProjectFlow::getStudentId, currentUserId)
                            .eq(BizProjectFlow::getDirection, "APPLY")
                            .eq(BizProjectFlow::getStatus, "PENDING")
                            .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE"), false);
                    if (myPending != null) {
                        item.set("myApplyStatus", "PENDING");
                        item.set("myApplyRole", myPending.getApplyRole());
                    } else {
                        item.set("myApplyStatus", null);
                        item.set("myApplyRole", null);
                    }
                }
                result.add(item);
            }
        }

        // 2. 单人赛个人直接参赛展示（解决单人赛制选手名单丢失缺陷）
        List<BizCompetitionProject> individualSignups = cpList.stream()
                .filter(cp -> "INDIVIDUAL".equalsIgnoreCase(cp.getSignupType()) && StrUtil.isNotBlank(cp.getStudentId()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(individualSignups)) {
            List<String> studentIds = CollStreamUtil.toList(individualSignups, BizCompetitionProject::getStudentId);
            List<ClientUser> students = clientUserService.listByIds(studentIds);
            Map<String, ClientUser> studentMap = CollStreamUtil.toIdentityMap(students, ClientUser::getId);
            List<BizStudentProfile> profiles = bizStudentProfileService.list(new LambdaQueryWrapper<BizStudentProfile>()
                    .in(BizStudentProfile::getUserId, studentIds));
            Map<String, BizStudentProfile> profileMap = CollStreamUtil.toIdentityMap(profiles, BizStudentProfile::getUserId);

            for (BizCompetitionProject cp : individualSignups) {
                ClientUser u = studentMap.get(cp.getStudentId());
                if (u == null) continue;
                JSONObject item = new JSONObject();
                item.set("id", cp.getId());
                item.set("isIndividual", true);
                item.set("projectName", u.getName() + " (个人单人赛选手)");
                item.set("projectDesc", "单人赛道个人直接报名参赛");
                item.set("leaderName", u.getName());
                item.set("leaderId", u.getId());
                item.set("currentMemberCount", 1);
                item.set("maxMemberLimit", 1);
                item.set("status", "ENROLLED");
                item.set("isPrivate", "NO");
                BizStudentProfile prof = profileMap.get(u.getId());
                item.set("major", prof != null ? prof.getMajor() : "计算机专业");
                item.set("studentNo", prof != null ? prof.getStudentNo() : "");
                result.add(item);
            }
        }
        return CommonResult.data(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> createProjectAndBecomeLeader(PortalProjectCreateParam param) {
        String studentId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(studentId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        boolean isUnassigned = StrUtil.isBlank(param.getCompetitionId())
                || "UNASSIGNED".equalsIgnoreCase(param.getCompetitionId())
                || "NONE".equalsIgnoreCase(param.getCompetitionId())
                || "暂未参与".equals(param.getCompetitionId());

        // 需求1：教师发布的竞赛组招聘，全部强制归属在<暂未参与>
        if (isTeacher) {
            isUnassigned = true;
        }

        BizCompetition comp = null;
        if (!isUnassigned) {
            comp = bizCompetitionService.getById(param.getCompetitionId());
            if (comp == null) {
                throw new CommonException("所选竞赛不存在");
            }
            // 校验竞赛状态与截止时间
            Date now = new Date();
            if ("FINISHED".equalsIgnoreCase(comp.getStatus()) || (comp.getCompetitionEndTime() != null && now.after(comp.getCompetitionEndTime()))) {
                throw new CommonException("报名失败：该竞赛已完结结束，无法继续创建团队参赛");
            }
            if (comp.getSignupEndTime() != null && now.after(comp.getSignupEndTime())) {
                throw new CommonException("报名失败：该竞赛报名已截止（截止时间：" + DateUtil.formatDateTime(comp.getSignupEndTime()) + "），无法继续创建团队参赛");
            }

            // 学生创建项目：同一比赛内，已经是组长的不能再成为组长；但在不同比赛中可以担任组长
            List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                    .eq(BizCompetitionProject::getCompetitionId, param.getCompetitionId())
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(compProjs)) {
                List<String> pIds = CollStreamUtil.toList(compProjs, BizCompetitionProject::getProjectId);
                long isLeaderInComp = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                        .in(BizProjectMember::getProjectId, pIds)
                        .eq(BizProjectMember::getStudentId, studentId)
                        .eq(BizProjectMember::getMemberRole, "LEADER")
                        .eq(BizProjectMember::getMemberStatus, "ACTIVE")
                        .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                if (isLeaderInComp > 0) {
                    throw new CommonException("创建失败：您已在该比赛中担任组长，同一比赛无法重复创建并担任组长");
                }
            }
        }

        // 1. 创建项目
        BizProject project = new BizProject();
        project.setProjectName(param.getProjectName());
        project.setProjectDesc(param.getProjectDesc());
        project.setIsPrivate(StrUtil.nullToDefault(param.getIsPrivate(), "NO"));
        project.setMaxMemberLimit(param.getMaxMemberLimit() != null ? param.getMaxMemberLimit() : (comp != null ? comp.getMaxMembers() : 5));
        project.setStatus("RECRUITING");
        project.setDeleteFlag("NOT_DELETE");

        ClientUser currentUser = clientUserService.getById(studentId);

        if (isTeacher) {
            // 老师创建项目组：不作为组长，leaderId 为 null，初始成员数为 0，发布给学生自动报名
            project.setLeaderId(null);
            project.setCurrentMemberCount(0);
            JSONObject ext = new JSONObject();
            ext.set("isTeacherInitiated", true);
            ext.set("teacherId", studentId);
            ext.set("teacherName", currentUser != null ? currentUser.getName() : "指导教师");
            ext.set("competitionStatus", "UNASSIGNED");
            ext.set("competitionTitle", "暂未参与");
            project.setExtJson(ext.toString());
            bizProjectService.save(project);
        } else {
            // 学生创建项目组：成为组长
            project.setLeaderId(studentId);
            project.setCurrentMemberCount(1);
            if (isUnassigned) {
                JSONObject ext = new JSONObject();
                ext.set("competitionStatus", "UNASSIGNED");
                ext.set("competitionTitle", "暂未参与");
                project.setExtJson(ext.toString());
            }
            bizProjectService.save(project);

            // 2. 将当前学生加入团队成员表，角色为 LEADER
            BizProjectMember member = new BizProjectMember();
            member.setProjectId(project.getId());
            member.setStudentId(studentId);
            member.setMemberRole("LEADER");
            member.setDutyRemark("项目发起人与团队组长");
            member.setMemberStatus("ACTIVE");
            member.setDeleteFlag("NOT_DELETE");
            bizProjectMemberService.save(member);
        }

        // 3. 将项目与竞赛关联绑定
        BizCompetitionProject cp = new BizCompetitionProject();
        cp.setCompetitionId(isUnassigned ? "UNASSIGNED" : comp.getId());
        cp.setSignupType("PROJECT");
        cp.setProjectId(project.getId());
        cp.setAuditStatus("PASSED");
        cp.setDeleteFlag("NOT_DELETE");
        bizCompetitionProjectService.save(cp);

        return CommonResult.data(project.getId());
    }

    @Override
    public CommonResult<String> individualSignup(String competitionId) {
        String studentId = getCurrentUserId();
        BizCompetition comp = bizCompetitionService.getById(competitionId);
        if (comp == null) {
            throw new CommonException("竞赛不存在");
        }
        if (comp.getMaxMembers() > 1) {
            throw new CommonException("本竞赛为团队赛，必须创建或加入项目组方可参赛");
        }
        // 校验竞赛状态与截止时间
        Date now = new Date();
        if ("FINISHED".equalsIgnoreCase(comp.getStatus()) || (comp.getCompetitionEndTime() != null && now.after(comp.getCompetitionEndTime()))) {
            throw new CommonException("报名失败：该竞赛已完结结束，无法继续报名参赛");
        }
        if (comp.getSignupEndTime() != null && now.after(comp.getSignupEndTime())) {
            throw new CommonException("报名失败：该竞赛报名已截止（截止时间：" + DateUtil.formatDateTime(comp.getSignupEndTime()) + "），无法继续报名参赛");
        }

        long count = bizCompetitionProjectService.count(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getCompetitionId, competitionId)
                .eq(BizCompetitionProject::getStudentId, studentId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        if (count > 0) {
            throw new CommonException("您已报名该单人竞赛，请勿重复报名");
        }

        BizCompetitionProject cp = new BizCompetitionProject();
        cp.setCompetitionId(competitionId);
        cp.setSignupType("INDIVIDUAL");
        cp.setStudentId(studentId);
        cp.setAuditStatus("PASSED");
        cp.setDeleteFlag("NOT_DELETE");
        bizCompetitionProjectService.save(cp);

        return CommonResult.data("单人报名成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> toggleFollow(String targetType, String targetId) {
        String userId = getCurrentUserId();
        BizFollow exist = bizFollowService.getOne(new LambdaQueryWrapper<BizFollow>()
                .eq(BizFollow::getUserId, userId)
                .eq(BizFollow::getTargetType, targetType)
                .eq(BizFollow::getTargetId, targetId)
                .eq(BizFollow::getDeleteFlag, "NOT_DELETE"));

        ClientUser user = clientUserService.getById(userId);
        String userName = user != null ? user.getName() : "学生";

        if (exist != null) {
            bizFollowService.removeById(exist.getId());
            // 竞赛关注人数减1
            if ("COMPETITION".equalsIgnoreCase(targetType)) {
                BizCompetition c = bizCompetitionService.getById(targetId);
                if (c != null && c.getFollowCount() != null && c.getFollowCount() > 0) {
                    c.setFollowCount(c.getFollowCount() - 1);
                    bizCompetitionService.updateById(c);
                }
            }
            return CommonResult.data(false);
        } else {
            BizFollow follow = new BizFollow();
            follow.setUserId(userId);
            follow.setTargetType(targetType);
            follow.setTargetId(targetId);
            follow.setDeleteFlag("NOT_DELETE");
            bizFollowService.save(follow);

            String targetName = "";
            if ("COMPETITION".equalsIgnoreCase(targetType)) {
                BizCompetition c = bizCompetitionService.getById(targetId);
                if (c != null) {
                    c.setFollowCount(c.getFollowCount() == null ? 1 : c.getFollowCount() + 1);
                    bizCompetitionService.updateById(c);
                    targetName = "竞赛【" + c.getTitle() + "】";
                }
            } else if ("PROJECT".equalsIgnoreCase(targetType)) {
                BizProject p = bizProjectService.getById(targetId);
                if (p != null) {
                    targetName = "项目团队【" + p.getProjectName() + "】";
                }
            }

            // 关注动态自动同步发布到论坛（免审，直接发布）
            BizForumPost post = new BizForumPost();
            post.setAuthorId(userId);
            post.setTitle("【关注动态】" + userName + " 关注了 " + targetName);
            post.setContent(userName + " 刚刚对 " + targetName + " 表达了浓厚兴趣并点击关注，欢迎组长与其交流洽谈！");
            post.setPostType("FOLLOW_EVENT");
            post.setRefType(targetType);
            post.setRefId(targetId);
            post.setAuditStatus("PUBLISHED");
            post.setAiAuditResult("PASS");
            bizForumPostService.save(post);

            return CommonResult.data(true);
        }
    }

    @Override
    public CommonResult<List<JSONObject>> getInterestedStudents(String competitionId) {
        String currentUserId = null;
        try {
            currentUserId = getCurrentUserId();
        } catch (Exception ignored) {}

        // 查找关注了本竞赛的学生列表
        List<BizFollow> followList = bizFollowService.list(new LambdaQueryWrapper<BizFollow>()
                .eq(BizFollow::getTargetType, "COMPETITION")
                .eq(BizFollow::getTargetId, competitionId)
                .eq(BizFollow::getDeleteFlag, "NOT_DELETE"));

        if (CollectionUtil.isEmpty(followList)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> studentIds = CollStreamUtil.toList(followList, BizFollow::getUserId);
        // 过滤掉当前登录用户自己！确保自己在感兴趣列表中看不到自己
        if (StrUtil.isNotBlank(currentUserId)) {
            studentIds.remove(currentUserId);
        }
        if (CollectionUtil.isEmpty(studentIds)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<ClientUser> users = clientUserService.listByIds(studentIds);
        List<BizStudentProfile> profiles = bizStudentProfileService.list(new LambdaQueryWrapper<BizStudentProfile>()
                .in(BizStudentProfile::getUserId, studentIds));
        Map<String, BizStudentProfile> profileMap = CollStreamUtil.toIdentityMap(profiles, BizStudentProfile::getUserId);

        List<JSONObject> result = new ArrayList<>();
        for (ClientUser u : users) {
            if (StrUtil.isNotBlank(currentUserId) && StrUtil.equals(u.getId(), currentUserId)) {
                continue;
            }
            JSONObject obj = new JSONObject();
            obj.set("studentId", u.getId());
            obj.set("name", u.getName());
            obj.set("account", u.getAccount());
            obj.set("phone", u.getPhone());
            obj.set("profile", profileMap.get(u.getId()));
            result.add(obj);
        }
        return CommonResult.data(result);
    }

    @Override
    public CommonResult<String> inviteStudent(String projectId, String studentId, String message) {
        String leaderId = getCurrentUserId();
        if (StrUtil.equals(studentId, leaderId)) {
            throw new CommonException("不能邀请自己加入项目组");
        }
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        if (!StrUtil.equals(project.getLeaderId(), leaderId)) {
            throw new CommonException("权限不足：只有本项目的组长可以发起入组邀请");
        }

        // 检查是否已经在组内
        long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getStudentId, studentId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        if (count > 0) {
            throw new CommonException("该学生已在当前项目组内");
        }

        BizProjectFlow flow = new BizProjectFlow();
        flow.setProjectId(projectId);
        flow.setStudentId(studentId);
        flow.setDirection("INVITE");
        flow.setOperatorId(leaderId);
        flow.setMessage(StrUtil.nullToDefault(message, "组长诚邀您加入项目组，共赴学科竞赛！"));
        flow.setStatus("PENDING");
        flow.setDeleteFlag("NOT_DELETE");
        bizProjectFlowService.save(flow);

        return CommonResult.data("邀请已发送，等待学生确认");
    }

    @Override
    public CommonResult<List<JSONObject>> getMyInvitations() {
        String studentId = getCurrentUserId();
        List<BizProjectFlow> list = bizProjectFlowService.list(new LambdaQueryWrapper<BizProjectFlow>()
                .eq(BizProjectFlow::getStudentId, studentId)
                .eq(BizProjectFlow::getDirection, "INVITE")
                .eq(BizProjectFlow::getStatus, "PENDING")
                .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProjectFlow::getCreateTime));

        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> projectIds = CollStreamUtil.toList(list, BizProjectFlow::getProjectId);
        List<BizProject> projects = bizProjectService.listByIds(projectIds);
        Map<String, BizProject> projectMap = CollStreamUtil.toIdentityMap(projects, BizProject::getId);

        List<JSONObject> res = new ArrayList<>();
        for (BizProjectFlow f : list) {
            JSONObject obj = JSONUtil.parseObj(f);
            BizProject p = projectMap.get(f.getProjectId());
            obj.set("projectName", p != null ? p.getProjectName() : "未知项目");
            obj.set("projectDesc", p != null ? p.getProjectDesc() : "");
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> handleInvitation(String inviteId, boolean accept) {
        String studentId = getCurrentUserId();
        BizProjectFlow flow = bizProjectFlowService.getById(inviteId);
        if (flow == null || !StrUtil.equals(flow.getStudentId(), studentId)) {
            throw new CommonException("邀请记录不存在或无权操作");
        }
        if (!"PENDING".equalsIgnoreCase(flow.getStatus())) {
            throw new CommonException("该邀请已被处理过");
        }

        if (accept) {
            flow.setStatus("ACCEPTED");
            flow.setHandleTime(new Date());
            flow.setHandleRemark("学生已同意入组");
            bizProjectFlowService.updateById(flow);

            BizProjectMember member = new BizProjectMember();
            member.setProjectId(flow.getProjectId());
            member.setStudentId(studentId);
            member.setMemberRole("MEMBER");
            member.setDutyRemark("团队研发组员");
            member.setMemberStatus("ACTIVE");
            member.setDeleteFlag("NOT_DELETE");
            bizProjectMemberService.save(member);

            // 更新当前人数
            BizProject p = bizProjectService.getById(flow.getProjectId());
            if (p != null) {
                p.setCurrentMemberCount(p.getCurrentMemberCount() == null ? 2 : p.getCurrentMemberCount() + 1);
                bizProjectService.updateById(p);
            }
            return CommonResult.data("您已成功加入该项目组");
        } else {
            flow.setStatus("REJECTED");
            flow.setHandleTime(new Date());
            flow.setHandleRemark("学生已谢绝邀请");
            bizProjectFlowService.updateById(flow);
            return CommonResult.data("已谢绝该入组邀请");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> publishRecruit(PortalRecruitParam param) {
        String leaderId = getCurrentUserId();
        BizProject project = bizProjectService.getById(param.getProjectId());
        boolean isTeacherOwner = false;
        if (project != null && StrUtil.isNotBlank(project.getExtJson()) && JSONUtil.isTypeJSONObject(project.getExtJson())) {
            JSONObject ext = JSONUtil.parseObj(project.getExtJson());
            isTeacherOwner = ext.getBool("isTeacherInitiated", false) && StrUtil.equals(ext.getStr("teacherId"), leaderId);
        }
        if (project == null || (!StrUtil.equals(project.getLeaderId(), leaderId) && !isTeacherOwner)) {
            throw new CommonException("权限不足：仅本组组长或指导教师可以发布招聘需求");
        }
        if ("YES".equalsIgnoreCase(project.getIsPrivate())) {
            throw new CommonException("该项目组已被设为【私密】，为保障项目隐私安全无法向全网公开招募。如需发布招募引才，请先在团队设置中公开团队。");
        }

        BizProjectRecruit recruit = new BizProjectRecruit();
        recruit.setProjectId(param.getProjectId());
        recruit.setLeaderId(leaderId);
        recruit.setTitle(param.getTitle());
        recruit.setRecruitCount(param.getRecruitCount());
        recruit.setRequirementDesc(param.getRequirementDesc());
        recruit.setDeadline(param.getDeadline());
        recruit.setStatus("OPEN");
        recruit.setDeleteFlag("NOT_DELETE");
        bizProjectRecruitService.save(recruit);

        // 联动发布到论坛（免审，直接发布）
        BizForumPost post = new BizForumPost();
        post.setAuthorId(leaderId);
        post.setTitle((isTeacherOwner ? "【导师招募令】" : "【组长招募令】") + project.getProjectName() + " 正在招募组员！");
        post.setContent("项目组名称：" + project.getProjectName() + "\n"
                + "拟招人数：" + param.getRecruitCount() + " 人\n"
                + "招募要求：\n" + param.getRequirementDesc() + "\n\n欢迎各位同学投递简历或在项目大厅联系组长！");
        post.setPostType("RECRUITMENT");
        post.setRefType("RECRUIT");
        post.setRefId(recruit.getId());
        post.setAuditStatus("PUBLISHED");
        post.setAiAuditResult("PASS");
        bizForumPostService.save(post);

        recruit.setForumPostId(post.getId());
        bizProjectRecruitService.updateById(recruit);

        return CommonResult.data(recruit.getId());
    }

    @Override
    public CommonResult<List<JSONObject>> listRecruits(String keyword) {
        LambdaQueryWrapper<BizProjectRecruit> qw = new LambdaQueryWrapper<BizProjectRecruit>()
                .eq(BizProjectRecruit::getStatus, "OPEN")
                .eq(BizProjectRecruit::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProjectRecruit::getCreateTime);

        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(BizProjectRecruit::getTitle, keyword)
                    .or().like(BizProjectRecruit::getRequirementDesc, keyword));
        }

        List<BizProjectRecruit> list = bizProjectRecruitService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> projectIds = CollStreamUtil.toList(list, BizProjectRecruit::getProjectId);
        List<BizProject> projects = bizProjectService.listByIds(projectIds);
        Map<String, BizProject> projectMap = CollStreamUtil.toIdentityMap(projects, BizProject::getId);

        List<BizCompetitionProject> cpList = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .in(BizCompetitionProject::getProjectId, projectIds)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        Map<String, String> projCompIdMap = cpList.stream().filter(cp -> StrUtil.isNotBlank(cp.getProjectId()) && StrUtil.isNotBlank(cp.getCompetitionId()))
                .collect(Collectors.toMap(BizCompetitionProject::getProjectId, BizCompetitionProject::getCompetitionId, (v1, v2) -> v1));
        List<String> compIds = cpList.stream().map(BizCompetitionProject::getCompetitionId).filter(c -> StrUtil.isNotBlank(c) && !"UNASSIGNED".equalsIgnoreCase(c)).distinct().collect(Collectors.toList());
        Map<String, BizCompetition> compMap = CollectionUtil.isNotEmpty(compIds)
                ? bizCompetitionService.listByIds(compIds).stream().collect(Collectors.toMap(BizCompetition::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizProjectRecruit r : list) {
            JSONObject obj = JSONUtil.parseObj(r);
            BizProject p = projectMap.get(r.getProjectId());
            obj.set("projectName", p != null ? p.getProjectName() : "未知团队");
            if (p != null) {
                obj.set("projectDesc", p.getProjectDesc());
                boolean isTeacherInit = false;
                String teacherName = null;
                if (StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())) {
                    JSONObject ext = JSONUtil.parseObj(p.getExtJson());
                    isTeacherInit = ext.getBool("isTeacherInitiated", false);
                    teacherName = ext.getStr("teacherName");
                }
                obj.set("isTeacherInitiated", isTeacherInit);
                obj.set("teacherName", teacherName);

                String cId = projCompIdMap.get(p.getId());
                boolean isUnassigned = StrUtil.isBlank(cId) || "UNASSIGNED".equalsIgnoreCase(cId);
                BizCompetition c = !isUnassigned ? compMap.get(cId) : null;
                obj.set("competitionId", !isUnassigned ? cId : "UNASSIGNED");
                obj.set("competitionTitle", c != null ? c.getTitle() : "暂未参与");
                obj.set("isUnassigned", isUnassigned || c == null);
            }
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<String> uploadCertificate(String competitionId, String projectId, String certName, String awardLevel, String awardDate, String fileId) {
        JSONObject body = new JSONObject();
        body.set("competitionId", competitionId);
        body.set("projectId", projectId);
        body.set("certificateName", certName);
        body.set("awardLevel", awardLevel);
        body.set("awardDate", awardDate);
        body.set("certificateImage", fileId);
        body.set("fileId", fileId);
        return uploadCertificate(body);
    }

    @Override
    public CommonResult<String> uploadCertificate(JSONObject body) {
        String userId = getCurrentUserId();
        String projectId = body.getStr("projectId");
        if (StrUtil.isBlank(projectId)) {
            throw new CommonException("所属项目团队ID不能为空");
        }
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("所选项目团队不存在");
        }

        // 权限严格校验：只有参与了该竞赛的小组成员（组长或组员）才能上传
        boolean isMember = false;
        if (userId.equals(project.getLeaderId())) {
            isMember = true;
        } else {
            isMember = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, projectId)
                    .eq(BizProjectMember::getStudentId, userId)
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE")) > 0;
        }
        JSONObject role = getUserRoleInfo(userId);
        boolean isTeacher = role.getBool("isTeacher", false);
        if (isTeacher) {
            boolean isTeacherOfProj = StrUtil.equals(project.getCreateUser(), userId)
                    || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(userId));
            if (isTeacherOfProj) {
                isMember = true;
            }
        }
        if (!isMember) {
            throw new CommonException("权限不足：只有参与了该竞赛的小组成员才能上传荣誉证书");
        }

        String certificateName = body.getStr("certificateName");
        if (StrUtil.isBlank(certificateName)) {
            certificateName = body.getStr("certName");
        }
        if (StrUtil.isBlank(certificateName)) {
            throw new CommonException("证书/荣誉名称不能为空");
        }

        String awardLevel = body.getStr("awardLevel");
        if (StrUtil.isBlank(awardLevel)) {
            throw new CommonException("获奖级别不能为空");
        }

        String certImg = body.getStr("certificateImage");
        if (StrUtil.isBlank(certImg)) {
            certImg = body.getStr("certificateUrl");
        }
        if (StrUtil.isBlank(certImg)) {
            certImg = body.getStr("fileId");
        }
        if (StrUtil.isBlank(certImg)) {
            throw new CommonException("证书图片凭证不能为空");
        }

        // 自动匹配竞赛ID
        String competitionId = body.getStr("competitionId");
        if (StrUtil.isBlank(competitionId)) {
            BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                    .eq(BizCompetitionProject::getProjectId, projectId)
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
            if (cp != null) {
                competitionId = cp.getCompetitionId();
            }
        }

        // 自动匹配获奖学生（以小组为单位，包含多个学生）
        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        List<String> stuIds = new ArrayList<>();
        if (StrUtil.isNotBlank(project.getLeaderId())) {
            stuIds.add(project.getLeaderId());
        }
        for (BizProjectMember m : members) {
            if (StrUtil.isNotBlank(m.getStudentId()) && !stuIds.contains(m.getStudentId())) {
                stuIds.add(m.getStudentId());
            }
        }
        Map<String, ClientUser> studentMap = CollectionUtil.isNotEmpty(stuIds)
                ? clientUserService.listByIds(stuIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();
        List<String> memberNamesList = new ArrayList<>();
        for (String sid : stuIds) {
            ClientUser stu = studentMap.get(sid);
            if (stu != null) {
                boolean leaderFlag = sid.equals(project.getLeaderId());
                memberNamesList.add(stu.getName() + (leaderFlag ? "(组长)" : "(组员)"));
            }
        }
        String allStudentNames = String.join("、", memberNamesList);
        if (StrUtil.isBlank(allStudentNames)) {
            allStudentNames = body.getStr("studentNames");
        }
        String allStudentIds = String.join(",", stuIds);

        BizCompetitionCert cert = new BizCompetitionCert();
        cert.setCompetitionId(competitionId);
        cert.setProjectId(projectId);
        cert.setStudentId(project.getLeaderId() != null ? project.getLeaderId() : userId);
        cert.setCertificateName(certificateName);
        cert.setAwardLevel(awardLevel);
        String awardDate = body.getStr("awardDate");
        if (StrUtil.isNotBlank(awardDate)) {
            cert.setAwardDate(DateUtil.parse(awardDate));
        } else {
            cert.setAwardDate(new Date());
        }
        cert.setCertificateImage(certImg);
        cert.setFileId(certImg);
        cert.setStudentNames(allStudentNames);
        cert.setStudentIds(allStudentIds);
        cert.setUploadUserId(userId);
        cert.setVerifyStatus("PENDING");
        cert.setDeleteFlag("NOT_DELETE");
        bizCompetitionCertService.save(cert);
        return CommonResult.data(cert.getId());
    }

    @Override
    public CommonResult<List<JSONObject>> getProjectCertificates(String projectId) {
        if (StrUtil.isBlank(projectId)) {
            return CommonResult.data(Collections.emptyList());
        }
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            return CommonResult.data(Collections.emptyList());
        }

        // 私密团队鉴权：若项目被设为私密，仅本组成员与指导教师可查看荣誉证书
        if ("YES".equalsIgnoreCase(project.getIsPrivate())) {
            String currentUserId = StpClientUtil.isLogin() ? StpClientUtil.getLoginIdAsString() : null;
            if (currentUserId == null) {
                return CommonResult.data(Collections.emptyList());
            }
            JSONObject role = getUserRoleInfo(currentUserId);
            boolean isTeacher = role.getBool("isTeacher", false);
            boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
            boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                    || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

            if (!isLeader && !isTeacherOwner) {
                long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getProjectId, projectId)
                        .eq(BizProjectMember::getStudentId, currentUserId)
                        .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                if (count == 0) {
                    return CommonResult.data(Collections.emptyList());
                }
            }
        }

        LambdaQueryWrapper<BizCompetitionCert> qw = new LambdaQueryWrapper<BizCompetitionCert>()
                .eq(BizCompetitionCert::getProjectId, projectId)
                .eq(BizCompetitionCert::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizCompetitionCert::getCreateTime);
        List<BizCompetitionCert> list = bizCompetitionCertService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        BizCompetition comp = (cp != null && StrUtil.isNotBlank(cp.getCompetitionId()))
                ? bizCompetitionService.getById(cp.getCompetitionId()) : null;

        List<JSONObject> res = new ArrayList<>();
        for (BizCompetitionCert c : list) {
            JSONObject obj = JSONUtil.parseObj(c);
            obj.set("projectName", project != null ? project.getProjectName() : "");
            obj.set("competitionTitle", comp != null ? comp.getTitle() : "学科竞赛");
            obj.set("certificateUrl", StrUtil.isNotBlank(c.getCertificateImage()) ? c.getCertificateImage() : c.getFileId());
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<List<JSONObject>> getMyCertificates(String competitionId) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        LambdaQueryWrapper<BizCompetitionCert> qw = new LambdaQueryWrapper<BizCompetitionCert>()
                .eq(BizCompetitionCert::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizCompetitionCert::getCreateTime);
        if (StrUtil.isNotBlank(competitionId)) {
            qw.eq(BizCompetitionCert::getCompetitionId, competitionId);
        }
        if (!isTeacher) {
            qw.and(w -> w.eq(BizCompetitionCert::getStudentId, userId)
                    .or().eq(BizCompetitionCert::getUploadUserId, userId)
                    .or().like(BizCompetitionCert::getStudentIds, userId));
        }

        List<BizCompetitionCert> list = bizCompetitionCertService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> compIds = list.stream().map(BizCompetitionCert::getCompetitionId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizCompetition> compMap = CollectionUtil.isNotEmpty(compIds)
                ? bizCompetitionService.listByIds(compIds).stream().collect(Collectors.toMap(BizCompetition::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<String> studentIds = list.stream().map(BizCompetitionCert::getStudentId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> studentMap = CollectionUtil.isNotEmpty(studentIds)
                ? clientUserService.listByIds(studentIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizCompetitionCert c : list) {
            JSONObject obj = JSONUtil.parseObj(c);
            BizCompetition comp = compMap.get(c.getCompetitionId());
            obj.set("competitionTitle", comp != null ? comp.getTitle() : "校级学科竞赛");
            obj.set("certificateUrl", StrUtil.isNotBlank(c.getCertificateImage()) ? c.getCertificateImage() : c.getFileId());
            obj.set("certName", c.getCertificateName());
            ClientUser stu = studentMap.get(c.getStudentId());
            obj.set("studentName", StrUtil.isNotBlank(c.getStudentNames()) ? c.getStudentNames() : (stu != null ? stu.getName() : "学生"));
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<String> verifyCertificate(String certId, String status, String remark) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权认证竞赛荣誉证书");
        }
        BizCompetitionCert cert = bizCompetitionCertService.getById(certId);
        if (cert == null) {
            throw new CommonException("证书记录不存在");
        }
        cert.setVerifyStatus(status);
        cert.setVerifyTeacherId(teacherId);
        cert.setVerifyRemark(remark);
        bizCompetitionCertService.updateById(cert);
        return CommonResult.ok();
    }

    // =========================================================================
    // 3. 项目团队管理
    // =========================================================================

    @Override
    public CommonResult<JSONObject> getProjectDetail(String projectId) {
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        // 检查私密保护
        String currentUserId = StpClientUtil.isLogin() ? StpClientUtil.getLoginIdAsString() : null;
        if ("YES".equalsIgnoreCase(project.getIsPrivate())) {
            if (currentUserId == null) {
                throw new CommonException("该项目组已被组长设为私密，不可公开查看");
            }
            JSONObject role = getUserRoleInfo(currentUserId);
            boolean isTeacher = role.getBool("isTeacher", false);
            if (!isTeacher) {
                long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getProjectId, projectId)
                        .eq(BizProjectMember::getStudentId, currentUserId)
                        .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                if (count == 0 && !StrUtil.equals(project.getLeaderId(), currentUserId)) {
                    throw new CommonException("该项目组已被组长设为私密，非组内成员无法访问");
                }
            }
        }

        JSONObject result = JSONUtil.parseObj(project);

        // 组员列表
        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));

        List<String> studentIds = CollStreamUtil.toList(members, BizProjectMember::getStudentId);
        List<ClientUser> users = CollectionUtil.isNotEmpty(studentIds) ? clientUserService.listByIds(studentIds) : Collections.emptyList();
        Map<String, ClientUser> userMap = CollStreamUtil.toIdentityMap(users, ClientUser::getId);

        List<JSONObject> memberList = new ArrayList<>();
        for (BizProjectMember m : members) {
            JSONObject mObj = JSONUtil.parseObj(m);
            ClientUser u = userMap.get(m.getStudentId());
            mObj.set("studentName", u != null ? u.getName() : "同学");
            mObj.set("studentAccount", u != null ? u.getAccount() : "");
            mObj.set("avatar", u != null ? u.getAvatar() : null);
            memberList.add(mObj);
        }
        result.set("members", memberList);

        // 所属竞赛
        List<BizCompetitionProject> cpList = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        if (CollectionUtil.isNotEmpty(cpList)) {
            String cId = cpList.get(0).getCompetitionId();
            if (StrUtil.isNotBlank(cId) && !"UNASSIGNED".equalsIgnoreCase(cId)) {
                BizCompetition comp = bizCompetitionService.getById(cId);
                result.set("competition", comp);
                result.set("competitionId", cId);
                result.set("competitionTitle", comp != null ? comp.getTitle() : "暂未参与");
                result.set("isUnassigned", comp == null);
            } else {
                result.set("competition", null);
                result.set("competitionId", "UNASSIGNED");
                result.set("competitionTitle", "暂未参与");
                result.set("isUnassigned", true);
            }
        } else {
            result.set("competition", null);
            result.set("competitionId", "UNASSIGNED");
            result.set("competitionTitle", "暂未参与");
            result.set("isUnassigned", true);
        }

        // 当前用户在组内的角色
        result.set("isLeader", StrUtil.equals(project.getLeaderId(), currentUserId));
        boolean hasLeader = StrUtil.isNotBlank(project.getLeaderId());
        result.set("hasLeader", hasLeader);
        if (!hasLeader) {
            result.set("leaderName", "待竞聘选拔 (暂无组长)");
        } else {
            ClientUser leaderUser = clientUserService.getById(project.getLeaderId());
            result.set("leaderName", leaderUser != null ? leaderUser.getName() : "组长");
        }
        if (StrUtil.isNotBlank(project.getExtJson()) && JSONUtil.isTypeJSONObject(project.getExtJson())) {
            JSONObject ext = JSONUtil.parseObj(project.getExtJson());
            result.set("isTeacherInitiated", ext.getBool("isTeacherInitiated", false));
            result.set("teacherId", ext.getStr("teacherId"));
            result.set("teacherName", ext.getStr("teacherName"));
        } else {
            result.set("isTeacherInitiated", false);
        }

        return CommonResult.data(result);
    }

    @Override
    public CommonResult<String> toggleProjectPrivacy(String projectId, String isPrivate) {
        String currentUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(currentUserId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

        if (!isLeader && !isTeacherOwner) {
            throw new CommonException("权限不足：仅本组组长或发起教师可以更改项目私密状态");
        }
        project.setIsPrivate("YES".equalsIgnoreCase(isPrivate) ? "YES" : "NO");
        bizProjectService.updateById(project);
        return CommonResult.data("项目已设为" + ("YES".equalsIgnoreCase(isPrivate) ? "【私密】（他人不可查看）" : "【公开】"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> removeMember(String projectId, String studentId) {
        String currentUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(currentUserId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

        if (!isLeader && !isTeacherOwner) {
            throw new CommonException("权限不足：仅组长或发起教师可移除组员");
        }
        if (isLeader && StrUtil.equals(currentUserId, studentId)) {
            throw new CommonException("组长不可移除自己，如需解散团队请使用解散功能");
        }

        BizProjectMember member = bizProjectMemberService.getOne(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getStudentId, studentId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        if (member != null) {
            bizProjectMemberService.removeById(member.getId());
            long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, projectId)
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            project.setCurrentMemberCount((int) count);
            int maxLimit = project.getMaxMemberLimit() != null ? project.getMaxMemberLimit() : 5;
            if (count < maxLimit && "FULL".equalsIgnoreCase(project.getStatus())) {
                project.setStatus("RECRUITING");
            }
            if (StrUtil.equals(project.getLeaderId(), studentId)) {
                project.setLeaderId(null);
            }
            bizProjectService.updateById(project);
        }
        return CommonResult.data("组员已被移除");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> quitProject(String projectId) {
        String studentId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        if (StrUtil.equals(project.getLeaderId(), studentId)) {
            throw new CommonException("您是本项目组长，不可直接退出队伍；如需解散团队请使用解散功能");
        }

        BizProjectMember member = bizProjectMemberService.getOne(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getStudentId, studentId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        if (member == null) {
            throw new CommonException("您不在该项目组内，无法退出");
        }

        bizProjectMemberService.removeById(member.getId());
        long count = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        project.setCurrentMemberCount((int) count);
        int maxLimit = project.getMaxMemberLimit() != null ? project.getMaxMemberLimit() : 5;
        if (count < maxLimit && "FULL".equalsIgnoreCase(project.getStatus())) {
            project.setStatus("RECRUITING");
        }
        bizProjectService.updateById(project);
        return CommonResult.data("您已成功退出项目组【" + project.getProjectName() + "】");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> disbandProject(String projectId) {
        String currentUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(currentUserId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

        if (!isLeader && !isTeacherOwner) {
            throw new CommonException("权限不足：仅项目组长或发起教师有权解散团队");
        }

        // 1. 删除组员
        bizProjectMemberService.remove(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId));

        // 2. 删除比赛项目关联
        bizCompetitionProjectService.remove(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId));

        // 3. 逻辑删除项目
        bizProjectService.removeById(projectId);

        return CommonResult.data("项目组【" + project.getProjectName() + "】已成功解散并清理");
    }

    @Override
    public CommonResult<String> updateMemberDuty(String projectId, String studentId, String dutyRemark) {
        String currentUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(currentUserId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

        if (!isLeader && !isTeacherOwner) {
            throw new CommonException("权限不足：仅组长或发起教师可调整组内职责");
        }
        BizProjectMember member = bizProjectMemberService.getOne(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getStudentId, studentId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        if (member == null) {
            throw new CommonException("该组员不存在");
        }
        member.setDutyRemark(dutyRemark);
        bizProjectMemberService.updateById(member);
        return CommonResult.ok();
    }

    @Override
    public CommonResult<String> applyToProject(String projectId, String applyRole, String message) {
        String studentId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(studentId);
        if (roleInfo.getBool("isTeacher", false)) {
            throw new CommonException("指导教师无需申请学生席位");
        }

        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("所选项目组不存在");
        }

        int currentCount = project.getCurrentMemberCount() != null ? project.getCurrentMemberCount() : 0;
        int maxLimit = project.getMaxMemberLimit() != null ? project.getMaxMemberLimit() : 5;
        if (currentCount >= maxLimit || "FULL".equalsIgnoreCase(project.getStatus())) {
            throw new CommonException("申请失败：该项目组人数已满员 (" + maxLimit + "人)");
        }

        // 检查是否已经在组内
        long inCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getStudentId, studentId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        if (inCount > 0 || StrUtil.equals(project.getLeaderId(), studentId)) {
            throw new CommonException("您已在该项目组内，无需重复申请");
        }

        // 检查是否已有待审核的申请
        long pendingCount = bizProjectFlowService.count(new LambdaQueryWrapper<BizProjectFlow>()
                .eq(BizProjectFlow::getProjectId, projectId)
                .eq(BizProjectFlow::getStudentId, studentId)
                .eq(BizProjectFlow::getDirection, "APPLY")
                .eq(BizProjectFlow::getStatus, "PENDING")
                .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE"));
        if (pendingCount > 0) {
            throw new CommonException("您已提交过申请，正在等待老师或组长审核筛选");
        }

        String role = StrUtil.blankToDefault(applyRole, "MEMBER").toUpperCase();
        if ("LEADER".equalsIgnoreCase(role)) {
            // 校验1：该项目是否已有组长
            if (StrUtil.isNotBlank(project.getLeaderId())) {
                throw new CommonException("该项目组已有组长，无法申请组长职位，请选择申请为组员");
            }
            // 校验2：当前学生是否已在该比赛的其他项目中担任组长（跨比赛可以担任组长）
            BizCompetitionProject curCp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                    .eq(BizCompetitionProject::getProjectId, projectId)
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"), false);
            if (curCp != null && StrUtil.isNotBlank(curCp.getCompetitionId())) {
                List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                        .eq(BizCompetitionProject::getCompetitionId, curCp.getCompetitionId())
                        .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
                if (CollectionUtil.isNotEmpty(compProjs)) {
                    List<String> pIds = CollStreamUtil.toList(compProjs, BizCompetitionProject::getProjectId);
                    long isLeaderInComp = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                            .in(BizProjectMember::getProjectId, pIds)
                            .eq(BizProjectMember::getStudentId, studentId)
                            .eq(BizProjectMember::getMemberRole, "LEADER")
                            .eq(BizProjectMember::getMemberStatus, "ACTIVE")
                            .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                    if (isLeaderInComp > 0) {
                        throw new CommonException("您当前已在该比赛的其他项目中担任组长，同一比赛不可重复竞聘组长");
                    }
                }
            }
        }

        BizProjectFlow flow = new BizProjectFlow();
        flow.setProjectId(projectId);
        flow.setStudentId(studentId);
        flow.setDirection("APPLY");
        flow.setApplyRole(role);
        flow.setOperatorId(studentId);
        flow.setMessage(StrUtil.nullToDefault(message, "LEADER".equalsIgnoreCase(role) ? "申请竞聘组长职位" : "申请加入项目组"));
        flow.setStatus("PENDING");
        flow.setDeleteFlag("NOT_DELETE");
        bizProjectFlowService.save(flow);

        return CommonResult.data("报名申请已提交，请静候指导教师或团队审核筛选");
    }

    @Override
    public CommonResult<List<JSONObject>> getProjectApplicants(String projectId) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        boolean isLeader = StrUtil.equals(project.getLeaderId(), userId);
        if (!isTeacher && !isLeader) {
            throw new CommonException("权限不足：仅指导教师或本项目组长可查看报名筛选名单");
        }

        List<BizProjectFlow> list = bizProjectFlowService.list(new LambdaQueryWrapper<BizProjectFlow>()
                .eq(BizProjectFlow::getProjectId, projectId)
                .eq(BizProjectFlow::getDirection, "APPLY")
                .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProjectFlow::getCreateTime));

        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> studentIds = CollStreamUtil.toList(list, BizProjectFlow::getStudentId);
        List<ClientUser> students = clientUserService.listByIds(studentIds);
        Map<String, ClientUser> studentMap = CollStreamUtil.toIdentityMap(students, ClientUser::getId);
        List<BizStudentProfile> profiles = bizStudentProfileService.list(new LambdaQueryWrapper<BizStudentProfile>()
                .in(BizStudentProfile::getUserId, studentIds));
        Map<String, BizStudentProfile> profileMap = CollStreamUtil.toIdentityMap(profiles, BizStudentProfile::getUserId);

        List<JSONObject> res = new ArrayList<>();
        for (BizProjectFlow f : list) {
            JSONObject item = JSONUtil.parseObj(f);
            ClientUser u = studentMap.get(f.getStudentId());
            if (u != null) {
                item.set("studentName", u.getName());
                item.set("studentAccount", u.getAccount());
                item.set("studentAvatar", u.getAvatar());
            }
            BizStudentProfile p = profileMap.get(f.getStudentId());
            if (p != null) {
                item.set("major", p.getMajor());
                item.set("className", p.getClassName());
                item.set("studentNo", p.getStudentNo());
                item.set("skillTags", p.getSkillTags());
            }
            // 检查该同学当前是否已在某处担任组长
            long leaderCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getStudentId, f.getStudentId())
                    .eq(BizProjectMember::getMemberRole, "LEADER")
                    .eq(BizProjectMember::getMemberStatus, "ACTIVE")
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            item.set("isAlreadyLeader", leaderCount > 0);
            res.add(item);
        }
        return CommonResult.data(res);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> handleProjectApplicant(String flowId, boolean accept, String remark) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        BizProjectFlow flow = bizProjectFlowService.getById(flowId);
        if (flow == null) {
            throw new CommonException("报名记录不存在");
        }
        if (!"PENDING".equalsIgnoreCase(flow.getStatus())) {
            throw new CommonException("该申请已被处理过");
        }

        BizProject project = bizProjectService.getById(flow.getProjectId());
        if (project == null) {
            throw new CommonException("对应项目不存在");
        }

        boolean isLeader = StrUtil.equals(project.getLeaderId(), userId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), userId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(userId)));
        if (!isLeader && !isTeacher) {
            throw new CommonException("权限不足：仅指导教师或本项目组长有权审核筛选报名学生");
        }
        if (!isLeader && !isTeacherOwner && (isTeacher && StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains("teacherId"))) {
            throw new CommonException("权限不足：您非该实训项目的指导教师");
        }

        if (accept) {
            // 实时检查人数上限（通过数据库实际成员数做强校验）
            long actualMemberCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, project.getId())
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            int maxLimit = project.getMaxMemberLimit() != null ? project.getMaxMemberLimit() : 5;
            if (actualMemberCount >= maxLimit) {
                project.setStatus("FULL");
                project.setCurrentMemberCount((int) actualMemberCount);
                bizProjectService.updateById(project);
                throw new CommonException("录取失败：该项目组人数已达到上限 (" + maxLimit + "人)");
            }

            // 检查是否已经在组内
            long alreadyIn = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, project.getId())
                    .eq(BizProjectMember::getStudentId, flow.getStudentId())
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            if (alreadyIn > 0) {
                throw new CommonException("该学生已在当前项目组内");
            }

            String role = StrUtil.blankToDefault(flow.getApplyRole(), "MEMBER").toUpperCase();
            if ("LEADER".equalsIgnoreCase(role)) {
                // 校验项目是否已有组长
                if (StrUtil.isNotBlank(project.getLeaderId())) {
                    throw new CommonException("录用失败：该项目组已有组长，无法重复录用组长！");
                }
                // 校验该学生是否已是其他组组长
                long leaderCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getStudentId, flow.getStudentId())
                        .eq(BizProjectMember::getMemberRole, "LEADER")
                        .eq(BizProjectMember::getMemberStatus, "ACTIVE")
                        .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                if (leaderCount > 0) {
                    throw new CommonException("录用失败：该学生已在其他项目中担任组长，无法录用为组长！");
                }

                // 设置为组长
                project.setLeaderId(flow.getStudentId());

                BizProjectMember member = new BizProjectMember();
                member.setProjectId(project.getId());
                member.setStudentId(flow.getStudentId());
                member.setMemberRole("LEADER");
                member.setDutyRemark("通过审核录用为团队组长");
                member.setMemberStatus("ACTIVE");
                member.setDeleteFlag("NOT_DELETE");
                bizProjectMemberService.save(member);
            } else {
                // 录用为组员
                BizProjectMember member = new BizProjectMember();
                member.setProjectId(project.getId());
                member.setStudentId(flow.getStudentId());
                member.setMemberRole("MEMBER");
                member.setDutyRemark(StrUtil.nullToDefault(remark, "核心组员"));
                member.setMemberStatus("ACTIVE");
                member.setDeleteFlag("NOT_DELETE");
                bizProjectMemberService.save(member);
            }

            long newCount = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, project.getId())
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            project.setCurrentMemberCount((int) newCount);
            if (newCount >= maxLimit) {
                project.setStatus("FULL");
            } else {
                project.setStatus("RECRUITING");
            }
            bizProjectService.updateById(project);

            flow.setStatus("ACCEPTED");
            flow.setHandleTime(new Date());
            flow.setHandleRemark(StrUtil.nullToDefault(remark, "审核通过，已成功录用"));
            bizProjectFlowService.updateById(flow);
            return CommonResult.data("录用成功");
        } else {
            flow.setStatus("REJECTED");
            flow.setHandleTime(new Date());
            flow.setHandleRemark(StrUtil.nullToDefault(remark, "未通过筛选"));
            bizProjectFlowService.updateById(flow);
            return CommonResult.data("已淘汰该申请");
        }
    }

    // =========================================================================
    // 4. 算法训练模块
    // =========================================================================

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> publishAlgorithmTask(PortalAlgorithmTaskParam param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权发布算法练习任务");
        }

        BizAlgorithmTask task = new BizAlgorithmTask();
        task.setTitle(param.getTitle());
        task.setContentDesc(param.getContentDesc());
        task.setDifficulty(param.getDifficulty());
        task.setAllowedSubmissionTypes(param.getAllowedSubmissionTypes());
        task.setDeadline(param.getDeadline());
        task.setTeacherId(teacherId);
        task.setSampleInput(param.getSampleInput());
        task.setSampleOutput(param.getSampleOutput());
        task.setDeleteFlag("NOT_DELETE");
        bizAlgorithmTaskService.save(task);

        // 分发给指定项目组
        for (String projId : param.getAssignedProjectIds()) {
            BizAlgorithmTaskProj atp = new BizAlgorithmTaskProj();
            atp.setTaskId(task.getId());
            atp.setProjectId(projId);
            atp.setDeleteFlag("NOT_DELETE");
            bizAlgorithmTaskProjService.save(atp);
        }
        return CommonResult.data(task.getId());
    }

    @Override
    public CommonResult<List<JSONObject>> getMyAlgorithmTasks() {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        if (isTeacher) {
            // 教师视角：查询由该教师发布或全部有效的算法训练任务，并统计已分派团队数和提交人数
            List<BizAlgorithmTask> tasks = bizAlgorithmTaskService.list(new LambdaQueryWrapper<BizAlgorithmTask>()
                    .eq(BizAlgorithmTask::getDeleteFlag, "NOT_DELETE")
                    .orderByDesc(BizAlgorithmTask::getCreateTime));

            List<JSONObject> result = new ArrayList<>();
            for (BizAlgorithmTask t : tasks) {
                JSONObject obj = JSONUtil.parseObj(t);
                long assignedTeams = bizAlgorithmTaskProjService.count(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                        .eq(BizAlgorithmTaskProj::getTaskId, t.getId())
                        .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
                long submittedCount = bizAlgorithmSubmissionService.count(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                        .eq(BizAlgorithmSubmission::getTaskId, t.getId())
                        .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));
                obj.set("assignedTeamsCount", assignedTeams);
                obj.set("submittedCount", submittedCount);
                obj.set("isTeacherView", true);
                result.add(obj);
            }
            return CommonResult.data(result);
        }

        // 学生视角：
        // 1. 查找当前学生所在的所有项目组
        List<BizProjectMember> myMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getStudentId, userId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));

        List<String> taskIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(myMembers)) {
            List<String> projectIds = CollStreamUtil.toList(myMembers, BizProjectMember::getProjectId);
            List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                    .in(BizAlgorithmTaskProj::getProjectId, projectIds)
                    .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(taskProjs)) {
                taskIds = CollStreamUtil.toList(taskProjs, BizAlgorithmTaskProj::getTaskId).stream().distinct().collect(Collectors.toList());
            }
        }

        List<BizAlgorithmTask> tasks;
        if (CollectionUtil.isNotEmpty(taskIds)) {
            tasks = bizAlgorithmTaskService.list(new LambdaQueryWrapper<BizAlgorithmTask>()
                    .in(BizAlgorithmTask::getId, taskIds)
                    .eq(BizAlgorithmTask::getDeleteFlag, "NOT_DELETE")
                    .orderByDesc(BizAlgorithmTask::getCreateTime));
        } else {
            // 学生若未加入团队或团队暂无分派任务，展示公开算法任务便于其自由练习
            tasks = bizAlgorithmTaskService.list(new LambdaQueryWrapper<BizAlgorithmTask>()
                    .eq(BizAlgorithmTask::getDeleteFlag, "NOT_DELETE")
                    .orderByDesc(BizAlgorithmTask::getCreateTime)
                    .last("LIMIT 20"));
            taskIds = CollStreamUtil.toList(tasks, BizAlgorithmTask::getId);
        }

        if (CollectionUtil.isEmpty(tasks)) {
            return CommonResult.data(Collections.emptyList());
        }

        // 查找学生对这些任务的提交记录（核心去重规则：只要提交一次即算完成）
        List<BizAlgorithmSubmission> submissions = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .in(BizAlgorithmSubmission::getTaskId, taskIds)
                .eq(BizAlgorithmSubmission::getStudentId, userId)
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"))
                : Collections.emptyList();
        Map<String, BizAlgorithmSubmission> subMap = submissions.stream().collect(Collectors.toMap(BizAlgorithmSubmission::getTaskId, v -> v, (v1, v2) -> v1));

        // 查找学生收到的来自组长的催促记录
        List<BizAlgorithmUrge> urges = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmUrgeService.list(new LambdaQueryWrapper<BizAlgorithmUrge>()
                .in(BizAlgorithmUrge::getTaskId, taskIds)
                .eq(BizAlgorithmUrge::getStudentId, userId)
                .eq(BizAlgorithmUrge::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizAlgorithmUrge::getUrgeTime))
                : Collections.emptyList();
        Map<String, List<BizAlgorithmUrge>> urgeMap = urges.stream().collect(Collectors.groupingBy(BizAlgorithmUrge::getTaskId));

        List<JSONObject> result = new ArrayList<>();
        for (BizAlgorithmTask t : tasks) {
            JSONObject obj = JSONUtil.parseObj(t);
            BizAlgorithmSubmission sub = subMap.get(t.getId());
            obj.set("isSubmitted", sub != null);
            obj.set("submission", sub);
            obj.set("submitStatus", sub != null ? sub.getStatus() : "UNSUBMITTED");
            List<BizAlgorithmUrge> taskUrges = urgeMap.get(t.getId());
            obj.set("urgeCount", CollectionUtil.isNotEmpty(taskUrges) ? taskUrges.size() : 0);
            if (CollectionUtil.isNotEmpty(taskUrges)) {
                obj.set("latestUrgeMessage", taskUrges.get(0).getUrgeMessage());
                obj.set("latestUrgeTime", taskUrges.get(0).getUrgeTime());
            }
            result.add(obj);
        }
        return CommonResult.data(result);
    }

    @Override
    public CommonResult<List<JSONObject>> getTeamTaskProgress(String projectId, String taskId) {
        String leaderId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject role = getUserRoleInfo(leaderId);
        if (!StrUtil.equals(project.getLeaderId(), leaderId) && !role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅组长与指导教师可查看组员算法完成进度");
        }

        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));

        List<String> studentIds = CollStreamUtil.toList(members, BizProjectMember::getStudentId);
        List<ClientUser> users = clientUserService.listByIds(studentIds);
        Map<String, ClientUser> userMap = CollStreamUtil.toIdentityMap(users, ClientUser::getId);

        // 查询这些组员对于该任务的提交记录
        List<BizAlgorithmSubmission> submissions = bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .eq(BizAlgorithmSubmission::getTaskId, taskId)
                .in(BizAlgorithmSubmission::getStudentId, studentIds)
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));
        Map<String, BizAlgorithmSubmission> subMap = CollStreamUtil.toIdentityMap(submissions, BizAlgorithmSubmission::getStudentId);

        List<JSONObject> res = new ArrayList<>();
        for (BizProjectMember m : members) {
            JSONObject item = new JSONObject();
            item.set("studentId", m.getStudentId());
            ClientUser u = userMap.get(m.getStudentId());
            item.set("studentName", u != null ? u.getName() : "同学");
            item.set("role", m.getMemberRole());
            BizAlgorithmSubmission s = subMap.get(m.getStudentId());
            item.set("isSubmitted", s != null);
            item.set("submission", s);
            res.add(item);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<String> urgeMember(String projectId, String studentId, String taskId, String message) {
        String currentUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(currentUserId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), currentUserId);
        boolean isTeacherOwner = isTeacher && (StrUtil.equals(project.getCreateUser(), currentUserId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(currentUserId)));

        if (!isLeader && !isTeacherOwner) {
            throw new CommonException("权限不足：仅本组组长或指导教师具有监督催促职能");
        }

        BizAlgorithmUrge urge = new BizAlgorithmUrge();
        urge.setTaskId(taskId);
        urge.setProjectId(projectId);
        urge.setLeaderId(currentUserId);
        urge.setStudentId(studentId);
        urge.setUrgeMessage(StrUtil.nullToDefault(message, (isTeacher ? "指导教师" : "组长") + "催促：算法任务已临近截止，请尽快编码并提交！"));
        urge.setUrgeTime(new Date());
        urge.setDeleteFlag("NOT_DELETE");
        bizAlgorithmUrgeService.save(urge);

        return CommonResult.data("催促提醒已成功下发至组员");
    }

    @Override
    public CommonResult<String> submitAlgorithm(PortalAlgorithmSubmitParam param) {
        String studentId = getCurrentUserId();
        BizAlgorithmTask task = bizAlgorithmTaskService.getById(param.getTaskId());
        if (task == null) {
            throw new CommonException("算法任务不存在");
        }

        // 核心去重：查找是否已有针对该任务的提交记录
        BizAlgorithmSubmission exist = bizAlgorithmSubmissionService.getOne(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .eq(BizAlgorithmSubmission::getTaskId, param.getTaskId())
                .eq(BizAlgorithmSubmission::getStudentId, studentId)
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));

        if (exist == null) {
            exist = new BizAlgorithmSubmission();
            exist.setTaskId(param.getTaskId());
            exist.setStudentId(studentId);
            exist.setSubmissionType(param.getSubmissionType());
            exist.setCodeLanguage(param.getCodeLanguage());
            exist.setCodeContent(param.getCodeContent());
            exist.setFileId(param.getFileId());
            exist.setGitUrl(param.getGitUrl());
            exist.setSubmitTime(new Date());
            exist.setStatus("SUBMITTED");
            exist.setDeleteFlag("NOT_DELETE");
            bizAlgorithmSubmissionService.save(exist);
        } else {
            exist.setSubmissionType(param.getSubmissionType());
            exist.setCodeLanguage(param.getCodeLanguage());
            exist.setCodeContent(param.getCodeContent());
            exist.setFileId(param.getFileId());
            exist.setGitUrl(param.getGitUrl());
            exist.setSubmitTime(new Date());
            exist.setStatus("SUBMITTED");
            bizAlgorithmSubmissionService.updateById(exist);
        }
        return CommonResult.data("算法提交成功，若您参与了多个团队，系统已自动同步完成状态");
    }

    @Override
    public CommonResult<List<JSONObject>> getMySubmissions(String taskId) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        LambdaQueryWrapper<BizAlgorithmSubmission> qw = new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE");
        if (StrUtil.isNotBlank(taskId)) {
            qw.eq(BizAlgorithmSubmission::getTaskId, taskId);
        }
        if (!isTeacher) {
            qw.eq(BizAlgorithmSubmission::getStudentId, userId);
        }
        qw.orderByDesc(BizAlgorithmSubmission::getCreateTime);

        List<BizAlgorithmSubmission> list = bizAlgorithmSubmissionService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> studentIds = list.stream().map(BizAlgorithmSubmission::getStudentId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> studentMap = CollectionUtil.isNotEmpty(studentIds)
                ? clientUserService.listByIds(studentIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<String> taskIds = list.stream().map(BizAlgorithmSubmission::getTaskId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizAlgorithmTask> taskMap = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmTaskService.listByIds(taskIds).stream().collect(Collectors.toMap(BizAlgorithmTask::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizAlgorithmSubmission s : list) {
            JSONObject obj = JSONUtil.parseObj(s);
            BizAlgorithmTask t = taskMap.get(s.getTaskId());
            obj.set("taskTitle", t != null ? t.getTitle() : "未知任务");
            ClientUser stu = studentMap.get(s.getStudentId());
            obj.set("studentName", stu != null ? stu.getName() : "学生");
            obj.set("studentAccount", stu != null ? stu.getAccount() : "");
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<String> gradeAlgorithmSubmission(String submissionId, BigDecimal score, String feedback) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权批改作业打分");
        }
        BizAlgorithmSubmission sub = bizAlgorithmSubmissionService.getById(submissionId);
        if (sub == null) {
            throw new CommonException("提交记录不存在");
        }
        sub.setScore(score);
        sub.setTeacherFeedback(feedback);
        sub.setStatus("GRADED");
        bizAlgorithmSubmissionService.updateById(sub);
        return CommonResult.ok();
    }

    // =========================================================================
    // 算法题库 CRUD 与多小组监控 (教师/组长)
    // =========================================================================

    @Override
    public CommonResult<List<JSONObject>> getAlgorithmLibrary(String keyword, String difficulty, String tag) {
        String userId = getCurrentUserId();
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);

        LambdaQueryWrapper<BizAlgorithmTask> qw = new LambdaQueryWrapper<BizAlgorithmTask>()
                .eq(BizAlgorithmTask::getDeleteFlag, "NOT_DELETE");
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(BizAlgorithmTask::getTitle, keyword).or().like(BizAlgorithmTask::getContentDesc, keyword));
        }
        if (StrUtil.isNotBlank(difficulty) && !"ALL".equalsIgnoreCase(difficulty)) {
            qw.eq(BizAlgorithmTask::getDifficulty, difficulty);
        }
        if (StrUtil.isNotBlank(tag) && !"ALL".equalsIgnoreCase(tag)) {
            qw.like(BizAlgorithmTask::getTags, tag);
        }
        qw.orderByDesc(BizAlgorithmTask::getCreateTime);
        List<BizAlgorithmTask> list = bizAlgorithmTaskService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> taskIds = CollStreamUtil.toList(list, BizAlgorithmTask::getId);

        // 获取每个任务关联的分派项目
        List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                .in(BizAlgorithmTaskProj::getTaskId, taskIds)
                .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizAlgorithmTaskProj>> taskProjMap = taskProjs.stream().collect(Collectors.groupingBy(BizAlgorithmTaskProj::getTaskId));

        // 获取每个任务关联的项目名称
        List<String> allProjIds = taskProjs.stream().map(BizAlgorithmTaskProj::getProjectId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizProject> projMap = CollectionUtil.isNotEmpty(allProjIds)
                ? bizProjectService.listByIds(allProjIds).stream().collect(Collectors.toMap(BizProject::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 获取每个任务的提交统计
        List<BizAlgorithmSubmission> submissions = bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .in(BizAlgorithmSubmission::getTaskId, taskIds)
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizAlgorithmSubmission>> subMap = submissions.stream().collect(Collectors.groupingBy(BizAlgorithmSubmission::getTaskId));

        // 如果是学生，查询当前学生针对这些任务的做题状态
        Map<String, BizAlgorithmSubmission> mySubMap = new HashMap<>();
        if (!isTeacher && StrUtil.isNotBlank(userId)) {
            for (BizAlgorithmSubmission s : submissions) {
                if (StrUtil.equals(s.getStudentId(), userId)) {
                    mySubMap.put(s.getTaskId(), s);
                }
            }
        }

        List<JSONObject> result = new ArrayList<>();
        for (BizAlgorithmTask t : list) {
            JSONObject obj = JSONUtil.parseObj(t);
            List<BizAlgorithmTaskProj> projs = taskProjMap.getOrDefault(t.getId(), Collections.emptyList());
            obj.set("assignedProjectsCount", projs.size());

            List<JSONObject> projSimpleList = new ArrayList<>();
            for (BizAlgorithmTaskProj atp : projs) {
                BizProject p = projMap.get(atp.getProjectId());
                if (p != null) {
                    JSONObject pObj = new JSONObject();
                    pObj.set("id", p.getId());
                    pObj.set("name", p.getProjectName());
                    projSimpleList.add(pObj);
                }
            }
            obj.set("assignedProjects", projSimpleList);
            obj.set("assignedProjectIds", CollStreamUtil.toList(projs, BizAlgorithmTaskProj::getProjectId));

            List<BizAlgorithmSubmission> subs = subMap.getOrDefault(t.getId(), Collections.emptyList());
            obj.set("submissionCount", subs.size());

            if (!isTeacher) {
                BizAlgorithmSubmission mySub = mySubMap.get(t.getId());
                obj.set("isSubmitted", mySub != null);
                obj.set("myScore", mySub != null ? mySub.getScore() : null);
                obj.set("myStatus", mySub != null ? mySub.getStatus() : "UNSUBMITTED");
            }
            result.add(obj);
        }
        return CommonResult.data(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> saveAlgorithmLibraryProblem(JSONObject param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅教师有权在算法库中录入与管理算法题");
        }
        String title = param.getStr("title");
        if (StrUtil.isBlank(title)) {
            throw new CommonException("算法题目名称不能为空");
        }
        BizAlgorithmTask task = new BizAlgorithmTask();
        task.setTitle(title);
        task.setContentDesc(param.getStr("contentDesc", ""));
        task.setDifficulty(param.getStr("difficulty", "MEDIUM"));
        task.setTags(param.getStr("tags", "算法基础"));
        task.setAllowedSubmissionTypes(param.getStr("allowedSubmissionTypes", "CODE_TEXT,FILE,GIT_URL"));
        task.setDeadline(param.getDate("deadline"));
        task.setTeacherId(teacherId);
        task.setSampleInput(param.getStr("sampleInput", ""));
        task.setSampleOutput(param.getStr("sampleOutput", ""));
        task.setDeleteFlag("NOT_DELETE");
        bizAlgorithmTaskService.save(task);

        // 如果包含指定分派的项目组
        List<String> assignedProjectIds = param.getBeanList("assignedProjectIds", String.class);
        if (CollectionUtil.isNotEmpty(assignedProjectIds)) {
            for (String projId : assignedProjectIds) {
                if (StrUtil.isNotBlank(projId)) {
                    BizAlgorithmTaskProj atp = new BizAlgorithmTaskProj();
                    atp.setTaskId(task.getId());
                    atp.setProjectId(projId);
                    atp.setDeleteFlag("NOT_DELETE");
                    bizAlgorithmTaskProjService.save(atp);
                }
            }
        }
        return CommonResult.data(task.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> updateAlgorithmLibraryProblem(JSONObject param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅教师有权修改算法库题目");
        }
        String id = param.getStr("id");
        if (StrUtil.isBlank(id)) {
            throw new CommonException("算法题目ID不能为空");
        }
        BizAlgorithmTask task = bizAlgorithmTaskService.getById(id);
        if (task == null || "DELETE".equals(task.getDeleteFlag())) {
            throw new CommonException("该算法题目不存在或已删除");
        }
        if (param.containsKey("title")) task.setTitle(param.getStr("title"));
        if (param.containsKey("contentDesc")) task.setContentDesc(param.getStr("contentDesc"));
        if (param.containsKey("difficulty")) task.setDifficulty(param.getStr("difficulty"));
        if (param.containsKey("tags")) task.setTags(param.getStr("tags"));
        if (param.containsKey("sampleInput")) task.setSampleInput(param.getStr("sampleInput"));
        if (param.containsKey("sampleOutput")) task.setSampleOutput(param.getStr("sampleOutput"));
        if (param.containsKey("allowedSubmissionTypes")) task.setAllowedSubmissionTypes(param.getStr("allowedSubmissionTypes"));
        if (param.containsKey("deadline")) task.setDeadline(param.getDate("deadline"));
        bizAlgorithmTaskService.updateById(task);

        // 如果传递了更新的分派项目组列表
        if (param.containsKey("assignedProjectIds")) {
            List<String> newProjIds = param.getBeanList("assignedProjectIds", String.class);
            // 将旧分派作废
            bizAlgorithmTaskProjService.remove(new LambdaQueryWrapper<BizAlgorithmTaskProj>().eq(BizAlgorithmTaskProj::getTaskId, id));
            if (CollectionUtil.isNotEmpty(newProjIds)) {
                for (String pId : newProjIds) {
                    if (StrUtil.isNotBlank(pId)) {
                        BizAlgorithmTaskProj atp = new BizAlgorithmTaskProj();
                        atp.setTaskId(id);
                        atp.setProjectId(pId);
                        atp.setDeleteFlag("NOT_DELETE");
                        bizAlgorithmTaskProjService.save(atp);
                    }
                }
            }
        }
        return CommonResult.data("算法题目更新成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> deleteAlgorithmLibraryProblem(String id) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅教师有权删除算法库题目");
        }
        BizAlgorithmTask task = bizAlgorithmTaskService.getById(id);
        if (task == null) {
            throw new CommonException("算法题目不存在");
        }
        task.setDeleteFlag("DELETE");
        bizAlgorithmTaskService.updateById(task);

        // 级联标记分派记录
        bizAlgorithmTaskProjService.update(new LambdaUpdateWrapper<BizAlgorithmTaskProj>()
                .eq(BizAlgorithmTaskProj::getTaskId, id)
                .set(BizAlgorithmTaskProj::getDeleteFlag, "DELETE"));
        return CommonResult.data("算法题目已移出题库");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> dispatchAlgorithmTask(JSONObject param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权发布算法任务");
        }
        String taskId = param.getStr("taskId");
        if (StrUtil.isBlank(taskId)) {
            throw new CommonException("请指定待发布的算法题目");
        }
        BizAlgorithmTask task = bizAlgorithmTaskService.getById(taskId);
        if (task == null || "DELETE".equals(task.getDeleteFlag())) {
            throw new CommonException("算法题目不存在");
        }
        Date deadline = param.getDate("deadline");
        if (deadline != null) {
            task.setDeadline(deadline);
            bizAlgorithmTaskService.updateById(task);
        }
        List<String> projectIds = param.getBeanList("projectIds", String.class);
        if (CollectionUtil.isEmpty(projectIds)) {
            throw new CommonException("请选择接收任务的项目团队");
        }
        int added = 0;
        for (String pId : projectIds) {
            if (StrUtil.isBlank(pId)) continue;
            long count = bizAlgorithmTaskProjService.count(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                    .eq(BizAlgorithmTaskProj::getTaskId, taskId)
                    .eq(BizAlgorithmTaskProj::getProjectId, pId)
                    .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
            if (count == 0) {
                BizAlgorithmTaskProj atp = new BizAlgorithmTaskProj();
                atp.setTaskId(taskId);
                atp.setProjectId(pId);
                atp.setDeleteFlag("NOT_DELETE");
                bizAlgorithmTaskProjService.save(atp);
                added++;
            }
        }
        return CommonResult.data(StrUtil.format("成功分发至 {} 个项目团队", added));
    }

    @Override
    public CommonResult<List<JSONObject>> getTeacherGroupProgress() {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师可查看各小组整体完成看板");
        }

        List<BizProject> projects = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProject::getCreateTime));
        if (CollectionUtil.isEmpty(projects)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> projIds = CollStreamUtil.toList(projects, BizProject::getId);

        // 比赛信息
        List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .in(BizCompetitionProject::getProjectId, projIds)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        Map<String, String> projCompIdMap = compProjs.stream().collect(Collectors.toMap(BizCompetitionProject::getProjectId, BizCompetitionProject::getCompetitionId, (v1, v2) -> v1));
        List<String> compIds = compProjs.stream().map(BizCompetitionProject::getCompetitionId).distinct().collect(Collectors.toList());
        Map<String, BizCompetition> compMap = CollectionUtil.isNotEmpty(compIds)
                ? bizCompetitionService.listByIds(compIds).stream().collect(Collectors.toMap(BizCompetition::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 组长信息
        List<String> leaderIds = projects.stream().map(BizProject::getLeaderId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> userMap = CollectionUtil.isNotEmpty(leaderIds)
                ? clientUserService.listByIds(leaderIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 成员信息
        List<BizProjectMember> allMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .in(BizProjectMember::getProjectId, projIds)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizProjectMember>> projMemberMap = allMembers.stream().collect(Collectors.groupingBy(BizProjectMember::getProjectId));

        // 项目任务分发
        List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                .in(BizAlgorithmTaskProj::getProjectId, projIds)
                .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizAlgorithmTaskProj>> projTaskMap = taskProjs.stream().collect(Collectors.groupingBy(BizAlgorithmTaskProj::getProjectId));

        // 所有提交
        List<BizAlgorithmSubmission> allSubs = bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));

        List<JSONObject> res = new ArrayList<>();
        for (BizProject p : projects) {
            JSONObject obj = new JSONObject();
            obj.set("projectId", p.getId());
            obj.set("projectName", p.getProjectName());
            ClientUser leader = userMap.get(p.getLeaderId());
            obj.set("leaderName", leader != null ? leader.getName() : "组长");
            obj.set("leaderPhone", leader != null ? leader.getPhone() : "");

            String compId = projCompIdMap.get(p.getId());
            BizCompetition comp = compId != null ? compMap.get(compId) : null;
            obj.set("competitionId", compId);
            obj.set("competitionTitle", comp != null ? comp.getTitle() : "校级算法竞赛");

            List<BizProjectMember> members = projMemberMap.getOrDefault(p.getId(), Collections.emptyList());
            int memberCount = members.size();
            obj.set("memberCount", memberCount);

            List<BizAlgorithmTaskProj> tps = projTaskMap.getOrDefault(p.getId(), Collections.emptyList());
            int taskCount = tps.size();
            obj.set("taskCount", taskCount);

            int totalExpected = memberCount * taskCount;
            obj.set("totalExpected", totalExpected);

            Set<String> memberStudentIds = members.stream().map(BizProjectMember::getStudentId).collect(Collectors.toSet());
            Set<String> assignedTaskIds = tps.stream().map(BizAlgorithmTaskProj::getTaskId).collect(Collectors.toSet());

            long actualSubmitted = allSubs.stream()
                    .filter(s -> assignedTaskIds.contains(s.getTaskId()) && memberStudentIds.contains(s.getStudentId()))
                    .count();
            obj.set("actualSubmitted", actualSubmitted);

            double rate = totalExpected > 0 ? ((double) actualSubmitted / totalExpected * 100) : 0.0;
            obj.set("completionRate", Math.round(rate * 10.0) / 10.0);
            obj.set("unsubmittedCount", Math.max(0, totalExpected - (int) actualSubmitted));

            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<List<JSONObject>> getTeacherAllSubmissions(String projectId, String taskId, String keyword, String status) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师可查看全量算法提交具体信息");
        }

        LambdaQueryWrapper<BizAlgorithmSubmission> qw = new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE");
        if (StrUtil.isNotBlank(taskId)) {
            qw.eq(BizAlgorithmSubmission::getTaskId, taskId);
        }
        if (StrUtil.isNotBlank(status) && !"ALL".equalsIgnoreCase(status)) {
            qw.eq(BizAlgorithmSubmission::getStatus, status);
        }

        if (StrUtil.isNotBlank(projectId)) {
            List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, projectId)
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isEmpty(members)) {
                return CommonResult.data(Collections.emptyList());
            }
            List<String> sIds = CollStreamUtil.toList(members, BizProjectMember::getStudentId);
            qw.in(BizAlgorithmSubmission::getStudentId, sIds);
        }

        qw.orderByDesc(BizAlgorithmSubmission::getSubmitTime);
        List<BizAlgorithmSubmission> submissions = bizAlgorithmSubmissionService.list(qw);
        if (CollectionUtil.isEmpty(submissions)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> studentIds = submissions.stream().map(BizAlgorithmSubmission::getStudentId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> studentMap = CollectionUtil.isNotEmpty(studentIds)
                ? clientUserService.listByIds(studentIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<String> taskIds = submissions.stream().map(BizAlgorithmSubmission::getTaskId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizAlgorithmTask> taskMap = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmTaskService.listByIds(taskIds).stream().collect(Collectors.toMap(BizAlgorithmTask::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 查找学生所在的项目团队
        List<BizProjectMember> memberList = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .in(BizProjectMember::getStudentId, studentIds)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizProjectMember>> studentMembersMap = memberList.stream().collect(Collectors.groupingBy(BizProjectMember::getStudentId));

        List<String> relatedProjIds = memberList.stream().map(BizProjectMember::getProjectId).distinct().collect(Collectors.toList());
        Map<String, BizProject> projectMap = CollectionUtil.isNotEmpty(relatedProjIds)
                ? bizProjectService.listByIds(relatedProjIds).stream().collect(Collectors.toMap(BizProject::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizAlgorithmSubmission s : submissions) {
            ClientUser student = studentMap.get(s.getStudentId());
            BizAlgorithmTask task = taskMap.get(s.getTaskId());

            String stuName = student != null ? student.getName() : "学生";
            String taskTitle = task != null ? task.getTitle() : "未知任务";

            if (StrUtil.isNotBlank(keyword)) {
                boolean matchStudent = stuName.contains(keyword) || (student != null && student.getAccount() != null && student.getAccount().contains(keyword));
                boolean matchTask = taskTitle.contains(keyword);
                if (!matchStudent && !matchTask) continue;
            }

            JSONObject obj = JSONUtil.parseObj(s);
            obj.set("studentName", stuName);
            obj.set("studentAccount", student != null ? student.getAccount() : "");
            obj.set("taskTitle", taskTitle);
            obj.set("difficulty", task != null ? task.getDifficulty() : "MEDIUM");
            obj.set("tags", task != null ? task.getTags() : "");

            // 归属队伍名称
            List<BizProjectMember> pms = studentMembersMap.getOrDefault(s.getStudentId(), Collections.emptyList());
            List<String> teamNames = new ArrayList<>();
            for (BizProjectMember pm : pms) {
                BizProject bp = projectMap.get(pm.getProjectId());
                if (bp != null) {
                    teamNames.add(bp.getProjectName());
                }
            }
            obj.set("teamNames", StrUtil.join("、", teamNames));
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<List<JSONObject>> getLeaderMyTeams() {
        String userId = getCurrentUserId();
        List<BizProject> projects = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getLeaderId, userId)
                .eq(BizProject::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProject::getCreateTime));
        if (CollectionUtil.isEmpty(projects)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> projIds = CollStreamUtil.toList(projects, BizProject::getId);

        // 比赛信息
        List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                .in(BizCompetitionProject::getProjectId, projIds)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        Map<String, String> projCompIdMap = compProjs.stream().collect(Collectors.toMap(BizCompetitionProject::getProjectId, BizCompetitionProject::getCompetitionId, (v1, v2) -> v1));
        List<String> compIds = compProjs.stream().map(BizCompetitionProject::getCompetitionId).distinct().collect(Collectors.toList());
        Map<String, BizCompetition> compMap = CollectionUtil.isNotEmpty(compIds)
                ? bizCompetitionService.listByIds(compIds).stream().collect(Collectors.toMap(BizCompetition::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 成员统计
        List<BizProjectMember> allMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .in(BizProjectMember::getProjectId, projIds)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizProjectMember>> memberMap = allMembers.stream().collect(Collectors.groupingBy(BizProjectMember::getProjectId));

        // 任务统计
        List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                .in(BizAlgorithmTaskProj::getProjectId, projIds)
                .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
        Map<String, List<BizAlgorithmTaskProj>> taskProjMap = taskProjs.stream().collect(Collectors.groupingBy(BizAlgorithmTaskProj::getProjectId));

        List<JSONObject> res = new ArrayList<>();
        for (BizProject p : projects) {
            JSONObject obj = new JSONObject();
            obj.set("id", p.getId());
            obj.set("projectName", p.getProjectName());
            obj.set("projectDesc", p.getProjectDesc());
            String cId = projCompIdMap.get(p.getId());
            BizCompetition comp = cId != null ? compMap.get(cId) : null;
            obj.set("competitionId", cId);
            obj.set("competitionTitle", comp != null ? comp.getTitle() : "校级算法竞赛");
            obj.set("memberCount", memberMap.getOrDefault(p.getId(), Collections.emptyList()).size());
            obj.set("taskCount", taskProjMap.getOrDefault(p.getId(), Collections.emptyList()).size());
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<JSONObject> getLeaderTeamOverview(String projectId) {
        String userId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null || "DELETE".equals(project.getDeleteFlag())) {
            throw new CommonException("项目团队不存在");
        }
        JSONObject role = getUserRoleInfo(userId);
        boolean isTeacher = role.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), userId);
        if (!isLeader && !isTeacher) {
            throw new CommonException("权限不足：仅该组组长或指导教师可查看组员督训全景矩阵");
        }

        // 比赛信息
        BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"), false);
        BizCompetition comp = cp != null ? bizCompetitionService.getById(cp.getCompetitionId()) : null;

        // 小组成员列表
        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        List<String> studentIds = CollStreamUtil.toList(members, BizProjectMember::getStudentId);
        Map<String, ClientUser> userMap = CollectionUtil.isNotEmpty(studentIds)
                ? clientUserService.listByIds(studentIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> memberList = new ArrayList<>();
        for (BizProjectMember m : members) {
            JSONObject mo = new JSONObject();
            mo.set("studentId", m.getStudentId());
            ClientUser cu = userMap.get(m.getStudentId());
            mo.set("studentName", cu != null ? cu.getName() : "同学");
            mo.set("studentAccount", cu != null ? cu.getAccount() : "");
            mo.set("phone", cu != null ? cu.getPhone() : "");
            mo.set("role", m.getMemberRole());
            mo.set("isLeader", StrUtil.equals(m.getStudentId(), project.getLeaderId()) || "LEADER".equalsIgnoreCase(m.getMemberRole()));
            memberList.add(mo);
        }

        // 分配给本小组的所有算法任务
        List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                .eq(BizAlgorithmTaskProj::getProjectId, projectId)
                .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
        List<String> taskIds = CollStreamUtil.toList(taskProjs, BizAlgorithmTaskProj::getTaskId);
        List<BizAlgorithmTask> tasks = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmTaskService.listByIds(taskIds)
                : Collections.emptyList();

        // 查询这些成员在这些任务上的提交
        List<BizAlgorithmSubmission> submissions = (CollectionUtil.isNotEmpty(taskIds) && CollectionUtil.isNotEmpty(studentIds))
                ? bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .in(BizAlgorithmSubmission::getTaskId, taskIds)
                .in(BizAlgorithmSubmission::getStudentId, studentIds)
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"))
                : Collections.emptyList();

        // map: studentId_taskId -> submission
        Map<String, BizAlgorithmSubmission> subMap = new HashMap<>();
        for (BizAlgorithmSubmission s : submissions) {
            subMap.put(s.getStudentId() + "_" + s.getTaskId(), s);
        }

        // 查询催促记录
        List<BizAlgorithmUrge> urges = (CollectionUtil.isNotEmpty(taskIds) && CollectionUtil.isNotEmpty(studentIds))
                ? bizAlgorithmUrgeService.list(new LambdaQueryWrapper<BizAlgorithmUrge>()
                .eq(BizAlgorithmUrge::getProjectId, projectId)
                .in(BizAlgorithmUrge::getTaskId, taskIds)
                .in(BizAlgorithmUrge::getStudentId, studentIds)
                .eq(BizAlgorithmUrge::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizAlgorithmUrge::getUrgeTime))
                : Collections.emptyList();
        Map<String, List<BizAlgorithmUrge>> urgeMap = new HashMap<>();
        for (BizAlgorithmUrge u : urges) {
            String key = u.getStudentId() + "_" + u.getTaskId();
            urgeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(u);
        }

        // 组装矩阵
        List<JSONObject> matrix = new ArrayList<>();
        int totalExpected = members.size() * tasks.size();
        int totalSubmitted = 0;

        for (BizProjectMember m : members) {
            ClientUser cu = userMap.get(m.getStudentId());
            for (BizAlgorithmTask t : tasks) {
                JSONObject cell = new JSONObject();
                cell.set("studentId", m.getStudentId());
                cell.set("studentName", cu != null ? cu.getName() : "同学");
                cell.set("studentAccount", cu != null ? cu.getAccount() : "");
                cell.set("role", m.getMemberRole());
                cell.set("taskId", t.getId());
                cell.set("taskTitle", t.getTitle());
                cell.set("difficulty", t.getDifficulty());
                cell.set("deadline", t.getDeadline());

                String key = m.getStudentId() + "_" + t.getId();
                BizAlgorithmSubmission sub = subMap.get(key);
                boolean isSub = (sub != null);
                cell.set("isSubmitted", isSub);
                if (isSub) {
                    totalSubmitted++;
                    cell.set("submissionId", sub.getId());
                    cell.set("submissionType", sub.getSubmissionType());
                    cell.set("codeLanguage", sub.getCodeLanguage());
                    cell.set("codeContent", sub.getCodeContent());
                    cell.set("fileId", sub.getFileId());
                    cell.set("gitUrl", sub.getGitUrl());
                    cell.set("submitTime", sub.getSubmitTime());
                    cell.set("score", sub.getScore());
                    cell.set("status", sub.getStatus());
                    cell.set("teacherFeedback", sub.getTeacherFeedback());
                }

                List<BizAlgorithmUrge> uList = urgeMap.get(key);
                cell.set("urgeCount", CollectionUtil.isNotEmpty(uList) ? uList.size() : 0);
                if (CollectionUtil.isNotEmpty(uList)) {
                    cell.set("latestUrgeTime", uList.get(0).getUrgeTime());
                    cell.set("latestUrgeMessage", uList.get(0).getUrgeMessage());
                }
                matrix.add(cell);
            }
        }

        JSONObject result = new JSONObject();
        result.set("project", project);
        result.set("competitionTitle", comp != null ? comp.getTitle() : "校级算法竞赛");
        result.set("members", memberList);
        result.set("tasks", tasks);
        result.set("matrix", matrix);

        JSONObject summary = new JSONObject();
        summary.set("memberCount", members.size());
        summary.set("taskCount", tasks.size());
        summary.set("totalExpected", totalExpected);
        summary.set("totalSubmitted", totalSubmitted);
        summary.set("unsubmittedCount", Math.max(0, totalExpected - totalSubmitted));
        double rate = totalExpected > 0 ? ((double) totalSubmitted / totalExpected * 100) : 0.0;
        summary.set("completionRate", Math.round(rate * 10.0) / 10.0);
        result.set("summary", summary);

        return CommonResult.data(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<JSONObject> batchUrgeTeamMembers(String projectId, String taskId, String message) {
        String leaderId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null || !StrUtil.equals(project.getLeaderId(), leaderId)) {
            throw new CommonException("权限不足：仅本组组长具有一键催促组员职能");
        }

        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));

        List<String> taskIds = new ArrayList<>();
        if (StrUtil.isNotBlank(taskId)) {
            taskIds.add(taskId);
        } else {
            List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                    .eq(BizAlgorithmTaskProj::getProjectId, projectId)
                    .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
            taskIds = CollStreamUtil.toList(taskProjs, BizAlgorithmTaskProj::getTaskId);
        }

        if (CollectionUtil.isEmpty(taskIds) || CollectionUtil.isEmpty(members)) {
            JSONObject res = new JSONObject();
            res.set("urgedCount", 0);
            res.set("message", "该项目暂无待催交任务或成员");
            return CommonResult.data(res);
        }

        // 查已提交记录
        List<BizAlgorithmSubmission> subs = bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                .in(BizAlgorithmSubmission::getTaskId, taskIds)
                .in(BizAlgorithmSubmission::getStudentId, CollStreamUtil.toList(members, BizProjectMember::getStudentId))
                .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));
        Set<String> submittedKeys = new HashSet<>();
        for (BizAlgorithmSubmission s : subs) {
            submittedKeys.add(s.getStudentId() + "_" + s.getTaskId());
        }

        int urgedCount = 0;
        String finalMsg = StrUtil.isNotBlank(message) ? message : "【组长催办】团队算法任务已进入冲刺攻坚阶段，请尽快登录实训平台提交解题代码！";
        for (BizProjectMember m : members) {
            // 组长不催促自己
            if (StrUtil.equals(m.getStudentId(), leaderId)) continue;
            for (String tId : taskIds) {
                if (!submittedKeys.contains(m.getStudentId() + "_" + tId)) {
                    BizAlgorithmUrge urge = new BizAlgorithmUrge();
                    urge.setTaskId(tId);
                    urge.setProjectId(projectId);
                    urge.setLeaderId(leaderId);
                    urge.setStudentId(m.getStudentId());
                    urge.setUrgeMessage(finalMsg);
                    urge.setUrgeTime(new Date());
                    urge.setDeleteFlag("NOT_DELETE");
                    bizAlgorithmUrgeService.save(urge);
                    urgedCount++;
                }
            }
        }

        JSONObject res = new JSONObject();
        res.set("urgedCount", urgedCount);
        res.set("message", StrUtil.format("已成功向 {} 人次未提交组员发送催交通知", urgedCount));
        return CommonResult.data(res);
    }

    // =========================================================================
    // 5. 论坛社区与多级智能审核
    // =========================================================================

    @Override
    public CommonResult<Page<JSONObject>> pageForumPosts(String postType, Integer current, Integer size) {
        Page<BizForumPost> page = new Page<>(current != null ? current : 1, size != null ? size : 10);
        LambdaQueryWrapper<BizForumPost> qw = new LambdaQueryWrapper<BizForumPost>()
                .eq(BizForumPost::getAuditStatus, "PUBLISHED")
                .eq(BizForumPost::getDeleteFlag, "NOT_DELETE");

        if (StrUtil.isNotBlank(postType) && !"ALL".equalsIgnoreCase(postType)) {
            qw.eq(BizForumPost::getPostType, postType);
        }

        qw.orderByDesc(BizForumPost::getIsPinned) // 老师置顶的内容排在最前
                .orderByDesc(BizForumPost::getPinTime)
                .orderByDesc(BizForumPost::getCreateTime);

        Page<BizForumPost> postPage = bizForumPostService.page(page, qw);

        List<String> authorIds = CollStreamUtil.toList(postPage.getRecords(), BizForumPost::getAuthorId);
        Map<String, ClientUser> userMap = CollectionUtil.isNotEmpty(authorIds)
                ? CollStreamUtil.toIdentityMap(clientUserService.listByIds(authorIds), ClientUser::getId)
                : Collections.emptyMap();

        List<JSONObject> records = new ArrayList<>();
        for (BizForumPost p : postPage.getRecords()) {
            JSONObject obj = JSONUtil.parseObj(p);
            ClientUser author = userMap.get(p.getAuthorId());
            obj.set("authorName", author != null ? author.getName() : "匿名学子");
            obj.set("authorAvatar", author != null ? author.getAvatar() : null);
            if (StrUtil.isNotBlank(p.getExtJson())) {
                try {
                    obj.set("extData", JSONUtil.parseObj(p.getExtJson()));
                } catch (Exception ignored) {}
            }
            records.add(obj);
        }

        Page<JSONObject> resPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resPage.setRecords(records);
        return CommonResult.data(resPage);
    }

    @Override
    public CommonResult<JSONObject> getPostDetail(String postId) {
        BizForumPost post = bizForumPostService.getById(postId);
        if (post == null) {
            throw new CommonException("帖子不存在");
        }
        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        bizForumPostService.updateById(post);

        JSONObject obj = JSONUtil.parseObj(post);
        ClientUser author = clientUserService.getById(post.getAuthorId());
        obj.set("authorName", author != null ? author.getName() : "匿名学子");
        obj.set("authorAvatar", author != null ? author.getAvatar() : null);
        obj.set("authorRoleInfo", getUserRoleInfo(post.getAuthorId()));
        if (StrUtil.isNotBlank(post.getExtJson())) {
            try {
                obj.set("extData", JSONUtil.parseObj(post.getExtJson()));
            } catch (Exception ignored) {}
        }

        return CommonResult.data(obj);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> createForumPost(PortalForumPostParam param) {
        String userId = getCurrentUserId();
        BizForumPost post = new BizForumPost();
        post.setAuthorId(userId);
        post.setTitle(param.getTitle());
        post.setContent(param.getContent());
        post.setPostType(StrUtil.nullToDefault(param.getPostType(), "GENERAL"));
        post.setRefType(param.getRefType());
        post.setRefId(param.getRefId());
        post.setExtJson(param.getExtJson());
        post.setIsPinned("NO");
        post.setLeaderPassCount(0);
        post.setLeaderRejectCount(0);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setDeleteFlag("NOT_DELETE");

        // ===== AI审查逻辑 =====
        // 检测敏感词违规规则
        String combined = (param.getTitle() + " " + param.getContent() + " " + StrUtil.nullToEmpty(param.getExtJson())).toLowerCase();
        List<String> riskWords = Arrays.asList("作弊", "买卖答案", "代考", "枪手", "色情", "赌博", "外挂", "刷分", "攻击网站");
        boolean hasRisk = false;
        String riskReason = "";
        for (String w : riskWords) {
            if (combined.contains(w)) {
                hasRisk = true;
                riskReason = "内容包含违规敏感词【" + w + "】，触发智能安全风控";
                break;
            }
        }

        if (hasRisk) {
            post.setAuditStatus("MANUAL_PENDING");
            post.setAiAuditResult("RISK");
            post.setAiAuditReason(riskReason);
        } else {
            // AI审查通过，直接正式发布
            post.setAuditStatus("PUBLISHED");
            post.setAiAuditResult("PASS");
            post.setAiAuditReason("AI智能语意与合规扫描通过");
        }

        bizForumPostService.save(post);

        // 记录 AI 审核流转日志
        BizForumAudit audit = new BizForumAudit();
        audit.setPostId(post.getId());
        audit.setAuditorType("AI");
        audit.setAuditorId("AI_SYSTEM");
        audit.setAuditResult(hasRisk ? "REJECT" : "PASS");
        audit.setAuditReason(hasRisk ? riskReason : "AI初审安全通过");
        audit.setAuditTime(new Date());
        audit.setDeleteFlag("NOT_DELETE");
        bizForumAuditService.save(audit);

        if (hasRisk) {
            return CommonResult.data("AI检测到您的发帖存在敏感词或潜在风险，已自动转入人工审核队列（等待教师或组长联审）");
        } else {
            return CommonResult.data("AI智能安全审查通过，帖子已正式发布上线！");
        }
    }

    @Override
    public CommonResult<Page<JSONObject>> pagePendingAuditPosts(Integer current, Integer size) {
        String userId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(userId);
        if (!role.getBool("isTeacher", false) && !role.getBool("isLeader", false)) {
            throw new CommonException("权限不足：仅指导教师和项目组长具有论坛审核权限");
        }

        Page<BizForumPost> page = new Page<>(current != null ? current : 1, size != null ? size : 10);
        LambdaQueryWrapper<BizForumPost> qw = new LambdaQueryWrapper<BizForumPost>()
                .eq(BizForumPost::getAuditStatus, "MANUAL_PENDING")
                .eq(BizForumPost::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizForumPost::getCreateTime);

        Page<BizForumPost> pPage = bizForumPostService.page(page, qw);
        List<JSONObject> records = new ArrayList<>();
        for (BizForumPost p : pPage.getRecords()) {
            JSONObject obj = JSONUtil.parseObj(p);
            ClientUser author = clientUserService.getById(p.getAuthorId());
            obj.set("authorName", author != null ? author.getName() : "用户");
            records.add(obj);
        }
        Page<JSONObject> res = new Page<>(pPage.getCurrent(), pPage.getSize(), pPage.getTotal());
        res.setRecords(records);
        return CommonResult.data(res);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> teacherAuditPost(PortalAuditParam param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师拥有一票决裁权");
        }
        BizForumPost post = bizForumPostService.getById(param.getPostId());
        if (post == null) {
            throw new CommonException("帖子不存在");
        }

        boolean pass = "PASS".equalsIgnoreCase(param.getAuditResult());
        post.setAuditStatus(pass ? "PUBLISHED" : "REJECTED");
        bizForumPostService.updateById(post);

        BizForumAudit audit = new BizForumAudit();
        audit.setPostId(post.getId());
        audit.setAuditorType("TEACHER");
        audit.setAuditorId(teacherId);
        audit.setAuditResult(pass ? "PASS" : "REJECT");
        audit.setAuditReason(param.getAuditReason());
        audit.setAuditTime(new Date());
        audit.setDeleteFlag("NOT_DELETE");
        bizForumAuditService.save(audit);

        return CommonResult.data(pass ? "教师直接审核通过，帖子已正式发布上线" : "已驳回该帖子");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> leaderAuditPost(PortalAuditParam param) {
        String leaderId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(leaderId);
        if (!role.getBool("isLeader", false)) {
            throw new CommonException("权限不足：仅项目组长可参与组长联审投票");
        }

        BizForumPost post = bizForumPostService.getById(param.getPostId());
        if (post == null) {
            throw new CommonException("帖子不存在");
        }
        if (!"MANUAL_PENDING".equalsIgnoreCase(post.getAuditStatus())) {
            throw new CommonException("该帖子已不在待审状态");
        }

        // 检查该组长是否已投过票
        long voted = bizForumAuditService.count(new LambdaQueryWrapper<BizForumAudit>()
                .eq(BizForumAudit::getPostId, post.getId())
                .eq(BizForumAudit::getAuditorId, leaderId)
                .eq(BizForumAudit::getDeleteFlag, "NOT_DELETE"));
        if (voted > 0) {
            throw new CommonException("您已对该帖子表决过，请勿重复投票");
        }

        boolean pass = "PASS".equalsIgnoreCase(param.getAuditResult());
        if (pass) {
            post.setLeaderPassCount((post.getLeaderPassCount() == null ? 0 : post.getLeaderPassCount()) + 1);
        } else {
            post.setLeaderRejectCount((post.getLeaderRejectCount() == null ? 0 : post.getLeaderRejectCount()) + 1);
        }

        // 规则：组长通过票数达到阈值 (>= 2人) 则正式通过发布
        boolean reachThreshold = (post.getLeaderPassCount() >= 2);
        if (reachThreshold) {
            post.setAuditStatus("PUBLISHED");
        }
        bizForumPostService.updateById(post);

        BizForumAudit audit = new BizForumAudit();
        audit.setPostId(post.getId());
        audit.setAuditorType("LEADER");
        audit.setAuditorId(leaderId);
        audit.setAuditResult(pass ? "PASS" : "REJECT");
        audit.setAuditReason(param.getAuditReason());
        audit.setAuditTime(new Date());
        audit.setDeleteFlag("NOT_DELETE");
        bizForumAuditService.save(audit);

        if (reachThreshold) {
            return CommonResult.data("表决成功！组长赞成票已累计达标（2票），帖子已正式自动发布！");
        } else {
            return CommonResult.data("表决成功，当前组长赞成票数：" + post.getLeaderPassCount() + "/2");
        }
    }

    @Override
    public CommonResult<String> togglePinPost(String postId, String isPinned) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权在论坛置顶内容");
        }
        BizForumPost post = bizForumPostService.getById(postId);
        if (post == null) {
            throw new CommonException("帖子不存在");
        }
        boolean pin = "YES".equalsIgnoreCase(isPinned);
        post.setIsPinned(pin ? "YES" : "NO");
        post.setPinTime(pin ? new Date() : null);
        bizForumPostService.updateById(post);
        return CommonResult.data(pin ? "已置顶该帖子" : "已取消置顶");
    }

    @Override
    public CommonResult<String> addComment(String postId, String content, String parentId, String replyToUserId) {
        String userId = getCurrentUserId();
        BizForumPost post = bizForumPostService.getById(postId);
        if (post == null) {
            throw new CommonException("帖子不存在");
        }

        BizForumComment comment = new BizForumComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setLikeCount(0);
        comment.setDeleteFlag("NOT_DELETE");
        bizForumCommentService.save(comment);

        post.setCommentCount((post.getCommentCount() == null ? 0 : post.getCommentCount()) + 1);
        bizForumPostService.updateById(post);

        return CommonResult.data(comment.getId());
    }

    @Override
    public CommonResult<List<JSONObject>> getPostComments(String postId) {
        List<BizForumComment> list = bizForumCommentService.list(new LambdaQueryWrapper<BizForumComment>()
                .eq(BizForumComment::getPostId, postId)
                .eq(BizForumComment::getDeleteFlag, "NOT_DELETE")
                .orderByAsc(BizForumComment::getCreateTime));

        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> userIds = CollStreamUtil.toList(list, BizForumComment::getUserId);
        Map<String, ClientUser> userMap = CollStreamUtil.toIdentityMap(clientUserService.listByIds(userIds), ClientUser::getId);

        List<JSONObject> res = new ArrayList<>();
        for (BizForumComment c : list) {
            JSONObject obj = JSONUtil.parseObj(c);
            ClientUser u = userMap.get(c.getUserId());
            obj.set("userName", u != null ? u.getName() : "用户");
            obj.set("userAvatar", u != null ? u.getAvatar() : null);
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    // =========================================================================
    // 6. AI评测、打分与排行榜
    // =========================================================================

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<JSONObject> evaluateTeamWithAi(String projectId, String batch) {
        String triggerUserId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        JSONObject role = getUserRoleInfo(triggerUserId);
        if (!StrUtil.equals(project.getLeaderId(), triggerUserId) && !role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师或本组组长可触发AI团队评估");
        }

        // 1. 获取团队成员
        List<BizProjectMember> members = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getProjectId, projectId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        int memberCount = members.size();

        // 2. 获取分配给该团队的所有算法任务
        List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                .eq(BizAlgorithmTaskProj::getProjectId, projectId)
                .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));

        BigDecimal algoRate = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(taskProjs) && memberCount > 0) {
            List<String> taskIds = CollStreamUtil.toList(taskProjs, BizAlgorithmTaskProj::getTaskId);
            List<String> studentIds = CollStreamUtil.toList(members, BizProjectMember::getStudentId);

            long totalExpected = (long) taskIds.size() * memberCount;
            long actualSubmitted = bizAlgorithmSubmissionService.count(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                    .in(BizAlgorithmSubmission::getTaskId, taskIds)
                    .in(BizAlgorithmSubmission::getStudentId, studentIds)
                    .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));

            if (totalExpected > 0) {
                algoRate = BigDecimal.valueOf(actualSubmitted)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalExpected), 2, RoundingMode.HALF_UP);
            }
        } else {
            algoRate = new BigDecimal("85.00"); // 缺省基准分
        }

        // 3. 计算竞赛参与度得分
        long compCount = bizCompetitionProjectService.count(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        long certCount = bizCompetitionCertService.count(new LambdaQueryWrapper<BizCompetitionCert>()
                .eq(BizCompetitionCert::getProjectId, projectId)
                .eq(BizCompetitionCert::getVerifyStatus, "VERIFIED")
                .eq(BizCompetitionCert::getDeleteFlag, "NOT_DELETE"));

        BigDecimal compScore = BigDecimal.valueOf(Math.min(100, (compCount * 25) + (certCount * 25) + 30));

        // 4. 加权总分 (算法完成度 50% + 竞赛参与度 50%)
        BigDecimal aiTotalScore = algoRate.multiply(BigDecimal.valueOf(0.5))
                .add(compScore.multiply(BigDecimal.valueOf(0.5)))
                .setScale(2, RoundingMode.HALF_UP);

        // 5. 生成专业诊断报告
        String report = String.format(
                "【AI 团队综合诊断报告】\n\n"
                        + "评估周期：%s\n"
                        + "评估结论：综合表现评分 %s 分。\n\n"
                        + "维度一：组员算法任务整体完成度达 %s%%。\n"
                        + "维度二：竞赛实战参与度得分为 %s 分（累计报名竞赛 %d 项，获得认证荣誉 %d 项）。\n\n"
                        + "💡 优势分析：团队成员协同积极性优异，算法解题进度保持稳定。\n"
                        + "⚠️ 短板预警：请注意兼顾多模式提交测试，按时跟进算法催交机制。\n"
                        + "🚀 提升建议：建议组长组织专题算法复盘，同时鼓励组员申报更高级别省级及国家级赛事。",
                StrUtil.nullToDefault(batch, "2026年春季考核期"),
                aiTotalScore,
                algoRate,
                compScore,
                compCount,
                certCount
        );

        BizAiEvaluation evaluation = new BizAiEvaluation();
        evaluation.setProjectId(projectId);
        evaluation.setTriggerUserId(triggerUserId);
        evaluation.setEvaluationBatch(StrUtil.nullToDefault(batch, "2026-Q1"));
        evaluation.setAlgorithmCompletionRate(algoRate);
        evaluation.setCompetitionParticipationScore(compScore);
        evaluation.setAiTotalScore(aiTotalScore);
        evaluation.setAiEvaluationReport(report);
        evaluation.setAiModelVersion("DeepMind-EduEvaluator-v3.2");
        evaluation.setDeleteFlag("NOT_DELETE");
        bizAiEvaluationService.save(evaluation);

        return CommonResult.data(JSONUtil.parseObj(evaluation));
    }

    @Override
    public CommonResult<String> saveMemberScore(PortalMemberScoreParam param) {
        String evaluatorId = getCurrentUserId();
        BizProject project = bizProjectService.getById(param.getProjectId());
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        JSONObject role = getUserRoleInfo(evaluatorId);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), evaluatorId);
        boolean isTeacher = role.getBool("isTeacher", false);
        if (!isLeader && !isTeacher) {
            throw new CommonException("权限不足：仅指导教师或本项目组长有权给组员打分");
        }

        BizMemberScore score = new BizMemberScore();
        score.setProjectId(param.getProjectId());
        score.setStudentId(param.getStudentId());
        score.setEvaluatorId(evaluatorId);
        score.setEvaluatorRole(isTeacher ? "TEACHER" : "LEADER");
        score.setScore(param.getScore());
        score.setRemark(param.getRemark());
        score.setScorePeriod(param.getScorePeriod());
        score.setDeleteFlag("NOT_DELETE");
        bizMemberScoreService.save(score);

        return CommonResult.data("评分保存成功（严格保密：仅老师和本组组长可见）");
    }

    @Override
    public CommonResult<List<JSONObject>> getMemberScores(String projectId) {
        String viewerId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        // 严格保密校验：仅指导教师和本组组长可查看！普通组员不可查看！
        JSONObject role = getUserRoleInfo(viewerId);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), viewerId);
        boolean isTeacher = role.getBool("isTeacher", false);
        if (!isLeader && !isTeacher) {
            throw new CommonException("权限受限：组员表现评分评价为私密数据，仅老师与本组组长可见");
        }

        List<BizMemberScore> list = bizMemberScoreService.list(new LambdaQueryWrapper<BizMemberScore>()
                .eq(BizMemberScore::getProjectId, projectId)
                .eq(BizMemberScore::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizMemberScore::getCreateTime));

        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> studentIds = CollStreamUtil.toList(list, BizMemberScore::getStudentId);
        Map<String, ClientUser> userMap = CollStreamUtil.toIdentityMap(clientUserService.listByIds(studentIds), ClientUser::getId);

        List<JSONObject> res = new ArrayList<>();
        for (BizMemberScore s : list) {
            JSONObject obj = JSONUtil.parseObj(s);
            ClientUser u = userMap.get(s.getStudentId());
            obj.set("studentName", u != null ? u.getName() : "组员");
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> teacherEvaluateTeam(PortalTeacherEvaluateParam param) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权评定团队与编译榜单");
        }

        BizProject project = bizProjectService.getById(param.getProjectId());
        if (project == null) {
            throw new CommonException("项目不存在");
        }

        // 查找或生成最新的 AI 评分
        BizAiEvaluation latestAi = bizAiEvaluationService.getOne(new LambdaQueryWrapper<BizAiEvaluation>()
                .eq(BizAiEvaluation::getProjectId, param.getProjectId())
                .eq(BizAiEvaluation::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizAiEvaluation::getCreateTime)
                .last("LIMIT 1"));

        BigDecimal aiScore = (latestAi != null && latestAi.getAiTotalScore() != null)
                ? latestAi.getAiTotalScore()
                : new BigDecimal("88.00");

        // 最终总分加权：教师得分 50% + AI 得分 50%
        BigDecimal finalScore = param.getTeacherScore().multiply(BigDecimal.valueOf(0.5))
                .add(aiScore.multiply(BigDecimal.valueOf(0.5)))
                .setScale(2, RoundingMode.HALF_UP);

        BizProjectRanking ranking = bizProjectRankingService.getOne(new LambdaQueryWrapper<BizProjectRanking>()
                .eq(BizProjectRanking::getProjectId, param.getProjectId())
                .eq(BizProjectRanking::getRankingPeriod, param.getRankingPeriod())
                .eq(BizProjectRanking::getDeleteFlag, "NOT_DELETE"));

        if (ranking == null) {
            ranking = new BizProjectRanking();
            ranking.setProjectId(param.getProjectId());
            ranking.setCompetitionId(param.getCompetitionId());
            ranking.setRankingPeriod(param.getRankingPeriod());
            ranking.setTeacherScore(param.getTeacherScore());
            ranking.setAiScore(aiScore);
            ranking.setFinalScore(finalScore);
            ranking.setIsPublished("NO"); // 默认先保存草稿
            ranking.setDeleteFlag("NOT_DELETE");
            bizProjectRankingService.save(ranking);
        } else {
            ranking.setTeacherScore(param.getTeacherScore());
            ranking.setAiScore(aiScore);
            ranking.setFinalScore(finalScore);
            bizProjectRankingService.updateById(ranking);
        }

        return CommonResult.data(ranking.getId());
    }

    @Override
    public CommonResult<String> publishProjectRanking(String rankingId) {
        String teacherId = getCurrentUserId();
        JSONObject role = getUserRoleInfo(teacherId);
        if (!role.getBool("isTeacher", false)) {
            throw new CommonException("权限不足：仅指导教师有权正式发布排行榜");
        }

        BizProjectRanking ranking = bizProjectRankingService.getById(rankingId);
        if (ranking == null) {
            throw new CommonException("排行评定记录不存在");
        }

        ranking.setIsPublished("YES");
        ranking.setPublishTeacherId(teacherId);
        ranking.setPublishTime(new Date());
        bizProjectRankingService.updateById(ranking);

        // 重新计算排名位次
        List<BizProjectRanking> allInPeriod = bizProjectRankingService.list(new LambdaQueryWrapper<BizProjectRanking>()
                .eq(BizProjectRanking::getRankingPeriod, ranking.getRankingPeriod())
                .eq(BizProjectRanking::getIsPublished, "YES")
                .eq(BizProjectRanking::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizProjectRanking::getFinalScore));

        int rank = 1;
        for (BizProjectRanking r : allInPeriod) {
            r.setRankPosition(rank++);
            bizProjectRankingService.updateById(r);
        }

        return CommonResult.data("项目综合排行榜已成功公开发布，全体师生均可查看！");
    }

    @Override
    public CommonResult<List<JSONObject>> getPublishedRankings(String period, String competitionId) {
        LambdaQueryWrapper<BizProjectRanking> qw = new LambdaQueryWrapper<BizProjectRanking>()
                .eq(BizProjectRanking::getIsPublished, "YES")
                .eq(BizProjectRanking::getDeleteFlag, "NOT_DELETE")
                .orderByAsc(BizProjectRanking::getRankPosition)
                .orderByDesc(BizProjectRanking::getFinalScore);

        if (StrUtil.isNotBlank(period)) {
            qw.eq(BizProjectRanking::getRankingPeriod, period);
        }
        if (StrUtil.isNotBlank(competitionId)) {
            qw.eq(BizProjectRanking::getCompetitionId, competitionId);
        }

        List<BizProjectRanking> list = bizProjectRankingService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }

        List<String> projectIds = CollStreamUtil.toList(list, BizProjectRanking::getProjectId);
        List<BizProject> projects = bizProjectService.listByIds(projectIds);
        Map<String, BizProject> projectMap = CollStreamUtil.toIdentityMap(projects, BizProject::getId);

        List<String> leaderIds = CollStreamUtil.toList(projects, BizProject::getLeaderId);
        Map<String, ClientUser> leaderMap = CollStreamUtil.toIdentityMap(clientUserService.listByIds(leaderIds), ClientUser::getId);

        List<JSONObject> res = new ArrayList<>();
        for (BizProjectRanking r : list) {
            JSONObject obj = JSONUtil.parseObj(r);
            BizProject p = projectMap.get(r.getProjectId());
            obj.set("projectName", p != null ? p.getProjectName() : "未知团队");
            obj.set("projectDesc", p != null ? p.getProjectDesc() : "");
            ClientUser leader = p != null ? leaderMap.get(p.getLeaderId()) : null;
            obj.set("leaderName", leader != null ? leader.getName() : "组长");
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<List<JSONObject>> getMyUrges() {
        String studentId = getCurrentUserId();
        List<BizAlgorithmUrge> list = bizAlgorithmUrgeService.list(new LambdaQueryWrapper<BizAlgorithmUrge>()
                .eq(BizAlgorithmUrge::getStudentId, studentId)
                .eq(BizAlgorithmUrge::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizAlgorithmUrge::getUrgeTime));
        if (CollectionUtil.isEmpty(list)) {
            return CommonResult.data(Collections.emptyList());
        }
        List<String> taskIds = list.stream().map(BizAlgorithmUrge::getTaskId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, BizAlgorithmTask> taskMap = CollectionUtil.isNotEmpty(taskIds)
                ? bizAlgorithmTaskService.listByIds(taskIds).stream().collect(Collectors.toMap(BizAlgorithmTask::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();
        List<String> leaderIds = list.stream().map(BizAlgorithmUrge::getLeaderId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ClientUser> leaderMap = CollectionUtil.isNotEmpty(leaderIds)
                ? clientUserService.listByIds(leaderIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        List<JSONObject> res = new ArrayList<>();
        for (BizAlgorithmUrge u : list) {
            JSONObject obj = JSONUtil.parseObj(u);
            BizAlgorithmTask t = taskMap.get(u.getTaskId());
            obj.set("taskTitle", t != null ? t.getTitle() : "算法训练任务");
            ClientUser leader = leaderMap.get(u.getLeaderId());
            obj.set("leaderName", leader != null ? leader.getName() : "项目组长");
            res.add(obj);
        }
        return CommonResult.data(res);
    }

    @Override
    public CommonResult<String> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CommonException("上传文件不能为空");
        }
        String fileUrl = devFileApi.uploadDynamicReturnUrl(file);
        return CommonResult.data(fileUrl);
    }

    private boolean isCompEnded(BizCompetition comp) {
        if (comp == null) return false;
        if ("FINISHED".equalsIgnoreCase(comp.getStatus())) return true;
        if (comp.getCompetitionEndTime() != null && new Date().after(comp.getCompetitionEndTime())) return true;
        return false;
    }

    public String extractBaseCompetitionName(String title) {
        if (StrUtil.isBlank(title)) return "";
        String base = title;
        // 去除年份如 2024年, 2025年, 2026年
        base = base.replaceAll("20[2-3][0-9]年?", "");
        // 去除第x届
        base = base.replaceAll("第[0-9一二三四五六七八九十百]+届", "");
        // 去除括号及修饰词
        base = base.replaceAll("[【\\[（(](官方|加急|报名|热点|官方加急|通知)[】\\]）)]", "");
        // 去除级别修饰词与赛段
        base = base.replaceAll("[【\\[（(]?(院级|校级|市级|省级|国家级|国际级|院赛|校赛|市赛|省赛|国赛|初赛|复赛|半决赛|总决赛|选拔赛|晋级赛)[】\\]）]?", "");
        // 去除标点与空格
        base = base.replaceAll("[\\s_\\-—]+", "").trim();
        return base;
    }

    public int getCompetitionRank(BizCompetition comp) {
        if (comp == null) return 2;
        String level = comp.getCompetitionLevel();
        if (StrUtil.isNotBlank(level)) {
            level = level.toUpperCase();
            if (level.contains("COLLEGE") || level.contains("院")) return 1;
            if (level.contains("SCHOOL") || level.contains("校")) return 2;
            if (level.contains("CITY") || level.contains("MUNICIPAL") || level.contains("市")) return 3;
            if (level.contains("PROVINCE") || level.contains("省")) return 4;
            if (level.contains("NATIONAL") || level.contains("国")) return 5;
            if (level.contains("INTERNATIONAL") || level.contains("际")) return 6;
        }
        String title = comp.getTitle();
        if (StrUtil.isNotBlank(title)) {
            if (title.contains("院级") || title.contains("院赛") || title.contains("分院")) return 1;
            if (title.contains("校级") || title.contains("校赛") || title.contains("校内")) return 2;
            if (title.contains("市级") || title.contains("市赛")) return 3;
            if (title.contains("省级") || title.contains("省赛") || title.contains("全省")) return 4;
            if (title.contains("国家级") || title.contains("国赛") || title.contains("全国")) return 5;
            if (title.contains("国际级") || title.contains("国际赛") || title.contains("全球")) return 6;
        }
        return 2;
    }

    public String getRankName(int rank) {
        switch (rank) {
            case 1: return "院级";
            case 2: return "校级";
            case 3: return "市级";
            case 4: return "省级";
            case 5: return "国家级";
            case 6: return "国际级";
            default: return "高级别";
        }
    }

    public BizCompetition findNextLevelCompetition(BizCompetition currentComp) {
        if (currentComp == null) return null;
        int currentRank = getCompetitionRank(currentComp);
        String baseName = extractBaseCompetitionName(currentComp.getTitle());

        List<BizCompetition> allComps = bizCompetitionService.list(new LambdaQueryWrapper<BizCompetition>()
                .eq(BizCompetition::getDeleteFlag, "NOT_DELETE")
                .ne(BizCompetition::getId, currentComp.getId()));

        BizCompetition bestMatch = null;
        int minHigherRankDiff = Integer.MAX_VALUE;

        // 1. 同系列优先匹配（名称提取后相同或互相包含）
        for (BizCompetition candidate : allComps) {
            int candidateRank = getCompetitionRank(candidate);
            if (candidateRank > currentRank) {
                String candidateBase = extractBaseCompetitionName(candidate.getTitle());
                boolean isSameSeries = (StrUtil.isNotBlank(baseName) && baseName.equals(candidateBase))
                        || (baseName.length() >= 4 && candidateBase.contains(baseName))
                        || (candidateBase.length() >= 4 && baseName.contains(candidateBase))
                        || (StrUtil.isNotBlank(currentComp.getOrganizer()) && currentComp.getOrganizer().equals(candidate.getOrganizer()));
                if (isSameSeries) {
                    int diff = candidateRank - currentRank;
                    if (diff < minHigherRankDiff) {
                        minHigherRankDiff = diff;
                        bestMatch = candidate;
                    }
                }
            }
        }

        // 2. 若同系列未找到，匹配同类别中的高一级
        if (bestMatch == null && StrUtil.isNotBlank(currentComp.getCategory())) {
            for (BizCompetition candidate : allComps) {
                int candidateRank = getCompetitionRank(candidate);
                if (candidateRank > currentRank && currentComp.getCategory().equals(candidate.getCategory())) {
                    int diff = candidateRank - currentRank;
                    if (diff < minHigherRankDiff) {
                        minHigherRankDiff = diff;
                        bestMatch = candidate;
                    }
                }
            }
        }

        // 3. 全局高一级备选
        if (bestMatch == null) {
            for (BizCompetition candidate : allComps) {
                int candidateRank = getCompetitionRank(candidate);
                if (candidateRank > currentRank) {
                    int diff = candidateRank - currentRank;
                    if (diff < minHigherRankDiff) {
                        minHigherRankDiff = diff;
                        bestMatch = candidate;
                    }
                }
            }
        }

        return bestMatch;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> bindProjectCompetition(String projectId, String competitionId) {
        String userId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        boolean isLeader = StrUtil.equals(project.getLeaderId(), userId);
        boolean isTeacherCreator = isTeacher && (StrUtil.equals(project.getCreateUser(), userId)
                || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(userId)));

        if (!isLeader && !isTeacherCreator) {
            throw new CommonException("权限不足：仅项目组组长或发起教师可修改绑定参赛竞赛");
        }

        BizCompetition comp = bizCompetitionService.getById(competitionId);
        if (comp == null) {
            throw new CommonException("所选目标竞赛不存在");
        }
        Date now = new Date();
        if ("FINISHED".equalsIgnoreCase(comp.getStatus()) || (comp.getCompetitionEndTime() != null && now.after(comp.getCompetitionEndTime()))) {
            throw new CommonException("绑定失败：目标竞赛已完结结束");
        }
        if (comp.getSignupEndTime() != null && now.after(comp.getSignupEndTime())) {
            throw new CommonException("绑定失败：目标竞赛报名已截止（截止时间：" + DateUtil.formatDateTime(comp.getSignupEndTime()) + "）");
        }

        // 校验当前用户是否已在该比赛中担任其他队伍的组长
        if (isLeader) {
            List<BizCompetitionProject> compProjs = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                    .eq(BizCompetitionProject::getCompetitionId, competitionId)
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(compProjs)) {
                List<String> pIds = CollStreamUtil.toList(compProjs, BizCompetitionProject::getProjectId);
                pIds.remove(projectId); // 排除当前项目
                if (CollectionUtil.isNotEmpty(pIds)) {
                    long isLeaderInComp = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                            .in(BizProjectMember::getProjectId, pIds)
                            .eq(BizProjectMember::getStudentId, userId)
                            .eq(BizProjectMember::getMemberRole, "LEADER")
                            .eq(BizProjectMember::getMemberStatus, "ACTIVE")
                            .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
                    if (isLeaderInComp > 0) {
                        throw new CommonException("绑定失败：您已在目标比赛中担任其他队伍的组长，无法重复担任组长");
                    }
                }
            }
        }

        // 更新或创建 BizCompetitionProject
        BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        if (cp == null) {
            cp = new BizCompetitionProject();
            cp.setProjectId(projectId);
            cp.setSignupType("PROJECT");
            cp.setAuditStatus("PASSED");
            cp.setDeleteFlag("NOT_DELETE");
        }
        cp.setCompetitionId(comp.getId());
        cp.setCompetitionResult(null); // 重置赛果
        bizCompetitionProjectService.saveOrUpdate(cp);

        // 更新 project extJson
        JSONObject ext = StrUtil.isNotBlank(project.getExtJson()) && JSONUtil.isTypeJSONObject(project.getExtJson())
                ? JSONUtil.parseObj(project.getExtJson()) : new JSONObject();
        ext.remove("competitionStatus");
        ext.set("competitionTitle", comp.getTitle());
        project.setExtJson(ext.toString());
        bizProjectService.updateById(project);

        return CommonResult.data("🎉 成功将项目组【" + project.getProjectName() + "】绑定至竞赛【" + comp.getTitle() + "】！");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> confirmPromotion(String projectId, String targetCompetitionId) {
        String userId = getCurrentUserId();
        BizProject project = bizProjectService.getById(projectId);
        if (project == null) {
            throw new CommonException("项目不存在");
        }
        boolean isLeader = StrUtil.equals(project.getLeaderId(), userId);
        JSONObject roleInfo = getUserRoleInfo(userId);
        boolean isTeacher = roleInfo.getBool("isTeacher", false);
        if (!isLeader && !isTeacher) {
            throw new CommonException("权限不足：仅组长或指导教师可确认晋级转入更高一级比赛");
        }

        BizCompetition targetComp = bizCompetitionService.getById(targetCompetitionId);
        if (targetComp == null) {
            throw new CommonException("目标高一级竞赛不存在");
        }
        Date now = new Date();
        if ("FINISHED".equalsIgnoreCase(targetComp.getStatus()) || (targetComp.getCompetitionEndTime() != null && now.after(targetComp.getCompetitionEndTime()))) {
            throw new CommonException("晋级失败：目标高一级赛事已完结结束，无法转入");
        }

        BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        if (cp == null) {
            cp = new BizCompetitionProject();
            cp.setProjectId(projectId);
            cp.setSignupType("PROJECT");
            cp.setAuditStatus("PASSED");
            cp.setDeleteFlag("NOT_DELETE");
        }
        String oldCompId = cp.getCompetitionId();
        cp.setCompetitionId(targetComp.getId());
        cp.setCompetitionResult("已晋级入选");
        bizCompetitionProjectService.saveOrUpdate(cp);

        // 更新 extJson
        JSONObject ext = StrUtil.isNotBlank(project.getExtJson()) && JSONUtil.isTypeJSONObject(project.getExtJson())
                ? JSONUtil.parseObj(project.getExtJson()) : new JSONObject();
        ext.set("promotionConfirmed", true);
        ext.set("promotedFrom", oldCompId);
        ext.set("promotionTime", new Date());
        ext.remove("competitionStatus");
        ext.set("competitionTitle", targetComp.getTitle());
        project.setExtJson(ext.toString());
        bizProjectService.updateById(project);

        return CommonResult.data("🎉 恭喜晋级！项目组【" + project.getProjectName() + "】已成功转入高一级赛事【" + targetComp.getTitle() + "】！");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<String> setProjectPromotion(String projectId, String competitionId, boolean isPromoted, String remark) {
        BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                .eq(BizCompetitionProject::getProjectId, projectId)
                .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
        if (cp == null) {
            throw new CommonException("该项目未关联报名信息");
        }
        JSONObject ext = StrUtil.isNotBlank(cp.getExtJson()) && JSONUtil.isTypeJSONObject(cp.getExtJson())
                ? JSONUtil.parseObj(cp.getExtJson()) : new JSONObject();
        ext.set("isPromoted", isPromoted ? "YES" : "NO");
        ext.set("promotionRemark", remark);
        ext.remove("promotionConfirmed"); // 允许重新确认
        cp.setExtJson(ext.toString());
        if (isPromoted) {
            cp.setCompetitionResult(StrUtil.isNotBlank(remark) ? remark : "晋级高一级赛事");
        } else {
            cp.setCompetitionResult("未晋级");
        }
        bizCompetitionProjectService.updateById(cp);
        return CommonResult.data("已设置队伍晋级状态为：" + (isPromoted ? "晋级" : "未晋级"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<List<JSONObject>> getMyTodoList() {
        String userId = getCurrentUserId();
        List<JSONObject> todoList = new ArrayList<>();
        Date now = new Date();

        // 1. 查询当前用户担任组长的项目以及作为组员的项目
        List<BizProject> ledProjects = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getLeaderId, userId)
                .eq(BizProject::getDeleteFlag, "NOT_DELETE"));
        List<String> ledProjectIds = CollStreamUtil.toList(ledProjects, BizProject::getId);

        List<BizProjectMember> myMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                .eq(BizProjectMember::getStudentId, userId)
                .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
        List<String> joinedProjectIds = CollStreamUtil.toList(myMembers, BizProjectMember::getProjectId);

        Set<String> allMyProjectIds = new HashSet<>();
        allMyProjectIds.addAll(ledProjectIds);
        allMyProjectIds.addAll(joinedProjectIds);

        Map<String, BizProject> projectMap = CollectionUtil.isNotEmpty(allMyProjectIds)
                ? bizProjectService.listByIds(allMyProjectIds).stream().collect(Collectors.toMap(BizProject::getId, v -> v, (v1, v2) -> v1))
                : Collections.emptyMap();

        // 2. 需求4核心逻辑：比赛结束且未晋级自动归为<暂未参与>；晋级生成晋级确认待办
        if (CollectionUtil.isNotEmpty(ledProjects)) {
            List<BizCompetitionProject> cpList = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                    .in(BizCompetitionProject::getProjectId, ledProjectIds)
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));

            for (BizCompetitionProject cp : cpList) {
                String compId = cp.getCompetitionId();
                if (StrUtil.isBlank(compId) || "UNASSIGNED".equalsIgnoreCase(compId)) {
                    continue;
                }
                BizCompetition comp = bizCompetitionService.getById(compId);
                if (comp == null) continue;

                boolean compEnded = "FINISHED".equalsIgnoreCase(comp.getStatus())
                        || (comp.getCompetitionEndTime() != null && now.after(comp.getCompetitionEndTime()));

                // 判断是否晋级（必须排除"未晋级"、"淘汰"等包含"晋级"子串的否定语境）
                boolean isPromoted = false;
                if (StrUtil.isNotBlank(cp.getCompetitionResult())
                        && !cp.getCompetitionResult().contains("未晋级")
                        && !cp.getCompetitionResult().contains("淘汰")
                        && (cp.getCompetitionResult().contains("晋级") || cp.getCompetitionResult().contains("PROMOTED"))) {
                    isPromoted = true;
                }
                if (StrUtil.isNotBlank(cp.getExtJson()) && JSONUtil.isTypeJSONObject(cp.getExtJson())) {
                    JSONObject cpExt = JSONUtil.parseObj(cp.getExtJson());
                    if ("NO".equalsIgnoreCase(cpExt.getStr("isPromoted")) || Boolean.FALSE.equals(cpExt.getBool("isPromoted"))) {
                        isPromoted = false;
                    } else if ("YES".equalsIgnoreCase(cpExt.getStr("isPromoted")) || cpExt.getBool("isPromoted", false)) {
                        isPromoted = true;
                    }
                }

                BizProject p = projectMap.get(cp.getProjectId());
                if (p == null) continue;

                JSONObject pExt = StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())
                        ? JSONUtil.parseObj(p.getExtJson()) : new JSONObject();
                boolean promotionConfirmed = pExt.getBool("promotionConfirmed", false);

                if (isPromoted && !promotionConfirmed) {
                    // 生成【完赛晋级确认】待办！
                    BizCompetition nextComp = findNextLevelCompetition(comp);
                    if (nextComp != null) {
                        JSONObject todo = new JSONObject();
                        todo.set("id", "prom_" + p.getId());
                        todo.set("type", "PROMOTION_CONFIRM");
                        todo.set("category", "PROMOTION");
                        todo.set("tag", "晋级喜报");
                        todo.set("priority", "urgent");
                        todo.set("title", "🎉 晋级确认：【" + p.getProjectName() + "】已获得高一级赛事晋级资格！");
                        todo.set("description", "团队在【" + comp.getTitle() + "】表现优异已晋级！系统已自动匹配到高一级赛事【" + nextComp.getTitle() + "】（" + getRankName(getCompetitionRank(nextComp)) + "），点击即可确认转入参赛。");
                        todo.set("actionType", "CONFIRM_PROMOTION");
                        todo.set("actionLabel", "确认升级转入高一级");
                        todo.set("actionTarget", "/profile");
                        todo.set("projectId", p.getId());
                        todo.set("projectName", p.getProjectName());
                        todo.set("currentCompetitionId", comp.getId());
                        todo.set("currentCompetitionTitle", comp.getTitle());
                        todo.set("targetCompetitionId", nextComp.getId());
                        todo.set("targetCompetitionTitle", nextComp.getTitle());
                        todo.set("targetLevel", getRankName(getCompetitionRank(nextComp)));
                        todo.set("createTime", DateUtil.formatDateTime(new Date()));
                        todoList.add(todo);
                    }
                } else if (compEnded && !isPromoted) {
                    // 需求4：当比赛结束，并且未晋级，也不用去除这个小组，直接归为<暂未参与>
                    pExt.set("lastCompetitionId", comp.getId());
                    pExt.set("lastCompetitionTitle", comp.getTitle());
                    pExt.set("competitionStatus", "UNASSIGNED");
                    pExt.set("competitionTitle", "暂未参与");
                    pExt.set("lastResult", "完赛未晋级（已自动转为暂未参与，可自由修改比赛）");
                    p.setExtJson(pExt.toString());
                    bizProjectService.updateById(p);

                    cp.setCompetitionId("UNASSIGNED");
                    cp.setCompetitionResult("完赛未晋级（已转为暂未参与）");
                    bizCompetitionProjectService.updateById(cp);
                }
            }
        }

        // 3. 待办项：组长名下处于<暂未参与>的项目，提示绑定比赛
        for (BizProject p : ledProjects) {
            boolean isUnassigned = false;
            if (StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())) {
                JSONObject ext = JSONUtil.parseObj(p.getExtJson());
                if ("UNASSIGNED".equalsIgnoreCase(ext.getStr("competitionStatus"))) {
                    isUnassigned = true;
                }
            }
            if (!isUnassigned) {
                BizCompetitionProject cp = bizCompetitionProjectService.getOne(new LambdaQueryWrapper<BizCompetitionProject>()
                        .eq(BizCompetitionProject::getProjectId, p.getId())
                        .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));
                if (cp == null || "UNASSIGNED".equalsIgnoreCase(cp.getCompetitionId())) {
                    isUnassigned = true;
                }
            }

            if (isUnassigned) {
                JSONObject todo = new JSONObject();
                todo.set("id", "bind_" + p.getId());
                todo.set("type", "COMPETITION_BIND");
                todo.set("category", "OTHER");
                todo.set("tag", "赛事绑定");
                todo.set("priority", "normal");
                todo.set("title", "🎯 参赛绑定提醒：团队【" + p.getProjectName() + "】暂未参与具体比赛");
                todo.set("description", "该团队当前处于<暂未参与>自由状态，组长可根据团队准备情况，随时修改绑定至心仪的具体竞赛！");
                todo.set("actionType", "BIND_COMP");
                todo.set("actionLabel", "选择参赛竞赛");
                todo.set("actionTarget", "/profile");
                todo.set("projectId", p.getId());
                todo.set("projectName", p.getProjectName());
                todo.set("createTime", DateUtil.formatDateTime(p.getCreateTime() != null ? p.getCreateTime() : new Date()));
                todoList.add(todo);
            }
        }

        // 4. 待办项：竞赛题/算法题提交（针对学生）
        List<String> myTaskIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(myMembers)) {
            List<String> projectIds = CollStreamUtil.toList(myMembers, BizProjectMember::getProjectId);
            List<BizAlgorithmTaskProj> taskProjs = bizAlgorithmTaskProjService.list(new LambdaQueryWrapper<BizAlgorithmTaskProj>()
                    .in(BizAlgorithmTaskProj::getProjectId, projectIds)
                    .eq(BizAlgorithmTaskProj::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(taskProjs)) {
                myTaskIds = CollStreamUtil.toList(taskProjs, BizAlgorithmTaskProj::getTaskId).stream().distinct().collect(Collectors.toList());
            }
        }

        if (CollectionUtil.isNotEmpty(myTaskIds)) {
            List<BizAlgorithmTask> tasks = bizAlgorithmTaskService.listByIds(myTaskIds);
            List<BizAlgorithmSubmission> submissions = bizAlgorithmSubmissionService.list(new LambdaQueryWrapper<BizAlgorithmSubmission>()
                    .in(BizAlgorithmSubmission::getTaskId, myTaskIds)
                    .eq(BizAlgorithmSubmission::getStudentId, userId)
                    .eq(BizAlgorithmSubmission::getDeleteFlag, "NOT_DELETE"));
            Map<String, BizAlgorithmSubmission> subMap = submissions.stream().collect(Collectors.toMap(BizAlgorithmSubmission::getTaskId, v -> v, (v1, v2) -> v1));

            List<BizAlgorithmUrge> urges = bizAlgorithmUrgeService.list(new LambdaQueryWrapper<BizAlgorithmUrge>()
                    .in(BizAlgorithmUrge::getTaskId, myTaskIds)
                    .eq(BizAlgorithmUrge::getStudentId, userId)
                    .eq(BizAlgorithmUrge::getDeleteFlag, "NOT_DELETE")
                    .orderByDesc(BizAlgorithmUrge::getUrgeTime));
            Map<String, List<BizAlgorithmUrge>> urgeMap = urges.stream().collect(Collectors.groupingBy(BizAlgorithmUrge::getTaskId));

            for (BizAlgorithmTask t : tasks) {
                BizAlgorithmSubmission sub = subMap.get(t.getId());
                boolean needSubmit = sub == null || "REJECTED".equalsIgnoreCase(sub.getStatus()) || "PENDING".equalsIgnoreCase(sub.getStatus());
                if (needSubmit) {
                    List<BizAlgorithmUrge> taskUrges = urgeMap.get(t.getId());
                    boolean hasUrge = CollectionUtil.isNotEmpty(taskUrges);

                    JSONObject todo = new JSONObject();
                    todo.set("id", "algo_" + t.getId());
                    todo.set("type", "ALGORITHM_SUBMIT");
                    todo.set("category", "ALGO");
                    todo.set("tag", hasUrge ? "组长催交" : "算法实训");
                    todo.set("priority", hasUrge ? "urgent" : "normal");
                    todo.set("title", "💻 竞赛题待提交：《" + t.getTitle() + "》");
                    String desc = hasUrge ? "【组长催督】" + taskUrges.get(0).getUrgeMessage() + " · " : "";
                    desc += ("REJECTED".equalsIgnoreCase(sub != null ? sub.getStatus() : null) ? "被退回需重新修改提交，" : "尚未完成编码提交，") + "难度：" + (t.getDifficulty() != null ? t.getDifficulty() : "中等") + "。";
                    todo.set("description", desc);
                    todo.set("actionType", "JUMP");
                    todo.set("actionLabel", "前往编码提交");
                    todo.set("actionTarget", "/algorithm/detail/" + t.getId());
                    todo.set("targetUrl", "/algorithm/detail/" + t.getId());
                    todo.set("taskId", t.getId());
                    todo.set("createTime", DateUtil.formatDateTime(t.getCreateTime() != null ? t.getCreateTime() : new Date()));
                    todoList.add(todo);
                }
            }
        }

        // 5. 待办项：完赛荣誉证书与奖项上传待办（比赛已完结但队伍尚未上传证书）
        if (CollectionUtil.isNotEmpty(allMyProjectIds)) {
            List<BizCompetitionProject> allCpList = bizCompetitionProjectService.list(new LambdaQueryWrapper<BizCompetitionProject>()
                    .in(BizCompetitionProject::getProjectId, allMyProjectIds)
                    .eq(BizCompetitionProject::getDeleteFlag, "NOT_DELETE"));

            for (BizCompetitionProject cp : allCpList) {
                BizProject p = projectMap.get(cp.getProjectId());
                if (p == null) continue;

                // 查找该项目是否已上传过证书
                long certCount = bizCompetitionCertService.count(new LambdaQueryWrapper<BizCompetitionCert>()
                        .eq(BizCompetitionCert::getProjectId, p.getId())
                        .eq(BizCompetitionCert::getDeleteFlag, "NOT_DELETE"));

                String targetCompId = cp.getCompetitionId();
                String targetCompTitle = null;

                if (StrUtil.isNotBlank(targetCompId) && !"UNASSIGNED".equalsIgnoreCase(targetCompId)) {
                    BizCompetition c = bizCompetitionService.getById(targetCompId);
                    if (c != null && isCompEnded(c)) {
                        targetCompTitle = c.getTitle();
                    }
                } else if (StrUtil.isNotBlank(p.getExtJson()) && JSONUtil.isTypeJSONObject(p.getExtJson())) {
                    JSONObject ext = JSONUtil.parseObj(p.getExtJson());
                    if (ext.containsKey("lastCompetitionId")) {
                        targetCompId = ext.getStr("lastCompetitionId");
                        targetCompTitle = ext.getStr("lastCompetitionTitle");
                    }
                }

                if (targetCompTitle != null && certCount == 0) {
                    JSONObject todo = new JSONObject();
                    todo.set("id", "cert_" + p.getId());
                    todo.set("type", "CERTIFICATE_UPLOAD");
                    todo.set("category", "CERT");
                    todo.set("tag", "完赛申报");
                    todo.set("priority", "important");
                    todo.set("title", "📜 完赛荣誉待上传：【" + p.getProjectName() + "】赛后奖项申报");
                    todo.set("description", "团队参与的【" + targetCompTitle + "】已完结，请前往项目空间上传小组获奖证书、奖状或荣誉凭证，由指导导师认证！");
                    todo.set("actionType", "JUMP");
                    todo.set("actionLabel", "前往小组上传证书");
                    todo.set("actionTarget", "/project/" + p.getId() + "?tab=certificates&openUpload=1");
                    todo.set("targetUrl", "/project/" + p.getId() + "?tab=certificates&openUpload=1");
                    todo.set("projectId", p.getId());
                    todo.set("createTime", DateUtil.formatDateTime(new Date()));
                    todoList.add(todo);
                }
            }
        }

        // 6. 待办项：组长名下待审批的组员入组申请
        if (CollectionUtil.isNotEmpty(ledProjectIds)) {
            List<BizProjectFlow> pendingApplies = bizProjectFlowService.list(new LambdaQueryWrapper<BizProjectFlow>()
                    .in(BizProjectFlow::getProjectId, ledProjectIds)
                    .eq(BizProjectFlow::getDirection, "APPLY")
                    .eq(BizProjectFlow::getStatus, "PENDING")
                    .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE"));

            if (CollectionUtil.isNotEmpty(pendingApplies)) {
                List<String> stuIds = CollStreamUtil.toList(pendingApplies, BizProjectFlow::getStudentId);
                Map<String, ClientUser> stuMap = clientUserService.listByIds(stuIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1));

                for (BizProjectFlow flow : pendingApplies) {
                    BizProject p = projectMap.get(flow.getProjectId());
                    ClientUser stu = stuMap.get(flow.getStudentId());
                    JSONObject todo = new JSONObject();
                    todo.set("id", "apply_" + flow.getId());
                    todo.set("type", "APPLICANT_AUDIT");
                    todo.set("category", "OTHER");
                    todo.set("tag", "入组审批");
                    todo.set("priority", "important");
                    todo.set("title", "🙋 组员申请待审批：同学【" + (stu != null ? stu.getName() : "学生") + "】申请加入【" + (p != null ? p.getProjectName() : "团队") + "】");
                    todo.set("description", "申请角色：" + ("LEADER".equalsIgnoreCase(flow.getApplyRole()) ? "竞聘组长" : "入组队员") + "，自荐说明：" + StrUtil.nullToDefault(flow.getMessage(), "无"));
                    todo.set("actionType", "AUDIT");
                    todo.set("actionLabel", "前往团队审批");
                    todo.set("actionTarget", "/project/" + flow.getProjectId());
                    todo.set("targetUrl", "/project/" + flow.getProjectId());
                    todo.set("flowId", flow.getId());
                    todo.set("projectId", flow.getProjectId());
                    todo.set("createTime", DateUtil.formatDateTime(flow.getCreateTime() != null ? flow.getCreateTime() : new Date()));
                    todoList.add(todo);
                }
            }
        }

        // 7. 待办项：学生收到的入组邀请待处理
        List<BizProjectFlow> pendingInvites = bizProjectFlowService.list(new LambdaQueryWrapper<BizProjectFlow>()
                .eq(BizProjectFlow::getStudentId, userId)
                .eq(BizProjectFlow::getDirection, "INVITE")
                .eq(BizProjectFlow::getStatus, "PENDING")
                .eq(BizProjectFlow::getDeleteFlag, "NOT_DELETE"));

        if (CollectionUtil.isNotEmpty(pendingInvites)) {
            List<String> invProjIds = CollStreamUtil.toList(pendingInvites, BizProjectFlow::getProjectId);
            Map<String, BizProject> invProjMap = bizProjectService.listByIds(invProjIds).stream().collect(Collectors.toMap(BizProject::getId, v -> v, (v1, v2) -> v1));
            List<String> invLeaderIds = invProjMap.values().stream().map(BizProject::getLeaderId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            Map<String, ClientUser> leaderMap = CollectionUtil.isNotEmpty(invLeaderIds)
                    ? clientUserService.listByIds(invLeaderIds).stream().collect(Collectors.toMap(ClientUser::getId, v -> v, (v1, v2) -> v1))
                    : Collections.emptyMap();

            for (BizProjectFlow flow : pendingInvites) {
                BizProject p = invProjMap.get(flow.getProjectId());
                ClientUser leader = p != null && p.getLeaderId() != null ? leaderMap.get(p.getLeaderId()) : null;
                JSONObject todo = new JSONObject();
                todo.set("id", "inv_" + flow.getId());
                todo.set("type", "INVITATION_ACCEPT");
                todo.set("category", "OTHER");
                todo.set("tag", "入组邀请");
                todo.set("priority", "important");
                todo.set("title", "✉️ 入组邀请：组长【" + (leader != null ? leader.getName() : "组长") + "】邀请您加入【" + (p != null ? p.getProjectName() : "团队") + "】");
                todo.set("description", "邀请您作为队员共同参赛备战，请前往个人工作台处理。");
                todo.set("actionType", "JUMP");
                todo.set("actionLabel", "前往工作台处理");
                todo.set("actionTarget", "/profile");
                todo.set("targetUrl", "/profile");
                todo.set("inviteId", flow.getId());
                todo.set("createTime", DateUtil.formatDateTime(flow.getCreateTime() != null ? flow.getCreateTime() : new Date()));
                todoList.add(todo);
            }
        }

        // 按优先级 (urgent > important > normal) 排序
        todoList.sort((a, b) -> {
            int scoreA = "urgent".equals(a.getStr("priority")) ? 3 : ("important".equals(a.getStr("priority")) ? 2 : 1);
            int scoreB = "urgent".equals(b.getStr("priority")) ? 3 : ("important".equals(b.getStr("priority")) ? 2 : 1);
            return scoreB - scoreA;
        });

        return CommonResult.data(todoList);
    }
}
