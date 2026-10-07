<template>
  <div class="project-detail-page" v-if="project">
    <!-- 头部横幅与状态控制 -->
    <div class="project-header glass-card">
      <div class="header-top">
        <a-button type="link" @click="$router.push('/project')">← 返回团队大厅</a-button>
        <div class="privacy-status-box">
          <span class="privacy-label">团队隐私状态：</span>
          <a-tag :color="project.isPrivate === 'YES' ? 'error' : 'success'">
            {{ project.isPrivate === 'YES' ? '🔒 私密团队（外界不可见）' : '🌐 公开展示团队' }}
          </a-tag>

          <!-- 组长或指导教师可以切换私密 -->
          <template v-if="project.isLeader || isTeacherOwner">
            <a-switch
              :checked="project.isPrivate === 'YES'"
              checked-children="私密"
              un-checked-children="公开"
              @change="handleTogglePrivacy"
            />
          </template>
        </div>
      </div>

      <div class="header-main">
        <div class="proj-title-block">
          <h2>{{ project.projectName }}</h2>
          <p class="proj-desc">{{ project.projectDesc }}</p>
          <div class="proj-meta">
            <span v-if="project.competition">🏆 关联竞赛：{{ project.competition.title }}</span>
            <span>·</span>
            <span>当前规模：{{ project.currentMemberCount }} / {{ project.maxMemberLimit }} 人</span>
            <span v-if="!project.hasLeader" style="color: #ea580c; font-weight: 600; margin-left: 8px;">· 👑 组长虚位以待 (面向学生竞聘)</span>
            <span v-else style="margin-left: 8px;">· 组长：{{ project.leaderName }}</span>
            <span v-if="project.teacherName" style="color: #7c3aed; margin-left: 8px;">· 👨‍🏫 指导教师：{{ project.teacherName }}</span>
          </div>
        </div>

        <div class="proj-actions">
          <a-space>
            <a-button
              v-if="project.isLeader || userStore.isTeacher"
              type="primary"
              shape="round"
              @click="handleTriggerAiEvaluation"
              :loading="aiLoading"
            >
              🤖 触发 AI 团队诊断打分
            </a-button>
            <a-button
              v-if="project.isLeader || userStore.isTeacher"
              type="dashed"
              shape="round"
              @click="openScoreModal"
            >
              📝 组员表现保密打分
            </a-button>
            <a-popconfirm
              v-if="isRegularMember"
              title="确定要主动退出该项目组吗？退出后名额将被释放"
              @confirm="handleQuitProject"
            >
              <a-button danger shape="round">
                🚪 退出队伍
              </a-button>
            </a-popconfirm>
            <a-popconfirm
              v-if="project.isLeader || isTeacherOwner"
              title="确定要解散该项目组吗？解散后团队成员与报名关系将被清理，此操作不可撤销！"
              @confirm="handleDisbandProject"
            >
              <a-button danger shape="round">
                💥 解散团队
              </a-button>
            </a-popconfirm>
          </a-space>
        </div>
      </div>
    </div>

    <!-- 未读讨论消息提示横幅：点击直达小组讨论 -->
    <div
      v-if="chatUnreadCount > 0 && activeTab !== 'chat'"
      class="chat-unread-banner glass-card"
      @click="activeTab = 'chat'"
    >
      <div class="banner-left">
        <span class="pulse-dot"></span>
        <span class="banner-icon">💬</span>
        <span class="banner-text">
          本项目协作研讨群有 <strong class="unread-highlight">{{ chatUnreadCount }}</strong> 条未读交流消息，点击立即前往群聊查看！
        </span>
      </div>
      <a-button type="primary" size="small" shape="round" class="go-chat-btn">
        立即查看 ➔
      </a-button>
    </div>

    <!-- AI 诊断评估卡片 (如果有评估数据) -->
    <div v-if="latestAiEvaluation" class="ai-report-card glass-card">
      <div class="ai-card-header">
        <div class="ai-title">
          <span class="ai-icon">🤖</span>
          <h3>AI 团队综合诊断与能力评分</h3>
          <a-tag color="purple">{{ latestAiEvaluation.aiModelVersion || 'AI诊断系统' }}</a-tag>
        </div>
        <div class="ai-score-display">
          <span class="score-val">{{ latestAiEvaluation.aiTotalScore }}</span>
          <span class="score-unit">分 (AI综合得分)</span>
        </div>
      </div>

      <div class="ai-metrics-row">
        <div class="metric-item">
          <div class="m-label">组员算法整体完成度</div>
          <div class="m-val">{{ latestAiEvaluation.algorithmCompletionRate }}%</div>
          <a-progress :percent="Number(latestAiEvaluation.algorithmCompletionRate)" status="active" />
        </div>
        <div class="metric-item">
          <div class="m-label">竞赛实战参与活跃度</div>
          <div class="m-val">{{ latestAiEvaluation.competitionParticipationScore }} 分</div>
          <a-progress :percent="Number(latestAiEvaluation.competitionParticipationScore)" stroke-color="#8b5cf6" />
        </div>
      </div>

      <div class="ai-report-text-box">
        <div class="report-title">📋 诊断报告详细剖析：</div>
        <div class="report-content">{{ latestAiEvaluation.aiEvaluationReport }}</div>
      </div>
    </div>

    <!-- 选项卡：成员阵容与管理 / 组员保密评分表 / 老师评定与排行 -->
    <div class="detail-tabs glass-card">
      <a-tabs v-model:activeKey="activeTab">
        <!-- Tab 1: 成员阵容 -->
        <a-tab-pane key="members" tab="👥 团队阵容与管理">
          <div class="members-grid">
            <div v-for="m in project.members" :key="m.id" class="member-card glass-card">
              <div class="m-top">
                <a-avatar size="large" :style="{ backgroundColor: m.memberRole === 'LEADER' ? '#f59e0b' : '#3b82f6' }">
                  {{ (m.studentName || 'S').substring(0, 1) }}
                </a-avatar>
                <div class="m-name-info">
                  <div class="m-name">
                    {{ m.studentName }}
                    <a-tag :color="m.memberRole === 'LEADER' ? 'gold' : 'blue'">
                      {{ m.memberRole === 'LEADER' ? '👑 队长/组长' : '组员' }}
                    </a-tag>
                  </div>
                  <div class="m-account">{{ m.studentAccount }}</div>
                </div>
              </div>

              <div class="m-duty">
                <span class="duty-label">组内职责：</span>
                <span class="duty-val">{{ m.dutyRemark || '核心研发成员' }}</span>
              </div>

              <!-- 组长或指导教师对组员的管理功能 -->
              <div v-if="(project.isLeader || isTeacherOwner) && m.memberRole !== 'LEADER'" class="m-actions">
                <a-button type="link" size="small" @click="openEditDutyModal(m)">调整职责</a-button>
                <a-popconfirm title="确定要将该组员移出项目组吗？" @confirm="handleRemoveMember(m.studentId)">
                  <a-button type="link" danger size="small">移出团队</a-button>
                </a-popconfirm>
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- Tab 2: 组员私密打分表 (严格限制：仅组长和老师可见) -->
        <a-tab-pane key="scores" tab="🔒 成员表现保密评价 (老师/组长专享)" v-if="project.isLeader || userStore.isTeacher">
          <div class="privacy-alert">
            <a-alert
              message="机密权限控制"
              description="本区域数据受严格数据权限保护，仅指导教师和本项目组长享有调阅与打分权限，普通组员无法查看。"
              type="warning"
              show-icon
              style="margin-bottom: 16px;"
            />
          </div>

          <a-table :dataSource="memberScores" :columns="scoreColumns" rowKey="id" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'score'">
                <strong style="color: #2563eb; font-size: 16px;">{{ record.score }} 分</strong>
              </template>
              <template v-if="column.key === 'evaluatorRole'">
                <a-tag :color="record.evaluatorRole === 'TEACHER' ? 'purple' : 'gold'">
                  {{ record.evaluatorRole === 'TEACHER' ? '指导教师打分' : '组长评价打分' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- Tab 3: 指导老师评定与榜单发布 (老师专属) -->
        <a-tab-pane key="teacherEvaluation" tab="👨‍🏫 教师综合评定与冲榜 (老师专属)" v-if="userStore.isTeacher">
          <div class="teacher-eval-box">
            <a-form layout="vertical" style="max-width: 500px;">
              <a-form-item label="榜单考核周期标识" required>
                <a-input v-model:value="teacherForm.rankingPeriod" placeholder="如：2026_SPRING" />
              </a-form-item>
              <a-form-item label="教师评定得分 (0-100)" required>
                <a-input-number v-model:value="teacherForm.teacherScore" :min="0" :max="100" style="width: 100%" />
              </a-form-item>
              <a-button type="primary" shape="round" @click="handleTeacherEvaluate">
                保存教师评分并生成榜单草稿
              </a-button>
            </a-form>
          </div>
        </a-tab-pane>

        <!-- Tab 4: 报名申请与成员筛选 (仅老师与组长可见) -->
        <a-tab-pane key="applicants" tab="📥 报名申请与成员筛选" v-if="project.isLeader || userStore.isTeacher">
          <div class="applicants-header" style="margin-bottom: 16px;">
            <a-alert
              message="组员与组长竞聘审核工作台"
              description="您可在此对主动申请加入本项目的学生进行筛选。对于教师发起的待聘项目，可在此直接任命组长或录用组员。"
              type="info"
              show-icon
            />
          </div>

          <a-table :dataSource="applicantsList" :columns="applicantColumns" rowKey="id" :loading="applicantsLoading" :pagination="false">
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
                  ⚠️ 已担任其他组组长
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
        </a-tab-pane>

        <!-- Tab 5: 团队竞赛荣誉与获奖证书 (以小组为单位) -->
        <a-tab-pane key="certificates" tab="🏆 团队竞赛荣誉与证书">
          <div class="cert-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;">
            <div>
              <h3 style="font-size: 18px; font-weight: 700; color: #0f172a; margin-bottom: 4px;">
                🏆 本团队竞赛荣誉与获奖证书库
              </h3>
              <p style="color: #64748b; font-size: 13px; margin: 0;">
                以小组为单位申报与归档，记录团队全体成员在本项目竞赛中的获奖荣誉。
              </p>
            </div>
            <div>
              <!-- 只有参与了该竞赛的小组成员才能上传 -->
              <a-button
                v-if="isProjectMember"
                type="primary"
                shape="round"
                style="background: linear-gradient(135deg, #10b981 0%, #059669 100%); border: none;"
                @click="openCertUploadModal"
              >
                <template #icon><UploadOutlined /></template>
                📤 上传团队竞赛荣誉证书
              </a-button>
              <a-tooltip v-else title="只有参与了该竞赛的小组成员才能上传证书">
                <a-button shape="round" disabled>
                  🔒 仅本参赛小组成员可上传
                </a-button>
              </a-tooltip>
            </div>
          </div>

          <div v-if="projectCertificates.length === 0" style="padding: 40px 0;">
            <a-empty description="当前项目组尚未上传任何竞赛荣誉证书，赛后快来上传获奖喜报吧！" />
          </div>

          <div v-else class="cert-cards-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 20px;">
            <div
              v-for="cert in projectCertificates"
              :key="cert.id"
              class="cert-item-card glass-card"
              style="padding: 20px; border-radius: 16px; border: 1px solid #e2e8f0; background: #ffffff; box-shadow: 0 4px 12px rgba(0,0,0,0.03);"
            >
              <div style="display: flex; gap: 16px;">
                <!-- 证书缩略图/原图预览 -->
                <div style="width: 110px; height: 110px; flex-shrink: 0; border-radius: 10px; overflow: hidden; background: #f1f5f9; border: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: center;">
                  <a-image
                    v-if="cert.certificateImage || cert.certificateUrl"
                    :src="cert.certificateImage || cert.certificateUrl"
                    alt="证书原件"
                    style="width: 110px; height: 110px; object-fit: cover;"
                  />
                  <span v-else style="color: #94a3b8; font-size: 12px;">无证书图</span>
                </div>

                <!-- 证书信息 -->
                <div style="flex: 1; min-width: 0;">
                  <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 8px;">
                    <h4 style="font-weight: 700; color: #0f172a; font-size: 16px; margin: 0 0 4px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                      {{ cert.certificateName }}
                    </h4>
                  </div>
                  <div style="margin-bottom: 6px;">
                    <a-tag color="gold" style="font-weight: 600;">{{ cert.awardLevel || '竞赛奖项' }}</a-tag>
                    <a-tag :color="cert.verifyStatus === 'VERIFIED' ? 'success' : (cert.verifyStatus === 'REJECTED' ? 'error' : 'warning')">
                      {{ cert.verifyStatus === 'VERIFIED' ? '✅ 导师已官方认证' : (cert.verifyStatus === 'REJECTED' ? '❌ 导师驳回' : '⏳ 待指导教师认证') }}
                    </a-tag>
                  </div>
                  <div style="font-size: 12px; color: #64748b; margin-bottom: 4px;">
                    📅 获奖日期：{{ formatDateOnly(cert.awardDate) }}
                  </div>
                  <div style="font-size: 12px; color: #4338ca; background: #e0e7ff; padding: 4px 8px; border-radius: 6px; margin-top: 6px; word-break: break-all;">
                    👥 获奖小组：{{ cert.studentNames || teamMemberNames || '本团队全员' }}
                  </div>
                </div>
              </div>

              <!-- 教师批注评语 (若有) -->
              <div v-if="cert.verifyRemark" style="margin-top: 12px; padding: 8px 12px; background: #f8fafc; border-radius: 8px; font-size: 12px; color: #64748b;">
                👨‍🏫 导师评语：{{ cert.verifyRemark }}
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- Tab 6: 小组研讨与交流群聊 (群成员与教师可收发) -->
        <a-tab-pane key="chat">
          <template #tab>
            <span>
              💬 小组讨论
              <a-badge v-if="chatUnreadCount > 0" :count="chatUnreadCount" style="margin-left: 6px;" />
            </span>
          </template>
          <div style="margin-top: 8px;">
            <ChatRoom
              v-if="project"
              chatType="PROJECT"
              :chatTargetId="projectId"
              :title="`💬 ${project.projectName} · 项目小组协作研讨群`"
              :subtitle="`仅本团队成员与指导教师（${project.teacherName || '指导老师'}）可收发互动，成员间同频研讨`"
              @read-marked="handleChatRead"
            />
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 上传团队竞赛荣誉证书弹窗 -->
    <a-modal
      v-model:open="certUploadModalVisible"
      title="📤 上传团队竞赛荣誉与获奖证书（以小组为单位）"
      ok-text="确认提交审核"
      cancel-text="取消"
      :confirm-loading="uploadingCert"
      @ok="handleSubmitCertificate"
      width="560px"
    >
      <div style="margin-bottom: 16px; padding: 12px; background: #ecfdf5; border-radius: 8px; border: 1px solid #a7f3d0; font-size: 13px; color: #065f46;">
        💡 <strong>全队成果共享</strong>：证书上传后将自动关联本团队全体参赛学生（{{ teamMemberNames }}），并由指导教师进行官方权威认证！
      </div>

      <a-form layout="vertical" :model="certForm">
        <a-form-item label="证书 / 荣誉名称" required>
          <a-input
            v-model:value="certForm.certificateName"
            placeholder="例如：2026年全国大学生算法设计大赛一等奖"
          />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="获奖级别" required>
              <a-select v-model:value="certForm.awardLevel" placeholder="选择获奖级别">
                <a-select-option value="国家特等奖">国家特等奖</a-select-option>
                <a-select-option value="国家一等奖">国家一等奖</a-select-option>
                <a-select-option value="国家二等奖">国家二等奖</a-select-option>
                <a-select-option value="国家三等奖">国家三等奖</a-select-option>
                <a-select-option value="省级特等奖">省级特等奖</a-select-option>
                <a-select-option value="省级一等奖">省级一等奖</a-select-option>
                <a-select-option value="省级二等奖">省级二等奖</a-select-option>
                <a-select-option value="省级三等奖">省级三等奖</a-select-option>
                <a-select-option value="校级一等奖">校级一等奖</a-select-option>
                <a-select-option value="校级二等奖">校级二等奖</a-select-option>
                <a-select-option value="优秀奖">优秀奖</a-select-option>
                <a-select-option value="其他荣誉">其他荣誉</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="获奖日期" required>
              <a-date-picker
                v-model:value="certForm.awardDate"
                style="width: 100%"
                placeholder="请选择获奖日期"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="证书图片（扫描件/实物拍照）" required>
          <div style="display: flex; gap: 16px; align-items: center;">
            <a-upload
              :show-upload-list="false"
              :custom-request="handleUploadCertImage"
              accept="image/*"
            >
              <a-button :loading="uploadingCertImage">
                <template #icon><UploadOutlined /></template>
                {{ certForm.certificateImage ? '重新上传证书图片' : '上传证书图片' }}
              </a-button>
            </a-upload>
            <span v-if="certForm.certificateImage" style="color: #10b981; font-size: 13px;">
              ✅ 证书图片已上传就绪
            </span>
          </div>
          <div v-if="certForm.certificateImage" style="margin-top: 10px; width: 140px; height: 100px; border-radius: 8px; overflow: hidden; border: 1px solid #cbd5e1;">
            <img :src="certForm.certificateImage" alt="证书预览" style="width: 100%; height: 100%; object-fit: cover;" />
          </div>
        </a-form-item>

        <!-- 自动匹配展示字段 (只读预览) -->
        <div style="background: #f8fafc; padding: 12px; border-radius: 8px; border: 1px solid #e2e8f0; font-size: 13px;">
          <div style="color: #64748b; margin-bottom: 4px;">
            🏆 所属竞赛：<strong style="color: #0f172a;">{{ project.competition?.title || '当前学科竞赛' }}</strong>（系统自动匹配）
          </div>
          <div style="color: #64748b; margin-bottom: 4px;">
            🚀 所属团队：<strong style="color: #0f172a;">{{ project.projectName }}</strong>（系统自动匹配）
          </div>
          <div style="color: #64748b;">
            👥 获奖成员（小组全体）：<strong style="color: #4338ca;">{{ teamMemberNames }}</strong>（以小组为单位自动保存）
          </div>
        </div>
      </a-form>
    </a-modal>

    <!-- 组长调整职责对话框 -->
    <a-modal v-model:open="editDutyVisible" title="调整组内职责分工" @ok="handleUpdateDuty">
      <div v-if="selectedMember" style="margin-bottom: 12px;">
        成员姓名：<strong>{{ selectedMember.studentName }}</strong>
      </div>
      <a-input v-model:value="dutyInput" placeholder="如：算法负责人、前端开发、演示报告撰写" />
    </a-modal>

    <!-- 打分对话框 -->
    <a-modal v-model:open="scoreModalVisible" title="录入成员表现评分（保密）" @ok="handleSaveScore">
      <a-form layout="vertical">
        <a-form-item label="选择被评价组员" required>
          <a-select v-model:value="scoreForm.studentId" placeholder="请选择组员">
            <a-select-option
              v-for="m in project.members?.filter(x => x.memberRole !== 'LEADER')"
              :key="m.studentId"
              :value="m.studentId"
            >
              {{ m.studentName }} ({{ m.dutyRemark || '组员' }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="表现评分 (0-100)" required>
          <a-input-number v-model:value="scoreForm.score" :min="0" :max="100" style="width: 100%" />
        </a-form-item>
        <a-form-item label="考核阶段说明">
          <a-input v-model:value="scoreForm.scorePeriod" placeholder="如：中期答辩表现 / 算法攻坚周" />
        </a-form-item>
        <a-form-item label="保密评语（仅老师与组长可见）">
          <a-textarea v-model:value="scoreForm.remark" :rows="3" placeholder="请客观评价组员完成质量、协作态度与贡献度" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'
import ChatRoom from '@/components/ChatRoom.vue'

const route = useRoute()
const userStore = useUserStore()

const projectId = route.params.id
const project = ref(null)
const activeTab = ref('members')
const aiLoading = ref(false)

// 小组讨论未读消息与定时器
const chatUnreadCount = ref(0)
let unreadTimer = null

const loadChatUnreadCount = async () => {
  if (typeof document !== 'undefined' && document.hidden) return
  try {
    const res = await portalApi.getUnreadCount('PROJECT', projectId)
    chatUnreadCount.value = typeof res === 'number' ? res : (res?.data || 0)
  } catch (e) {}
}

const handleChatRead = () => {
  chatUnreadCount.value = 0
}

watch(activeTab, (val) => {
  if (val === 'chat') {
    chatUnreadCount.value = 0
  }
})

// 荣誉与证书状态
const projectCertificates = ref([])
const certUploadModalVisible = ref(false)
const uploadingCert = ref(false)
const uploadingCertImage = ref(false)
const certForm = reactive({
  certificateName: '',
  awardLevel: '国家一等奖',
  awardDate: null,
  certificateImage: ''
})

// 判断当前用户是否是本项目组的参赛成员（组长或组员）
const isProjectMember = computed(() => {
  if (!project.value) return false
  const uid = userStore.userInfo?.id
  if (!uid) return false
  if (project.value.isLeader || project.value.leaderId === uid) return true
  return project.value.members?.some(m => m.studentId === uid)
})

// 判断当前用户是否是本项目组的指导教师所有者
const isTeacherOwner = computed(() => {
  if (!project.value || !userStore.isTeacher) return false
  const uid = userStore.userInfo?.id
  if (!uid) return false
  return project.value.teacherId === uid || project.value.createUser === uid || project.value.isTeacherInitiated
})

// 判断当前用户是否是普通组员（非组长）
const isRegularMember = computed(() => {
  if (!project.value || !userStore.isStudent) return false
  const uid = userStore.userInfo?.id
  if (!uid) return false
  if (project.value.leaderId === uid || project.value.isLeader) return false
  return project.value.members?.some(m => m.studentId === uid)
})

// 自动匹配本团队全体获奖成员（以小组为单位，包含多个学生）
const teamMemberNames = computed(() => {
  if (!project.value || !project.value.members || project.value.members.length === 0) return '本团队全员'
  return project.value.members.map(m => `${m.studentName}(${m.memberRole === 'LEADER' ? '组长' : '组员'})`).join('、')
})

const latestAiEvaluation = ref(null)
const memberScores = ref([])

const editDutyVisible = ref(false)
const selectedMember = ref(null)
const dutyInput = ref('')

const scoreModalVisible = ref(false)
const scoreForm = reactive({
  studentId: '',
  score: 90,
  scorePeriod: '阶段实训综合评价',
  remark: ''
})

const teacherForm = reactive({
  rankingPeriod: '2026_SPRING',
  teacherScore: 92
})

const scoreColumns = [
  { title: '组员姓名', dataIndex: 'studentName', key: 'studentName' },
  { title: '评价阶段', dataIndex: 'scorePeriod', key: 'scorePeriod' },
  { title: '打分主体', key: 'evaluatorRole' },
  { title: '得分', key: 'score' },
  { title: '保密评语', dataIndex: 'remark', key: 'remark' }
]

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

const loadApplicants = async () => {
  if (project.value?.isLeader || userStore.isTeacher) {
    applicantsLoading.value = true
    try {
      const res = await portalApi.getProjectApplicants(projectId)
      applicantsList.value = Array.isArray(res) ? res : (res?.data || [])
    } catch (e) {
    } finally {
      applicantsLoading.value = false
    }
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
    loadApplicants()
    loadProject()
    await userStore.fetchProfile()
  } catch (e) {
    console.error(e)
  }
}

const loadProject = async () => {
  try {
    const res = await portalApi.getProjectDetail(projectId)
    project.value = res
  } catch (e) {
    console.error(e)
  }
}

const loadMemberScores = async () => {
  if (project.value?.isLeader || userStore.isTeacher) {
    try {
      const res = await portalApi.getMemberScores(projectId)
      memberScores.value = res || []
    } catch (e) {}
  }
}

// 切换私密状态
const handleTogglePrivacy = async (checked) => {
  const isPrivate = checked ? 'YES' : 'NO'
  try {
    const res = await portalApi.toggleProjectPrivacy({
      projectId,
      isPrivate
    })
    project.value.isPrivate = isPrivate
    message.success(res)
  } catch (e) {}
}

// 触发 AI 诊断打分
const handleTriggerAiEvaluation = async () => {
  aiLoading.value = true
  try {
    const res = await portalApi.evaluateTeamWithAi({
      projectId,
      batch: '2026春季实训期'
    })
    latestAiEvaluation.value = res
    message.success('AI 团队深度综合诊断报告已完成生成！')
  } catch (e) {
    console.error(e)
  } finally {
    aiLoading.value = false
  }
}

// 移除组员
const handleRemoveMember = async (studentId) => {
  try {
    await portalApi.removeMember({ projectId, studentId })
    message.success('已移除该组员')
    loadProject()
  } catch (e) {}
}

// 组员主动退出队伍
const handleQuitProject = async () => {
  try {
    const res = await portalApi.quitProject({ projectId })
    message.success(res || '已成功退出队伍')
    router.push('/project')
  } catch (e) {
    message.error(e.message || '退出队伍失败')
  }
}

// 组长或指导教师解散团队
const handleDisbandProject = async () => {
  try {
    const res = await portalApi.disbandProject({ projectId })
    message.success(res || '项目组已成功解散')
    router.push('/project')
  } catch (e) {
    message.error(e.message || '解散团队失败')
  }
}

// 调整职责
const openEditDutyModal = (m) => {
  selectedMember.value = m
  dutyInput.value = m.dutyRemark || ''
  editDutyVisible.value = true
}

const handleUpdateDuty = async () => {
  try {
    await portalApi.updateMemberDuty({
      projectId,
      studentId: selectedMember.value.studentId,
      dutyRemark: dutyInput.value
    })
    message.success('职责分工调整成功')
    editDutyVisible.value = false
    loadProject()
  } catch (e) {}
}

// 录入保密评分
const openScoreModal = () => {
  scoreModalVisible.value = true
}

const handleSaveScore = async () => {
  if (!scoreForm.studentId) {
    message.warning('请选择组员')
    return
  }
  try {
    await portalApi.saveMemberScore({
      projectId,
      ...scoreForm
    })
    message.success('评分已保密保存')
    scoreModalVisible.value = false
    loadMemberScores()
  } catch (e) {}
}

// 加载团队荣誉证书列表
const loadProjectCertificates = async () => {
  try {
    const res = await portalApi.getProjectCertificates(projectId)
    projectCertificates.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  }
}

// 打开证书上传弹窗
const openCertUploadModal = () => {
  certForm.certificateName = ''
  certForm.awardLevel = '国家一等奖'
  certForm.awardDate = null
  certForm.certificateImage = ''
  certUploadModalVisible.value = true
}

// 证书图片上传
const handleUploadCertImage = async ({ file }) => {
  uploadingCertImage.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const url = typeof res === 'string' ? res : (res?.data || res)
    certForm.certificateImage = url
    message.success('证书图片上传成功！')
  } catch (e) {
    message.error('图片上传失败，请重试')
  } finally {
    uploadingCertImage.value = false
  }
}

// 提交团队荣誉证书
const handleSubmitCertificate = async () => {
  if (!certForm.certificateName?.trim()) {
    message.warning('请填写证书/荣誉名称')
    return
  }
  if (!certForm.awardLevel) {
    message.warning('请选择获奖级别')
    return
  }
  if (!certForm.awardDate) {
    message.warning('请选择获奖日期')
    return
  }
  if (!certForm.certificateImage) {
    message.warning('请上传证书图片凭证')
    return
  }

  uploadingCert.value = true
  try {
    await portalApi.uploadCertificate({
      projectId,
      competitionId: project.value.competitionId || project.value.competition?.id,
      certificateName: certForm.certificateName.trim(),
      awardLevel: certForm.awardLevel,
      awardDate: dayjs(certForm.awardDate).format('YYYY-MM-DD'),
      certificateImage: certForm.certificateImage,
      fileId: certForm.certificateImage,
      studentNames: teamMemberNames.value
    })
    message.success('🎉 团队竞赛荣誉证书已提交！已自动以小组为单位保存，等待指导教师审核认证。')
    certUploadModalVisible.value = false
    loadProjectCertificates()
  } catch (e) {
    message.error(e.message || '上传失败')
  } finally {
    uploadingCert.value = false
  }
}

// 格式化日期
const formatDateOnly = (d) => {
  if (!d) return '未记录'
  return dayjs(d).format('YYYY-MM-DD')
}

// 老师评定与榜单
const handleTeacherEvaluate = async () => {
  try {
    const rankingId = await portalApi.teacherEvaluateTeam({
      projectId,
      competitionId: project.value.competition?.id,
      ...teacherForm
    })
    message.success('教师评分保存成功，点击排行榜即可正式上榜发布！')
  } catch (e) {}
}

onMounted(async () => {
  if (route.query.tab === 'chat') {
    activeTab.value = 'chat'
  } else if (route.query.tab === 'certificates' || route.query.tab === 'certs') {
    activeTab.value = 'certificates'
    if (route.query.openUpload === '1') {
      setTimeout(() => {
        openCertUploadModal()
      }, 350)
    }
  }
  await loadProject()
  loadMemberScores()
  loadApplicants()
  loadProjectCertificates()
  loadChatUnreadCount()
  unreadTimer = setInterval(loadChatUnreadCount, 6000)
})

onUnmounted(() => {
  if (unreadTimer) {
    clearInterval(unreadTimer)
    unreadTimer = null
  }
})
</script>

<style scoped>
.project-detail-page {
  max-width: 1320px;
  margin: 32px auto;
  padding: 0 24px;
}

.project-header {
  padding: 32px;
  margin-bottom: 24px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.privacy-status-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.privacy-label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  flex-wrap: wrap;
}

.proj-title-block h2 {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.proj-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  max-width: 720px;
  margin-bottom: 12px;
}

.proj-meta {
  font-size: 13px;
  color: #64748b;
  display: flex;
  gap: 8px;
}

.ai-report-card {
  padding: 28px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.7) 0%, rgba(243, 232, 255, 0.7) 100%), #ffffff;
  border: 1px solid #bfdbfe;
}

.ai-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.ai-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-icon {
  font-size: 24px;
}

.ai-title h3 {
  font-size: 18px;
  font-weight: 800;
  color: #1e1b4b;
}

.ai-score-display {
  text-align: right;
}

.score-val {
  font-size: 32px;
  font-weight: 900;
  color: #2563eb;
  line-height: 1;
}

.score-unit {
  font-size: 12px;
  color: #64748b;
  display: block;
}

.ai-metrics-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 20px;
}

.metric-item {
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.m-label {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

.m-val {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.ai-report-text-box {
  background: #ffffff;
  border-radius: 12px;
  padding: 18px;
  border: 1px solid #e2e8f0;
}

.report-title {
  font-weight: 700;
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 8px;
}

.report-content {
  font-size: 13.5px;
  color: #334155;
  line-height: 1.7;
  white-space: pre-wrap;
}

.detail-tabs {
  padding: 24px 32px;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.member-card {
  padding: 20px;
}

.m-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.m-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 6px;
}

.m-account {
  font-size: 12px;
  color: #94a3b8;
}

.m-duty {
  font-size: 13px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 12px;
}

.duty-label {
  color: #64748b;
}

.duty-val {
  color: #1e293b;
  font-weight: 600;
}

.m-actions {
  text-align: right;
  border-top: 1px solid #f1f5f9;
  padding-top: 8px;
}

.teacher-eval-box {
  padding: 16px 0;
}

/* 小组讨论未读提示横幅 */
.chat-unread-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 22px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(238, 242, 255, 0.95) 0%, rgba(243, 232, 255, 0.95) 100%);
  border: 1px solid #c7d2fe;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.1);
}

.chat-unread-banner:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.18);
  border-color: #818cf8;
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
  color: #1e1b4b;
  font-weight: 500;
}

.unread-highlight {
  color: #ef4444;
  font-size: 16px;
  font-weight: 800;
  margin: 0 4px;
}

.go-chat-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
  font-weight: 600;
}
</style>
