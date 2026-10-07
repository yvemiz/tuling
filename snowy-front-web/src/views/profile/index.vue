<template>
  <div class="profile-container">
    <!-- 个人头部名片卡 -->
    <div class="profile-header-card">
      <div class="header-main">
        <a-avatar :size="72" class="user-avatar">
          {{ userStore.userInfo?.name ? userStore.userInfo.name.charAt(0) : 'U' }}
        </a-avatar>
        <div class="user-meta">
          <div class="name-row">
            <h2 class="user-name">{{ userStore.userInfo?.name || '用户名' }}</h2>
            <div class="role-badges">
              <a-tag v-if="userStore.isTeacher" color="purple" class="custom-badge">
                👨‍🏫 指导教师
              </a-tag>
              <a-tag v-if="userStore.isLeader" color="orange" class="custom-badge">
                👑 团队组长 (带领 {{ userStore.userRoleInfo?.ledProjectsCount || 1 }} 组)
              </a-tag>
              <a-tag v-if="userStore.isStudent" color="blue" class="custom-badge">
                👨‍🎓 认证学生
              </a-tag>
            </div>
          </div>
          <div class="user-sub">
            <span>账号：{{ userStore.userInfo?.account }}</span>
            <span class="divider">|</span>
            <span>邮箱：{{ userStore.userInfo?.email || '未绑定邮箱' }}</span>
            <span class="divider">|</span>
            <span>手机：{{ userStore.userInfo?.phone || '未绑定手机' }}</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <a-button @click="openEditProfileModal">
          <template #icon><EditOutlined /></template>
          编辑资料
        </a-button>
        <a-button type="primary" class="gradient-btn" @click="openCreateProjectModal">
          <template #icon><PlusOutlined /></template>
          创建项目组（晋升组长）
        </a-button>
      </div>
    </div>

    <!-- 组长与教师大群未读消息提示横幅 -->
    <div
      v-if="leaderTeacherUnreadCount > 0 && activePerspective !== 'leaderTeacherChat' && (userStore.isLeader || userStore.isTeacher || (ledProjects && ledProjects.length > 0))"
      class="leader-chat-unread-banner glass-card"
      @click="activePerspective = 'leaderTeacherChat'"
    >
      <div class="banner-left">
        <span class="pulse-dot"></span>
        <span class="banner-icon">👑</span>
        <span class="banner-text">
          组长与教师领航群有 <strong class="unread-highlight">{{ leaderTeacherUnreadCount }}</strong> 条未读交流消息，点击立即前往群聊查看！
        </span>
      </div>
      <a-button type="primary" size="small" shape="round" class="go-chat-btn">
        立即查看 ➔
      </a-button>
    </div>

    <!-- 三大角色视角导航标签栏 -->
    <div class="perspective-nav">
      <a-tabs v-model:activeKey="activePerspective" type="card" class="custom-perspective-tabs">
        <!-- 学生视角 -->
        <a-tab-pane key="student" tab="👨‍🎓 学生个人空间">
          <!-- 组长催交算法通知横幅 -->
          <div v-if="myUrges.length > 0" class="urges-banner" style="background: #fef2f2; border: 1px solid #fee2e2; border-radius: 16px; padding: 16px 20px; margin-bottom: 20px;">
            <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 8px;">
              <div style="display: flex; align-items: center; gap: 10px; color: #dc2626; font-weight: 700;">
                <AlertOutlined style="font-size: 18px" />
                <span>催交提醒：您收到 {{ myUrges.length }} 条来自组长的算法任务督促！</span>
              </div>
              <a-button type="primary" danger size="small" @click="router.push('/algorithm')">
                立即前往算法中心
              </a-button>
            </div>
            <div style="margin-top: 10px; font-size: 13px; color: #7f1d1d;">
              最新督促【{{ myUrges[0].taskTitle }}】: {{ myUrges[0].urgeMessage }} (来自 {{ myUrges[0].leaderName }})
            </div>
          </div>

          <!-- 组队邀请通知横幅 -->
          <div v-if="invitations.length > 0" class="invitations-banner">
            <div class="banner-title">
              <BellOutlined style="color: #f59e0b; font-size: 18px" />
              <span>您有 <strong>{{ invitations.length }}</strong> 条待处理的入组邀请！</span>
            </div>
            <div class="invites-grid">
              <div v-for="inv in invitations" :key="inv.id" class="invite-card">
                <div class="invite-info">
                  <div class="inv-team">来自项目组：<strong>{{ inv.projectName }}</strong></div>
                  <div class="inv-meta">
                    <span>邀请人：{{ inv.operatorName || '组长' }}</span>
                    <span>竞赛：{{ inv.competitionTitle }}</span>
                  </div>
                </div>
                <div class="invite-actions">
                  <a-button
                    type="primary"
                    size="small"
                    style="background: #10b981; border-color: #10b981"
                    @click="handleInvite(inv.id, 'ACCEPT')"
                  >
                    接受加入
                  </a-button>
                  <a-button
                    danger
                    size="small"
                    @click="handleInvite(inv.id, 'REJECT')"
                  >
                    婉拒
                  </a-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 我加入的项目组 -->
          <div class="section-card">
            <div class="section-header">
              <div class="section-title">
                <TeamOutlined style="color: #4f46e5" />
                <span>我参与的项目团队（{{ joinedProjects.length }}）</span>
              </div>
              <a-button type="link" @click="loadMyProjects">刷新列表</a-button>
            </div>

            <div v-if="joinedProjects.length === 0" class="empty-box">
              <a-empty description="暂未加入任何团队，前往竞赛广场或关注组长招募加入吧！" />
            </div>

            <div v-else class="project-cards-grid">
              <div
                v-for="p in joinedProjects"
                :key="p.id"
                class="project-item-card"
                @click="goToProject(p.id)"
              >
                <div class="card-top">
                  <span class="comp-badge">{{ p.competitionTitle || '校级竞赛' }}</span>
                  <a-tag :color="p.isPrivate === 'YES' ? 'red' : 'green'">
                    {{ p.isPrivate === 'YES' ? '🔒 私密团队' : '🌐 公开' }}
                  </a-tag>
                </div>
                <h3 class="proj-name">{{ p.projectName }}</h3>
                <p class="proj-desc">{{ p.projectDesc || '暂无项目简介' }}</p>
                <div class="proj-footer">
                  <span class="leader-text">组长：{{ p.leaderName }}</span>
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <a-button type="link" size="small" style="padding: 0; color: #4f46e5; font-weight: 600;" @click.stop="goToProjectChat(p.id)">
                      💬 小组讨论
                      <a-badge v-if="projectUnreadMap[p.id] > 0" :count="projectUnreadMap[p.id]" style="margin-left: 4px;" />
                    </a-button>
                    <span class="member-count">👥 {{ p.memberCount || 1 }} 位成员</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 待办任务与通知中心 (学生与组长全量待办任务) -->
          <div class="section-card todo-center-card">
            <div class="section-header">
              <div class="section-title">
                <BellOutlined style="color: #6366f1" />
                <span>待办任务与通知中心</span>
                <a-badge :count="todoList.length" :overflow-count="99" style="margin-left: 8px;" />
              </div>
              <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">
                <a-radio-group v-model:value="todoFilter" size="small" button-style="solid">
                  <a-radio-button value="ALL">全部 ({{ todoList.length }})</a-radio-button>
                  <a-radio-button value="PROMOTION">晋级确认</a-radio-button>
                  <a-radio-button value="ALGO">竞赛题/算法</a-radio-button>
                  <a-radio-button value="CERT">荣誉上传</a-radio-button>
                  <a-radio-button value="OTHER">审批与绑定</a-radio-button>
                </a-radio-group>
                <a-button type="link" size="small" @click="loadTodoList">刷新待办</a-button>
              </div>
            </div>

            <div v-if="filteredTodoList.length === 0" class="empty-box">
              <a-empty description="当前暂无待处理事项，所有竞赛题、晋级确认与团队任务均处于最新状态！" />
            </div>

            <div v-else class="todo-items-list">
              <div
                v-for="item in filteredTodoList"
                :key="item.id"
                class="todo-item-card glass-card"
                :class="'priority-' + item.priority"
              >
                <div class="todo-main">
                  <div class="todo-title-row">
                    <span class="todo-tag" :class="'tag-' + item.type">{{ item.tag || '通知' }}</span>
                    <h4 class="todo-title">{{ item.title }}</h4>
                    <span v-if="item.priority === 'urgent'" class="urgent-indicator">🔥 紧急处理</span>
                  </div>
                  <p class="todo-desc">{{ item.description }}</p>
                  <div class="todo-meta">
                    <span>⏱️ 时间：{{ item.createTime }}</span>
                    <span v-if="item.projectName" style="margin-left: 12px;">🚀 涉及项目：{{ item.projectName }}</span>
                  </div>
                </div>

                <div class="todo-action">
                  <!-- 1. 晋级确认 -->
                  <a-button
                    v-if="item.actionType === 'CONFIRM_PROMOTION'"
                    type="primary"
                    shape="round"
                    style="background: linear-gradient(135deg, #10b981, #059669); border: none; font-weight: 700;"
                    :loading="confirmingPromotionId === item.id"
                    @click="handleConfirmPromotion(item)"
                  >
                    🎉 确认升级至{{ item.targetLevel }}
                  </a-button>

                  <!-- 2. 赛事绑定 -->
                  <a-button
                    v-else-if="item.actionType === 'BIND_COMP'"
                    type="primary"
                    shape="round"
                    style="background: linear-gradient(135deg, #6366f1, #4f46e5); border: none;"
                    @click="openBindCompModalById(item.projectId)"
                  >
                    🎯 选择参赛竞赛
                  </a-button>

                  <!-- 3. 前往处理 / 跳转 -->
                  <a-button
                    v-else
                    type="primary"
                    shape="round"
                    @click="handleTodoJump(item)"
                  >
                    {{ item.actionLabel || '前往处理' }} ➔
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- 组长视角 -->
        <a-tab-pane key="leader" tab="👑 组长指挥空间">
          <div v-if="!userStore.isLeader && ledProjects.length === 0" class="not-leader-banner">
            <CrownOutlined style="font-size: 36px; color: #f59e0b; margin-bottom: 12px" />
            <h3>您目前尚未担任任何项目组组长</h3>
            <p>学生创建竞赛项目组后，将直接晋升为组长，享有团队全权管理、组员调配、私密设置与AI智能诊断特权！</p>
            <a-button type="primary" class="gradient-btn" @click="openCreateProjectModal">
              立即创建团队并晋升组长
            </a-button>
          </div>

          <div v-else>
            <!-- 组长管理的各团队大卡片 -->
            <div v-for="proj in ledProjects" :key="proj.id" class="leader-project-box">
              <div class="box-header">
                <div class="proj-main-info">
                  <div class="proj-title-row">
                    <CrownOutlined style="color: #f59e0b; font-size: 20px" />
                    <h2>{{ proj.projectName }}</h2>
                    <span class="comp-tag" :style="proj.isUnassigned || proj.competitionTitle === '暂未参与' ? 'background: #e0f2fe; color: #0284c7; border: 1px solid #bae6fd;' : ''">
                      {{ proj.isUnassigned || proj.competitionTitle === '暂未参与' ? '🏷️ 暂未参与具体赛事' : (proj.competitionTitle || '竞赛项目') }}
                    </span>
                    <a-button
                      v-if="proj.isUnassigned || proj.competitionTitle === '暂未参与' || proj.competitionId === 'UNASSIGNED'"
                      type="primary"
                      size="small"
                      shape="round"
                      style="background: linear-gradient(135deg, #6366f1, #4f46e5); border: none; margin-left: 8px;"
                      @click="openBindCompModal(proj)"
                    >
                      🎯 选择/修改参赛竞赛
                    </a-button>
                  </div>
                  <p class="proj-desc-text">{{ proj.projectDesc || '暂无描述' }}</p>
                </div>

                <!-- 组长专属权限：私密团队开关 -->
                <div class="privacy-control">
                  <div class="control-label">
                    <span>团队私密状态：</span>
                    <a-tooltip title="设置为私密后，外界用户与搜索将无法查看到该项目组，仅团队成员和指导导师可访问。">
                      <QuestionCircleOutlined style="color: #94a3b8" />
                    </a-tooltip>
                  </div>
                  <a-switch
                    :checked="proj.isPrivate === 'YES'"
                    checked-children="私密"
                    un-checked-children="公开"
                    @change="(checked) => handleTogglePrivacy(proj, checked)"
                  />
                </div>
              </div>

              <!-- 组长核心工具集 -->
              <div class="leader-toolbox">
                <a-button
                  type="primary"
                  style="background: #6366f1; border-color: #6366f1"
                  @click="runAiDiagnosis(proj)"
                >
                  <template #icon><RobotOutlined /></template>
                  🤖 AI团队战力诊断
                </a-button>

                <a-button
                  type="primary"
                  style="background: #0ea5e9; border-color: #0ea5e9"
                  @click="openMemberScoreModal(proj)"
                >
                  <template #icon><LockOutlined /></template>
                  🔒 组员私密绩效打分
                </a-button>

                <a-button @click="openInviteModal(proj)">
                  <template #icon><UserAddOutlined /></template>
                  邀请成员加入
                </a-button>

                <a-button @click="openRecruitModal(proj)">
                  <template #icon><NotificationOutlined /></template>
                  发布招聘引才
                </a-button>

                <a-button type="link" @click="goToProject(proj.id)">
                  进入项目主页 ➔
                </a-button>

                <a-button type="primary" size="middle" shape="round" style="background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%); border: none;" @click="goToProjectChat(proj.id)">
                  💬 小组讨论
                  <a-badge v-if="projectUnreadMap[proj.id] > 0" :count="projectUnreadMap[proj.id]" style="margin-left: 6px;" />
                </a-button>
              </div>

              <!-- 团队成员管理列表 -->
              <div class="member-list-section">
                <div class="sub-title">👥 项目组成员花名册（{{ proj.members?.length || 1 }} 人）</div>
                <a-table
                  :data-source="proj.members || []"
                  :columns="memberColumns"
                  size="small"
                  :pagination="false"
                  row-key="id"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'role'">
                      <a-tag :color="record.isLeader ? 'gold' : 'blue'">
                        {{ record.isLeader ? '👑 组长' : '组员' }}
                      </a-tag>
                    </template>
                    <template v-else-if="column.key === 'duty'">
                      <span>{{ record.duty || '暂未分配职责' }}</span>
                    </template>
                    <template v-else-if="column.key === 'action'">
                      <a-space v-if="!record.isLeader">
                        <a-button type="link" size="small" @click="openDutyEdit(proj, record)">
                          变更分工
                        </a-button>
                        <a-popconfirm
                          title="确定将该组员移出本团队吗？"
                          @confirm="handleRemoveMember(proj.id, record.studentId)"
                        >
                          <a-button type="link" danger size="small">移除</a-button>
                        </a-popconfirm>
                      </a-space>
                      <span v-else style="color: #94a3b8; font-size: 12px">负责人</span>
                    </template>
                  </template>
                </a-table>
              </div>

              <!-- 组长审核招募申请与接受组员 -->
              <div class="applicants-section" style="margin-top: 20px; padding: 16px; background: #f8fafc; border-radius: 12px; border: 1px dashed #cbd5e1;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                  <div class="sub-title" style="margin-bottom: 0; color: #1e293b; font-weight: 700;">
                    📥 招募引才审核与组员录取（待审批：{{ proj.pendingApplicants?.length || 0 }} 人）
                  </div>
                  <a-button type="link" size="small" @click="loadApplicantsForProject(proj)">刷新申请名单</a-button>
                </div>
                <div v-if="!proj.pendingApplicants || proj.pendingApplicants.length === 0" style="color: #94a3b8; font-size: 13px; padding: 8px 0;">
                  暂无待审批的入组申请。组长可点击上方【发布招聘引才】前往团队大厅广播需求，或点击【邀请成员加入】！
                </div>
                <a-table
                  v-else
                  :data-source="proj.pendingApplicants"
                  :columns="leaderApplicantColumns"
                  size="small"
                  :pagination="false"
                  row-key="id"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'student'">
                      <div style="display: flex; align-items: center; gap: 8px;">
                        <a-avatar size="small" style="background-color: #3b82f6;">
                          {{ (record.studentName || 'S').substring(0, 1) }}
                        </a-avatar>
                        <div>
                          <div style="font-weight: 600; color: #0f172a;">{{ record.studentName }}</div>
                          <div style="font-size: 11px; color: #64748b;">{{ record.major || '学生' }} · {{ record.studentAccount }}</div>
                        </div>
                      </div>
                    </template>
                    <template v-else-if="column.key === 'applyRole'">
                      <a-tag :color="record.applyRole === 'LEADER' ? 'gold' : 'blue'">
                        {{ record.applyRole === 'LEADER' ? '👑 竞聘组长' : '👥 申请组员' }}
                      </a-tag>
                    </template>
                    <template v-else-if="column.key === 'message'">
                      <span style="font-size: 12px; color: #475569;">{{ record.message || '申请加入团队协作' }}</span>
                    </template>
                    <template v-else-if="column.key === 'action'">
                      <a-space>
                        <a-popconfirm
                          :title="`确认接受并录用【${record.studentName}】为本团队组员？`"
                          ok-text="确认录用"
                          cancel-text="取消"
                          @confirm="handleLeaderAcceptApplicant(proj, record)"
                        >
                          <a-button type="primary" size="small" style="background: #10b981; border-color: #10b981;">
                            ✅ 接受组员
                          </a-button>
                        </a-popconfirm>
                        <a-popconfirm
                          title="确认婉拒该申请？"
                          ok-text="婉拒"
                          cancel-text="取消"
                          @confirm="handleLeaderRejectApplicant(proj, record)"
                        >
                          <a-button danger size="small">婉拒</a-button>
                        </a-popconfirm>
                      </a-space>
                    </template>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- 组长与教师大群（与原来的组长指挥中心同级） -->
        <a-tab-pane
          v-if="userStore.isLeader || userStore.isTeacher || (ledProjects && ledProjects.length > 0)"
          key="leaderTeacherChat"
        >
          <template #tab>
            <span>
              👑 组长与教师领航群
              <a-badge v-if="leaderTeacherUnreadCount > 0" :count="leaderTeacherUnreadCount" style="margin-left: 6px;" />
            </span>
          </template>
          <div style="padding: 12px 0;">
            <ChatRoom
              chatType="LEADER_TEACHER"
              chatTargetId="GLOBAL_LEADER_TEACHER"
              title="👑 师生领航 · 全校竞赛组长与教师大群"
              subtitle="全校各学科竞赛项目组长与指导教师专属交流大群。成为组长自动进群，名下无组长项目自动退群。"
              @read-marked="handleLeaderTeacherRead"
            />
          </div>
        </a-tab-pane>

        <!-- 老师视角 -->
        <a-tab-pane v-if="userStore.isTeacher" key="teacher" tab="👨‍🏫 导师管理工作台">
          <div class="teacher-dashboard">
            <!-- 教学与竞赛管理快捷入口 -->
            <div class="teacher-quick-grid">
              <div class="quick-stat-card" @click="openPublishCompModal">
                <div class="stat-icon comp-color"><TrophyOutlined /></div>
                <div class="stat-info">
                  <div class="stat-title">发布新竞赛</div>
                  <div class="stat-desc">发起校级/省级/国家级赛事与规格</div>
                </div>
              </div>

              <div class="quick-stat-card" @click="openPublishAlgoModal">
                <div class="stat-icon algo-color"><CodeOutlined /></div>
                <div class="stat-info">
                  <div class="stat-title">发布算法训练题</div>
                  <div class="stat-desc">面向全员或团队下达算法演练作业</div>
                </div>
              </div>

              <div class="quick-stat-card" @click="router.push('/ranking')">
                <div class="stat-icon rank-color"><BarChartOutlined /></div>
                <div class="stat-info">
                  <div class="stat-title">综合榜单公示发布</div>
                  <div class="stat-desc">导师评估打分与全网风云榜发布</div>
                </div>
              </div>

              <div class="quick-stat-card" @click="router.push('/forum?tab=audit')">
                <div class="stat-icon forum-color"><AuditOutlined /></div>
                <div class="stat-info">
                  <div class="stat-title">社区论坛内容治理</div>
                  <div class="stat-desc">AI预审拦截帖直接过审与置顶权限</div>
                </div>
              </div>
            </div>

            <!-- 教师专属核心功能：管理所有学生 -->
            <div class="section-card" style="margin-top: 24px">
              <div class="section-header">
                <div class="section-title">
                  <IdcardOutlined style="color: #4f46e5" />
                  <span>全体注册学生档案中心（共计 {{ allStudents.length }} 名）</span>
                </div>
                <div class="search-input-box">
                  <a-input-search
                    v-model:value="studentKeyword"
                    placeholder="输入姓名、学号、专业或技能标签搜索..."
                    style="width: 320px"
                    @search="loadAllStudents"
                    allow-clear
                  />
                </div>
              </div>

              <a-table
                :data-source="allStudents"
                :columns="studentColumns"
                :pagination="{ pageSize: 8 }"
                row-key="id"
                :loading="studentsLoading"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'role'">
                    <a-tag v-if="record.roleInfo?.isLeader" color="orange">
                      👑 组长 ({{ record.roleInfo?.ledProjectsCount }}组)
                    </a-tag>
                    <a-tag v-else color="blue">学生组员</a-tag>
                  </template>

                  <template v-else-if="column.key === 'major'">
                    <span>{{ record.profile?.major || '计算机科学与技术' }}</span>
                  </template>

                  <template v-else-if="column.key === 'skills'">
                    <div class="tag-cloud">
                      <a-tag
                        v-for="tag in (record.profile?.skillTags || 'Java,算法,Vue3').split(',')"
                        :key="tag"
                        color="cyan"
                      >
                        {{ tag }}
                      </a-tag>
                    </div>
                  </template>

                  <template v-else-if="column.key === 'joinedCount'">
                    <span>{{ record.roleInfo?.joinedProjectsCount || 0 }} 个团队</span>
                  </template>

                  <template v-else-if="column.key === 'action'">
                    <a-button type="link" size="small" @click="handleContactStudent(record)">
                      查看档案
                    </a-button>
                  </template>
                </template>
              </a-table>
            </div>

            <!-- 教师专属：学生竞赛荣誉证书审核中心 -->
            <div class="section-card" style="margin-top: 24px">
              <div class="section-header">
                <div class="section-title">
                  <SafetyCertificateOutlined style="color: #10b981" />
                  <span>学生竞赛荣誉证书审核认证中心（共计 {{ certificates.length }} 份）</span>
                </div>
                <a-button type="link" @click="loadCertificates">刷新证书</a-button>
              </div>

              <a-table
                :data-source="certificates"
                :columns="teacherCertColumns"
                :pagination="{ pageSize: 5 }"
                row-key="id"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'certificateUrl'">
                    <a v-if="record.certificateUrl" :href="record.certificateUrl" target="_blank" style="color: #4f46e5; text-decoration: underline;">
                      查看原件 ↗
                    </a>
                    <span v-else style="color: #94a3b8;">无附件</span>
                  </template>
                  <template v-else-if="column.key === 'verifyStatus'">
                    <a-tag :color="record.verifyStatus === 'VERIFIED' ? 'success' : (record.verifyStatus === 'REJECTED' ? 'error' : 'warning')">
                      {{ record.verifyStatus === 'VERIFIED' ? '✅ 已官方认证' : (record.verifyStatus === 'REJECTED' ? '❌ 已驳回' : '⏳ 待审核') }}
                    </a-tag>
                  </template>
                  <template v-else-if="column.key === 'action'">
                    <a-button
                      v-if="record.verifyStatus === 'PENDING'"
                      type="primary"
                      size="small"
                      shape="round"
                      @click="openTeacherVerifyModal(record)"
                    >
                      审核认证
                    </a-button>
                    <span v-else style="color: #94a3b8; font-size: 12px">{{ record.verifyRemark || '已处理' }}</span>
                  </template>
                </template>
              </a-table>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 弹窗：创建竞赛项目组（晋升组长） -->
    <a-modal
      v-model:open="createModalVisible"
      title="🚀 创建竞赛项目组并晋升组长"
      @ok="handleCreateProject"
      :confirmLoading="creatingProject"
      width="560px"
    >
      <a-form layout="vertical" :model="createForm">
        <a-form-item label="参与竞赛" required>
          <a-select
            v-model:value="createForm.competitionId"
            placeholder="请选择目标参赛项目或选择暂未参与"
          >
            <a-select-option value="UNASSIGNED">
              🏷️ 暂未参与（创建自由组队项目，后续可自由修改选择具体比赛）
            </a-select-option>
            <a-select-option
              v-for="c in competitions"
              :key="c.id"
              :value="c.id"
              :disabled="isCompEnded(c) || isSignupClosed(c)"
            >
              {{ c.title }} {{ isCompEnded(c) ? '（已结束）' : (isSignupClosed(c) ? '（报名已截止）' : '（🔥报名中）') }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="项目组 / 战队名称" required>
          <a-input
            v-model:value="createForm.projectName"
            placeholder="例如：智绘未来高能研发组"
          />
        </a-form-item>
        <a-form-item label="项目简介与战队愿景">
          <a-textarea
            v-model:value="createForm.projectDesc"
            placeholder="简述团队目标与开发路线..."
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="是否初始设为私密团队">
          <a-radio-group v-model:value="createForm.isPrivate">
            <a-radio value="NO">公开（任何人可在竞赛广场查看与申请加入）</a-radio>
            <a-radio value="YES">私密（隐蔽战队，仅本组组员和导师可见）</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：编辑个人资料 -->
    <a-modal
      v-model:open="editProfileModalVisible"
      title="📝 编辑个人基本信息"
      @ok="handleUpdateProfile"
      width="500px"
    >
      <a-form layout="vertical" :model="profileForm">
        <a-form-item label="姓名 / 昵称">
          <a-input v-model:value="profileForm.name" />
        </a-form-item>
        <a-form-item label="联系邮箱">
          <a-input v-model:value="profileForm.email" />
        </a-form-item>
        <a-form-item label="手机号码">
          <a-input v-model:value="profileForm.phone" />
        </a-form-item>
        <a-form-item label="所属专业">
          <a-input v-model:value="profileForm.major" placeholder="例如：软件工程" />
        </a-form-item>
        <a-form-item label="核心技能标签（逗号隔开）">
          <a-input v-model:value="profileForm.skillTags" placeholder="例如：Vue3, Python, 算法设计" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：邀请学生加入项目组 -->
    <a-modal
      v-model:open="inviteModalVisible"
      title="➕ 邀请学生加入战队"
      @ok="handleSendInvite"
      width="500px"
    >
      <a-form layout="vertical">
        <a-form-item label="选择目标学生" required>
          <a-select
            v-model:value="selectedStudentId"
            show-search
            placeholder="搜索并选择学生"
            :filter-option="filterStudentOption"
          >
            <a-select-option v-for="s in candidateStudents" :key="s.id" :value="s.id">
              {{ s.name }} ({{ s.account }}) - {{ s.profile?.major || '学生' }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：AI团队战力评估结果 -->
    <a-modal
      v-model:open="aiModalVisible"
      title="🤖 AI团队全景诊断报告"
      :footer="null"
      width="650px"
    >
      <div v-if="aiReport" class="ai-report-box">
        <div class="ai-score-banner">
          <div class="ai-score-number">{{ aiReport.overallScore || 88 }}</div>
          <div class="ai-score-label">AI综合战力总分</div>
        </div>
        <div class="ai-metrics-row">
          <div class="metric-item">
            <span class="m-val">{{ aiReport.algorithmCompletionRate != null ? aiReport.algorithmCompletionRate + '%' : '92%' }}</span>
            <span class="m-lab">算法训练完成度 (50%)</span>
          </div>
          <div class="metric-item">
            <span class="m-val">{{ aiReport.competitionParticipationScore != null ? aiReport.competitionParticipationScore + '分' : '85分' }}</span>
            <span class="m-lab">竞赛参与积极度 (50%)</span>
          </div>
        </div>
        <div class="ai-advice">
          <h4>💡 AI诊断调优建议：</h4>
          <p>{{ aiReport.aiAdvice || '团队算法作业提交积极，配合默契，建议进一步攻坚动态规划与高级图论题目！' }}</p>
        </div>
      </div>
    </a-modal>

    <!-- 弹窗：组员私密评分 -->
    <a-modal
      v-model:open="memberScoreModalVisible"
      title="🔒 组员私密绩效评分（仅导师与该组长可见）"
      @ok="handleSaveMemberScore"
      width="550px"
    >
      <a-form layout="vertical" :model="memberScoreForm">
        <a-alert
          type="warning"
          show-icon
          message="保密协议"
          description="本评分具有严格保密权限，组员本人不可见，仅项目组长与指导导师在评估榜单时可见。"
          style="margin-bottom: 16px"
        />
        <a-form-item label="选择被评估组员" required>
          <a-select v-model:value="memberScoreForm.studentId" placeholder="选择组员">
            <a-select-option
              v-for="m in currentScoringProject?.members"
              :key="m.studentId"
              :value="m.studentId"
            >
              {{ m.studentName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="绩效打分 (0 - 100分)" required>
          <a-slider v-model:value="memberScoreForm.score" :min="0" :max="100" />
          <a-input-number v-model:value="memberScoreForm.score" :min="0" :max="100" style="width: 100%" />
        </a-form-item>
        <a-form-item label="评价详情与贡献评估">
          <a-textarea v-model:value="memberScoreForm.evaluationComments" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：上传竞赛荣誉证书 -->
    <a-modal
      v-model:open="certModalVisible"
      title="📜 上传竞赛获奖证书"
      @ok="handleUploadCert"
      width="500px"
    >
      <a-form layout="vertical" :model="certForm">
        <a-form-item label="证书名称 / 奖项" required>
          <a-input v-model:value="certForm.certificateName" placeholder="例如：全国大学生算法设计一等奖" />
        </a-form-item>
        <a-form-item label="所属竞赛">
          <a-select v-model:value="certForm.competitionId" placeholder="选择关联的竞赛">
            <a-select-option v-for="c in competitions" :key="c.id" :value="c.id">
              {{ c.title }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="证书文件或图片链接" required>
          <div style="display: flex; gap: 8px;">
            <a-input v-model:value="certForm.certificateUrl" placeholder="输入图片链接或点击右侧本地上传" />
            <a-upload
              :show-upload-list="false"
              :customRequest="handleCertFileUpload"
              accept="image/*,.pdf"
            >
              <a-button :loading="certUploading">
                <template #icon><UploadOutlined /></template>
                本地上传
              </a-button>
            </a-upload>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：修改组员职责 -->
    <a-modal
      v-model:open="dutyModalVisible"
      title="🛠️ 变更组员分工职责"
      @ok="handleUpdateDuty"
      width="400px"
    >
      <a-form layout="vertical">
        <a-form-item label="组员姓名">
          <a-input :value="currentDutyMember?.studentName" disabled />
        </a-form-item>
        <a-form-item label="分工职责 / 承担模块" required>
          <a-input v-model:value="newDuty" placeholder="例如：核心算法编写、前端交互、技术答辩" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：教师审核竞赛证书 -->
    <a-modal
      v-model:open="teacherVerifyModalVisible"
      title="👨‍🏫 指导教师审核认证竞赛证书"
      ok-text="提交审核结论"
      cancel-text="取消"
      @ok="handleTeacherVerifyCert"
      width="500px"
    >
      <div v-if="selectedTeacherCert" style="margin-bottom: 16px; padding: 12px; background: #f8fafc; border-radius: 8px;">
        <div>申报学生：<strong>{{ selectedTeacherCert.studentName }}</strong></div>
        <div style="margin-top: 4px;">证书荣誉：<strong>{{ selectedTeacherCert.certificateName }}</strong> ({{ selectedTeacherCert.awardLevel || '奖项' }})</div>
        <div v-if="selectedTeacherCert.certificateUrl" style="margin-top: 6px;">
          <a :href="selectedTeacherCert.certificateUrl" target="_blank" style="color: #4f46e5;">点击查验原件 ↗</a>
        </div>
      </div>
      <a-form layout="vertical">
        <a-form-item label="审核结论" required>
          <a-radio-group v-model:value="teacherVerifyForm.status" button-style="solid">
            <a-radio-button value="VERIFIED">✅ 官方认证通过</a-radio-button>
            <a-radio-button value="REJECTED">❌ 驳回存疑</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="导师批注 / 评语">
          <a-textarea v-model:value="teacherVerifyForm.remark" :rows="3" placeholder="请输入指导教师评语或意见" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 弹窗：为暂未参与竞赛的团队选择/修改参赛具体竞赛 -->
    <a-modal
      v-model:open="bindCompModalVisible"
      title="🎯 选择 / 修改团队参赛竞赛"
      ok-text="确认报名并绑定竞赛"
      cancel-text="取消"
      :confirm-loading="bindingComp"
      @ok="handleBindCompetition"
      width="540px"
    >
      <div v-if="currentBindProject" style="margin-bottom: 16px; padding: 12px; background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px;">
        <div>当前团队：<strong style="color: #166534;">{{ currentBindProject.projectName }}</strong></div>
        <div style="font-size: 13px; color: #15803d; margin-top: 4px;">
          提示：当前团队处于【暂未参与具体赛事】状态。绑定后，团队将正式入驻目标比赛，并参与该比赛下的赛题、评审与排行榜！
        </div>
      </div>
      <a-form layout="vertical">
        <a-form-item label="目标参赛竞赛" required>
          <a-select
            v-model:value="bindCompetitionId"
            placeholder="请选择想要参加的具体竞赛"
            show-search
            option-filter-prop="label"
          >
            <a-select-option
              v-for="c in activeCompetitions"
              :key="c.id"
              :value="c.id"
              :label="c.title"
            >
              {{ c.title }} ({{ c.level || '赛事' }} · 截止: {{ c.signupEndTime ? c.signupEndTime.substring(0, 10) : '未设' }})
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  EditOutlined,
  PlusOutlined,
  BellOutlined,
  TeamOutlined,
  SafetyCertificateOutlined,
  CrownOutlined,
  RobotOutlined,
  LockOutlined,
  UserAddOutlined,
  NotificationOutlined,
  UploadOutlined,
  FilePdfOutlined,
  TrophyOutlined,
  CodeOutlined,
  BarChartOutlined,
  AuditOutlined,
  IdcardOutlined,
  QuestionCircleOutlined,
  AlertOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/store/user'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import ChatRoom from '@/components/ChatRoom.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 判断竞赛是否已全流程结束
const isCompEnded = (comp) => {
  if (!comp) return false
  if (comp.isEnded) return true
  if (comp.status === 'FINISHED') return true
  if (comp.competitionEndTime && dayjs().isAfter(dayjs(comp.competitionEndTime))) return true
  return false
}

// 判断竞赛报名是否已截止
const isSignupClosed = (comp) => {
  if (!comp) return false
  if (isCompEnded(comp)) return true
  if (comp.isSignupClosed) return true
  if (comp.status === 'ONGOING' || comp.status === 'REVIEWING' || comp.status === 'FINISHED') return true
  if (comp.signupEndTime && dayjs().isAfter(dayjs(comp.signupEndTime))) return true
  return false
}

const activePerspective = ref(userStore.isTeacher ? 'teacher' : (userStore.isLeader ? 'leader' : 'student'))

// 组长与教师大群未读数 & 项目未读映射
const leaderTeacherUnreadCount = ref(0)
const projectUnreadMap = ref({})
let profileUnreadTimer = null

const loadLeaderTeacherUnread = async () => {
  try {
    if (userStore.isLeader || userStore.isTeacher || ledProjects.value.length > 0) {
      const res = await portalApi.getUnreadCount('LEADER_TEACHER', 'GLOBAL_LEADER_TEACHER')
      leaderTeacherUnreadCount.value = typeof res === 'number' ? res : (res?.data || 0)
    }
  } catch (e) {}
}

const loadProjectUnreads = async () => {
  try {
    const allProjs = [...joinedProjects.value, ...ledProjects.value]
    const map = {}
    for (const p of allProjs) {
      try {
        const res = await portalApi.getUnreadCount('PROJECT', p.id)
        map[p.id] = typeof res === 'number' ? res : (res?.data || 0)
      } catch (err) {}
    }
    projectUnreadMap.value = map
  } catch (e) {}
}

const handleLeaderTeacherRead = () => {
  leaderTeacherUnreadCount.value = 0
}

watch(activePerspective, (val) => {
  if (val === 'leaderTeacherChat') {
    leaderTeacherUnreadCount.value = 0
  }
})

const joinedProjects = ref([])
const ledProjects = ref([])
const invitations = ref([])
const certificates = ref([])
const competitions = ref([])
const myUrges = ref([])
const certUploading = ref(false)

// 待办任务中心
const todoList = ref([])
const todoFilter = ref('ALL')
const confirmingPromotionId = ref(null)

const filteredTodoList = computed(() => {
  if (todoFilter.value === 'ALL') return todoList.value
  return todoList.value.filter(item => item.category === todoFilter.value)
})

const loadTodoList = async () => {
  try {
    const res = await portalApi.getMyTodoList()
    todoList.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error('加载待办失败:', e)
  }
}

// 绑定/修改竞赛
const bindCompModalVisible = ref(false)
const currentBindProject = ref(null)
const bindCompetitionId = ref(undefined)
const bindingComp = ref(false)

const activeCompetitions = computed(() => {
  return competitions.value.filter(c => !isCompEnded(c) && !isSignupClosed(c))
})

const openBindCompModal = (proj) => {
  currentBindProject.value = proj
  bindCompetitionId.value = undefined
  bindCompModalVisible.value = true
}

const openBindCompModalById = (projectId) => {
  const p = [...ledProjects.value, ...joinedProjects.value].find(item => item.id === projectId)
  if (p) {
    openBindCompModal(p)
  } else {
    currentBindProject.value = { id: projectId, projectName: '待选项目' }
    bindCompetitionId.value = undefined
    bindCompModalVisible.value = true
  }
}

const handleBindCompetition = async () => {
  if (!bindCompetitionId.value) {
    message.warning('请选择要绑定的目标竞赛')
    return
  }
  bindingComp.value = true
  try {
    await portalApi.bindProjectCompetition({
      projectId: currentBindProject.value.id,
      competitionId: bindCompetitionId.value
    })
    message.success('🎉 团队已成功绑定到新竞赛！')
    bindCompModalVisible.value = false
    loadMyProjects()
    loadTodoList()
  } catch (e) {
    message.error(e.message || '绑定竞赛失败')
  } finally {
    bindingComp.value = false
  }
}

const handleConfirmPromotion = async (item) => {
  confirmingPromotionId.value = item.id
  try {
    await portalApi.confirmPromotion({
      projectId: item.projectId,
      targetCompetitionId: item.targetCompetitionId
    })
    message.success(`🎉 恭喜！团队已成功确认晋级并转入高一级【${item.targetCompetitionTitle || item.targetLevel}】！`)
    loadTodoList()
    loadMyProjects()
  } catch (e) {
    message.error(e.message || '确认晋级失败')
  } finally {
    confirmingPromotionId.value = null
  }
}

const handleTodoJump = (item) => {
  if (item.actionType === 'JUMP') {
    if (item.actionTarget) {
      router.push(item.actionTarget)
    }
  } else if (item.actionType === 'MODAL') {
    if (item.actionTarget === 'uploadCertModal') {
      if (item.projectId) {
        router.push(`/project/${item.projectId}?tab=certificates&openUpload=1`)
      }
    }
  }
}

// 老师管理学生
const allStudents = ref([])
const studentsLoading = ref(false)
const studentKeyword = ref('')

// 弹窗状态
const createModalVisible = ref(false)
const creatingProject = ref(false)
const createForm = ref({
  competitionId: undefined,
  projectName: '',
  projectDesc: '',
  isPrivate: 'NO'
})

const editProfileModalVisible = ref(false)
const profileForm = ref({
  name: '',
  email: '',
  phone: '',
  major: '',
  skillTags: ''
})

const inviteModalVisible = ref(false)
const candidateStudents = ref([])
const selectedStudentId = ref(undefined)
const currentInviteProject = ref(null)

const aiModalVisible = ref(false)
const aiReport = ref(null)

const memberScoreModalVisible = ref(false)
const currentScoringProject = ref(null)
const memberScoreForm = ref({
  projectId: undefined,
  studentId: undefined,
  score: 90,
  evaluationComments: ''
})

const certModalVisible = ref(false)
const certForm = ref({
  certificateName: '',
  competitionId: undefined,
  certificateUrl: ''
})

const dutyModalVisible = ref(false)
const currentDutyProject = ref(null)
const currentDutyMember = ref(null)
const newDuty = ref('')

// 列配置
const memberColumns = [
  { title: '组员', dataIndex: 'studentName', key: 'studentName' },
  { title: '角色', key: 'role', width: 100 },
  { title: '职责分工', key: 'duty' },
  { title: '操作', key: 'action', width: 140, align: 'center' }
]

const leaderApplicantColumns = [
  { title: '申请学生', key: 'student', width: 160 },
  { title: '意向岗位', key: 'applyRole', width: 110 },
  { title: '自荐留言 / 技能简介', key: 'message' },
  { title: '审核操作', key: 'action', width: 160, align: 'center' }
]

const studentColumns = [
  { title: '学生姓名', dataIndex: 'name', key: 'name', width: 120 },
  { title: '账号 / 学号', dataIndex: 'account', key: 'account', width: 140 },
  { title: '角色身份', key: 'role', width: 120 },
  { title: '所属专业', key: 'major', width: 160 },
  { title: '技能标签', key: 'skills' },
  { title: '参与团队', key: 'joinedCount', width: 110, align: 'center' },
  { title: '操作', key: 'action', width: 100, align: 'center' }
]

const teacherCertColumns = [
  { title: '申报学生', dataIndex: 'studentName', key: 'studentName', width: 120 },
  { title: '荣誉证书名称', dataIndex: 'certificateName', key: 'certificateName' },
  { title: '获奖级别', dataIndex: 'awardLevel', key: 'awardLevel', width: 120 },
  { title: '证书凭证', key: 'certificateUrl', width: 120 },
  { title: '审核状态', key: 'verifyStatus', width: 120 },
  { title: '导师审核操作', key: 'action', width: 120, align: 'center' }
]

const teacherVerifyModalVisible = ref(false)
const selectedTeacherCert = ref(null)
const teacherVerifyForm = ref({
  status: 'VERIFIED',
  remark: ''
})

// 证书文件上传
const handleCertFileUpload = async ({ file }) => {
  certUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const url = typeof res === 'string' ? res : (res?.data || res)
    certForm.value.certificateUrl = url
    message.success('证书文件上传成功！')
  } catch (e) {
    message.error('文件上传失败')
  } finally {
    certUploading.value = false
  }
}

// 组长加载该项目待审申请
const loadApplicantsForProject = async (project) => {
  try {
    const res = await portalApi.getProjectApplicants(project.id)
    const list = Array.isArray(res) ? res : (res?.data || [])
    project.pendingApplicants = list.filter(a => a.status === 'PENDING')
  } catch (e) {
    console.error(e)
  }
}

// 组长接受录取组员
const handleLeaderAcceptApplicant = async (project, applicant) => {
  try {
    await portalApi.handleProjectApplicant({
      flowId: applicant.id,
      accept: true,
      remark: '组长同意录用'
    })
    message.success(`🎉 已成功录用【${applicant.studentName}】为本团队组员！`)
    loadMyProjects()
  } catch (e) {
    message.error(e.message || '操作失败')
  }
}

// 组长婉拒组员申请
const handleLeaderRejectApplicant = async (project, applicant) => {
  try {
    await portalApi.handleProjectApplicant({
      flowId: applicant.id,
      accept: false,
      remark: '组长婉拒'
    })
    message.success(`已婉拒该申请`)
    loadApplicantsForProject(project)
  } catch (e) {
    message.error(e.message || '操作失败')
  }
}

// 数据加载
const loadMyProjects = async () => {
  try {
    const res = await portalApi.getMyProjects()
    const list = Array.isArray(res) ? res : (res?.data || [])
    if (res && (res.ledProjects || res.joinedProjects)) {
      ledProjects.value = res.ledProjects || []
      joinedProjects.value = res.joinedProjects || []
    } else {
      ledProjects.value = list.filter(p => p.isLeader)
      joinedProjects.value = list.filter(p => !p.isLeader)
    }
    for (const p of ledProjects.value) {
      loadApplicantsForProject(p)
    }
  } catch (e) {
    console.error(e)
  }
}

const loadInvitations = async () => {
  try {
    const res = await portalApi.getMyInvitations()
    invitations.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

const loadCertificates = async () => {
  try {
    const res = await portalApi.getMyCertificates()
    certificates.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

const loadCompetitions = async () => {
  try {
    const res = await portalApi.getCompetitions({ current: 1, size: 50 })
    competitions.value = res.records || res.data?.records || (Array.isArray(res) ? res : [])
  } catch (e) {
    console.error(e)
  }
}

const loadAllStudents = async () => {
  if (!userStore.isTeacher) return
  studentsLoading.value = true
  try {
    const res = await portalApi.getAllStudents(studentKeyword.value)
    allStudents.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    message.error('加载学生列表失败')
  } finally {
    studentsLoading.value = false
  }
}

const loadMyUrges = async () => {
  try {
    const res = await portalApi.getMyUrges()
    myUrges.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

// 邀请处理
const handleInvite = async (inviteId, action) => {
  try {
    await portalApi.handleInvitation({ inviteId, accept: action === 'ACCEPT' })
    message.success(action === 'ACCEPT' ? '已成功加入该项目组！' : '已婉拒邀请')
    loadInvitations()
    loadMyProjects()
    userStore.fetchProfile()
  } catch (e) {
    message.error('处理邀请失败')
  }
}

// 创建项目组晋升组长
const openCreateProjectModal = () => {
  createModalVisible.value = true
}

const handleCreateProject = async () => {
  if (!createForm.value.competitionId || !createForm.value.projectName) {
    message.warning('请选择竞赛并填写项目组名称')
    return
  }
  creatingProject.value = true
  try {
    await portalApi.createProject(createForm.value)
    message.success('🎉 创建成功！您已正式成为该项目组长！')
    createModalVisible.value = false
    loadMyProjects()
    userStore.fetchProfile()
    activePerspective.value = 'leader'
  } catch (e) {
    message.error(e.message || '创建项目失败')
  } finally {
    creatingProject.value = false
  }
}

// 私密团队切换
const handleTogglePrivacy = async (project, checked) => {
  const isPrivate = checked ? 'YES' : 'NO'
  try {
    await portalApi.toggleProjectPrivacy({ projectId: project.id, isPrivate })
    project.isPrivate = isPrivate
    message.success(checked ? '团队已设置为私密（外界不可见）' : '团队已公开')
  } catch (e) {
    message.error('切换私密状态失败')
  }
}

// AI战力诊断
const runAiDiagnosis = async (project) => {
  try {
    const res = await portalApi.evaluateTeamWithAi({ projectId: project.id })
    aiReport.value = res.data
    aiModalVisible.value = true
  } catch (e) {
    message.error('AI诊断失败')
  }
}

// 私密打分
const openMemberScoreModal = (project) => {
  currentScoringProject.value = project
  memberScoreForm.value.projectId = project.id
  if (project.members?.length > 0) {
    memberScoreForm.value.studentId = project.members[0].studentId
  }
  memberScoreModalVisible.value = true
}

const handleSaveMemberScore = async () => {
  if (!memberScoreForm.value.studentId) {
    message.warning('请选择需要评分的组员')
    return
  }
  try {
    await portalApi.saveMemberScore(memberScoreForm.value)
    message.success('组员私密绩效评分已保存（保密存储，仅导师和组长可见）')
    memberScoreModalVisible.value = false
  } catch (e) {
    message.error('保存评分失败')
  }
}

// 邀请新成员
const openInviteModal = async (project) => {
  currentInviteProject.value = project
  inviteModalVisible.value = true
  try {
    const res = await portalApi.getAllStudents()
    candidateStudents.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

const filterStudentOption = (input, option) => {
  const stu = candidateStudents.value.find(s => s.id === option.value)
  if (!stu) return false
  const kw = (input || '').toLowerCase()
  return (stu.name && stu.name.toLowerCase().includes(kw))
    || (stu.account && stu.account.toLowerCase().includes(kw))
    || (stu.profile?.major && stu.profile.major.toLowerCase().includes(kw))
}

const handleSendInvite = async () => {
  if (!selectedStudentId.value) {
    message.warning('请选择要邀请的学生')
    return
  }
  try {
    await portalApi.inviteStudent({
      projectId: currentInviteProject.value.id,
      studentId: selectedStudentId.value,
      targetStudentId: selectedStudentId.value
    })
    message.success('组队邀请已发送给该同学！')
    inviteModalVisible.value = false
  } catch (e) {
    message.error(e.message || '发送邀请失败')
  }
}

// 移除组员
const handleRemoveMember = async (projectId, studentId) => {
  try {
    await portalApi.removeMember({ projectId, studentId })
    message.success('已成功移除该成员')
    loadMyProjects()
  } catch (e) {
    message.error('移除失败')
  }
}

// 修改职责
const openDutyEdit = (project, member) => {
  currentDutyProject.value = project
  currentDutyMember.value = member
  newDuty.value = member.duty || ''
  dutyModalVisible.value = true
}

const handleUpdateDuty = async () => {
  try {
    await portalApi.updateMemberDuty({
      projectId: currentDutyProject.value.id,
      studentId: currentDutyMember.value.studentId,
      duty: newDuty.value,
      dutyRemark: newDuty.value
    })
    message.success('职责变更成功')
    dutyModalVisible.value = false
    loadMyProjects()
  } catch (e) {
    message.error('修改失败')
  }
}

// 上传证书
const openCertModal = () => {
  certModalVisible.value = true
}

const handleUploadCert = async () => {
  if (!certForm.value.certificateName || !certForm.value.certificateUrl) {
    message.warning('请填写证书名称并上传凭证')
    return
  }
  try {
    await portalApi.uploadCertificate({
      ...certForm.value,
      certName: certForm.value.certificateName,
      fileId: certForm.value.certificateUrl
    })
    message.success('证书已提交，等待指导教师审核认证')
    certModalVisible.value = false
    loadCertificates()
  } catch (e) {
    message.error('上传失败')
  }
}

// 教师审核认证证书
const openTeacherVerifyModal = (cert) => {
  selectedTeacherCert.value = cert
  teacherVerifyForm.value.status = 'VERIFIED'
  teacherVerifyForm.value.remark = ''
  teacherVerifyModalVisible.value = true
}

const handleTeacherVerifyCert = async () => {
  try {
    await portalApi.verifyCertificate({
      certId: selectedTeacherCert.value.id,
      status: teacherVerifyForm.value.status,
      remark: teacherVerifyForm.value.remark
    })
    message.success('审核结论提交成功！')
    teacherVerifyModalVisible.value = false
    loadCertificates()
  } catch (e) {
    message.error('审核提交失败')
  }
}

// 编辑资料
const openEditProfileModal = () => {
  profileForm.value = {
    name: userStore.userInfo?.name || '',
    email: userStore.userInfo?.email || '',
    phone: userStore.userInfo?.phone || '',
    major: userStore.userProfile?.major || '',
    skillTags: userStore.userProfile?.skillTags || ''
  }
  editProfileModalVisible.value = true
}

const handleUpdateProfile = async () => {
  try {
    await portalApi.updateMyProfile(profileForm.value)
    message.success('个人资料已更新')
    editProfileModalVisible.value = false
    userStore.fetchProfile()
  } catch (e) {
    message.error('更新失败')
  }
}

const goToProject = (id) => {
  router.push(`/project/${id}`)
}

const goToProjectChat = (id) => {
  router.push(`/project/${id}?tab=chat`)
}

const openPublishCompModal = () => {
  router.push('/competition')
}

const openPublishAlgoModal = () => {
  router.push('/algorithm')
}

const openRecruitModal = (project) => {
  router.push({ path: '/project', query: { openRecruit: '1', projectId: project.id } })
}

const handleContactStudent = (student) => {
  message.info(`学生：${student.name}，邮箱：${student.email || '未提供'}，手机：${student.phone || '未提供'}`)
}

onMounted(() => {
  if (route.query.tab) {
    activePerspective.value = route.query.tab
  }
  loadMyProjects().then(() => {
    loadProjectUnreads()
  })
  loadInvitations()
  loadCertificates()
  loadCompetitions()
  loadMyUrges()
  loadLeaderTeacherUnread()
  loadTodoList()
  if (userStore.isTeacher) {
    loadAllStudents()
  }
  profileUnreadTimer = setInterval(() => {
    if (typeof document !== 'undefined' && document.hidden) return
    loadLeaderTeacherUnread()
    loadProjectUnreads()
    loadTodoList()
  }, 8000)
})

onUnmounted(() => {
  if (profileUnreadTimer) {
    clearInterval(profileUnreadTimer)
    profileUnreadTimer = null
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.profile-header-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  font-size: 28px;
  font-weight: 700;
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.user-name {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
}

.user-sub {
  font-size: 13px;
  color: #64748b;
  display: flex;
  gap: 10px;
  align-items: center;
}

.divider {
  color: #cbd5e1;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.gradient-btn {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
}

/* 邀请横幅 */
.invitations-banner {
  background: #fffbeb;
  border: 1px solid #fef3c7;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.banner-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #b45309;
  margin-bottom: 12px;
}

.invites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.invite-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.inv-team {
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 4px;
}

.inv-meta {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  gap: 12px;
}

.invite-actions {
  display: flex;
  gap: 8px;
}

/* 通用区块卡片 */
.section-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.project-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.project-item-card {
  background: #f8fafc;
  border-radius: 14px;
  padding: 18px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
}

.project-item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  border-color: #cbd5e1;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comp-badge {
  font-size: 12px;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.1);
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 600;
}

.proj-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
}

.proj-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}

.proj-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #94a3b8;
  border-top: 1px dashed #e2e8f0;
  padding-top: 8px;
}

/* 证书展示网格 */
.cert-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.cert-card {
  background: #f8fafc;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.cert-preview {
  height: 120px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cert-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cert-icon.pdf {
  font-size: 40px;
  color: #ef4444;
}

.cert-meta {
  padding: 10px 12px;
}

.cert-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

/* 组长指挥空间样式 */
.not-leader-banner {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 20px;
}

.leader-project-box {
  background: #ffffff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.box-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
  padding-bottom: 18px;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 18px;
}

.proj-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.proj-title-row h2 {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
}

.comp-tag {
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
}

.proj-desc-text {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.privacy-control {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f8fafc;
  padding: 8px 14px;
  border-radius: 10px;
}

.control-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.leader-toolbox {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.member-list-section .sub-title {
  font-size: 15px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 12px;
}

/* 教师专属工作台 */
.teacher-quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 18px;
}

.quick-stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.2s;
}

.quick-stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.comp-color { background: rgba(99, 102, 241, 0.12); color: #4f46e5; }
.algo-color { background: rgba(16, 185, 129, 0.12); color: #10b981; }
.rank-color { background: rgba(245, 158, 11, 0.12); color: #f59e0b; }
.forum-color { background: rgba(239, 68, 68, 0.12); color: #ef4444; }

.stat-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
}

.stat-desc {
  font-size: 12px;
  color: #94a3b8;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

/* AI报告弹窗 */
.ai-report-box {
  text-align: center;
  padding: 10px;
}

.ai-score-banner {
  margin-bottom: 20px;
}

.ai-score-number {
  font-size: 56px;
  font-weight: 900;
  color: #4f46e5;
  line-height: 1;
}

.ai-score-label {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  margin-top: 4px;
}

.ai-metrics-row {
  display: flex;
  justify-content: space-around;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.m-val {
  display: block;
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
}

.m-lab {
  font-size: 12px;
  color: #94a3b8;
}

.ai-advice {
  text-align: left;
  background: rgba(99, 102, 241, 0.05);
  border: 1px solid rgba(99, 102, 241, 0.15);
  border-radius: 12px;
  padding: 16px;
}

.ai-advice h4 {
  color: #4f46e5;
  margin-bottom: 6px;
  font-weight: 700;
}

.ai-advice p {
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
}

/* 组长与教师大群未读提示横幅 */
.leader-chat-unread-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 24px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(254, 243, 199, 0.95) 0%, rgba(253, 230, 138, 0.7) 100%);
  border: 1px solid #fde68a;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 14px rgba(245, 158, 11, 0.12);
}

.leader-chat-unread-banner:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.22);
  border-color: #f59e0b;
}

.banner-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pulse-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #ef4444;
  box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  animation: pulse-ring 1.8s infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 8px rgba(239, 68, 68, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}

.banner-icon {
  font-size: 20px;
}

.banner-text {
  font-size: 14px;
  color: #78350f;
  font-weight: 500;
}

.unread-highlight {
  color: #dc2626;
  font-size: 16px;
  font-weight: 800;
  margin: 0 4px;
}

.go-chat-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  font-weight: 600;
}

/* 待办中心样式 */
.todo-center-card {
  margin-top: 24px;
}

.todo-items-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 12px;
}

.todo-item-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  transition: all 0.25s ease;
  gap: 16px;
  flex-wrap: wrap;
}

.todo-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.08);
}

.todo-item-card.priority-urgent {
  border-left: 5px solid #ef4444;
  background: linear-gradient(to right, #fff5f5, #ffffff);
}

.todo-item-card.priority-high {
  border-left: 5px solid #f59e0b;
  background: linear-gradient(to right, #fffdf5, #ffffff);
}

.todo-item-card.priority-normal {
  border-left: 5px solid #6366f1;
}

.todo-main {
  flex: 1;
  min-width: 280px;
}

.todo-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.todo-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.todo-tag {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 6px;
}

.tag-PROMOTION {
  background: #dcfce7;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.tag-ALGO {
  background: #eff6ff;
  color: #1d4ed8;
  border: 1px solid #bfdbfe;
}

.tag-CERT {
  background: #fef3c7;
  color: #b45309;
  border: 1px solid #fde68a;
}

.tag-APPLY, .tag-OTHER {
  background: #f3e8ff;
  color: #7e22ce;
  border: 1px solid #e9d5ff;
}

.urgent-indicator {
  font-size: 12px;
  font-weight: 700;
  color: #ef4444;
  animation: pulse-ring 1.8s infinite;
}

.todo-desc {
  font-size: 13px;
  color: #475569;
  margin: 0 0 6px 0;
  line-height: 1.5;
}

.todo-meta {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.todo-action {
  display: flex;
  align-items: center;
}
</style>
