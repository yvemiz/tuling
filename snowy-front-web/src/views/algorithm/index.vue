<template>
  <div class="algorithm-page">
    <!-- 顶部横幅 Banner -->
    <div class="page-header glass-card">
      <div class="header-left">
        <div class="header-title-row">
          <h2>💻 算法训练与督训平台</h2>
          <span v-if="userStore.isTeacher" class="role-badge teacher">👨‍🏫 教师特权</span>
          <span v-else-if="myLedTeams.length > 0" class="role-badge leader">👑 担任 {{ myLedTeams.length }} 个小组组长</span>
          <span v-else class="role-badge student">🎓 参赛队员</span>
        </div>
        <p class="header-subtitle">
          题库驱动的算法训练闭环 · 教师全权题库管理与全局监督 · 组长跨比赛多小组督训与一键催交 · 智能判题提交与多团队互联互通
        </p>
      </div>
      <div class="header-right">
        <!-- 教师特权按钮 -->
        <template v-if="userStore.isTeacher">
          <a-button type="primary" size="large" shape="round" @click="openAddProblemModal">
            <template #icon><PlusOutlined /></template>
            录入算法新题
          </a-button>
        </template>
        <!-- 组长快捷提示 -->
        <template v-else-if="myLedTeams.length > 0">
          <a-button type="dashed" size="large" shape="round" @click="activeMainTab = 'LEADER'">
            <template #icon><TeamOutlined /></template>
            进入组长督训 ({{ myLedTeams.length }} 队)
          </a-button>
        </template>
      </div>
    </div>

    <!-- 主导航选项卡 -->
    <div class="tab-nav-wrapper glass-card">
      <a-tabs v-model:activeKey="activeMainTab" size="large" @change="onMainTabChange">
        <a-tab-pane key="TRAIN">
          <template #tab>
            <span><CodeOutlined /> 📚 算法训练与题库大厅</span>
          </template>
        </a-tab-pane>

        <a-tab-pane key="LEADER" v-if="userStore.isLeader || myLedTeams.length > 0">
          <template #tab>
            <a-badge :count="leaderUnsubmittedTotal" :offset="[10, 0]">
              <span><TeamOutlined /> 👑 组长算法督训中心</span>
            </a-badge>
          </template>
        </a-tab-pane>

        <a-tab-pane key="TEACHER" v-if="userStore.isTeacher">
          <template #tab>
            <span><TrophyOutlined /> 👨‍🏫 教师题库管理与各组监控</span>
          </template>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- ========================================================================= -->
    <!-- 模块一：算法训练与题库大厅 (全员可用) -->
    <!-- ========================================================================= -->
    <div v-show="activeMainTab === 'TRAIN'" class="tab-content-section">
      <!-- 检索与分类过滤器 -->
      <div class="filter-card glass-card">
        <div class="filter-top-row">
          <div class="search-box">
            <a-input-search
              v-model:value="searchKeyword"
              placeholder="搜索算法题目名称、核心考点、题意描述..."
              enter-button="检索题目"
              size="large"
              allow-clear
              @search="loadLibraryTasks"
            />
          </div>
          <div class="filter-dropdowns">
            <a-select v-model:value="filterDifficulty" style="width: 140px" size="large" @change="loadLibraryTasks">
              <a-select-option value="ALL">全部难度</a-select-option>
              <a-select-option value="EASY">🟢 EASY 简单</a-select-option>
              <a-select-option value="MEDIUM">🟡 MEDIUM 中等</a-select-option>
              <a-select-option value="HARD">🔴 HARD 困难</a-select-option>
            </a-select>

            <a-select v-model:value="filterMyStatus" style="width: 140px" size="large" @change="filterProblems">
              <a-select-option value="ALL">全部状态</a-select-option>
              <a-select-option value="SUBMITTED">✅ 我已完成</a-select-option>
              <a-select-option value="UNSUBMITTED">⏳ 我待完成</a-select-option>
            </a-select>
          </div>
        </div>

        <!-- 分类标签过滤器 -->
        <div class="tags-filter-row">
          <span class="tag-label">算法分类：</span>
          <div class="tag-pill-list">
            <span
              v-for="tag in popularTags"
              :key="tag"
              :class="['tag-pill', { active: selectedTag === tag }]"
              @click="onSelectTag(tag)"
            >
              {{ tag }}
            </span>
          </div>
        </div>
      </div>

      <!-- 题目列表卡片 -->
      <div class="problems-grid" v-if="filteredProblems.length > 0">
        <div
          v-for="item in filteredProblems"
          :key="item.id"
          class="problem-card glass-card"
        >
          <div class="card-main-info" @click="goToDetail(item.id)">
            <div class="card-top-meta">
              <a-tag :color="getDifficultyColor(item.difficulty)">
                {{ getDifficultyText(item.difficulty) }}
              </a-tag>
              <span class="tag-category-badge" v-if="item.tags">{{ item.tags }}</span>
              <span class="dispatch-badge" v-if="item.assignedProjectsCount > 0">
                🚀 已分派 {{ item.assignedProjectsCount }} 组
              </span>
              <span class="submission-count-badge">
                👥 提交 {{ item.submissionCount || 0 }} 人次
              </span>
            </div>

            <h3 class="problem-title">{{ item.title }}</h3>
            <p class="problem-desc">{{ item.contentDesc }}</p>

            <div class="problem-footer-meta">
              <span v-if="item.deadline">📅 截止时间：{{ formatDate(item.deadline) }}</span>
              <span v-else>📅 长期有效实训题</span>
              <span>·</span>
              <span>模式：{{ item.allowedSubmissionTypes }}</span>
            </div>
          </div>

          <div class="card-action-side">
            <!-- 学生做题状态 -->
            <div class="status-pill-box">
              <template v-if="!userStore.isTeacher">
                <a-tag v-if="item.isSubmitted" color="success" class="state-tag">
                  ✅ 已提交完成
                  <strong v-if="item.myScore != null"> ({{ item.myScore }}分)</strong>
                </a-tag>
                <a-tag v-else color="warning" class="state-tag">
                  ⏳ 待完成提交
                </a-tag>
              </template>
              <template v-else>
                <a-tag color="blue" class="state-tag">
                  题库收录题
                </a-tag>
              </template>
            </div>

            <div class="buttons-group">
              <a-button type="primary" shape="round" @click="goToDetail(item.id)">
                {{ item.isSubmitted ? '查看解析与代码 >' : '立即编码实训 >' }}
              </a-button>

              <!-- 教师快捷操作按钮 -->
              <div v-if="userStore.isTeacher" class="teacher-actions">
                <a-tooltip title="分发发布到指定项目团队">
                  <a-button size="small" type="default" shape="circle" @click.stop="openDispatchModal(item)">
                    <SendOutlined />
                  </a-button>
                </a-tooltip>
                <a-tooltip title="编辑该算法题">
                  <a-button size="small" type="default" shape="circle" @click.stop="openEditProblemModal(item)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="确认将该题目从题库中删除吗？"
                  ok-text="确认删除"
                  cancel-text="取消"
                  @confirm="handleDeleteProblem(item.id)"
                >
                  <a-button size="small" danger shape="circle" @click.stop>
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state glass-card">
        <a-empty description="未检索到符合条件的算法实训题目" />
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- 模块二：👑 组长算法督训中心 (针对组长，解决多比赛小组负责) -->
    <!-- ========================================================================= -->
    <div v-show="activeMainTab === 'LEADER'" class="tab-content-section">
      <!-- 若没有负责的小组 -->
      <div v-if="myLedTeams.length === 0" class="empty-state glass-card">
        <a-empty description="您当前暂未担任任何竞赛小组的组长。在竞赛广场创建项目并成为组长后，将在此自动激活全景督训中心。" />
      </div>

      <div v-else class="leader-supervision-view">
        <!-- 组长多团队切换与指挥台控制条 -->
        <div class="leader-control-panel glass-card">
          <div class="team-switcher-box">
            <div class="switcher-label">
              <TeamOutlined class="icon-pulse" />
              <span>切换管辖小组（您共负责 <strong>{{ myLedTeams.length }}</strong> 个比赛战队）：</span>
            </div>
            <a-select
              v-model:value="currentLedProjectId"
              style="min-width: 380px"
              size="large"
              @change="onSwitchLedProject"
            >
              <a-select-option
                v-for="team in myLedTeams"
                :key="team.id"
                :value="team.id"
              >
                【{{ team.competitionTitle }}】{{ team.projectName }} (共{{ team.memberCount }}人, 任务{{ team.taskCount }}题)
              </a-select-option>
            </a-select>
          </div>

          <div class="batch-urge-box">
            <a-button
              type="primary"
              danger
              size="large"
              shape="round"
              :loading="batchUrgeLoading"
              @click="handleBatchUrge"
            >
              <ThunderboltOutlined /> 一键催交全队未交成员
            </a-button>
          </div>
        </div>

        <!-- 团队算法完成大屏看板 -->
        <div class="team-stats-grid" v-if="teamOverviewData">
          <div class="stat-card glass-card">
            <div class="stat-num color-primary">{{ teamOverviewData.summary?.memberCount || 0 }}</div>
            <div class="stat-title">👥 小组正式成员</div>
          </div>
          <div class="stat-card glass-card">
            <div class="stat-num color-info">{{ teamOverviewData.summary?.taskCount || 0 }}</div>
            <div class="stat-title">📚 分配实训题目</div>
          </div>
          <div class="stat-card glass-card">
            <div class="stat-num color-success">
              {{ teamOverviewData.summary?.completionRate || 0 }}%
            </div>
            <div class="stat-title">📊 全队算法交付率</div>
            <a-progress
              :percent="teamOverviewData.summary?.completionRate || 0"
              :show-info="false"
              stroke-color="#10b981"
              size="small"
              style="margin-top: 8px;"
            />
          </div>
          <div class="stat-card glass-card">
            <div class="stat-num color-warning">{{ teamOverviewData.summary?.unsubmittedCount || 0 }}</div>
            <div class="stat-title">⏳ 待完成/待催交人次</div>
          </div>
        </div>

        <!-- 组员督训全景矩阵表 -->
        <div class="supervision-table-card glass-card">
          <div class="table-header-row">
            <div>
              <h3>📋 小组成员算法任务督促与成果矩阵</h3>
              <p class="table-sub-hint">
                组长肩负督学推进责任，可随时查阅组员提交代码、掌握实训通过情况，并向未提交成员定向施压催交
              </p>
            </div>
            <a-button type="link" @click="loadLeaderTeamOverview">
              <ReloadOutlined /> 刷新完成状态
            </a-button>
          </div>

          <a-table
            :dataSource="teamOverviewData?.matrix || []"
            :columns="leaderMatrixColumns"
            rowKey="id"
            :pagination="{ pageSize: 10 }"
            bordered
          >
            <!-- 组员姓名与角色 -->
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'studentInfo'">
                <div class="student-cell">
                  <strong>{{ record.studentName }}</strong>
                  <span class="student-account">{{ record.studentAccount }}</span>
                  <a-tag v-if="record.role === 'LEADER'" color="gold" size="small">组长(我)</a-tag>
                  <a-tag v-else color="blue" size="small">组员</a-tag>
                </div>
              </template>

              <!-- 算法题目 -->
              <template v-else-if="column.key === 'taskInfo'">
                <div class="task-cell">
                  <a-tag :color="getDifficultyColor(record.difficulty)">{{ getDifficultyText(record.difficulty) }}</a-tag>
                  <span class="task-cell-title" @click="goToDetail(record.taskId)">{{ record.taskTitle }}</span>
                </div>
              </template>

              <!-- 完成状态 -->
              <template v-else-if="column.key === 'status'">
                <div class="status-cell">
                  <a-tag v-if="record.isSubmitted" color="success">
                    ✅ 已提交
                  </a-tag>
                  <a-tag v-else color="error">
                    ⏳ 待提交
                  </a-tag>
                  <span v-if="record.score != null" class="score-text">
                    得分: {{ record.score }}
                  </span>
                  <span v-if="record.urgeCount > 0 && !record.isSubmitted" class="urge-tag">
                    (已催交{{ record.urgeCount }}次)
                  </span>
                </div>
              </template>

              <!-- 提交方式与语言 -->
              <template v-else-if="column.key === 'submitDetail'">
                <div v-if="record.isSubmitted">
                  <div>{{ record.submissionType }} / {{ record.codeLanguage || '默认' }}</div>
                  <div class="meta-time">{{ formatDate(record.submitTime) }}</div>
                </div>
                <span v-else class="text-muted">-</span>
              </template>

              <!-- 督促与管理操作 -->
              <template v-else-if="column.key === 'action'">
                <div class="action-buttons-cell">
                  <a-button
                    v-if="record.isSubmitted"
                    type="link"
                    size="small"
                    @click="openCodeViewer(record)"
                  >
                    <EyeOutlined /> 查看提交代码/作业
                  </a-button>

                  <a-button
                    v-if="!record.isSubmitted"
                    type="primary"
                    danger
                    size="small"
                    shape="round"
                    @click="handleSingleUrge(record)"
                  >
                    <ThunderboltOutlined /> ⚡ 催交
                  </a-button>
                </div>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- 模块三：👨‍🏫 教师题库管理与各组监控中心 (仅教师可用) -->
    <!-- ========================================================================= -->
    <div v-show="activeMainTab === 'TEACHER'" class="tab-content-section" v-if="userStore.isTeacher">
      <!-- 教师子视图切换栏 -->
      <div class="teacher-sub-nav glass-card">
        <a-radio-group v-model:value="teacherSubTab" button-style="solid" size="large">
          <a-radio-button value="GROUPS">📋 各参赛小组算法完成总览看板</a-radio-button>
          <a-radio-button value="SUBMISSIONS">📝 所有学生算法具体提交与在线批阅</a-radio-button>
          <a-radio-button value="LIBRARY">📚 算法题库集中管理 (增删改查)</a-radio-button>
        </a-radio-group>
      </div>

      <!-- 子视图 1：各参赛小组算法完成看板 -->
      <div v-if="teacherSubTab === 'GROUPS'" class="teacher-groups-view">
        <div class="table-card glass-card">
          <div class="table-header-row">
            <div>
              <h3>🏢 参赛项目团队算法实训完成大表</h3>
              <p class="table-sub-hint">统计各个小组的算法任务分发量、组员规模与全队综合完成率</p>
            </div>
            <a-button type="primary" shape="round" @click="loadTeacherGroupProgress">
              <ReloadOutlined /> 刷新小组数据
            </a-button>
          </div>

          <a-table
            :dataSource="teacherGroups"
            :columns="teacherGroupColumns"
            rowKey="projectId"
            :pagination="{ pageSize: 10 }"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'projectName'">
                <div>
                  <strong>{{ record.projectName }}</strong>
                  <div class="comp-sub-title">所属竞赛：{{ record.competitionTitle }}</div>
                </div>
              </template>

              <template v-else-if="column.key === 'leader'">
                <div>
                  <span>{{ record.leaderName }}</span>
                  <div class="meta-time" v-if="record.leaderPhone">📞 {{ record.leaderPhone }}</div>
                </div>
              </template>

              <template v-else-if="column.key === 'completion'">
                <div class="progress-cell">
                  <div class="progress-bar-wrap">
                    <a-progress
                      :percent="record.completionRate"
                      size="small"
                      :status="record.completionRate === 100 ? 'success' : 'active'"
                    />
                  </div>
                  <div class="progress-ratio">
                    已交 <strong>{{ record.actualSubmitted }}</strong> / 应交 {{ record.totalExpected }} 人次
                  </div>
                </div>
              </template>

              <template v-else-if="column.key === 'action'">
                <a-button type="link" size="small" @click="filterSubmissionsByGroup(record.projectId)">
                  查阅该组学生具体提交 >
                </a-button>
              </template>
            </template>
          </a-table>
        </div>
      </div>

      <!-- 子视图 2：所有学生的算法具体提交信息与批阅 -->
      <div v-if="teacherSubTab === 'SUBMISSIONS'" class="teacher-submissions-view">
        <div class="filter-card glass-card">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-select
                v-model:value="subFilterProjectId"
                placeholder="筛选参赛项目团队"
                style="width: 100%"
                allow-clear
                @change="loadTeacherSubmissions"
              >
                <a-select-option value="">全部项目团队</a-select-option>
                <a-select-option v-for="g in teacherGroups" :key="g.projectId" :value="g.projectId">
                  {{ g.projectName }}
                </a-select-option>
              </a-select>
            </a-col>
            <a-col :span="6">
              <a-select
                v-model:value="subFilterStatus"
                placeholder="批阅状态"
                style="width: 100%"
                allow-clear
                @change="loadTeacherSubmissions"
              >
                <a-select-option value="ALL">全部状态</a-select-option>
                <a-select-option value="SUBMITTED">⏳ 待批改</a-select-option>
                <a-select-option value="GRADED">✅ 已评分批改</a-select-option>
              </a-select>
            </a-col>
            <a-col :span="8">
              <a-input-search
                v-model:value="subFilterKeyword"
                placeholder="搜索学生姓名、学号或算法题目名称..."
                enter-button="搜索"
                allow-clear
                @search="loadTeacherSubmissions"
              />
            </a-col>
            <a-col :span="4" style="text-align: right;">
              <a-button @click="resetTeacherSubFilter">重置筛选</a-button>
            </a-col>
          </a-row>
        </div>

        <div class="table-card glass-card">
          <a-table
            :dataSource="teacherSubmissions"
            :columns="teacherSubmissionColumns"
            rowKey="id"
            :pagination="{ pageSize: 10 }"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'student'">
                <div>
                  <strong>{{ record.studentName }}</strong>
                  <div class="meta-time">{{ record.studentAccount }}</div>
                </div>
              </template>

              <template v-else-if="column.key === 'teamNames'">
                <a-tag color="cyan">{{ record.teamNames || '参赛队伍' }}</a-tag>
              </template>

              <template v-else-if="column.key === 'task'">
                <div>
                  <a-tag :color="getDifficultyColor(record.difficulty)">{{ getDifficultyText(record.difficulty) }}</a-tag>
                  <span>{{ record.taskTitle }}</span>
                </div>
              </template>

              <template v-else-if="column.key === 'status'">
                <a-tag v-if="record.status === 'GRADED'" color="success">
                  已批改: {{ record.score }}分
                </a-tag>
                <a-tag v-else color="warning">
                  待批改
                </a-tag>
              </template>

              <template v-else-if="column.key === 'submitTime'">
                <span>{{ formatDate(record.submitTime) }}</span>
              </template>

              <template v-else-if="column.key === 'action'">
                <div class="action-buttons-cell">
                  <a-button type="link" size="small" @click="openCodeViewer(record)">
                    <EyeOutlined /> 查阅代码/详情
                  </a-button>
                  <a-button type="primary" size="small" shape="round" @click="openGradingModal(record)">
                    <EditOutlined /> {{ record.status === 'GRADED' ? '重新打分' : '批阅打分' }}
                  </a-button>
                </div>
              </template>
            </template>
          </a-table>
        </div>
      </div>

      <!-- 子视图 3：算法题库集中管理 (增删改查) -->
      <div v-if="teacherSubTab === 'LIBRARY'" class="teacher-library-view">
        <div class="table-card glass-card">
          <div class="table-header-row">
            <div>
              <h3>📚 官方算法题库列表 (增删改查与定向发布)</h3>
              <p class="table-sub-hint">教师可随时维护题库，设计前沿竞赛题，并灵活分派给指定团队考核</p>
            </div>
            <a-button type="primary" shape="round" @click="openAddProblemModal">
              <PlusOutlined /> 录入算法新题
            </a-button>
          </div>

          <a-table
            :dataSource="libraryProblems"
            :columns="teacherLibraryColumns"
            rowKey="id"
            :pagination="{ pageSize: 10 }"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'title'">
                <div>
                  <strong>{{ record.title }}</strong>
                  <div class="problem-summary-line">{{ record.contentDesc }}</div>
                </div>
              </template>

              <template v-else-if="column.key === 'difficulty'">
                <a-tag :color="getDifficultyColor(record.difficulty)">{{ getDifficultyText(record.difficulty) }}</a-tag>
              </template>

              <template v-else-if="column.key === 'tags'">
                <a-tag color="blue" v-if="record.tags">{{ record.tags }}</a-tag>
                <span v-else class="text-muted">-</span>
              </template>

              <template v-else-if="column.key === 'assigned'">
                <span>已分派 {{ record.assignedProjectsCount || 0 }} 组</span>
              </template>

              <template v-else-if="column.key === 'action'">
                <div class="action-buttons-cell">
                  <a-button type="link" size="small" @click="openDispatchModal(record)">
                    <SendOutlined /> 发布到小组
                  </a-button>
                  <a-button type="link" size="small" @click="openEditProblemModal(record)">
                    <EditOutlined /> 编辑
                  </a-button>
                  <a-popconfirm
                    title="确定将该题目从题库中删除吗？"
                    ok-text="删除"
                    cancel-text="取消"
                    @confirm="handleDeleteProblem(record.id)"
                  >
                    <a-button type="link" danger size="small">
                      <DeleteOutlined /> 删除
                    </a-button>
                  </a-popconfirm>
                </div>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>

    <!-- ========================================================================= -->
    <!-- 弹窗 1：教师录入/编辑算法题库模态框 -->
    <!-- ========================================================================= -->
    <a-modal
      v-model:open="problemModalVisible"
      :title="isEditProblem ? '✏️ 修改算法题库题目' : '➕ 录入全新算法题目至题库'"
      width="780px"
      ok-text="保存题库"
      cancel-text="取消"
      @ok="handleSaveProblem"
    >
      <a-form :model="problemForm" layout="vertical">
        <a-form-item label="算法题目名称" required>
          <a-input v-model:value="problemForm.title" placeholder="如：动态规划经典：最长递增子序列(LIS)与二分优化" />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="题目难度" required>
              <a-select v-model:value="problemForm.difficulty">
                <a-select-option value="EASY">🟢 EASY 简单</a-select-option>
                <a-select-option value="MEDIUM">🟡 MEDIUM 中等</a-select-option>
                <a-select-option value="HARD">🔴 HARD 困难 / 极客挑战</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="题目分类标签 (逗号分隔)">
              <a-input v-model:value="problemForm.tags" placeholder="例如：动态规划,贪心,二分查找" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="任务详情与题目规格描述" required>
          <a-textarea
            v-model:value="problemForm.contentDesc"
            :rows="5"
            placeholder="详细阐明题目背景、输入限制、输出标准以及时间/空间复杂度要求..."
          />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="输入样例 (Sample Input)">
              <a-textarea v-model:value="problemForm.sampleInput" :rows="3" placeholder="例如：[10, 9, 2, 5, 3, 7, 101, 18]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="输出样例 (Sample Output)">
              <a-textarea v-model:value="problemForm.sampleOutput" :rows="3" placeholder="例如：4" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="允许提交形式">
              <a-input v-model:value="problemForm.allowedSubmissionTypes" placeholder="CODE_TEXT,FILE,GIT_URL" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="考核截止时间 (选填)">
              <a-date-picker v-model:value="problemForm.deadline" show-time style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="立即分派至指定参赛小组 (选填，保存时直接发布)">
          <a-select
            v-model:value="problemForm.assignedProjectIds"
            mode="multiple"
            placeholder="选择接收分派的项目团队（可多选）"
            allow-clear
          >
            <a-select-option v-for="p in allAvailableProjects" :key="p.id" :value="p.id">
              {{ p.projectName }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- ========================================================================= -->
    <!-- 弹窗 2：从题库发布/分发题目至各组模态框 -->
    <!-- ========================================================================= -->
    <a-modal
      v-model:open="dispatchModalVisible"
      title="🚀 发布算法题目至参赛项目团队"
      width="600px"
      ok-text="确认发布分发"
      cancel-text="取消"
      @ok="handleConfirmDispatch"
    >
      <div v-if="dispatchTargetProblem" style="margin-bottom: 16px;">
        <a-alert
          :message="`正在分发：${dispatchTargetProblem.title}`"
          :description="`难度：${getDifficultyText(dispatchTargetProblem.difficulty)} · 分类：${dispatchTargetProblem.tags || '综合算法'}`"
          type="info"
          show-icon
        />
      </div>

      <a-form layout="vertical">
        <a-form-item label="选择接收该任务的参赛团队" required>
          <div style="margin-bottom: 8px;">
            <a-button size="small" @click="selectAllProjects">全选所有项目团队</a-button>
            <a-button size="small" style="margin-left: 8px;" @click="dispatchProjectIds = []">清空选择</a-button>
          </div>
          <a-select
            v-model:value="dispatchProjectIds"
            mode="multiple"
            placeholder="请勾选需要考核该题的项目小组"
            style="width: 100%"
          >
            <a-select-option v-for="p in allAvailableProjects" :key="p.id" :value="p.id">
              {{ p.projectName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="设置考核截止时间">
          <a-date-picker v-model:value="dispatchDeadline" show-time style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- ========================================================================= -->
    <!-- 抽屉 3：代码与提交详情查阅抽屉 (组长/教师通用) -->
    <!-- ========================================================================= -->
    <a-drawer
      v-model:open="codeViewerVisible"
      title="💻 算法解题代码与成果查阅"
      width="680px"
      placement="right"
    >
      <div v-if="viewingRecord" class="code-viewer-content">
        <div class="viewer-meta-card">
          <div class="viewer-meta-row">
            <span><strong>提交学生：</strong>{{ viewingRecord.studentName }} ({{ viewingRecord.studentAccount }})</span>
            <span><strong>算法题目：</strong>{{ viewingRecord.taskTitle }}</span>
          </div>
          <div class="viewer-meta-row">
            <span><strong>提交形式：</strong>{{ viewingRecord.submissionType }}</span>
            <span><strong>编程语言：</strong>{{ viewingRecord.codeLanguage || 'Java / 通用' }}</span>
          </div>
          <div class="viewer-meta-row">
            <span><strong>提交时间：</strong>{{ formatDate(viewingRecord.submitTime) }}</span>
            <span v-if="viewingRecord.score != null" style="color: #10b981; font-weight: 700;">
              <strong>教师得分：</strong>{{ viewingRecord.score }} 分
            </span>
          </div>
        </div>

        <!-- 源代码区域 -->
        <div class="viewer-code-block" v-if="viewingRecord.codeContent">
          <div class="code-block-header">
            <span>📄 解题源码 ({{ viewingRecord.codeLanguage || 'Code' }})</span>
            <a-button size="small" type="link" @click="copyCode(viewingRecord.codeContent)">复制代码</a-button>
          </div>
          <pre class="code-text-area">{{ viewingRecord.codeContent }}</pre>
        </div>

        <!-- 附件文件 -->
        <div v-if="viewingRecord.fileId" class="viewer-file-block">
          <span>📎 附件工程原件：</span>
          <a :href="viewingRecord.fileId" target="_blank" class="download-link">点击下载并查阅工程包 ↗</a>
        </div>

        <!-- Git 仓库 -->
        <div v-if="viewingRecord.gitUrl" class="viewer-git-block">
          <span>🔗 Git 仓库代码源：</span>
          <a :href="viewingRecord.gitUrl" target="_blank" class="download-link">{{ viewingRecord.gitUrl }} ↗</a>
        </div>

        <!-- 教师评语批改 -->
        <div v-if="viewingRecord.teacherFeedback" class="viewer-feedback-block">
          <h4>👨‍🏫 指导教师批注反馈：</h4>
          <p>{{ viewingRecord.teacherFeedback }}</p>
        </div>
      </div>
    </a-drawer>

    <!-- ========================================================================= -->
    <!-- 弹窗 4：教师批改评分模态框 -->
    <!-- ========================================================================= -->
    <a-modal
      v-model:open="gradingModalVisible"
      title="📝 教师在线评测与打分批注"
      width="540px"
      ok-text="确认提交批改"
      cancel-text="取消"
      @ok="handleConfirmGrade"
    >
      <div v-if="gradingTargetRecord" style="margin-bottom: 16px;">
        <p><strong>学生：</strong>{{ gradingTargetRecord.studentName }} · <strong>题目：</strong>{{ gradingTargetRecord.taskTitle }}</p>
      </div>

      <a-form layout="vertical">
        <a-form-item label="作业得分 (0 - 100 分)" required>
          <a-input-number
            v-model:value="gradingScore"
            :min="0"
            :max="100"
            style="width: 100%"
            placeholder="请输入 0-100 的得分"
          />
        </a-form-item>

        <a-form-item label="教师专业批改评语">
          <a-textarea
            v-model:value="gradingFeedback"
            :rows="4"
            placeholder="请指出该算法在时空复杂度、边界条件、鲁棒性方面的优缺点，帮助学生进阶提升..."
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SendOutlined,
  EyeOutlined,
  ReloadOutlined,
  TeamOutlined,
  CodeOutlined,
  TrophyOutlined,
  ThunderboltOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 主 Tab
const activeMainTab = ref('TRAIN')

// 模块一：题库大厅状态
const libraryProblems = ref([])
const searchKeyword = ref('')
const filterDifficulty = ref('ALL')
const filterMyStatus = ref('ALL')
const selectedTag = ref('全部')
const popularTags = ['全部', '动态规划', '图论', '贪心', '数据结构', '滑动窗口', '搜索', '字符串']

// 模块二：组长督训中心状态
const myLedTeams = ref([])
const currentLedProjectId = ref('')
const teamOverviewData = ref(null)
const batchUrgeLoading = ref(false)

// 模块三：教师中心状态
const teacherSubTab = ref('GROUPS')
const teacherGroups = ref([])
const teacherSubmissions = ref([])
const subFilterProjectId = ref('')
const subFilterStatus = ref('ALL')
const subFilterKeyword = ref('')
const allAvailableProjects = ref([])

// 弹窗状态
const problemModalVisible = ref(false)
const isEditProblem = ref(false)
const problemForm = reactive({
  id: '',
  title: '',
  difficulty: 'MEDIUM',
  tags: '算法基础',
  contentDesc: '',
  sampleInput: '',
  sampleOutput: '',
  allowedSubmissionTypes: 'CODE_TEXT,FILE,GIT_URL',
  deadline: null,
  assignedProjectIds: []
})

const dispatchModalVisible = ref(false)
const dispatchTargetProblem = ref(null)
const dispatchProjectIds = ref([])
const dispatchDeadline = ref(null)

const codeViewerVisible = ref(false)
const viewingRecord = ref(null)

const gradingModalVisible = ref(false)
const gradingTargetRecord = ref(null)
const gradingScore = ref(90)
const gradingFeedback = ref('算法结构清晰，时空复杂度优秀，边界条件完备！')

// =============================================================================
// 计算属性与过滤
// =============================================================================
const filteredProblems = computed(() => {
  return libraryProblems.value.filter(p => {
    // 难度筛选
    if (filterDifficulty.value !== 'ALL' && p.difficulty !== filterDifficulty.value) {
      return false
    }
    // 状态筛选
    if (filterMyStatus.value === 'SUBMITTED' && !p.isSubmitted) return false
    if (filterMyStatus.value === 'UNSUBMITTED' && p.isSubmitted) return false
    // 标签筛选
    if (selectedTag.value !== '全部') {
      if (!p.tags || !p.tags.includes(selectedTag.value)) return false
    }
    return true
  })
})

const leaderUnsubmittedTotal = computed(() => {
  return teamOverviewData.value?.summary?.unsubmittedCount || 0
})

// =============================================================================
// 表格列定义
// =============================================================================
const leaderMatrixColumns = [
  { title: '小组成员', key: 'studentInfo', width: 170 },
  { title: '算法实训题目', key: 'taskInfo', width: 220 },
  { title: '完成状态', key: 'status', width: 150 },
  { title: '提交详情 / 时间', key: 'submitDetail', width: 180 },
  { title: '督训推进操作', key: 'action', width: 180, align: 'center' }
]

const teacherGroupColumns = [
  { title: '参赛项目团队名称', key: 'projectName', width: 240 },
  { title: '负责组长', key: 'leader', width: 160 },
  { title: '全队实训交付进度', key: 'completion', width: 260 },
  { title: '操作', key: 'action', width: 160, align: 'center' }
]

const teacherSubmissionColumns = [
  { title: '学生信息', key: 'student', width: 150 },
  { title: '所属项目团队', key: 'teamNames', width: 180 },
  { title: '考核题目', key: 'task', width: 220 },
  { title: '批阅状态 / 得分', key: 'status', width: 140 },
  { title: '提交时间', key: 'submitTime', width: 160 },
  { title: '操作', key: 'action', width: 180, align: 'center' }
]

const teacherLibraryColumns = [
  { title: '题目名称与摘要', key: 'title', width: 280 },
  { title: '难度', key: 'difficulty', width: 100 },
  { title: '算法分类', key: 'tags', width: 140 },
  { title: '分派情况', key: 'assigned', width: 120 },
  { title: '题库管理操作', key: 'action', width: 220, align: 'center' }
]

// =============================================================================
// 数据加载方法
// =============================================================================

// 1. 加载题库题目
const loadLibraryTasks = async () => {
  try {
    const res = await portalApi.getAlgorithmLibrary({
      keyword: searchKeyword.value,
      difficulty: filterDifficulty.value === 'ALL' ? '' : filterDifficulty.value,
      tag: selectedTag.value === '全部' ? '' : selectedTag.value
    })
    libraryProblems.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

// 2. 加载组长所带领的所有小组
const loadLeaderTeams = async () => {
  try {
    const res = await portalApi.getLeaderMyTeams()
    const list = Array.isArray(res) ? res : (res?.data || [])
    myLedTeams.value = list
    if (list.length > 0 && !currentLedProjectId.value) {
      currentLedProjectId.value = list[0].id
      loadLeaderTeamOverview()
    }
  } catch (e) {
    console.error(e)
  }
}

// 3. 组长切换小组并获取督训大屏
const onSwitchLedProject = () => {
  loadLeaderTeamOverview()
}

const loadLeaderTeamOverview = async () => {
  if (!currentLedProjectId.value) return
  try {
    const res = await portalApi.getLeaderTeamOverview({ projectId: currentLedProjectId.value })
    teamOverviewData.value = res?.data || res
  } catch (e) {
    console.error(e)
  }
}

// 4. 加载教师端各组完成看板
const loadTeacherGroupProgress = async () => {
  if (!userStore.isTeacher) return
  try {
    const res = await portalApi.getTeacherGroupProgress()
    teacherGroups.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

// 5. 加载教师端全量提交记录
const loadTeacherSubmissions = async () => {
  if (!userStore.isTeacher) return
  try {
    const res = await portalApi.getTeacherAllSubmissions({
      projectId: subFilterProjectId.value,
      status: subFilterStatus.value === 'ALL' ? '' : subFilterStatus.value,
      keyword: subFilterKeyword.value
    })
    teacherSubmissions.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

// 6. 加载可用项目列表（用于发布分发下拉选择）
const loadAllProjects = async () => {
  try {
    const compRes = await portalApi.getCompetitions({ current: 1, size: 50 })
    const records = compRes.records || compRes.data?.records || []
    const projectList = []
    for (const comp of records) {
      const pRes = await portalApi.getCompetitionProjects(comp.id)
      const list = Array.isArray(pRes) ? pRes : (pRes?.data || [])
      for (const p of list) {
        if (!projectList.some(item => item.id === p.id)) {
          projectList.push(p)
        }
      }
    }
    allAvailableProjects.value = projectList
  } catch (e) {}
}

const onMainTabChange = (key) => {
  if (key === 'TRAIN') {
    loadLibraryTasks()
  } else if (key === 'LEADER') {
    loadLeaderTeams()
  } else if (key === 'TEACHER') {
    loadTeacherGroupProgress()
    loadTeacherSubmissions()
    loadAllProjects()
  }
}

const onSelectTag = (tag) => {
  selectedTag.value = tag
  loadLibraryTasks()
}

const filterProblems = () => {
  // 触发 computed 重新筛选
}

const goToDetail = (id) => {
  router.push(`/algorithm/detail/${id}`)
}

// =============================================================================
// 组长督训操作 (一键催交 & 单独催交)
// =============================================================================
const handleBatchUrge = async () => {
  if (!currentLedProjectId.value) return
  batchUrgeLoading.value = true
  try {
    const res = await portalApi.leaderBatchUrge({
      projectId: currentLedProjectId.value,
      message: '【组长督办】团队算法实训任务已临近考核阶段，请各位组员尽快登录系统编写并提交算法代码！'
    })
    const msg = res.message || res.data?.message || '一键催办令已成功下发至全队未交成员！'
    message.success(msg)
    loadLeaderTeamOverview()
  } catch (e) {
  } finally {
    batchUrgeLoading.value = false
  }
}

const handleSingleUrge = async (record) => {
  try {
    const res = await portalApi.urgeMember({
      projectId: currentLedProjectId.value,
      studentId: record.studentId,
      taskId: record.taskId,
      message: `【组长提醒】算法题目【${record.taskTitle}】需尽快完成，请在实训中心抓紧编码提交！`
    })
    message.success(res || '催交通知已送达该组员')
    loadLeaderTeamOverview()
  } catch (e) {}
}

// =============================================================================
// 教师管理题库操作 (CRUD & 发布分发)
// =============================================================================
const openAddProblemModal = () => {
  isEditProblem.value = false
  problemForm.id = ''
  problemForm.title = ''
  problemForm.difficulty = 'MEDIUM'
  problemForm.tags = '动态规划,算法基础'
  problemForm.contentDesc = ''
  problemForm.sampleInput = ''
  problemForm.sampleOutput = ''
  problemForm.allowedSubmissionTypes = 'CODE_TEXT,FILE,GIT_URL'
  problemForm.deadline = null
  problemForm.assignedProjectIds = []
  problemModalVisible.value = true
  loadAllProjects()
}

const openEditProblemModal = (item) => {
  isEditProblem.value = true
  problemForm.id = item.id
  problemForm.title = item.title
  problemForm.difficulty = item.difficulty
  problemForm.tags = item.tags || ''
  problemForm.contentDesc = item.contentDesc
  problemForm.sampleInput = item.sampleInput || ''
  problemForm.sampleOutput = item.sampleOutput || ''
  problemForm.allowedSubmissionTypes = item.allowedSubmissionTypes || 'CODE_TEXT,FILE,GIT_URL'
  problemForm.deadline = item.deadline ? dayjs(item.deadline) : null
  problemForm.assignedProjectIds = item.assignedProjectIds || []
  problemModalVisible.value = true
  loadAllProjects()
}

const handleSaveProblem = async () => {
  if (!problemForm.title || !problemForm.contentDesc) {
    message.warning('请填写完整的算法题目名称与题目描述')
    return
  }
  try {
    const payload = {
      ...problemForm,
      deadline: problemForm.deadline ? dayjs(problemForm.deadline).format('YYYY-MM-DD HH:mm:ss') : null
    }
    if (isEditProblem.value) {
      await portalApi.updateAlgorithmProblem(payload)
      message.success('算法题目更新成功！')
    } else {
      await portalApi.saveAlgorithmProblem(payload)
      message.success('算法新题已成功录入算法库！')
    }
    problemModalVisible.value = false
    loadLibraryTasks()
    if (userStore.isTeacher) {
      loadTeacherGroupProgress()
    }
  } catch (e) {}
}

const handleDeleteProblem = async (id) => {
  try {
    await portalApi.deleteAlgorithmProblem(id)
    message.success('该题目已从题库中移除')
    loadLibraryTasks()
  } catch (e) {}
}

const openDispatchModal = (item) => {
  dispatchTargetProblem.value = item
  dispatchProjectIds.value = item.assignedProjectIds ? [...item.assignedProjectIds] : []
  dispatchDeadline.value = item.deadline ? dayjs(item.deadline) : null
  dispatchModalVisible.value = true
  loadAllProjects()
}

const selectAllProjects = () => {
  dispatchProjectIds.value = allAvailableProjects.value.map(p => p.id)
}

const handleConfirmDispatch = async () => {
  if (dispatchProjectIds.value.length === 0) {
    message.warning('请至少选择一个接收分派的项目团队')
    return
  }
  try {
    await portalApi.dispatchAlgorithmTask({
      taskId: dispatchTargetProblem.value.id,
      projectIds: dispatchProjectIds.value,
      deadline: dispatchDeadline.value ? dayjs(dispatchDeadline.value).format('YYYY-MM-DD HH:mm:ss') : null
    })
    message.success('题目已成功发布并分派至指定参赛团队！')
    dispatchModalVisible.value = false
    loadLibraryTasks()
    loadTeacherGroupProgress()
  } catch (e) {}
}

// =============================================================================
// 查阅代码抽屉 & 教师在线批改
// =============================================================================
const openCodeViewer = (record) => {
  viewingRecord.value = record
  codeViewerVisible.value = true
}

const copyCode = (code) => {
  if (!code) return
  navigator.clipboard.writeText(code).then(() => {
    message.success('代码已成功复制到剪贴板')
  })
}

const openGradingModal = (record) => {
  gradingTargetRecord.value = record
  gradingScore.value = record.score != null ? record.score : 90
  gradingFeedback.value = record.teacherFeedback || '解题思路严密，时空复杂度优秀，代码规范！'
  gradingModalVisible.value = true
}

const handleConfirmGrade = async () => {
  if (!gradingTargetRecord.value) return
  try {
    await portalApi.gradeAlgorithmSubmission({
      submissionId: gradingTargetRecord.value.id || gradingTargetRecord.value.submissionId,
      score: gradingScore.value,
      feedback: gradingFeedback.value
    })
    message.success('批阅打分完成！')
    gradingModalVisible.value = false
    loadTeacherSubmissions()
    if (activeMainTab.value === 'LEADER') {
      loadLeaderTeamOverview()
    }
  } catch (e) {}
}

const filterSubmissionsByGroup = (projectId) => {
  teacherSubTab.value = 'SUBMISSIONS'
  subFilterProjectId.value = projectId
  loadTeacherSubmissions()
}

const resetTeacherSubFilter = () => {
  subFilterProjectId.value = ''
  subFilterStatus.value = 'ALL'
  subFilterKeyword.value = ''
  loadTeacherSubmissions()
}

// 辅助格式化
const getDifficultyColor = (diff) => {
  if (diff === 'EASY') return 'green'
  if (diff === 'HARD') return 'red'
  return 'orange'
}

const getDifficultyText = (diff) => {
  if (diff === 'EASY') return 'EASY 简单'
  if (diff === 'HARD') return 'HARD 困难'
  return 'MEDIUM 中等'
}

const formatDate = (d) => {
  if (!d) return '-'
  return dayjs(d).format('YYYY-MM-DD HH:mm')
}

// 初始化
onMounted(async () => {
  loadLibraryTasks()
  loadLeaderTeams()
  if (userStore.isTeacher) {
    loadTeacherGroupProgress()
  }
})
</script>

<style scoped>
.algorithm-page {
  max-width: 1360px;
  margin: 28px auto;
  padding: 0 24px;
}

/* 顶部 Banner */
.page-header {
  padding: 28px 36px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title-row h2 {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.role-badge.teacher {
  background: #ede9fe;
  color: #6d28d9;
  border: 1px solid #c4b5fd;
}

.role-badge.leader {
  background: #fef3c7;
  color: #b45309;
  border: 1px solid #fde68a;
}

.role-badge.student {
  background: #e0f2fe;
  color: #0369a1;
  border: 1px solid #bae6fd;
}

.header-subtitle {
  color: #64748b;
  font-size: 13.5px;
  margin-top: 6px;
}

/* 主 Tab 导航 */
.tab-nav-wrapper {
  padding: 0 24px;
  margin-bottom: 20px;
}

/* 过滤工具卡片 */
.filter-card {
  padding: 20px 24px;
  margin-bottom: 20px;
}

.filter-top-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.search-box {
  flex: 1;
}

.filter-dropdowns {
  display: flex;
  gap: 12px;
}

.tags-filter-row {
  display: flex;
  align-items: center;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #f1f5f9;
}

.tag-label {
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  margin-right: 12px;
}

.tag-pill-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-pill {
  padding: 4px 14px;
  background: #f1f5f9;
  color: #475569;
  border-radius: 16px;
  font-size: 12.5px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-pill:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.tag-pill.active {
  background: #3b82f6;
  color: #ffffff;
  font-weight: 600;
}

/* 题目卡片列表 */
.problems-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.problem-card {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
}

.problem-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08);
}

.card-main-info {
  flex: 1;
  cursor: pointer;
}

.card-top-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.tag-category-badge {
  font-size: 11.5px;
  color: #2563eb;
  background: #eff6ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.dispatch-badge {
  font-size: 11.5px;
  color: #7c3aed;
  background: #f5f3ff;
  padding: 2px 8px;
  border-radius: 4px;
}

.submission-count-badge {
  font-size: 11.5px;
  color: #64748b;
}

.problem-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.problem-desc {
  font-size: 13.5px;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.problem-footer-meta {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  gap: 8px;
}

.card-action-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  margin-left: 24px;
}

.status-pill-box .state-tag {
  font-size: 12px;
  padding: 4px 10px;
}

.buttons-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.teacher-actions {
  display: flex;
  gap: 6px;
}

/* 组长多团队督训 */
.leader-control-panel {
  padding: 20px 28px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.team-switcher-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switcher-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.icon-pulse {
  color: #f59e0b;
  font-size: 18px;
}

.team-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 20px;
  text-align: center;
}

.stat-num {
  font-size: 28px;
  font-weight: 800;
  line-height: 1.2;
}

.color-primary { color: #2563eb; }
.color-info { color: #0284c7; }
.color-success { color: #10b981; }
.color-warning { color: #f59e0b; }

.stat-title {
  font-size: 13px;
  color: #64748b;
  margin-top: 6px;
}

.supervision-table-card, .table-card {
  padding: 24px 28px;
  margin-bottom: 24px;
}

.table-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.table-header-row h3 {
  font-size: 17px;
  font-weight: 800;
  color: #0f172a;
}

.table-sub-hint {
  font-size: 12.5px;
  color: #64748b;
  margin-top: 2px;
}

.student-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.student-account {
  font-size: 11.5px;
  color: #94a3b8;
}

.task-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.task-cell-title {
  font-weight: 600;
  color: #1e293b;
  cursor: pointer;
}

.task-cell-title:hover {
  color: #2563eb;
  text-decoration: underline;
}

.status-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.score-text {
  font-size: 12px;
  font-weight: 700;
  color: #10b981;
}

.urge-tag {
  font-size: 11px;
  color: #ef4444;
}

.meta-time {
  font-size: 11.5px;
  color: #94a3b8;
}

.action-buttons-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
}

/* 教师视图 */
.teacher-sub-nav {
  padding: 16px 24px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.comp-sub-title {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.progress-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.progress-ratio {
  font-size: 11.5px;
  color: #64748b;
}

.problem-summary-line {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 代码查阅抽屉 */
.code-viewer-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.viewer-meta-card {
  background: #f8fafc;
  padding: 14px 18px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 13px;
}

.viewer-meta-row {
  display: flex;
  justify-content: space-between;
}

.viewer-code-block {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #1e293b;
}

.code-block-header {
  background: #0f172a;
  color: #94a3b8;
  padding: 8px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12.5px;
}

.code-text-area {
  background: #1e293b;
  color: #38bdf8;
  padding: 16px;
  margin: 0;
  max-height: 420px;
  overflow-y: auto;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12.5px;
  line-height: 1.6;
}

.viewer-file-block, .viewer-git-block {
  padding: 12px 16px;
  background: #f1f5f9;
  border-radius: 8px;
  font-size: 13px;
}

.download-link {
  color: #2563eb;
  font-weight: 600;
}

.viewer-feedback-block {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  padding: 14px 18px;
  border-radius: 8px;
  color: #1e40af;
}

.viewer-feedback-block h4 {
  color: #1e40af;
  font-weight: 700;
  margin-bottom: 6px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.text-muted {
  color: #94a3b8;
}
</style>
