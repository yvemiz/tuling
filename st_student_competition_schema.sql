/*
 Navicat Premium Data Transfer

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001 (utf8mb4)

 Project Name          : 学生竞赛与实训管理平台 (Student Competition & Training Management System)
 Architecture Standard : 严格适配小诺 (Snowy 3.0) 底座技术规范与代码生成器标准
 Primary Key Standard  : VARCHAR(20) 19位雪花算法ID (Snowflake ID)
 Table Engine & Charset: InnoDB / utf8mb4 / utf8mb4_general_ci
 Design Philosophy     : 高内聚低耦合、无物理外键强依赖、业务数据软删除、预留JSON扩展字段
 Date                  : 2026-10-04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 模块一：用户与角色档案扩展模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_STUDENT_PROFILE
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_STUDENT_PROFILE`;
CREATE TABLE `BIZ_STUDENT_PROFILE` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统用户ID（关联SYS_USER.ID）',
  `STUDENT_NO` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学号',
  `COLLEGE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属学院机构ID（关联SYS_ORG.ID）',
  `MAJOR` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业名称',
  `GRADE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '年级（如2023级）',
  `CLASS_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '班级名称',
  `SKILL_TAGS` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '技能标签（逗号分隔，如: Java,Python,算法,前端）',
  `BIO` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '个人简介/技术特长',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展信息(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`USER_ID`) USING BTREE,
  UNIQUE KEY `uk_student_no` (`STUDENT_NO`) USING BTREE,
  KEY `idx_college_id` (`COLLEGE_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生扩展档案表';

-- ----------------------------
-- Table structure for BIZ_TEACHER_PROFILE
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_TEACHER_PROFILE`;
CREATE TABLE `BIZ_TEACHER_PROFILE` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统用户ID（关联SYS_USER.ID）',
  `TEACHER_NO` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师工号',
  `COLLEGE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属学院机构ID（关联SYS_ORG.ID）',
  `TITLE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职称（教授/副教授/讲师等）',
  `RESEARCH_DIRECTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '研究方向',
  `BIO` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '教师简介',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展信息(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_teacher_user_id` (`USER_ID`) USING BTREE,
  UNIQUE KEY `uk_teacher_no` (`TEACHER_NO`) USING BTREE,
  KEY `idx_teacher_college` (`COLLEGE_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师扩展档案表';


-- ----------------------------
-- 模块二：竞赛与项目团队核心模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_COMPETITION
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_COMPETITION`;
CREATE TABLE `BIZ_COMPETITION` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `TITLE` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '竞赛名称',
  `COMPETITION_LEVEL` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'SCHOOL' COMMENT '竞赛级别：SCHOOL-校级, PROVINCE-省级, NATIONAL-国家级, INTERNATIONAL-国际级',
  `ORGANIZER` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主办单位/承办方',
  `CATEGORY` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛类别（计算机/算法/创客/数学建模等）',
  `SIGNUP_START_TIME` datetime DEFAULT NULL COMMENT '报名开始时间',
  `SIGNUP_END_TIME` datetime DEFAULT NULL COMMENT '报名截止时间',
  `COMPETITION_START_TIME` datetime DEFAULT NULL COMMENT '竞赛开始时间',
  `COMPETITION_END_TIME` datetime DEFAULT NULL COMMENT '竞赛结束时间',
  `MIN_MEMBERS` int NOT NULL DEFAULT 1 COMMENT '最少参赛人数（1表示允许单人参赛）',
  `MAX_MEMBERS` int NOT NULL DEFAULT 1 COMMENT '最多参赛人数（若为1则为单人竞赛，无须建组；大于1则须以组为单位且组长报名）',
  `REQUIREMENTS` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '竞赛要求与参赛规程',
  `ATTACHMENT_FILE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规程附件文件ID（关联DEV_FILE.ID）',
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '竞赛状态：PENDING-未开始, SIGNUP-报名中, ONGOING-进行中, REVIEWING-评审中, FINISHED-已结束',
  `PUBLISH_TEACHER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布教师用户ID（关联SYS_USER.ID，仅教师可增删改）',
  `VIEW_COUNT` int DEFAULT 0 COMMENT '浏览次数',
  `FOLLOW_COUNT` int DEFAULT 0 COMMENT '关注人数',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展配置(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_comp_status` (`STATUS`) USING BTREE,
  KEY `idx_comp_publish_teacher` (`PUBLISH_TEACHER_ID`) USING BTREE,
  KEY `idx_comp_time` (`SIGNUP_START_TIME`, `SIGNUP_END_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='竞赛信息主表';

-- ----------------------------
-- Table structure for BIZ_PROJECT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_PROJECT`;
CREATE TABLE `BIZ_PROJECT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `PROJECT_DESC` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目描述',
  `LEADER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目组长用户ID（关联SYS_USER.ID，与项目一对一绑定，该学生自动获组长身份）',
  `IS_PRIVATE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NO' COMMENT '是否私密：YES-私密（不能被公开查看）, NO-公开',
  `CURRENT_MEMBER_COUNT` int DEFAULT 1 COMMENT '当前成员总数（含组长）',
  `MAX_MEMBER_LIMIT` int DEFAULT 5 COMMENT '队伍成员上限',
  `AVATAR_FILE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目/团队头像文件ID（关联DEV_FILE.ID）',
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'RECRUITING' COMMENT '状态：RECRUITING-招募中, FORMULATED-组队完成, ARCHIVED-已归档',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展属性(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_proj_leader` (`LEADER_ID`) USING BTREE,
  KEY `idx_proj_privacy` (`IS_PRIVATE`) USING BTREE,
  KEY `idx_proj_status` (`STATUS`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目与项目团队表';

-- ----------------------------
-- Table structure for BIZ_PROJECT_MEMBER
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_PROJECT_MEMBER`;
CREATE TABLE `BIZ_PROJECT_MEMBER` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目ID（关联BIZ_PROJECT.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成员学生用户ID（关联SYS_USER.ID，组员可进多个组）',
  `MEMBER_ROLE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'MEMBER' COMMENT '团队内角色：LEADER-组长, MEMBER-普通组员',
  `DUTY_REMARK` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组内职责描述（如算法负责人、前端开发、报告撰写等）',
  `JOIN_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `MEMBER_STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '成员状态：ACTIVE-正式在组, QUIT-已退出',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_project_student` (`PROJECT_ID`, `STUDENT_ID`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_member_student` (`STUDENT_ID`) USING BTREE,
  KEY `idx_member_project` (`PROJECT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目团队成员关系表';

-- ----------------------------
-- Table structure for BIZ_COMPETITION_PROJECT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_COMPETITION_PROJECT`;
CREATE TABLE `BIZ_COMPETITION_PROJECT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `COMPETITION_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '竞赛ID（关联BIZ_COMPETITION.ID）',
  `SIGNUP_TYPE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PROJECT' COMMENT '参赛形式：PROJECT-团队参赛, INDIVIDUAL-个人参赛',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '参赛项目组ID（多对多关联BIZ_PROJECT.ID，团队参赛时必填，仅组长可操作）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '参赛学生ID（个人参赛时必填，关联SYS_USER.ID）',
  `INSTRUCTOR_TEACHER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指导老师用户ID（关联SYS_USER.ID）',
  `ENROLL_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `AUDIT_STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PASSED' COMMENT '参赛资格审核状态：PENDING-待审核, PASSED-报名成功, REJECTED-已驳回',
  `COMPETITION_RESULT` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最终赛果/名次（如一等奖/银奖/进入决赛等）',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展报名资料(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_cp_competition` (`COMPETITION_ID`) USING BTREE,
  KEY `idx_cp_project` (`PROJECT_ID`) USING BTREE,
  KEY `idx_cp_student` (`STUDENT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='竞赛与项目报名参赛关联表（支持团队与单人多对多）';

-- ----------------------------
-- Table structure for BIZ_COMPETITION_CERTIFICATE
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_COMPETITION_CERTIFICATE`;
CREATE TABLE `BIZ_COMPETITION_CERTIFICATE` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `COMPETITION_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属竞赛ID（关联BIZ_COMPETITION.ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属项目ID（关联BIZ_PROJECT.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '获得证书学生ID（关联SYS_USER.ID）',
  `CERTIFICATE_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '证书名称',
  `AWARD_LEVEL` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '获奖级别（国家一等奖/省级二等奖/优秀奖等）',
  `AWARD_DATE` date DEFAULT NULL COMMENT '获奖日期',
  `FILE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '证书扫描件/照片文件ID（关联DEV_FILE.ID）',
  `UPLOAD_USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '上传人用户ID（组员或组长皆可上传）',
  `VERIFY_STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT '教师认证状态：PENDING-待认证, VERIFIED-已认证, REJECTED-已驳回',
  `VERIFY_TEACHER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认证教师ID（关联SYS_USER.ID）',
  `VERIFY_REMARK` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核评语',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_cert_comp` (`COMPETITION_ID`) USING BTREE,
  KEY `idx_cert_proj` (`PROJECT_ID`) USING BTREE,
  KEY `idx_cert_student` (`STUDENT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='竞赛证书与荣誉表';


-- ----------------------------
-- 模块三：关注、招募与组队流转模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_FOLLOW
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_FOLLOW`;
CREATE TABLE `BIZ_FOLLOW` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注人学生用户ID（关联SYS_USER.ID）',
  `TARGET_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注目标类型：COMPETITION-竞赛, PROJECT-项目组',
  `TARGET_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标ID（根据TARGET_TYPE关联BIZ_COMPETITION.ID或BIZ_PROJECT.ID）',
  `FOLLOW_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_user_target` (`USER_ID`, `TARGET_TYPE`, `TARGET_ID`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_target_lookup` (`TARGET_TYPE`, `TARGET_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生关注竞赛或项目组表';

-- ----------------------------
-- Table structure for BIZ_PROJECT_RECRUIT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_PROJECT_RECRUIT`;
CREATE TABLE `BIZ_PROJECT_RECRUIT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属项目ID（关联BIZ_PROJECT.ID）',
  `LEADER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布组长用户ID（关联SYS_USER.ID）',
  `TITLE` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '招聘标题',
  `RECRUIT_COUNT` int DEFAULT 1 COMMENT '拟招募人数',
  `REQUIREMENT_DESC` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '招募技能要求与详细说明',
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-招募中, CLOSED-已招满/关闭',
  `DEADLINE` datetime DEFAULT NULL COMMENT '招募截止时间',
  `FORUM_POST_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联动发布的论坛帖子ID（关联BIZ_FORUM_POST.ID）',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_recruit_project` (`PROJECT_ID`) USING BTREE,
  KEY `idx_recruit_leader` (`LEADER_ID`) USING BTREE,
  KEY `idx_recruit_status` (`STATUS`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目组招聘与组员招募需求表';

-- ----------------------------
-- Table structure for BIZ_PROJECT_APPLY_INVITE
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_PROJECT_APPLY_INVITE`;
CREATE TABLE `BIZ_PROJECT_APPLY_INVITE` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目ID（关联BIZ_PROJECT.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '涉及的学生用户ID（关联SYS_USER.ID）',
  `DIRECTION` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流转方向：INVITE-组长邀请学生, APPLY-学生主动申请加入',
  `OPERATOR_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发起人用户ID（组长或学生，关联SYS_USER.ID）',
  `MESSAGE` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '附言/申请理由/邀请语',
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '流转状态：PENDING-待处理, ACCEPTED-已接受并入组, REJECTED-已拒绝',
  `HANDLE_TIME` datetime DEFAULT NULL COMMENT '处理时间',
  `HANDLE_REMARK` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '处理备注',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_flow_project` (`PROJECT_ID`) USING BTREE,
  KEY `idx_flow_student` (`STUDENT_ID`) USING BTREE,
  KEY `idx_flow_status` (`STATUS`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组员入组申请与组长邀请流转记录表';


-- ----------------------------
-- 模块四：算法练习与任务督促模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_ALGORITHM_TASK
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_ALGORITHM_TASK`;
CREATE TABLE `BIZ_ALGORITHM_TASK` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `TITLE` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '算法题目/练习任务名称',
  `CONTENT_DESC` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务详情与题目描述',
  `DIFFICULTY` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'MEDIUM' COMMENT '题目难度：EASY-简单, MEDIUM-中等, HARD-困难',
  `ALLOWED_SUBMISSION_TYPES` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'CODE_TEXT,FILE,GIT_URL' COMMENT '允许的提交形式（逗号分隔：CODE_TEXT-代码文本, FILE-附件文件, GIT_URL-Git仓库链接）',
  `DEADLINE` datetime DEFAULT NULL COMMENT '截止提交时间',
  `TEACHER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布教师ID（关联SYS_USER.ID）',
  `SAMPLE_INPUT` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '输入样例',
  `SAMPLE_OUTPUT` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '输出样例',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展配置(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_algo_teacher` (`TEACHER_ID`) USING BTREE,
  KEY `idx_algo_deadline` (`DEADLINE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='算法练习题任务主表';

-- ----------------------------
-- Table structure for BIZ_ALGORITHM_TASK_PROJECT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_ALGORITHM_TASK_PROJECT`;
CREATE TABLE `BIZ_ALGORITHM_TASK_PROJECT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `TASK_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '算法任务ID（关联BIZ_ALGORITHM_TASK.ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指派的项目组ID（关联BIZ_PROJECT.ID，老师按组分发任务）',
  `ASSIGN_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分发指派时间',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_task_proj` (`TASK_ID`, `PROJECT_ID`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_atp_proj` (`PROJECT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='算法任务分发到项目组关联表';

-- ----------------------------
-- Table structure for BIZ_ALGORITHM_SUBMISSION
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_ALGORITHM_SUBMISSION`;
CREATE TABLE `BIZ_ALGORITHM_SUBMISSION` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `TASK_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '算法任务ID（关联BIZ_ALGORITHM_TASK.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提交学生ID（关联SYS_USER.ID。核心规则：学生进多组只要提交一次，各组统计自动生效）',
  `SUBMISSION_TYPE` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提交形式：CODE_TEXT-在线代码, FILE-文件, GIT_URL-Git链接',
  `CODE_LANGUAGE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编程语言（Java, C++, Python, Go等）',
  `CODE_CONTENT` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '代码文本内容',
  `FILE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '提交的文件附件ID（关联DEV_FILE.ID）',
  `GIT_URL` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Git仓库地址/分支说明',
  `SUBMIT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `SCORE` decimal(5,2) DEFAULT NULL COMMENT '得分（0-100）',
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态：SUBMITTED-已提交, GRADED-已评分, REWORK-需重做',
  `TEACHER_FEEDBACK` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '教师批改评语',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_task_student` (`TASK_ID`, `STUDENT_ID`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_sub_student` (`STUDENT_ID`) USING BTREE,
  KEY `idx_sub_status` (`STATUS`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='算法作业提交与批改表（按学生+任务唯一，多组共享）';

-- ----------------------------
-- Table structure for BIZ_ALGORITHM_URGE_LOG
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_ALGORITHM_URGE_LOG`;
CREATE TABLE `BIZ_ALGORITHM_URGE_LOG` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `TASK_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '催交的算法任务ID（关联BIZ_ALGORITHM_TASK.ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所在项目组ID（关联BIZ_PROJECT.ID）',
  `LEADER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '催促组长用户ID（关联SYS_USER.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被催促的组员学生ID（关联SYS_USER.ID）',
  `URGE_MESSAGE` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '请尽快完成算法练习题任务并提交！' COMMENT '催促消息内容',
  `URGE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '催促时间',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_urge_project` (`PROJECT_ID`) USING BTREE,
  KEY `idx_urge_student` (`STUDENT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组长督促组员完成算法记录表';


-- ----------------------------
-- 模块五：论坛社区与多级智能审核模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_FORUM_POST
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_FORUM_POST`;
CREATE TABLE `BIZ_FORUM_POST` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `AUTHOR_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布人用户ID（关联SYS_USER.ID，所有注册用户均可发布）',
  `TITLE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子标题',
  `CONTENT` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子正文内容(Markdown/富文本)',
  `POST_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GENERAL' COMMENT '帖子类别：GENERAL-普通交流, RECRUITMENT-组长招聘, FOLLOW_EVENT-关注动态, COMPETITION_EVENT-竞赛发布',
  `REF_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联实体类型：RECRUIT-招聘, COMPETITION-竞赛, PROJECT-项目组, NONE-无',
  `REF_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联实体主键ID',
  `IS_PINNED` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NO' COMMENT '是否置顶：YES-置顶（仅老师可置顶）, NO-普通',
  `PIN_TIME` datetime DEFAULT NULL COMMENT '置顶时间',
  `AUDIT_STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'AI_PENDING' COMMENT '审核状态：AI_PENDING-待AI审查, PUBLISHED-已发布(通过), MANUAL_PENDING-转人工审核, REJECTED-已驳回',
  `AI_AUDIT_RESULT` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AI审查结论：PASS-正常通过, RISK-有风险/违规, ERROR-AI配置/调用异常',
  `AI_AUDIT_REASON` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AI审查不通过或存疑原因',
  `LEADER_PASS_COUNT` int DEFAULT 0 COMMENT '组长通过票数计数（超过阈值后自动正式发布）',
  `LEADER_REJECT_COUNT` int DEFAULT 0 COMMENT '组长驳回票数计数',
  `VIEW_COUNT` int DEFAULT 0 COMMENT '浏览数',
  `LIKE_COUNT` int DEFAULT 0 COMMENT '点赞数',
  `COMMENT_COUNT` int DEFAULT 0 COMMENT '评论总数',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展信息(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_post_author` (`AUTHOR_ID`) USING BTREE,
  KEY `idx_post_type` (`POST_TYPE`) USING BTREE,
  KEY `idx_post_audit_status` (`AUDIT_STATUS`) USING BTREE,
  KEY `idx_post_pinned` (`IS_PINNED`, `PIN_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='论坛帖子主表';

-- ----------------------------
-- Table structure for BIZ_FORUM_POST_AUDIT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_FORUM_POST_AUDIT`;
CREATE TABLE `BIZ_FORUM_POST_AUDIT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `POST_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审核的帖子ID（关联BIZ_FORUM_POST.ID）',
  `AUDITOR_TYPE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审核主体类型：AI-智能审查, TEACHER-老师直接审核, LEADER-组长联审投票',
  `AUDITOR_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审核人用户ID（关联SYS_USER.ID，AI审核时可填AI_SYSTEM）',
  `AUDIT_RESULT` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审核决议：PASS-通过, REJECT-驳回/存疑',
  `AUDIT_REASON` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核意见与处理说明',
  `AUDIT_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_post_auditor` (`POST_ID`, `AUDITOR_ID`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_audit_post` (`POST_ID`) USING BTREE,
  KEY `idx_auditor` (`AUDITOR_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='论坛帖子多级审核与组长投票记录表';

-- ----------------------------
-- Table structure for BIZ_FORUM_COMMENT
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_FORUM_COMMENT`;
CREATE TABLE `BIZ_FORUM_COMMENT` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `POST_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属帖子ID（关联BIZ_FORUM_POST.ID）',
  `USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论人用户ID（关联SYS_USER.ID）',
  `CONTENT` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `PARENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上级父评论ID（支持盖楼回复，顶层为NULL）',
  `REPLY_TO_USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '被回复人用户ID（关联SYS_USER.ID）',
  `LIKE_COUNT` int DEFAULT 0 COMMENT '点赞数',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_comment_post` (`POST_ID`) USING BTREE,
  KEY `idx_comment_user` (`USER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='论坛评论表';


-- ----------------------------
-- 模块六：AI评测、手动评分与项目排行榜模块
-- ----------------------------

-- ----------------------------
-- Table structure for BIZ_AI_TEAM_EVALUATION
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_AI_TEAM_EVALUATION`;
CREATE TABLE `BIZ_AI_TEAM_EVALUATION` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评估的项目组ID（关联BIZ_PROJECT.ID）',
  `TRIGGER_USER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发评估人用户ID（组长或老师，关联SYS_USER.ID）',
  `EVALUATION_BATCH` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评估批次/周期（如 2026-Q1, STAGE_1 等）',
  `ALGORITHM_COMPLETION_RATE` decimal(5,2) DEFAULT '0.00' COMMENT '组员算法整体完成度百分比(0-100)',
  `COMPETITION_PARTICIPATION_SCORE` decimal(5,2) DEFAULT '0.00' COMMENT '竞赛参与度与活跃度得分(0-100)',
  `AI_TOTAL_SCORE` decimal(5,2) DEFAULT '0.00' COMMENT 'AI加权综合评估得分(0-100)',
  `AI_EVALUATION_REPORT` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AI生成的诊断报告文本（含各维度深度分析、优势分析、短板预警及提升建议）',
  `AI_MODEL_VERSION` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行评分的AI模型版本/规则版本',
  `EXT_JSON` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展指标数据(JSON)',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_aieval_proj` (`PROJECT_ID`) USING BTREE,
  KEY `idx_aieval_trigger` (`TRIGGER_USER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='团队AI综合评估打分与诊断分析报告表';

-- ----------------------------
-- Table structure for BIZ_MEMBER_SCORE
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_MEMBER_SCORE`;
CREATE TABLE `BIZ_MEMBER_SCORE` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属项目ID（关联BIZ_PROJECT.ID）',
  `STUDENT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被评价的组员学生ID（关联SYS_USER.ID）',
  `EVALUATOR_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打分人用户ID（组长或老师，关联SYS_USER.ID）',
  `EVALUATOR_ROLE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打分人身份：LEADER-组长手动评分, TEACHER-老师评分',
  `SCORE` decimal(5,2) NOT NULL COMMENT '表现评分（0-100）',
  `REMARK` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表现评价与评语（保密限制：仅老师和该组组长有权限查看）',
  `SCORE_PERIOD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '打分考核周期/阶段（如第3周、中期考核）',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `idx_mscore_proj_stu` (`PROJECT_ID`, `STUDENT_ID`) USING BTREE,
  KEY `idx_mscore_evaluator` (`EVALUATOR_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组员表现手动打分表（仅老师与该组组长可见）';

-- ----------------------------
-- Table structure for BIZ_PROJECT_RANKING
-- ----------------------------
DROP TABLE IF EXISTS `BIZ_PROJECT_RANKING`;
CREATE TABLE `BIZ_PROJECT_RANKING` (
  `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花ID）',
  `PROJECT_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目组ID（关联BIZ_PROJECT.ID）',
  `COMPETITION_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属竞赛ID（若为全局总排行榜则为NULL，关联BIZ_COMPETITION.ID）',
  `RANKING_PERIOD` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '榜单期数/学期标识（如 2026_SPRING）',
  `TEACHER_SCORE` decimal(5,2) DEFAULT '0.00' COMMENT '教师评定得分(0-100)',
  `AI_SCORE` decimal(5,2) DEFAULT '0.00' COMMENT 'AI综合评分(0-100)',
  `FINAL_SCORE` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '加权计算最终总得分(0-100)',
  `RANK_POSITION` int DEFAULT NULL COMMENT '排名名次（1, 2, 3...）',
  `IS_PUBLISHED` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NO' COMMENT '老师是否已发布：YES-已正式发布上榜（全员可见）, NO-草稿/未发布',
  `PUBLISH_TIME` datetime DEFAULT NULL COMMENT '发布排行榜时间',
  `PUBLISH_TEACHER_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '发布操作教师ID（关联SYS_USER.ID）',
  `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOT_DELETE' COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `SORT_CODE` int DEFAULT 0 COMMENT '排序码',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `uk_proj_period` (`PROJECT_ID`, `COMPETITION_ID`, `RANKING_PERIOD`, `DELETE_FLAG`) USING BTREE,
  KEY `idx_ranking_published` (`IS_PUBLISHED`, `FINAL_SCORE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目组评分与综合排行榜发布表';

SET FOREIGN_KEY_CHECKS = 1;
