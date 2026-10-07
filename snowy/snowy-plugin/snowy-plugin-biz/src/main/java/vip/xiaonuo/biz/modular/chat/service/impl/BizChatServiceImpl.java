package vip.xiaonuo.biz.modular.chat.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.biz.modular.chat.entity.BizChatMessage;
import vip.xiaonuo.biz.modular.chat.entity.BizChatRead;
import vip.xiaonuo.biz.modular.chat.mapper.BizChatMessageMapper;
import vip.xiaonuo.biz.modular.chat.mapper.BizChatReadMapper;
import vip.xiaonuo.biz.modular.chat.service.BizChatService;
import vip.xiaonuo.biz.modular.project.entity.BizProject;
import vip.xiaonuo.biz.modular.project.service.BizProjectService;
import vip.xiaonuo.biz.modular.projectmember.entity.BizProjectMember;
import vip.xiaonuo.biz.modular.projectmember.service.BizProjectMemberService;
import vip.xiaonuo.biz.modular.teacherprofile.entity.BizTeacherProfile;
import vip.xiaonuo.biz.modular.teacherprofile.service.BizTeacherProfileService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 群聊与讨论服务实现
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Slf4j
@Service
public class BizChatServiceImpl extends ServiceImpl<BizChatMessageMapper, BizChatMessage> implements BizChatService {

    @Resource
    private BizChatReadMapper bizChatReadMapper;

    @Resource
    private BizProjectService bizProjectService;

    @Resource
    private BizProjectMemberService bizProjectMemberService;

    @Resource
    private ClientUserService clientUserService;

    @Resource
    private BizTeacherProfileService bizTeacherProfileService;

    public static final String CHAT_TYPE_PROJECT = "PROJECT";
    public static final String CHAT_TYPE_LEADER_TEACHER = "LEADER_TEACHER";
    public static final String TARGET_LEADER_TEACHER = "GLOBAL_LEADER_TEACHER";

    private String getCurrentUserId() {
        try {
            if (StpClientUtil.isLogin()) {
                return StpClientUtil.getLoginIdAsString();
            }
        } catch (Exception ignored) {}
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception ignored) {}
        throw new CommonException("请先登录");
    }

    /** 判断是否为指导教师 */
    private boolean isTeacherUser(String userId, ClientUser user) {
        if (user != null && StrUtil.isNotBlank(user.getExtJson())) {
            try {
                JSONObject obj = JSONUtil.parseObj(user.getExtJson());
                if ("TEACHER".equalsIgnoreCase(obj.getStr("userRole"))) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return bizTeacherProfileService.count(new LambdaQueryWrapper<BizTeacherProfile>()
                .eq(BizTeacherProfile::getUserId, userId)
                .eq(BizTeacherProfile::getDeleteFlag, "NOT_DELETE")) > 0;
    }

    /** 判断用户是否担任至少一个项目的组长 */
    private boolean isLeaderOfAnyProject(String userId) {
        return bizProjectService.count(new LambdaQueryWrapper<BizProject>()
                .eq(BizProject::getLeaderId, userId)
                .eq(BizProject::getDeleteFlag, "NOT_DELETE")) > 0;
    }

    /** 校验群聊进入与操作权限 */
    private String checkChatPermission(String userId, String chatType, String chatTargetId, ClientUser user) {
        boolean isTeacher = isTeacherUser(userId, user);

        if (CHAT_TYPE_LEADER_TEACHER.equalsIgnoreCase(chatType)) {
            if (isTeacher) {
                return "TEACHER";
            }
            if (isLeaderOfAnyProject(userId)) {
                return "LEADER";
            }
            throw new CommonException("您当前不是项目组长或指导教师，无法加入该领航交流群！");
        } else if (CHAT_TYPE_PROJECT.equalsIgnoreCase(chatType)) {
            BizProject project = bizProjectService.getById(chatTargetId);
            if (project == null) {
                throw new CommonException("项目组不存在");
            }
            if (isTeacher) {
                boolean isProjectTeacher = StrUtil.equals(project.getCreateUser(), userId)
                        || (StrUtil.isNotBlank(project.getExtJson()) && project.getExtJson().contains(userId));
                if (isProjectTeacher || StrUtil.isBlank(project.getExtJson()) || !project.getExtJson().contains("teacherId")) {
                    return "TEACHER";
                }
                throw new CommonException("您非该实训项目的指导教师，无法进入该项目的讨论空间");
            }
            if (StrUtil.equals(project.getLeaderId(), userId)) {
                return "LEADER";
            }
            boolean isMember = bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, chatTargetId)
                    .eq(BizProjectMember::getStudentId, userId)
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE")) > 0;
            if (isMember) {
                return "MEMBER";
            }
            throw new CommonException("您不是该项目组成员，无法进入该项目讨论空间");
        }
        throw new CommonException("未知的群聊类型: " + chatType);
    }

    @Override
    public CommonResult<BizChatMessage> sendMessage(String chatType, String chatTargetId, String content, String messageType) {
        if (StrUtil.isBlank(content)) {
            throw new CommonException("消息内容不能为空");
        }
        String userId = getCurrentUserId();
        ClientUser user = clientUserService.getById(userId);
        if (user == null) {
            throw new CommonException("用户不存在");
        }

        String role = checkChatPermission(userId, chatType, chatTargetId, user);

        BizChatMessage msg = new BizChatMessage();
        msg.setChatType(chatType);
        msg.setChatTargetId(chatTargetId);
        msg.setSenderId(userId);
        msg.setSenderName(StrUtil.isNotBlank(user.getName()) ? user.getName() : user.getAccount());
        msg.setSenderRole(role);
        msg.setSenderAvatar(user.getAvatar());
        msg.setContent(content);
        msg.setMessageType(StrUtil.isNotBlank(messageType) ? messageType : "TEXT");
        msg.setCreateTime(new Date());

        this.save(msg);

        // 发送后自动更新本人的已读时间
        markRead(chatType, chatTargetId);

        return CommonResult.data(msg);
    }

    @Override
    public CommonResult<List<BizChatMessage>> getHistory(String chatType, String chatTargetId, Integer limit) {
        String userId = getCurrentUserId();
        ClientUser user = clientUserService.getById(userId);
        checkChatPermission(userId, chatType, chatTargetId, user);

        int maxRows = (limit != null && limit > 0 && limit <= 200) ? limit : 60;

        List<BizChatMessage> list = this.list(new LambdaQueryWrapper<BizChatMessage>()
                .eq(BizChatMessage::getChatType, chatType)
                .eq(BizChatMessage::getChatTargetId, chatTargetId)
                .eq(BizChatMessage::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizChatMessage::getCreateTime)
                .last("LIMIT " + maxRows));

        // 翻转为时间升序
        Collections.reverse(list);

        // 自动标为已读
        markRead(chatType, chatTargetId);

        return CommonResult.data(list);
    }

    @Override
    public CommonResult<JSONObject> getUnreadCount(String chatType, String chatTargetId) {
        String userId = getCurrentUserId();
        BizChatRead read = bizChatReadMapper.selectOne(new LambdaQueryWrapper<BizChatRead>()
                .eq(BizChatRead::getUserId, userId)
                .eq(BizChatRead::getChatType, chatType)
                .eq(BizChatRead::getChatTargetId, chatTargetId));

        Date lastReadTime = (read != null && read.getLastReadTime() != null) ? read.getLastReadTime() : new Date(0);

        long unreadCount = this.count(new LambdaQueryWrapper<BizChatMessage>()
                .eq(BizChatMessage::getChatType, chatType)
                .eq(BizChatMessage::getChatTargetId, chatTargetId)
                .eq(BizChatMessage::getDeleteFlag, "NOT_DELETE")
                .ne(BizChatMessage::getSenderId, userId)
                .gt(BizChatMessage::getCreateTime, lastReadTime));

        BizChatMessage latestMsg = this.getOne(new LambdaQueryWrapper<BizChatMessage>()
                .eq(BizChatMessage::getChatType, chatType)
                .eq(BizChatMessage::getChatTargetId, chatTargetId)
                .eq(BizChatMessage::getDeleteFlag, "NOT_DELETE")
                .orderByDesc(BizChatMessage::getCreateTime)
                .last("LIMIT 1"));

        JSONObject result = new JSONObject();
        result.set("unreadCount", unreadCount);
        result.set("latestMessage", latestMsg);
        return CommonResult.data(result);
    }

    @Override
    public CommonResult<String> markRead(String chatType, String chatTargetId) {
        String userId = getCurrentUserId();
        BizChatRead read = bizChatReadMapper.selectOne(new LambdaQueryWrapper<BizChatRead>()
                .eq(BizChatRead::getUserId, userId)
                .eq(BizChatRead::getChatType, chatType)
                .eq(BizChatRead::getChatTargetId, chatTargetId));

        Date now = new Date();
        if (read == null) {
            read = new BizChatRead();
            read.setId(IdUtil.getSnowflakeNextIdStr());
            read.setUserId(userId);
            read.setChatType(chatType);
            read.setChatTargetId(chatTargetId);
            read.setLastReadTime(now);
            bizChatReadMapper.insert(read);
        } else {
            read.setLastReadTime(now);
            bizChatReadMapper.updateById(read);
        }
        return CommonResult.data("已标为已读");
    }

    @Override
    public CommonResult<List<JSONObject>> getGroupMembers(String chatType, String chatTargetId) {
        String currentUserId = getCurrentUserId();
        List<JSONObject> members = new ArrayList<>();

        if (CHAT_TYPE_PROJECT.equalsIgnoreCase(chatType)) {
            BizProject project = bizProjectService.getById(chatTargetId);
            if (project == null) {
                throw new CommonException("项目不存在");
            }
            // 组长
            ClientUser leader = clientUserService.getById(project.getLeaderId());
            if (leader != null) {
                JSONObject obj = new JSONObject();
                obj.set("userId", leader.getId());
                obj.set("name", leader.getName());
                obj.set("role", "LEADER");
                obj.set("roleLabel", "👑 项目组长");
                obj.set("avatar", leader.getAvatar());
                members.add(obj);
            }
            // 组员
            List<BizProjectMember> pMembers = bizProjectMemberService.list(new LambdaQueryWrapper<BizProjectMember>()
                    .eq(BizProjectMember::getProjectId, chatTargetId)
                    .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(pMembers)) {
                List<String> memberUserIds = CollStreamUtil.toList(pMembers, BizProjectMember::getStudentId);
                List<ClientUser> mUsers = clientUserService.listByIds(memberUserIds);
                for (ClientUser mu : mUsers) {
                    if (leader != null && StrUtil.equals(mu.getId(), leader.getId())) continue;
                    JSONObject obj = new JSONObject();
                    obj.set("userId", mu.getId());
                    obj.set("name", mu.getName());
                    obj.set("role", "MEMBER");
                    obj.set("roleLabel", "组员");
                    obj.set("avatar", mu.getAvatar());
                    members.add(obj);
                }
            }
            // 教师
            List<BizTeacherProfile> teachers = bizTeacherProfileService.list(new LambdaQueryWrapper<BizTeacherProfile>()
                    .eq(BizTeacherProfile::getDeleteFlag, "NOT_DELETE"));
            if (CollectionUtil.isNotEmpty(teachers)) {
                List<String> tUserIds = CollStreamUtil.toList(teachers, BizTeacherProfile::getUserId);
                List<ClientUser> tUsers = clientUserService.listByIds(tUserIds);
                for (ClientUser tu : tUsers) {
                    JSONObject obj = new JSONObject();
                    obj.set("userId", tu.getId());
                    obj.set("name", tu.getName());
                    obj.set("role", "TEACHER");
                    obj.set("roleLabel", "👨‍🏫 指导教师");
                    obj.set("avatar", tu.getAvatar());
                    members.add(obj);
                }
            }
        } else if (CHAT_TYPE_LEADER_TEACHER.equalsIgnoreCase(chatType)) {
            // 教师全员
            List<BizTeacherProfile> teachers = bizTeacherProfileService.list(new LambdaQueryWrapper<BizTeacherProfile>()
                    .eq(BizTeacherProfile::getDeleteFlag, "NOT_DELETE"));
            Set<String> teacherUserIds = new HashSet<>();
            if (CollectionUtil.isNotEmpty(teachers)) {
                teacherUserIds.addAll(CollStreamUtil.toList(teachers, BizTeacherProfile::getUserId));
            }
            // 补充 extJson 标注为 TEACHER 的 client_user
            List<ClientUser> allUsers = clientUserService.list(new LambdaQueryWrapper<ClientUser>()
                    .eq(ClientUser::getDeleteFlag, "NOT_DELETE"));
            for (ClientUser cu : allUsers) {
                if (isTeacherUser(cu.getId(), cu)) {
                    teacherUserIds.add(cu.getId());
                }
            }
            if (CollectionUtil.isNotEmpty(teacherUserIds)) {
                List<ClientUser> tUsers = clientUserService.listByIds(teacherUserIds);
                for (ClientUser tu : tUsers) {
                    JSONObject obj = new JSONObject();
                    obj.set("userId", tu.getId());
                    obj.set("name", tu.getName());
                    obj.set("role", "TEACHER");
                    obj.set("roleLabel", "👨‍🏫 指导教师");
                    obj.set("avatar", tu.getAvatar());
                    members.add(obj);
                }
            }

            // 所有活跃项目的组长（多组组长自动去重）
            List<BizProject> projects = bizProjectService.list(new LambdaQueryWrapper<BizProject>()
                    .eq(BizProject::getDeleteFlag, "NOT_DELETE"));
            // 聚合所有组长 ID 及其管理的项目名称
            Map<String, List<String>> leaderProjectMap = new HashMap<>();
            for (BizProject p : projects) {
                if (StrUtil.isNotBlank(p.getLeaderId())) {
                    leaderProjectMap.computeIfAbsent(p.getLeaderId(), k -> new ArrayList<>()).add(p.getProjectName());
                }
            }

            if (CollectionUtil.isNotEmpty(leaderProjectMap.keySet())) {
                List<ClientUser> leaderUsers = clientUserService.listByIds(leaderProjectMap.keySet());
                for (ClientUser lu : leaderUsers) {
                    if (teacherUserIds.contains(lu.getId())) continue; // 避免与老师重复
                    List<String> projs = leaderProjectMap.get(lu.getId());
                    JSONObject obj = new JSONObject();
                    obj.set("userId", lu.getId());
                    obj.set("name", lu.getName());
                    obj.set("role", "LEADER");
                    obj.set("roleLabel", "👑 组长");
                    obj.set("projects", projs);
                    obj.set("projectCount", projs.size());
                    obj.set("avatar", lu.getAvatar());
                    members.add(obj);
                }
            }
        }

        return CommonResult.data(members);
    }

    @Override
    public CommonResult<JSONObject> checkMyChatPermissions(String projectId) {
        String userId = getCurrentUserId();
        ClientUser user = clientUserService.getById(userId);
        boolean isTeacher = isTeacherUser(userId, user);
        boolean isLeader = isLeaderOfAnyProject(userId);

        JSONObject result = new JSONObject();
        result.set("isTeacher", isTeacher);
        result.set("isLeader", isLeader);
        // 是否能进入组长与教师大群
        result.set("canAccessLeaderTeacherChat", isTeacher || isLeader);

        // 如果传了 projectId，判断对项目群的权限
        if (StrUtil.isNotBlank(projectId)) {
            BizProject project = bizProjectService.getById(projectId);
            if (project != null) {
                boolean isProjectLeader = StrUtil.equals(project.getLeaderId(), userId);
                boolean isMember = isProjectLeader || bizProjectMemberService.count(new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getProjectId, projectId)
                        .eq(BizProjectMember::getStudentId, userId)
                        .eq(BizProjectMember::getDeleteFlag, "NOT_DELETE")) > 0;
                result.set("canAccessProjectChat", isTeacher || isMember);
                result.set("roleInProject", isTeacher ? "TEACHER" : (isProjectLeader ? "LEADER" : (isMember ? "MEMBER" : "NONE")));
            }
        }
        return CommonResult.data(result);
    }
}
