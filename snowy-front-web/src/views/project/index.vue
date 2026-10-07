<template>
  <div class="project-page">
    <!-- 头部横幅 -->
    <div class="page-header glass-card">
      <div class="header-left">
        <h2>👥 团队大厅与组长招募墙</h2>
        <p>跨学科跨专业协同结组，查看各项目团队阵容，投递组长招募需求</p>
      </div>
      <div class="header-right" style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
        <a-button v-if="userStore.isTeacher" type="primary" size="large" shape="round" style="background: linear-gradient(135deg, #7c3aed, #4f46e5); border: none;" @click="openTeacherCreateModal">
          👨‍🏫 导师发布实训招募组
        </a-button>
        <a-button v-if="userStore.isLeader" type="primary" size="large" shape="round" @click="openRecruitModal">
          📢 组长发布新招募
        </a-button>
        <a-button size="large" shape="round" @click="$router.push('/competition')">
          🏆 前往竞赛广场建组
        </a-button>
      </div>
    </div>

    <!-- 选项卡：组长招募需求 / 所有公开团队 -->
    <div class="content-tabs glass-card">
      <a-tabs v-model:activeKey="activeTab">
        <!-- 招募需求墙 -->
        <a-tab-pane key="recruits" tab="📢 组长引才招募令">
          <div class="recruit-grid" v-if="recruitList.length > 0">
            <div v-for="rec in recruitList" :key="rec.id" class="recruit-card glass-card">
              <div class="rec-header">
                <div class="rec-team-name">
                  <span class="icon">🚀</span>
                  <span>{{ rec.projectName }}</span>
                </div>
                <div style="display: flex; gap: 6px; align-items: center;">
                  <a-tag v-if="rec.isTeacherInitiated" color="gold">👨‍🏫 导师指导</a-tag>
                  <a-tag color="warning">拟招 {{ rec.recruitCount }} 人</a-tag>
                </div>
              </div>

              <div style="margin: 8px 0;">
                <a-tag v-if="rec.isUnassigned || !rec.competitionTitle || rec.competitionTitle === '暂未参与'" color="blue">
                  🏷️ 暂未参与具体赛事
                </a-tag>
                <a-tag v-else color="purple">
                  🏆 {{ rec.competitionTitle }}
                </a-tag>
              </div>

              <h3 class="rec-title">{{ rec.title }}</h3>
              <p class="rec-desc">{{ rec.requirementDesc }}</p>

              <div class="rec-meta">
                <span v-if="rec.teacherName" style="margin-right: 12px; color: #7c3aed; font-weight: 600;">👨‍🏫 指导教师：{{ rec.teacherName }}</span>
                <span>截止时间：{{ formatDate(rec.deadline) }}</span>
              </div>

              <div class="rec-actions" style="display: flex; gap: 8px; margin-top: auto; padding-top: 12px;">
                <a-button type="primary" shape="round" @click="openApplyModal(rec)">
                  🙋 申请加入该队
                </a-button>
                <a-button shape="round" @click="$router.push(`/project/detail/${rec.projectId}`)">
                  团队详情
                </a-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <a-empty description="当前暂无进行中的组长招募" />
          </div>
        </a-tab-pane>

        <!-- 我的相关团队 -->
        <a-tab-pane key="myProjects" tab="⭐ 我的团队空间 (我加入与领导的队伍)" v-if="userStore.isLoggedIn">
          <div class="my-team-grid" v-if="myProjects.length > 0">
            <div v-for="p in myProjects" :key="p.id" class="my-team-card glass-card" @click="$router.push(`/project/detail/${p.id}`)">
              <div class="my-team-header">
                <span class="my-team-title">{{ p.projectName }}</span>
                <a-tag :color="p.isLeader ? 'gold' : 'blue'">
                  {{ p.isLeader ? '👑 我是组长' : '👤 我是组员' }}
                </a-tag>
              </div>
              <p class="my-team-desc">{{ p.projectDesc }}</p>
              <div class="my-team-footer">
                <span :style="{ color: p.isPrivate === 'YES' ? '#ef4444' : '#10b981', fontSize: '12px' }">
                  {{ p.isPrivate === 'YES' ? '🔒 私密团队' : '🌐 公开团队' }}
                </span>
                <a-button type="link" size="small">进入团队管理 ></a-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <a-empty description="您尚未加入或创建任何项目团队" />
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 组长发布招募需求对话框 -->
    <a-modal
      v-model:open="recruitModalVisible"
      title="组长发布招募令"
      ok-text="确认发布并广播至论坛"
      cancel-text="取消"
      @ok="handlePublishRecruit"
    >
      <a-form :model="recruitForm" layout="vertical">
        <a-form-item label="所属项目组" required>
          <a-select v-model:value="recruitForm.projectId" placeholder="选择您的项目组">
            <a-select-option v-for="p in myLeaderProjects" :key="p.id" :value="p.id">
              {{ p.projectName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="招募标题" required>
          <a-input v-model:value="recruitForm.title" placeholder="如：急招算法优化专家、前端开发与答辩PPT主讲人" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="拟招人数">
              <a-input-number v-model:value="recruitForm.recruitCount" :min="1" :max="5" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="招募截止时间">
              <a-date-picker v-model:value="recruitForm.deadline" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="技能要求与详细说明" required>
          <a-textarea v-model:value="recruitForm.requirementDesc" :rows="4" placeholder="请详细阐述对组员的技术栈偏好、责任心及每周实训投入时间要求" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 学生向组长申请加入团队对话框 -->
    <a-modal
      v-model:open="applyModalVisible"
      :title="`🙋 投递应聘申请：${selectedRecruit?.projectName || '项目团队'}`"
      ok-text="确认提交申请"
      cancel-text="取消"
      :confirm-loading="applying"
      @ok="handleApplyRecruit"
    >
      <div v-if="selectedRecruit" style="margin-bottom: 16px; padding: 12px; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0;">
        <div style="font-weight: 700; color: #0f172a; font-size: 15px;">{{ selectedRecruit.title }}</div>
        <div style="color: #64748b; font-size: 13px; margin-top: 4px;">招募需求：{{ selectedRecruit.requirementDesc }}</div>
        <div style="margin-top: 6px;">
          <a-tag color="warning">拟招 {{ selectedRecruit.recruitCount }} 人</a-tag>
          <span style="font-size: 12px; color: #94a3b8; margin-left: 8px;">提交后将直接发送给组长审批录取</span>
        </div>
      </div>
      <a-form layout="vertical">
        <a-form-item label="自荐说明 / 技能特长与联系方式" required>
          <a-textarea
            v-model:value="applyMessage"
            :rows="4"
            placeholder="请详细说明您的专业、技术储备、每周可投入时间及联系方式，帮助组长更快录取您..."
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 导师发布实训招募组对话框 -->
    <a-modal
      v-model:open="teacherCreateModalVisible"
      title="👨‍🏫 导师发布实训组与招募令"
      ok-text="确认创建并发布招募"
      cancel-text="取消"
      :confirm-loading="creatingTeacherProject"
      @ok="handleCreateTeacherProject"
      width="560px"
    >
      <div style="margin-bottom: 16px; padding: 12px; background: #f5f3ff; border: 1px solid #ddd6fe; border-radius: 8px; font-size: 13px; color: #6d28d9;">
        💡 导师发布的实训招募组将默认归属在【暂未参与具体赛事】中，展示于团队大厅招募墙，供全校优秀学生报名竞选组长或组员！
      </div>
      <a-form :model="teacherProjectForm" layout="vertical">
        <a-form-item label="参与竞赛" required>
          <a-input value="🏷️ 暂未参与（后续可由团队根据参赛意愿绑定具体竞赛）" disabled style="background: #f8fafc; color: #475569;" />
        </a-form-item>
        <a-form-item label="实训组 / 项目名称" required>
          <a-input v-model:value="teacherProjectForm.projectName" placeholder="例如：2026计算机视觉与具身智能实训组" />
        </a-form-item>
        <a-form-item label="招募令标题" required>
          <a-input v-model:value="teacherProjectForm.recruitTitle" placeholder="例如：急招算法优化专家、数据清洗与答辩骨干" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="拟招人数">
              <a-input-number v-model:value="teacherProjectForm.recruitCount" :min="1" :max="10" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="招募截止时间">
              <a-date-picker v-model:value="teacherProjectForm.deadline" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="团队简介与招募要求" required>
          <a-textarea v-model:value="teacherProjectForm.requirementDesc" :rows="4" placeholder="请详细阐述实训项目研究方向、技术栈偏好、对学生每周投入时间的要求及导师提供的指导资源..." />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('recruits')
const recruitList = ref([])
const myProjects = ref([])
const myLeaderProjects = ref([])

const recruitModalVisible = ref(false)
const recruitForm = reactive({
  projectId: '',
  title: '',
  recruitCount: 1,
  deadline: null,
  requirementDesc: ''
})

const applyModalVisible = ref(false)
const selectedRecruit = ref(null)
const applyMessage = ref('')
const applying = ref(false)

const openApplyModal = (rec) => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录平台后再申请入组')
    return
  }
  selectedRecruit.value = rec
  applyMessage.value = ''
  applyModalVisible.value = true
}

const handleApplyRecruit = async () => {
  if (!applyMessage.value.trim()) {
    message.warning('请填写自荐说明或联系方式')
    return
  }
  applying.value = true
  try {
    await portalApi.applyToProject({
      projectId: selectedRecruit.value.projectId,
      applyRole: 'MEMBER',
      message: applyMessage.value
    })
    message.success('🎉 申请已提交！已发送至该组长的个人工作台指挥空间，请静候组长审核录取通知。')
    applyModalVisible.value = false
  } catch (e) {
    message.error(e.message || '申请提交失败')
  } finally {
    applying.value = false
  }
}

const loadRecruits = async () => {
  try {
    const res = await portalApi.listRecruits()
    recruitList.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {}
}

const loadMyProjects = async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await portalApi.getMyProjects()
      const list = Array.isArray(res) ? res : (res?.data || [])
      let led = []
      let joined = []
      if (res && (res.ledProjects || res.joinedProjects)) {
        led = res.ledProjects || []
        joined = res.joinedProjects || []
      } else {
        led = list.filter(p => p.isLeader)
        joined = list.filter(p => !p.isLeader)
      }
      myProjects.value = [...led, ...joined]
      myLeaderProjects.value = led
    } catch (e) {}
  }
}

const openRecruitModal = () => {
  if (myLeaderProjects.value.length === 0) {
    message.warning('您当前名下暂无担任组长的项目')
    return
  }
  recruitForm.projectId = myLeaderProjects.value[0].id
  recruitModalVisible.value = true
}

const handlePublishRecruit = async () => {
  if (!recruitForm.projectId || !recruitForm.title || !recruitForm.requirementDesc) {
    message.warning('请填写必填项')
    return
  }
  try {
    await portalApi.publishRecruit({
      ...recruitForm,
      deadline: recruitForm.deadline ? dayjs(recruitForm.deadline).format('YYYY-MM-DD HH:mm:ss') : null
    })
    message.success('招募令发布成功，已自动同步到论坛社区！')
    recruitModalVisible.value = false
    loadRecruits()
  } catch (e) {}
}

const teacherCreateModalVisible = ref(false)
const creatingTeacherProject = ref(false)
const teacherProjectForm = reactive({
  projectName: '',
  recruitTitle: '',
  recruitCount: 3,
  deadline: null,
  requirementDesc: ''
})

const openTeacherCreateModal = () => {
  teacherProjectForm.projectName = ''
  teacherProjectForm.recruitTitle = ''
  teacherProjectForm.recruitCount = 3
  teacherProjectForm.deadline = null
  teacherProjectForm.requirementDesc = ''
  teacherCreateModalVisible.value = true
}

const handleCreateTeacherProject = async () => {
  if (!teacherProjectForm.projectName.trim() || !teacherProjectForm.recruitTitle.trim() || !teacherProjectForm.requirementDesc.trim()) {
    message.warning('请填写实训组名称、招募标题和团队简介说明')
    return
  }
  creatingTeacherProject.value = true
  try {
    // 1. 创建项目（后端强制设置为 UNASSIGNED 暂未参与）
    const projRes = await portalApi.createProject({
      projectName: teacherProjectForm.projectName.trim(),
      projectDesc: teacherProjectForm.requirementDesc.trim(),
      competitionId: 'UNASSIGNED',
      isPrivate: 'NO',
      maxMemberLimit: teacherProjectForm.recruitCount + 2
    })
    const projectId = typeof projRes === 'string' ? projRes : (projRes?.data || projRes)

    // 2. 联动发布导师招募令
    await portalApi.publishRecruit({
      projectId,
      title: teacherProjectForm.recruitTitle.trim(),
      recruitCount: teacherProjectForm.recruitCount,
      deadline: teacherProjectForm.deadline ? dayjs(teacherProjectForm.deadline).format('YYYY-MM-DD HH:mm:ss') : null,
      requirementDesc: teacherProjectForm.requirementDesc.trim()
    })

    message.success('🎉 导师实训招募组已成功创建并发布至大厅招募墙！')
    teacherCreateModalVisible.value = false
    await loadRecruits()
    await loadMyProjects()
  } catch (e) {
    message.error(e.message || '发布导师实训组失败')
  } finally {
    creatingTeacherProject.value = false
  }
}

const formatDate = (d) => {
  if (!d) return '长期有效'
  return dayjs(d).format('YYYY-MM-DD')
}

onMounted(async () => {
  await loadRecruits()
  await loadMyProjects()
  if (route.query.openRecruit === '1') {
    activeTab.value = 'recruits'
    if (route.query.projectId) {
      recruitForm.projectId = route.query.projectId
    } else if (myLeaderProjects.value.length > 0) {
      recruitForm.projectId = myLeaderProjects.value[0].id
    }
    recruitModalVisible.value = true
  }
  if (route.query.openTeacherCreate === '1') {
    openTeacherCreateModal()
  }
})
</script>

<style scoped>
.project-page {
  max-width: 1320px;
  margin: 32px auto;
  padding: 0 24px;
}

.page-header {
  padding: 28px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left h2 {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.header-left p {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.content-tabs {
  padding: 24px 32px;
}

.recruit-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.recruit-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.rec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.rec-team-name {
  font-size: 13px;
  color: #2563eb;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
}

.rec-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.rec-desc {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 16px;
}

.rec-actions {
  text-align: right;
  margin-top: auto;
}

.my-team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.my-team-card {
  padding: 20px;
  cursor: pointer;
}

.my-team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.my-team-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.my-team-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.my-team-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}
</style>
