<template>
  <div class="competition-detail-page" v-if="competition">
    <!-- 顶部概览 Banner -->
    <div class="detail-banner glass-card">
      <div class="banner-top-row">
        <a-button type="link" @click="$router.push('/competition')">← 返回竞赛广场</a-button>
        <div class="action-badges">
          <a-tag :color="getLevelColor(competition.competitionLevel)" style="font-size: 13px; padding: 4px 10px;">
            {{ competition.competitionLevel }}
          </a-tag>
          <a-tag :color="isCompetitionEnded ? 'default' : (isSignupClosed ? 'warning' : 'processing')" style="font-size: 13px; padding: 4px 10px;">
            {{ isCompetitionEnded ? '已结束' : (isSignupClosed ? '进行中 / 评审中' : '🔥 报名中') }}
          </a-tag>
        </div>
      </div>

      <div class="banner-main-row">
        <div class="banner-info">
          <h1 class="comp-title">{{ competition.title }}</h1>
          <div class="meta-tags">
            <span>🏛️ 主办单位：{{ competition.organizer || '教务处/学院委员会' }}</span>
            <span>·</span>
            <span>📂 竞赛类别：{{ competition.category || '学科实训' }}</span>
            <span>·</span>
            <span>👥 参赛规模：{{ competition.maxMembers === 1 ? '单人个人赛' : `${competition.minMembers} - ${competition.maxMembers} 人团队协作` }}</span>
          </div>
        </div>

        <div class="banner-cta">
          <!-- 关注 / 取消关注 按钮 -->
          <a-button
            :type="competition.isFollowed ? 'default' : 'dashed'"
            shape="round"
            size="large"
            :loading="followLoading"
            @click="handleToggleFollow"
            style="margin-right: 12px;"
          >
            {{ competition.isFollowed ? '⭐ 已关注' : '☆ 关注赛事' }} ({{ competition.followCount || 0 }})
          </a-button>

          <!-- 完结或截止状态提示 -->
          <template v-if="isCompetitionEnded">
            <a-tag color="error" style="font-size: 15px; padding: 6px 18px; border-radius: 20px; font-weight: 600;">
              🏁 该赛事已圆满完结结束
            </a-tag>
          </template>
          <template v-else-if="isSignupClosed">
            <a-tag color="warning" style="font-size: 15px; padding: 6px 18px; border-radius: 20px; font-weight: 600;">
              ⏳ 报名阶段已截止，评审实训中
            </a-tag>
          </template>
          <!-- 如果是单人赛 -->
          <template v-else-if="competition.maxMembers === 1">
            <a-button type="primary" shape="round" size="large" @click="handleIndividualSignup">
              🙋 单人报名参赛
            </a-button>
          </template>

          <!-- 如果是团队赛：学生创建项目组成为组长 / 老师前往团队大厅发布 -->
          <template v-else>
            <a-button v-if="userStore.isTeacher" type="primary" shape="round" size="large" @click="$router.push('/project?openTeacherCreate=1')">
              👨‍🏫 教师发布实训团队/招募令 (统一在团队大厅展示)
            </a-button>
            <a-button v-else type="primary" shape="round" size="large" @click="openCreateProjectModal">
              👑 创建参赛项目组 (成为组长)
            </a-button>
          </template>
        </div>
      </div>
    </div>

    <!-- 主要内容 Tabs 选项卡 -->
    <div class="detail-content-tabs glass-card">
      <a-tabs v-model:activeKey="activeTab" size="large">
        <!-- Tab 1: 参赛项目组/选手列表 -->
        <a-tab-pane key="teams" :tab="competition.maxMembers === 1 ? '👤 参赛选手阵容' : '👥 参赛项目组阵容'">
          <div class="teams-tab-header">
            <div class="header-desc">
              本竞赛目前已入驻 <strong>{{ teamList.length }}</strong> {{ competition.maxMembers === 1 ? '位报名参赛选手' : '支公开项目组' }}。
            </div>
            <template v-if="!isSignupClosed">
              <template v-if="competition.maxMembers > 1">
                <a-button v-if="userStore.isTeacher" type="primary" shape="round" @click="$router.push('/project?openTeacherCreate=1')">
                  👨‍🏫 前往团队大厅发布实训招募
                </a-button>
                <a-button v-else type="primary" shape="round" @click="openCreateProjectModal">
                  ➕ 发起我的团队
                </a-button>
              </template>
              <a-button type="primary" shape="round" @click="handleIndividualSignup" v-else>
                🙋 单人报名参赛
              </a-button>
            </template>
            <a-tag v-else color="default" style="padding: 4px 12px; border-radius: 12px;">
              {{ isCompetitionEnded ? '赛事已完结' : '报名已截止' }}
            </a-tag>
          </div>

          <div class="team-cards-grid" v-if="teamList.length > 0">
            <div v-for="team in teamList" :key="team.id" class="team-item-card glass-card">
              <div class="team-card-header">
                <div class="team-title-row">
                  <span class="team-avatar">{{ team.isIndividual ? '👤' : (team.hasLeader ? '🚀' : '🚩') }}</span>
                  <span class="team-name">{{ team.projectName }}</span>
                </div>
                <div class="team-tag-group">
                  <a-tag v-if="team.isTeacherInitiated" color="purple">👨‍🏫 教师指导组</a-tag>
                  <a-tag v-if="!team.isIndividual && !team.hasLeader" color="orange">👑 急聘组长/组员</a-tag>
                  <a-tag :color="team.isIndividual ? 'cyan' : (team.status === 'RECRUITING' ? 'warning' : 'default')">
                    {{ team.isIndividual ? '🎯 个人参赛选手' : (team.status === 'RECRUITING' ? '🔥 招募选拔中' : '组队完成') }}
                  </a-tag>
                </div>
              </div>
              <p class="team-desc">{{ team.projectDesc }}</p>

              <div class="team-meta-row">
                <div class="leader-info">
                  <a-avatar size="small" :style="{ backgroundColor: team.hasLeader ? '#f59e0b' : '#94a3b8' }">
                    {{ team.hasLeader ? (team.leaderName || 'L').substring(0, 1) : '?' }}
                  </a-avatar>
                  <span v-if="team.isIndividual">选手姓名：{{ team.leaderName }}</span>
                  <span v-else-if="team.hasLeader">组长：{{ team.leaderName }}</span>
                  <span v-else style="color: #ea580c; font-weight: 600;">组长：虚位以待 (面向学生竞聘)</span>
                  <span v-if="team.teacherName" style="margin-left: 8px; color: #7c3aed; font-size: 12px;">(指导教师：{{ team.teacherName }})</span>
                </div>
                <div class="member-count">
                  <span v-if="team.isIndividual">专业：{{ team.major || '计算机专业' }}</span>
                  <span v-else>当前队员：<strong>{{ team.currentMemberCount }}</strong> / {{ team.maxMemberLimit }} 人</span>
                </div>
              </div>

              <div class="team-card-actions">
                <template v-if="!team.isIndividual">
                  <a-space>
                    <!-- 学生申请报名加入按钮 -->
                    <template v-if="userStore.isStudent">
                      <span v-if="team.isMember" class="applied-badge success-badge">
                        ✅ 我已在该组内
                      </span>
                      <span v-else-if="team.myApplyStatus === 'PENDING'" class="applied-badge warning-badge">
                        ⏳ 已提交报名 ({{ team.myApplyRole === 'LEADER' ? '竞聘组长' : '申请组员' }})
                      </span>
                      <a-button
                        v-else-if="team.status === 'RECRUITING' && (team.currentMemberCount < team.maxMemberLimit)"
                        type="primary"
                        size="small"
                        shape="round"
                        @click="openApplyModal(team)"
                      >
                        🙋 报名竞聘/入组
                      </a-button>
                      <span v-else class="applied-badge default-badge">已满员</span>
                    </template>

                    <!-- 老师或组长筛选管理 -->
                    <a-button
                      v-if="userStore.isTeacher || team.isLeader"
                      type="primary"
                      ghost
                      size="small"
                      shape="round"
                      @click="openApplicantsModal(team)"
                    >
                      📋 筛选学生 ({{ team.pendingApplicantCount || 0 }})
                    </a-button>

                    <a-button type="link" size="small" @click="$router.push(`/project/detail/${team.id}`)">
                      进入主页 >
                    </a-button>
                  </a-space>
                </template>
                <span v-else style="font-size: 12px; color: #10b981; font-weight: 600;">已正式参赛</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-tab">
            <a-empty :description="competition.maxMembers === 1 ? '暂无选手报名，快点击右上角单人报名参赛吧！' : '当前竞赛暂无公开团队，快成为第一个创建团队的组长吧！'" />
          </div>
        </a-tab-pane>

        <!-- Tab 2: 竞赛规程与要求 -->
        <a-tab-pane key="requirements" tab="📋 规程与要求">
          <div class="rule-box">
            <div class="rule-section">
              <h3>一、赛事背景与简介</h3>
              <p>{{ competition.title }} 作为本学期重点实训学科竞赛，旨在通过实践驱动理论转化，提升全校学生的算法工程实践与跨领域团队协作水平。</p>
            </div>
            <div class="rule-section">
              <h3>二、参赛要求与规程细则</h3>
              <div class="req-text-box">
                {{ competition.requirements || '详见主办方规程发布说明文件。' }}
              </div>
            </div>
            <div class="rule-section">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                <h3>三、时间轴规程</h3>
                <a-button v-if="userStore.isTeacher" type="link" size="small" @click="openEditCompModal">
                  ✏️ 修改规程与竞赛时间
                </a-button>
              </div>
              <a-timeline style="margin-top: 16px;">
                <a-timeline-item color="green">
                  报名启动：{{ formatDate(competition.signupStartTime) }}
                </a-timeline-item>
                <a-timeline-item color="blue">
                  报名与组队截止：{{ formatDate(competition.signupEndTime) }}
                </a-timeline-item>
                <a-timeline-item color="orange">
                  竞赛开始时间：{{ formatDate(competition.competitionStartTime) }}
                </a-timeline-item>
                <a-timeline-item color="red" v-if="competition.competitionEndTime">
                  竞赛结束时间：{{ formatDate(competition.competitionEndTime) }}
                </a-timeline-item>
              </a-timeline>
            </div>
          </div>
        </a-tab-pane>

        <!-- Tab 3: 组长专属-感兴趣的学生库 (已关注本竞赛) -->
        <a-tab-pane key="interested" tab="⭐ 感兴趣的学生 (组长招募专区)">
          <div class="interested-header">
            <a-alert
              message="组长引才雷达"
              description="以下学生已对本竞赛点击“关注”，表明具备浓厚参赛意向。作为组长，您可以直接发起邀请，组建梦之队！"
              type="info"
              show-icon
              style="margin-bottom: 20px;"
            />
          </div>

          <div class="student-cards-grid" v-if="interestedStudents.length > 0">
            <div v-for="stu in interestedStudents" :key="stu.studentId" class="stu-card glass-card">
              <div class="stu-header">
                <a-avatar size="large" style="background-color: #3b82f6;">{{ (stu.name || 'S').substring(0, 1) }}</a-avatar>
                <div class="stu-info">
                  <div class="stu-name">{{ stu.name }}</div>
                  <div class="stu-major">{{ stu.profile?.major || '计算机学院' }} · {{ stu.profile?.className || '2023级' }}</div>
                </div>
              </div>
              <div class="stu-tags" v-if="stu.profile?.skillTags">
                <a-tag v-for="tag in stu.profile.skillTags.split(',')" :key="tag" color="blue" size="small">{{ tag }}</a-tag>
              </div>
              <div class="stu-bio">{{ stu.profile?.bio || '暂无个人简介' }}</div>
              <div class="stu-actions" v-if="currentCompLeaderProject">
                <a-button type="primary" size="small" shape="round" @click="openInviteModal(stu)">
                  ✉️ 邀请加入我的项目组 ({{ currentCompLeaderProject.projectName }})
                </a-button>
              </div>
              <div class="stu-actions" v-else-if="userStore.isLoggedIn">
                <a-tag color="default" style="font-size: 11px;">需在本赛事创建项目成为组长后发起邀请</a-tag>
              </div>
              <div class="stu-actions" v-else>
                <a-tag color="default" style="font-size: 11px;">登录后可发起邀请</a-tag>
              </div>
            </div>
          </div>
          <div v-else class="empty-tab">
            <a-empty description="暂未有其他学生关注本竞赛" />
          </div>
        </a-tab-pane>

        <!-- Tab 4: 竞赛证书与荣誉申报 -->
        <a-tab-pane key="certs" tab="📜 竞赛证书与荣誉">
          <div class="cert-header">
            <div class="header-desc">
              参赛队员或组长均可上传参赛获奖证书，提交后由指导教师进行官方认证认证。
            </div>
            <a-button type="primary" shape="round" @click="openUploadCertModal">
              📤 上传新获奖证书
            </a-button>
          </div>

          <a-table :dataSource="certificateList" :columns="certColumns" :pagination="false" rowKey="id" style="margin-top: 16px;">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'verifyStatus'">
                <a-tag :color="record.verifyStatus === 'VERIFIED' ? 'success' : record.verifyStatus === 'REJECTED' ? 'error' : 'warning'">
                  {{ record.verifyStatus === 'VERIFIED' ? '已认证' : record.verifyStatus === 'REJECTED' ? '已驳回' : '待教师认证' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <!-- 教师可直接认证 -->
                <a-button
                  v-if="userStore.isTeacher && record.verifyStatus === 'PENDING'"
                  type="link"
                  size="small"
                  @click="openVerifyModal(record)"
                >
                  审核认证
                </a-button>
                <span v-else style="color: #94a3b8; font-size: 12px;">{{ record.verifyRemark || '无评语' }}</span>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 创建参赛项目组对话框 -->
    <a-modal
      v-model:open="createProjModalVisible"
      :title="userStore.isTeacher ? '教师发布实训项目组 (面向学生公开招募)' : '创建参赛项目组 (成为组长)'"
      :ok-text="userStore.isTeacher ? '确认发布项目组' : '确认建组并报名'"
      cancel-text="取消"
      @ok="handleCreateProject"
    >
      <a-alert
        :message="userStore.isTeacher ? '教师建组招募机制说明' : '组长身份说明'"
        :description="userStore.isTeacher ? '作为指导教师，您创建的项目组将公开发布到竞赛广场，您不担任组长。学生可自主报名竞聘【组长】或申请【组员】，由您统一在线筛选录取！' : '创建项目后，您将自动获得本项目【组长】身份与管理工作台，享有组员招募、管理、私密控制、AI评测与论坛审查表决权！'"
        type="success"
        show-icon
        style="margin-bottom: 16px;"
      />
      <a-form :model="createProjForm" layout="vertical">
        <a-form-item label="项目团队名称" required>
          <a-input v-model:value="createProjForm.projectName" placeholder="如：智行千里创新实训队" />
        </a-form-item>
        <a-form-item label="项目简介与愿景目标" required>
          <a-textarea v-model:value="createProjForm.projectDesc" :rows="3" placeholder="请阐述团队拟研发的项目方向、算法攻关目标及技术路线" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="队伍人数上限">
              <a-input-number v-model:value="createProjForm.maxMemberLimit" :min="1" :max="competition.maxMembers || 10" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="隐私设置 (私密项目他人不可查看)">
              <a-select v-model:value="createProjForm.isPrivate">
                <a-select-option value="NO">公开团队 (在广场展示)</a-select-option>
                <a-select-option value="YES">私密团队 (仅组员可查看)</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 学生报名入组/竞聘组长对话框 -->
    <a-modal
      v-model:open="applyModalVisible"
      :title="`报名申请加入项目组 - ${currentApplyTeam?.projectName || ''}`"
      ok-text="确认提交申请"
      cancel-text="取消"
      :confirm-loading="applyLoading"
      @ok="handleApplyToProject"
    >
      <a-alert
        v-if="!currentApplyTeam?.hasLeader"
        message="组长虚位以待"
        description="本项目由指导教师发起，目前暂未设立组长！您可以选择竞聘【组长】带领队伍，也可以申请成为【组员】。已担任其他项目组长的学生无法再次竞聘组长。"
        type="info"
        show-icon
        style="margin-bottom: 16px;"
      />
      <a-alert
        v-else
        message="申请加入团队"
        description="本项目已有组长，您可申请成为组员。提交后需等待指导教师或组长筛选审核通过后入组。"
        type="success"
        show-icon
        style="margin-bottom: 16px;"
      />
      <a-form layout="vertical">
        <a-form-item label="申请意向岗位" required>
          <a-radio-group v-model:value="applyForm.applyRole">
            <a-radio value="LEADER" :disabled="currentApplyTeam?.hasLeader || userStore.isLeader">
              👑 竞聘组长
              <span v-if="currentApplyTeam?.hasLeader" style="color: #94a3b8; font-size: 12px; margin-left: 4px;">(已有组长)</span>
              <span v-else-if="userStore.isLeader" style="color: #ef4444; font-size: 12px; margin-left: 4px;">(您已是组长，不可重复竞聘)</span>
            </a-radio>
            <a-radio value="MEMBER">
              👥 申请成为组员
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="申请留言 / 个人特长与优势介绍" required>
          <a-textarea
            v-model:value="applyForm.message"
            :rows="3"
            placeholder="请简要介绍您的技术栈、特长经验，以及为项目攻关能带来的贡献..."
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 老师或组长筛选报名学生抽屉/对话框 -->
    <a-modal
      v-model:open="applicantsModalVisible"
      :title="`报名学生筛选与录用 - ${currentScreeningTeam?.projectName || ''}`"
      width="820px"
      :footer="null"
    >
      <a-alert
        message="筛选录用规则"
        description="指导教师与组长可在线对提交报名的学生进行资格审核与选拔。录用为组长后，学生将获得组长管理权限（已担任其他项目组长的学生系统将自动限制录用为组长）。"
        type="info"
        show-icon
        style="margin-bottom: 16px;"
      />
      <a-table
        :dataSource="applicantsList"
        :columns="applicantColumns"
        rowKey="id"
        :loading="applicantsLoading"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'student'">
            <div style="display: flex; align-items: center; gap: 8px;">
              <a-avatar size="small" style="background-color: #3b82f6;">
                {{ (record.studentName || 'S').substring(0, 1) }}
              </a-avatar>
              <div>
                <div style="font-weight: 600; color: #0f172a;">{{ record.studentName }}</div>
                <div style="font-size: 12px; color: #64748b;">{{ record.major || '计算机' }} · {{ record.studentNo || record.studentAccount }}</div>
              </div>
            </div>
          </template>
          <template v-if="column.key === 'applyRole'">
            <a-tag :color="record.applyRole === 'LEADER' ? 'gold' : 'blue'">
              {{ record.applyRole === 'LEADER' ? '👑 竞聘组长' : '👥 申请组员' }}
            </a-tag>
            <div v-if="record.isAlreadyLeader" style="color: #ef4444; font-size: 11px; margin-top: 2px;">
              ⚠️ 现已担任组长
            </div>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACCEPTED' ? 'success' : record.status === 'REJECTED' ? 'error' : 'warning'">
              {{ record.status === 'ACCEPTED' ? '已录用' : record.status === 'REJECTED' ? '已淘汰' : '待审核' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <template v-if="record.status === 'PENDING'">
              <a-space>
                <a-popconfirm
                  :title="`确认录用 ${record.studentName} 同学为【${record.applyRole === 'LEADER' ? '团队组长' : '核心组员'}】？`"
                  ok-text="确认录用"
                  cancel-text="取消"
                  @confirm="handleScreenApplicant(record.id, true)"
                >
                  <a-button type="primary" size="small">录用</a-button>
                </a-popconfirm>
                <a-popconfirm
                  title="确认淘汰该申请？"
                  ok-text="淘汰"
                  cancel-text="取消"
                  @confirm="handleScreenApplicant(record.id, false)"
                >
                  <a-button danger size="small">淘汰</a-button>
                </a-popconfirm>
              </a-space>
            </template>
            <span v-else style="font-size: 12px; color: #94a3b8;">
              {{ record.handleRemark || '已处理' }}
            </span>
          </template>
        </template>
      </a-table>
    </a-modal>

    <!-- 老师修改竞赛规程与时间对话框 -->
    <a-modal
      v-model:open="editCompModalVisible"
      title="修改竞赛信息与时间规程"
      width="680px"
      ok-text="保存修改"
      cancel-text="取消"
      @ok="handleEditCompetitionSubmit"
    >
      <a-form :model="editCompForm" layout="vertical" style="margin-top: 16px;">
        <a-form-item label="竞赛名称" required>
          <a-input v-model:value="editCompForm.title" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="竞赛级别">
              <a-select v-model:value="editCompForm.competitionLevel">
                <a-select-option value="SCHOOL">校级竞赛</a-select-option>
                <a-select-option value="PROVINCE">省级竞赛</a-select-option>
                <a-select-option value="NATIONAL">国家级竞赛</a-select-option>
                <a-select-option value="INTERNATIONAL">国际级赛事</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="主办单位/承办方">
              <a-input v-model:value="editCompForm.organizer" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="报名开始时间">
              <a-date-picker v-model:value="editCompForm.signupStartTime" show-time style="width: 100%" placeholder="选择报名开始时间" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="报名截止时间">
              <a-date-picker v-model:value="editCompForm.signupEndTime" show-time style="width: 100%" placeholder="选择报名截止时间" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="竞赛开始时间">
              <a-date-picker v-model:value="editCompForm.competitionStartTime" show-time style="width: 100%" placeholder="选择竞赛开始时间" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="竞赛结束时间">
              <a-date-picker v-model:value="editCompForm.competitionEndTime" show-time style="width: 100%" placeholder="选择竞赛结束时间" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="竞赛要求与参赛规程" required>
          <a-textarea v-model:value="editCompForm.requirements" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 组长发送入组邀请对话框 -->
    <a-modal
      v-model:open="inviteModalVisible"
      title="发送入组邀请"
      ok-text="发出邀请"
      cancel-text="取消"
      @ok="handleSendInvite"
    >
      <div v-if="selectedStudent" style="margin-bottom: 16px;">
        邀请目标学生：<strong>{{ selectedStudent.name }}</strong> ({{ selectedStudent.profile?.major || '学生' }})
      </div>
      <div class="auto-matched-project-box glass-panel" style="padding: 12px 16px; margin-bottom: 16px; border-radius: 8px; background: rgba(59, 130, 246, 0.08); border: 1px solid rgba(59, 130, 246, 0.2);">
        <div style="font-size: 12px; color: #64748b; margin-bottom: 4px;">🚀 邀请加入的本竞赛项目组（自动匹配）：</div>
        <div style="font-size: 15px; font-weight: 600; color: #1e293b;">
          {{ currentCompLeaderProject?.projectName || '本竞赛项目组' }}
        </div>
      </div>
      <a-form layout="vertical">
        <a-form-item label="附言/邀请诚意说明">
          <a-textarea v-model:value="inviteForm.message" :rows="3" placeholder="例如：同学你好，在竞赛关注区看到了你的技能特长，诚挚邀请你加入我们队伍！" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 上传证书对话框 -->
    <a-modal
      v-model:open="uploadCertModalVisible"
      title="上传竞赛获奖荣誉证书"
      ok-text="确认上传"
      cancel-text="取消"
      @ok="handleUploadCert"
    >
      <a-form :model="certForm" layout="vertical">
        <a-form-item label="证书/荣誉名称" required>
          <a-input v-model:value="certForm.certName" placeholder="如：2026年全国大学生算法实训大赛国家一等奖" />
        </a-form-item>
        <a-form-item label="获奖级别">
          <a-select v-model:value="certForm.awardLevel">
            <a-select-option value="特等奖">特等奖 / 冠军</a-select-option>
            <a-select-option value="国家一等奖">国家一等奖</a-select-option>
            <a-select-option value="国家二等奖">国家二等奖</a-select-option>
            <a-select-option value="省级一等奖">省级一等奖</a-select-option>
            <a-select-option value="校级荣誉">校级荣誉 / 优秀奖</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="获奖日期">
          <a-date-picker v-model:value="certForm.awardDate" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 教师审核证书对话框 -->
    <a-modal
      v-model:open="verifyModalVisible"
      title="教师审核竞赛证书"
      ok-text="提交审核结果"
      cancel-text="取消"
      @ok="handleVerifyCert"
    >
      <div v-if="selectedCert" style="margin-bottom: 16px;">
        审核证书：<strong>{{ selectedCert.certificateName }}</strong> ({{ selectedCert.awardLevel }})
      </div>
      <a-form layout="vertical">
        <a-form-item label="审核结论" required>
          <a-radio-group v-model:value="verifyForm.status" button-style="solid">
            <a-radio-button value="VERIFIED">✅ 认证通过</a-radio-button>
            <a-radio-button value="REJECTED">❌ 驳回存疑</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="审核评语与批注">
          <a-textarea v-model:value="verifyForm.remark" :rows="3" placeholder="请输入指导教师评语" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const competitionId = route.params.id
const competition = ref(null)
const activeTab = ref('teams')
const followLoading = ref(false)

const isCompetitionEnded = computed(() => {
  if (!competition.value) return false
  if (competition.value.isEnded) return true
  if (competition.value.status === 'FINISHED') return true
  if (competition.value.competitionEndTime && dayjs().isAfter(dayjs(competition.value.competitionEndTime))) return true
  return false
})

const isSignupClosed = computed(() => {
  if (!competition.value) return false
  if (isCompetitionEnded.value) return true
  if (competition.value.isSignupClosed) return true
  if (competition.value.status === 'ONGOING' || competition.value.status === 'FINISHED') return true
  if (competition.value.signupEndTime && dayjs().isAfter(dayjs(competition.value.signupEndTime))) return true
  return false
})

const teamList = ref([])
const interestedStudents = ref([])
const certificateList = ref([])
const myLeaderProjects = ref([])

// 当前登录用户在该竞赛下担任组长的项目（自动匹配）
const currentCompLeaderProject = computed(() => {
  return myLeaderProjects.value.find(p => p.competitionId === competitionId)
})

// 对话框状态
const createProjModalVisible = ref(false)
const inviteModalVisible = ref(false)
const uploadCertModalVisible = ref(false)
const verifyModalVisible = ref(false)

const applyModalVisible = ref(false)
const currentApplyTeam = ref(null)
const applyLoading = ref(false)
const applyForm = reactive({
  applyRole: 'MEMBER',
  message: ''
})

const applicantsModalVisible = ref(false)
const currentScreeningTeam = ref(null)
const applicantsList = ref([])
const applicantsLoading = ref(false)
const applicantColumns = [
  { title: '申请学生', key: 'student', width: 220 },
  { title: '意向岗位', key: 'applyRole', width: 130 },
  { title: '申请理由/特长', dataIndex: 'message', key: 'message', ellipsis: true },
  { title: '申请时间', dataIndex: 'createTime', key: 'createTime', width: 150 },
  { title: '状态', key: 'status', width: 90 },
  { title: '筛选操作', key: 'action', width: 140 }
]

const editCompModalVisible = ref(false)
const editCompForm = reactive({
  id: '',
  title: '',
  competitionLevel: 'SCHOOL',
  organizer: '',
  category: '',
  minMembers: 1,
  maxMembers: 5,
  signupStartTime: null,
  signupEndTime: null,
  competitionStartTime: null,
  competitionEndTime: null,
  requirements: '',
  status: 'SIGNUP'
})

const selectedStudent = ref(null)
const selectedCert = ref(null)

const createProjForm = reactive({
  projectName: '',
  projectDesc: '',
  maxMemberLimit: 5,
  isPrivate: 'NO'
})

const inviteForm = reactive({
  projectId: '',
  message: ''
})

const certForm = reactive({
  certName: '',
  awardLevel: '国家一等奖',
  awardDate: null
})

const verifyForm = reactive({
  status: 'VERIFIED',
  remark: ''
})

const certColumns = [
  { title: '证书荣誉名称', dataIndex: 'certificateName', key: 'certificateName' },
  { title: '获奖级别', dataIndex: 'awardLevel', key: 'awardLevel' },
  { title: '获得日期', dataIndex: 'awardDate', key: 'awardDate' },
  { title: '认证状态', key: 'verifyStatus' },
  { title: '教师评语 / 操作', key: 'action' }
]

const loadDetail = async () => {
  try {
    const res = await portalApi.getCompetitionDetail(competitionId)
    competition.value = res
  } catch (e) {
    console.error(e)
  }
}

const loadTeams = async () => {
  try {
    const res = await portalApi.getCompetitionProjects(competitionId)
    const list = Array.isArray(res) ? res : (res?.data || [])
    teamList.value = list.filter(t => t.competitionId !== 'UNASSIGNED')
  } catch (e) {}
}

const loadInterested = async () => {
  try {
    const res = await portalApi.getInterestedStudents(competitionId)
    const list = Array.isArray(res) ? res : (res?.data || [])
    // 过滤掉当前登录用户自己，避免在关注列表里看到自己
    interestedStudents.value = list.filter(s => s.studentId !== userStore.userInfo?.id)
  } catch (e) {}
}

const loadCertificates = async () => {
  try {
    const res = await portalApi.getMyCertificates(competitionId)
    certificateList.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {}
}

const loadMyLeaderProjects = async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await portalApi.getMyProjects()
      const projs = res.ledProjects || res.data?.ledProjects || (Array.isArray(res) ? res : [])
      myLeaderProjects.value = (projs || []).filter(p => p.isLeader || p.leaderId === userStore.userInfo?.id)
    } catch (e) {}
  }
}

// 关注 / 取消关注
const handleToggleFollow = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  followLoading.value = true
  try {
    const res = await portalApi.toggleFollow({
      targetType: 'COMPETITION',
      targetId: competitionId
    })
    competition.value.isFollowed = res
    competition.value.followCount = (competition.value.followCount || 0) + (res ? 1 : -1)
    message.success(res ? '已成功关注该竞赛，并自动将动态同步至论坛社区！' : '已取消关注')
    loadInterested()
  } catch (e) {
    console.error(e)
  } finally {
    followLoading.value = false
  }
}

// 单人报名
const handleIndividualSignup = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (isCompetitionEnded.value) {
    message.warning('该竞赛已圆满完结结束，无法继续报名')
    return
  }
  if (isSignupClosed.value) {
    message.warning('该竞赛报名已截止，无法继续报名')
    return
  }
  try {
    await portalApi.individualSignup(competitionId)
    message.success('单人报名成功！已录入官方选手名册')
    loadTeams()
  } catch (e) {}
}

// 打开创建项目 Modal
const openCreateProjectModal = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (userStore.isTeacher) {
    message.info('教师发布的实训团队与招募令统一在团队大厅展示，正在为您跳转...')
    router.push('/project?openTeacherCreate=1')
    return
  }
  if (isCompetitionEnded.value) {
    message.warning('该竞赛已圆满完结结束，无法创建参赛项目组')
    return
  }
  if (isSignupClosed.value) {
    message.warning('该竞赛报名已截止，无法创建参赛项目组')
    return
  }
  if (userStore.isStudent && userStore.isLeader) {
    message.warning('您当前已在其他项目中担任组长，无法再次创建并担任组长！')
    return
  }
  createProjModalVisible.value = true
}

// 确认创建项目
const handleCreateProject = async () => {
  if (!createProjForm.projectName || !createProjForm.projectDesc) {
    message.warning('请填写项目名称和描述')
    return
  }
  try {
    const res = await portalApi.createProject({
      competitionId,
      ...createProjForm
    })
    if (userStore.isTeacher) {
      message.success('🎉 老师实训项目组发布成功！已面向广大学生开放竞聘报名，请在团队列表中筛选学生。')
    } else {
      message.success('🎉 恭喜！项目组创建成功，您已自动晋升为组长！')
    }
    createProjModalVisible.value = false
    await userStore.fetchProfile()
    loadTeams()
    loadMyLeaderProjects()
    if (!userStore.isTeacher) {
      router.push(`/project/detail/${res}`)
    }
  } catch (e) {
    console.error(e)
  }
}

// 学生报名申请加入团队
const openApplyModal = (team) => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  currentApplyTeam.value = team
  if (!team.hasLeader && !userStore.isLeader) {
    applyForm.applyRole = 'LEADER'
  } else {
    applyForm.applyRole = 'MEMBER'
  }
  applyForm.message = ''
  applyModalVisible.value = true
}

const handleApplyToProject = async () => {
  if (!currentApplyTeam.value) return
  if (applyForm.applyRole === 'LEADER' && userStore.isLeader) {
    message.warning('您当前已是组长，无法再次竞聘组长职位！')
    return
  }
  applyLoading.value = true
  try {
    await portalApi.applyToProject({
      projectId: currentApplyTeam.value.id,
      applyRole: applyForm.applyRole,
      message: applyForm.message
    })
    message.success('报名申请提交成功，请静候指导教师或团队审核筛选！')
    applyModalVisible.value = false
    loadTeams()
  } catch (e) {
    console.error(e)
  } finally {
    applyLoading.value = false
  }
}

// 老师或组长筛选学生
const openApplicantsModal = async (team) => {
  currentScreeningTeam.value = team
  applicantsModalVisible.value = true
  loadApplicants(team.id)
}

const loadApplicants = async (projectId) => {
  applicantsLoading.value = true
  try {
    const res = await portalApi.getProjectApplicants(projectId)
    applicantsList.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  } finally {
    applicantsLoading.value = false
  }
}

const handleScreenApplicant = async (flowId, accept) => {
  try {
    await portalApi.handleProjectApplicant({
      flowId,
      accept,
      remark: accept ? '审核通过，已成功录用' : '未通过筛选'
    })
    message.success(accept ? '已成功录用该同学！' : '已淘汰该申请')
    if (currentScreeningTeam.value) {
      loadApplicants(currentScreeningTeam.value.id)
    }
    loadTeams()
    await userStore.fetchProfile()
  } catch (e) {
    console.error(e)
  }
}

// 老师修改竞赛信息与时间规程
const openEditCompModal = () => {
  if (!competition.value) return
  const c = competition.value
  editCompForm.id = c.id
  editCompForm.title = c.title
  editCompForm.competitionLevel = c.competitionLevel || 'SCHOOL'
  editCompForm.organizer = c.organizer || ''
  editCompForm.category = c.category || '计算机与算法'
  editCompForm.minMembers = c.minMembers || 1
  editCompForm.maxMembers = c.maxMembers || 5
  editCompForm.signupStartTime = c.signupStartTime ? dayjs(c.signupStartTime) : null
  editCompForm.signupEndTime = c.signupEndTime ? dayjs(c.signupEndTime) : null
  editCompForm.competitionStartTime = c.competitionStartTime ? dayjs(c.competitionStartTime) : null
  editCompForm.competitionEndTime = c.competitionEndTime ? dayjs(c.competitionEndTime) : null
  editCompForm.requirements = c.requirements || ''
  editCompForm.status = c.status || 'SIGNUP'
  editCompModalVisible.value = true
}

const handleEditCompetitionSubmit = async () => {
  if (!editCompForm.title || !editCompForm.requirements) {
    message.warning('请填写竞赛名称及参赛要求')
    return
  }
  try {
    const payload = {
      ...editCompForm,
      signupStartTime: editCompForm.signupStartTime ? dayjs(editCompForm.signupStartTime).format('YYYY-MM-DD HH:mm:ss') : null,
      signupEndTime: editCompForm.signupEndTime ? dayjs(editCompForm.signupEndTime).format('YYYY-MM-DD HH:mm:ss') : null,
      competitionStartTime: editCompForm.competitionStartTime ? dayjs(editCompForm.competitionStartTime).format('YYYY-MM-DD HH:mm:ss') : null,
      competitionEndTime: editCompForm.competitionEndTime ? dayjs(editCompForm.competitionEndTime).format('YYYY-MM-DD HH:mm:ss') : null
    }
    await portalApi.editCompetition(payload)
    message.success('竞赛规程与时间修改成功！')
    editCompModalVisible.value = false
    loadDetail()
  } catch (e) {
    console.error(e)
  }
}

// 打开邀请 Modal
const openInviteModal = (student) => {
  const myProj = currentCompLeaderProject.value
  if (!myProj) {
    message.warning('您当前在本竞赛下尚未创建项目组，请先在上方【创建参赛项目组】成为组长！')
    return
  }
  selectedStudent.value = student
  inviteForm.projectId = myProj.id
  inviteModalVisible.value = true
}

// 发送入组邀请
const handleSendInvite = async () => {
  if (!inviteForm.projectId) {
    message.warning('请选择项目组')
    return
  }
  try {
    await portalApi.inviteStudent({
      projectId: inviteForm.projectId,
      studentId: selectedStudent.value.studentId,
      message: inviteForm.message
    })
    message.success(`已向 ${selectedStudent.value.name} 发送入组邀请！`)
    inviteModalVisible.value = false
  } catch (e) {}
}

// 上传证书
const openUploadCertModal = () => {
  uploadCertModalVisible.value = true
}

const handleUploadCert = async () => {
  if (!certForm.certName) {
    message.warning('请输入证书名称')
    return
  }
  try {
    await portalApi.uploadCertificate({
      competitionId,
      certName: certForm.certName,
      awardLevel: certForm.awardLevel,
      awardDate: certForm.awardDate ? dayjs(certForm.awardDate).format('YYYY-MM-DD') : null
    })
    message.success('证书上传成功，已提交指导教师审核！')
    uploadCertModalVisible.value = false
    loadCertificates()
  } catch (e) {}
}

// 教师审核认证
const openVerifyModal = (record) => {
  selectedCert.value = record
  verifyModalVisible.value = true
}

const handleVerifyCert = async () => {
  try {
    await portalApi.verifyCertificate({
      certId: selectedCert.value.id,
      status: verifyForm.status,
      remark: verifyForm.remark
    })
    message.success('审核结论提交成功！')
    verifyModalVisible.value = false
    loadCertificates()
  } catch (e) {}
}

const getLevelColor = (level) => {
  if (level === 'NATIONAL') return 'red'
  if (level === 'PROVINCE') return 'orange'
  if (level === 'INTERNATIONAL') return 'purple'
  return 'blue'
}

const formatDate = (d) => {
  if (!d) return '待定'
  return dayjs(d).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  loadDetail()
  loadTeams()
  loadInterested()
  loadCertificates()
  loadMyLeaderProjects()
})
</script>

<style scoped>
.competition-detail-page {
  max-width: 1320px;
  margin: 32px auto;
  padding: 0 24px;
}

.detail-banner {
  padding: 32px;
  margin-bottom: 24px;
}

.banner-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.action-badges {
  display: flex;
  gap: 8px;
}

.banner-main-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  flex-wrap: wrap;
}

.comp-title {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.3;
  margin-bottom: 10px;
}

.meta-tags {
  display: flex;
  gap: 10px;
  color: #64748b;
  font-size: 14px;
  flex-wrap: wrap;
}

.detail-content-tabs {
  padding: 24px 32px;
}

.teams-tab-header, .interested-header, .cert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-desc {
  font-size: 14px;
  color: #475569;
}

.team-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.team-item-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.team-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.team-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-avatar {
  font-size: 20px;
}

.team-name {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.team-desc {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.team-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.leader-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.team-card-actions {
  text-align: right;
}

.rule-box {
  padding: 12px 0;
}

.rule-section {
  margin-bottom: 28px;
}

.rule-section h3 {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 10px;
}

.req-text-box {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 16px;
  border-radius: 10px;
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.6;
  color: #334155;
}

.student-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.stu-card {
  padding: 20px;
}

.stu-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.stu-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.stu-major {
  font-size: 12px;
  color: #64748b;
}

.stu-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.stu-bio {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
  margin-bottom: 16px;
}

.stu-actions {
  text-align: right;
}

.empty-tab {
  padding: 60px 0;
  text-align: center;
}

.applied-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}

.applied-badge.success-badge {
  background: #ecfdf5;
  color: #059669;
  border: 1px solid #a7f3d0;
}

.applied-badge.warning-badge {
  background: #fffbeb;
  color: #d97706;
  border: 1px solid #fde68a;
}

.applied-badge.default-badge {
  background: #f1f5f9;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.team-tag-group {
  display: flex;
  gap: 4px;
  align-items: center;
  flex-wrap: wrap;
}
</style>
