<template>
  <div class="algorithm-detail-page" v-if="task">
    <!-- 头部信息 -->
    <div class="detail-header glass-card">
      <div class="header-top">
        <a-button type="link" @click="$router.push('/algorithm')">← 返回算法列表</a-button>
        <div class="header-badges">
          <a-tag :color="getDifficultyColor(task.difficulty)">{{ getDifficultyText(task.difficulty) }}</a-tag>
          <a-tag :color="task.isSubmitted ? 'success' : 'warning'">
            {{ task.isSubmitted ? '✅ 我已提交完成' : '⏳ 待提交' }}
          </a-tag>
        </div>
      </div>
      <h1 class="task-title">{{ task.title }}</h1>
      <div class="task-meta">
        <span>截止时间：{{ formatDate(task.deadline) }}</span>
        <span>·</span>
        <span>支持提交形式：{{ task.allowedSubmissionTypes }}</span>
        <span v-if="task.submission?.score != null" style="color: #10b981; font-weight: 700;">
          · 教师批改得分：{{ task.submission.score }} 分
        </span>
      </div>
    </div>

    <!-- 主要布局：左侧题目，右侧提交与组长督促面板 -->
    <div class="detail-layout">
      <!-- 左侧：题目详情与样例 -->
      <div class="problem-col glass-card">
        <div class="section-block">
          <h3>📖 题目与任务描述</h3>
          <div class="problem-desc">{{ task.contentDesc }}</div>
        </div>

        <div class="section-block" v-if="task.sampleInput">
          <h3>📥 输入样例 (Sample Input)</h3>
          <pre class="code-box">{{ task.sampleInput }}</pre>
        </div>

        <div class="section-block" v-if="task.sampleOutput">
          <h3>📤 输出样例 (Sample Output)</h3>
          <pre class="code-box">{{ task.sampleOutput }}</pre>
        </div>

        <!-- 如果已有教师评语 -->
        <div class="section-block" v-if="task.submission?.teacherFeedback">
          <h3>👨‍🏫 教师批改评语</h3>
          <div class="feedback-box">
            {{ task.submission.teacherFeedback }}
          </div>
        </div>
      </div>

      <!-- 右侧：提交面板 与 组长督促面板 -->
      <div class="action-col">
        <!-- 提交控制台 -->
        <div class="action-card glass-card">
          <div class="card-header-styled">
            <span class="icon">🚀</span>
            <h3>在线作业提交中心</h3>
          </div>

          <a-alert
            message="智能去重机制生效中"
            description="即使您进入了多个实训项目组，在此只需提交一次算法作业，所有团队的完成指标将自动同步生效！"
            type="info"
            show-icon
            style="margin-bottom: 16px;"
          />

          <a-tabs v-model:activeKey="submitMode">
            <!-- 模式一：在线代码 -->
            <a-tab-pane key="CODE_TEXT" tab="💻 在线代码文本">
              <a-form layout="vertical">
                <a-form-item label="编程语言">
                  <a-select v-model:value="codeForm.codeLanguage" style="width: 140px;">
                    <a-select-option value="Java">Java 17 / 21</a-select-option>
                    <a-select-option value="Python">Python 3.x</a-select-option>
                    <a-select-option value="C++">C++ 17</a-select-option>
                    <a-select-option value="Go">Go 1.22</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="源码内容">
                  <a-textarea
                    v-model:value="codeForm.codeContent"
                    :rows="12"
                    placeholder="在此键入或粘贴您的核心算法解决方案代码..."
                    style="font-family: monospace; font-size: 13px;"
                  />
                </a-form-item>
              </a-form>
            </a-tab-pane>

            <!-- 模式二：文件附件 -->
            <a-tab-pane key="FILE" tab="📁 附件文件">
              <div class="file-upload-sim">
                <a-form layout="vertical">
                  <a-form-item label="附件文件地址/文件ID">
                    <div style="display: flex; gap: 8px;">
                      <a-input v-model:value="codeForm.fileId" placeholder="输入文件链接或点击右侧上传" />
                      <a-upload
                        :show-upload-list="false"
                        :customRequest="handleAlgoFileUpload"
                        accept=".zip,.rar,.tar.gz,.pdf,.cpp,.java,.py"
                      >
                        <a-button :loading="fileUploading">
                          <template #icon><UploadOutlined /></template>
                          本地上传
                        </a-button>
                      </a-upload>
                    </div>
                  </a-form-item>
                </a-form>
                <div style="font-size: 12px; color: #94a3b8;">
                  支持打包上传测试数据集与源码工程包 (.zip, .pdf, .tar.gz, .java, .py, .cpp)
                </div>
              </div>
            </a-tab-pane>

            <!-- 模式三：Git 仓库链接 -->
            <a-tab-pane key="GIT_URL" tab="🔗 Git 仓库">
              <a-form layout="vertical">
                <a-form-item label="Git 仓库 URL">
                  <a-input v-model:value="codeForm.gitUrl" placeholder="如：https://github.com/myteam/algo-repo.git" />
                </a-form-item>
              </a-form>
            </a-tab-pane>
          </a-tabs>

          <a-button type="primary" block size="large" shape="round" @click="handleSubmitAlgorithm" style="margin-top: 16px;">
            {{ task.isSubmitted ? '更新并重新提交算法' : '立即提交算法作业' }}
          </a-button>
        </div>

        <!-- 组长监督督促面板 (如果当前用户是组长) -->
        <div class="supervision-card glass-card" v-if="myLeaderProjects.length > 0 && myLeaderProject">
          <div class="card-header-styled">
            <span class="icon">⏰</span>
            <h3>组长监督与一键催交</h3>
          </div>

          <!-- 组长跨比赛多小组切换器 -->
          <div v-if="myLeaderProjects.length > 1" style="margin-bottom: 14px;">
            <div style="font-size: 12px; font-weight: 700; color: #475569; margin-bottom: 4px;">
              👑 切换管辖小组 (您共负责 {{ myLeaderProjects.length }} 个比赛战队)：
            </div>
            <a-select :value="myLeaderProject.id" style="width: 100%" @change="onSwitchDetailLeaderProject">
              <a-select-option v-for="p in myLeaderProjects" :key="p.id" :value="p.id">
                【{{ p.competitionTitle }}】{{ p.projectName }}
              </a-select-option>
            </a-select>
          </div>

          <p class="super-desc">团队【{{ myLeaderProject.projectName }}】组员完成情况如下，组长应督促组员按期完成：</p>

          <div class="member-progress-list">
            <div v-for="m in teamProgress" :key="m.studentId" class="progress-row">
              <div class="p-info">
                <span class="p-name">{{ m.studentName }}</span>
                <a-tag :color="m.isSubmitted ? 'success' : 'error'">
                  {{ m.isSubmitted ? '已完成' : '待提交' }}
                </a-tag>
              </div>
              <div class="p-action">
                <a-button
                  v-if="!m.isSubmitted"
                  type="primary"
                  danger
                  size="small"
                  shape="round"
                  @click="handleUrge(m)"
                >
                  ⚡ 催交
                </a-button>
                <span v-else style="font-size: 12px; color: #10b981;">已按时交卷</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 教师批改打分面板 (如果是教师) -->
        <div class="teacher-grade-card glass-card" v-if="userStore.isTeacher">
          <div class="card-header-styled">
            <span class="icon">📝</span>
            <h3>学生作业批改与评定中心</h3>
          </div>
          <div v-if="studentSubmissions.length === 0" style="padding: 20px 0; text-align: center; color: #94a3b8;">
            暂无学生提交该作业
          </div>
          <div v-else>
            <a-form layout="vertical">
              <a-form-item label="选择批改的学生作业" required>
                <a-select v-model:value="selectedSubmissionId" placeholder="请选择学生" @change="onSelectSubmission">
                  <a-select-option v-for="sub in studentSubmissions" :key="sub.id" :value="sub.id">
                    {{ sub.studentName || '学生' }} ({{ sub.studentAccount }}) - {{ sub.score != null ? `已批改: ${sub.score}分` : '待批改' }}
                  </a-select-option>
                </a-select>
              </a-form-item>

              <div v-if="currentStudentSubmission" style="margin-bottom: 16px; padding: 12px; background: #f8fafc; border-radius: 8px;">
                <div style="font-weight: 600; margin-bottom: 6px;">
                  学生内容 ({{ currentStudentSubmission.submissionType }} / {{ currentStudentSubmission.codeLanguage || '默认' }}):
                </div>
                <pre v-if="currentStudentSubmission.codeContent" style="max-height: 200px; overflow-y: auto; background: #1e293b; color: #e2e8f0; padding: 8px; border-radius: 6px; font-size: 12px;">{{ currentStudentSubmission.codeContent }}</pre>
                <div v-if="currentStudentSubmission.fileId" style="margin-top: 6px;">
                  <a :href="currentStudentSubmission.fileId" target="_blank" style="color: #4f46e5;">📎 查看作业附件原件 ↗</a>
                </div>
                <div v-if="currentStudentSubmission.gitUrl" style="margin-top: 6px;">
                  <a :href="currentStudentSubmission.gitUrl" target="_blank" style="color: #4f46e5;">🔗 Git 仓库地址: {{ currentStudentSubmission.gitUrl }}</a>
                </div>
              </div>

              <a-form-item label="作业评分 (0-100)">
                <a-input-number v-model:value="gradeForm.score" :min="0" :max="100" style="width: 100%" />
              </a-form-item>
              <a-form-item label="批改评语">
                <a-textarea v-model:value="gradeForm.feedback" :rows="3" placeholder="请指出算法的时间效率与优化空间" />
              </a-form-item>
              <a-button type="primary" block shape="round" @click="handleGrade">
                提交批改打分
              </a-button>
            </a-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()

const taskId = route.params.id
const task = ref(null)
const submitMode = ref('CODE_TEXT')
const fileUploading = ref(false)

const codeForm = reactive({
  codeLanguage: 'Java',
  codeContent: '',
  fileId: '',
  gitUrl: ''
})

const myLeaderProjects = ref([])
const myLeaderProject = ref(null)
const teamProgress = ref([])
const studentSubmissions = ref([])
const selectedSubmissionId = ref(null)
const currentStudentSubmission = ref(null)

const gradeForm = reactive({
  score: 95,
  feedback: '解题思路严密，时空复杂度优秀，代码规范！'
})

const handleAlgoFileUpload = async ({ file }) => {
  fileUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const url = typeof res === 'string' ? res : (res?.data || res)
    codeForm.fileId = url
    message.success('文件上传成功！')
  } catch (e) {
    message.error('文件上传失败')
  } finally {
    fileUploading.value = false
  }
}

const loadTaskDetail = async () => {
  try {
    const rawList = await portalApi.getMyAlgorithmTasks()
    const list = Array.isArray(rawList) ? rawList : (rawList?.data || [])
    const found = (list || []).find(t => t.id === taskId)
    if (found) {
      task.value = found
      if (found.submission) {
        codeForm.codeLanguage = found.submission.codeLanguage || 'Java'
        codeForm.codeContent = found.submission.codeContent || ''
        codeForm.fileId = found.submission.fileId || ''
        codeForm.gitUrl = found.submission.gitUrl || ''
        if (found.submission.submissionType) {
          submitMode.value = found.submission.submissionType
        }
      }
    } else {
      // 缺省Mock详情
      task.value = {
        id: taskId,
        title: '算法练习题实战任务',
        contentDesc: '请使用动态规划或高效贪心算法实现题目所要求解，兼顾边界条件处理与复杂度约束。',
        difficulty: 'MEDIUM',
        allowedSubmissionTypes: 'CODE_TEXT,FILE,GIT_URL',
        sampleInput: '5\n1 3 5 2 9',
        sampleOutput: '4',
        isSubmitted: false
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const loadLeaderProgress = async () => {
  try {
    const teamsRes = await portalApi.getLeaderMyTeams()
    const list = Array.isArray(teamsRes) ? teamsRes : (teamsRes?.data || [])
    myLeaderProjects.value = list
    if (list.length > 0) {
      if (!myLeaderProject.value || !list.some(p => p.id === myLeaderProject.value.id)) {
        myLeaderProject.value = list[0]
      }
      const prog = await portalApi.getTeamTaskProgress({
        projectId: myLeaderProject.value.id,
        taskId
      })
      teamProgress.value = Array.isArray(prog) ? prog : (prog?.data || [])
    }
  } catch (e) {}
}

const onSwitchDetailLeaderProject = async (projId) => {
  const found = myLeaderProjects.value.find(p => p.id === projId)
  if (found) {
    myLeaderProject.value = found
    try {
      const prog = await portalApi.getTeamTaskProgress({
        projectId: found.id,
        taskId
      })
      teamProgress.value = Array.isArray(prog) ? prog : (prog?.data || [])
    } catch (e) {}
  }
}

const onSelectSubmission = (id) => {
  selectedSubmissionId.value = id
  const sub = studentSubmissions.value.find(s => s.id === id)
  if (sub) {
    currentStudentSubmission.value = sub
    gradeForm.score = sub.score != null ? sub.score : 90
    gradeForm.feedback = sub.teacherFeedback || '解题思路严密，时空复杂度优秀，代码规范！'
  }
}

const loadSubmissions = async () => {
  if (userStore.isTeacher) {
    try {
      const res = await portalApi.getMySubmissions(taskId)
      studentSubmissions.value = Array.isArray(res) ? res : (res?.data || [])
      if (studentSubmissions.value.length > 0) {
        if (!selectedSubmissionId.value || !studentSubmissions.value.find(s => s.id === selectedSubmissionId.value)) {
          onSelectSubmission(studentSubmissions.value[0].id)
        }
      }
    } catch (e) {}
  }
}

// 提交作业
const handleSubmitAlgorithm = async () => {
  try {
    const res = await portalApi.submitAlgorithm({
      taskId,
      submissionType: submitMode.value,
      codeLanguage: codeForm.codeLanguage,
      codeContent: codeForm.codeContent,
      fileId: codeForm.fileId,
      gitUrl: codeForm.gitUrl
    })
    message.success(res)
    loadTaskDetail()
    loadLeaderProgress()
  } catch (e) {}
}

// 催交组员
const handleUrge = async (member) => {
  try {
    const res = await portalApi.urgeMember({
      projectId: myLeaderProject.value.id,
      studentId: member.studentId,
      taskId,
      message: `组长提醒：算法任务【${task.value.title}】即将截止，请抓紧完成！`
    })
    message.success(res)
  } catch (e) {}
}

// 老师批改打分
const handleGrade = async () => {
  const targetId = userStore.isTeacher ? selectedSubmissionId.value : task.value?.submission?.id
  if (!targetId) {
    message.warning('请选择需要批改的学生作业记录')
    return
  }
  try {
    await portalApi.gradeAlgorithmSubmission({
      submissionId: targetId,
      score: gradeForm.score,
      feedback: gradeForm.feedback
    })
    message.success('批改完成！')
    loadTaskDetail()
    loadSubmissions()
  } catch (e) {}
}

const getDifficultyColor = (diff) => {
  if (diff === 'EASY') return 'green'
  if (diff === 'HARD') return 'red'
  return 'orange'
}

const getDifficultyText = (diff) => {
  if (diff === 'EASY') return '简单'
  if (diff === 'HARD') return '困难'
  return '中等'
}

const formatDate = (d) => {
  if (!d) return '长期有效'
  return dayjs(d).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  loadTaskDetail()
  loadLeaderProgress()
  loadSubmissions()
})
</script>

<style scoped>
.algorithm-detail-page {
  max-width: 1320px;
  margin: 32px auto;
  padding: 0 24px;
}

.detail-header {
  padding: 28px 32px;
  margin-bottom: 24px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-badges {
  display: flex;
  gap: 8px;
}

.task-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.task-meta {
  font-size: 13px;
  color: #64748b;
  display: flex;
  gap: 8px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 440px;
  gap: 24px;
  align-items: flex-start;
}

.problem-col {
  padding: 32px;
}

.section-block {
  margin-bottom: 28px;
}

.section-block h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 10px;
}

.problem-desc {
  font-size: 14.5px;
  line-height: 1.8;
  color: #334155;
}

.code-box {
  background: #0f172a;
  color: #38bdf8;
  padding: 16px 20px;
  border-radius: 10px;
  font-family: monospace;
  font-size: 13px;
  white-space: pre-wrap;
}

.feedback-box {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  padding: 16px;
  border-radius: 10px;
  font-size: 14px;
  color: #1e40af;
  line-height: 1.6;
}

.action-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-card, .supervision-card, .teacher-grade-card {
  padding: 24px;
}

.card-header-styled {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
}

.card-header-styled h3 {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.super-desc {
  font-size: 12.5px;
  color: #64748b;
  margin-bottom: 14px;
}

.member-progress-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.progress-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.p-name {
  font-weight: 600;
  font-size: 13.5px;
  margin-right: 8px;
}
</style>
