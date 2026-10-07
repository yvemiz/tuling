<template>
  <div class="ranking-container">
    <!-- 顶部 Banner -->
    <div class="ranking-header">
      <div class="header-badge">
        <span class="badge-dot"></span>
        <span>EXCELLENCE LEADERBOARD</span>
      </div>
      <h1 class="header-title">🏆 竞赛团队风云榜</h1>
      <p class="header-desc">
        结合 AI 智能诊断（算法完成度 50% + 竞赛参与度 50%）与指导导师综合打分，沉淀各项目组硬核战斗力与竞技风采。
      </p>

      <!-- 快捷周期与竞赛筛选栏 -->
      <div class="filter-card">
        <div class="filter-group">
          <label class="filter-label">评选周期：</label>
          <a-radio-group v-model:value="filterPeriod" button-style="solid" @change="loadRankings">
            <a-radio-button value="2026_SPRING">2026 春季联赛</a-radio-button>
            <a-radio-button value="2026_AUTUMN">2026 秋季赛</a-radio-button>
            <a-radio-button value="2025_FULL">2025 年度总决赛</a-radio-button>
            <a-radio-button value="">全部周期</a-radio-button>
          </a-radio-group>
        </div>

        <div class="filter-group right-controls">
          <a-select
            v-model:value="filterCompetitionId"
            placeholder="筛选指定竞赛..."
            allow-clear
            style="width: 220px"
            @change="loadRankings"
          >
            <a-select-option v-for="c in competitions" :key="c.id" :value="c.id">
              {{ c.title }}
            </a-select-option>
          </a-select>

          <a-button type="primary" class="gradient-btn" @click="loadRankings">
            <template #icon><ReloadOutlined /></template>
            刷新榜单
          </a-button>

          <!-- 教师专属操作 -->
          <a-button
            v-if="userStore.isTeacher"
            type="primary"
            style="background: #10b981; border-color: #10b981;"
            @click="openTeacherEvalModal"
          >
            <template #icon><TrophyOutlined /></template>
            导师评分与榜单发布
          </a-button>
        </div>
      </div>
    </div>

    <a-spin :spinning="loading">
      <!-- 领奖台 (Top 3 Podium) -->
      <div v-if="rankings.length >= 3" class="podium-section">
        <!-- 亚军 (Rank 2) -->
        <div class="podium-card silver" @click="goToProject(rankings[1].projectId)">
          <div class="medal-icon">🥈</div>
          <div class="podium-rank">TOP 2 · 亚军</div>
          <div class="team-name">{{ rankings[1].projectName }}</div>
          <div class="team-leader">组长: {{ rankings[1].leaderName }}</div>
          <div class="score-display">
            <span class="score-val">{{ rankings[1].finalScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <div class="score-breakdown">
            <span>AI评估: {{ rankings[1].aiScore || '--' }}</span>
            <span>导师评定: {{ rankings[1].teacherScore || '--' }}</span>
          </div>
          <div class="podium-pillar pillar-2">2</div>
        </div>

        <!-- 冠军 (Rank 1) -->
        <div class="podium-card gold" @click="goToProject(rankings[0].projectId)">
          <div class="crown">👑</div>
          <div class="medal-icon">🥇</div>
          <div class="podium-rank">TOP 1 · 冠军卓越战队</div>
          <div class="team-name">{{ rankings[0].projectName }}</div>
          <div class="team-leader">组长: {{ rankings[0].leaderName }}</div>
          <div class="score-display gold-score">
            <span class="score-val">{{ rankings[0].finalScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <div class="score-breakdown">
            <span>AI评估: {{ rankings[0].aiScore || '--' }}</span>
            <span>导师评定: {{ rankings[0].teacherScore || '--' }}</span>
          </div>
          <div class="podium-pillar pillar-1">1</div>
        </div>

        <!-- 季军 (Rank 3) -->
        <div class="podium-card bronze" @click="goToProject(rankings[2].projectId)">
          <div class="medal-icon">🥉</div>
          <div class="podium-rank">TOP 3 · 季军</div>
          <div class="team-name">{{ rankings[2].projectName }}</div>
          <div class="team-leader">组长: {{ rankings[2].leaderName }}</div>
          <div class="score-display">
            <span class="score-val">{{ rankings[2].finalScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <div class="score-breakdown">
            <span>AI评估: {{ rankings[2].aiScore || '--' }}</span>
            <span>导师评定: {{ rankings[2].teacherScore || '--' }}</span>
          </div>
          <div class="podium-pillar pillar-3">3</div>
        </div>
      </div>

      <!-- 排行榜完整数据表格 -->
      <div class="rank-table-card">
        <div class="table-header-bar">
          <div class="title-with-icon">
            <TrophyOutlined style="color: #f59e0b; font-size: 20px" />
            <span class="title-text">完整排名列表</span>
            <span class="sub-text">（共计 {{ rankings.length }} 支上榜战队）</span>
          </div>
        </div>

        <a-table
          :data-source="rankings"
          :columns="columns"
          :pagination="{ pageSize: 10 }"
          row-key="id"
          class="custom-rank-table"
        >
          <!-- 排名徽章 -->
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'rank'">
              <div class="rank-badge" :class="'rank-' + (record.rankPosition || index + 1)">
                <span v-if="(record.rankPosition || index + 1) === 1">🥇 1</span>
                <span v-else-if="(record.rankPosition || index + 1) === 2">🥈 2</span>
                <span v-else-if="(record.rankPosition || index + 1) === 3">🥉 3</span>
                <span v-else>{{ record.rankPosition || index + 1 }}</span>
              </div>
            </template>

            <!-- 战队与项目名 -->
            <template v-else-if="column.key === 'projectName'">
              <div class="project-cell">
                <a @click="goToProject(record.projectId)" class="project-name-link">
                  {{ record.projectName }}
                </a>
                <div class="project-desc-mini" v-if="record.projectDesc">
                  {{ record.projectDesc }}
                </div>
              </div>
            </template>

            <!-- AI评分 -->
            <template v-else-if="column.key === 'aiScore'">
              <div class="score-item">
                <a-tag color="blue">
                  🤖 {{ record.aiScore != null ? record.aiScore + '分' : '待评估' }}
                </a-tag>
              </div>
            </template>

            <!-- 导师评分 -->
            <template v-else-if="column.key === 'teacherScore'">
              <div class="score-item">
                <a-tag color="purple">
                  👨‍🏫 {{ record.teacherScore != null ? record.teacherScore + '分' : '待评分' }}
                </a-tag>
              </div>
            </template>

            <!-- 综合总分 -->
            <template v-else-if="column.key === 'finalScore'">
              <div class="final-score-badge">
                {{ record.finalScore != null ? record.finalScore : '--' }}
              </div>
            </template>

            <!-- 发布状态 -->
            <template v-else-if="column.key === 'isPublished'">
              <a-badge
                :status="record.isPublished === 'YES' ? 'success' : 'warning'"
                :text="record.isPublished === 'YES' ? '已正式公示' : '草稿评审中'"
              />
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="goToProject(record.projectId)">
                  查看团队
                </a-button>
                <a-button
                  v-if="userStore.isTeacher && record.isPublished !== 'YES'"
                  type="link"
                  size="small"
                  style="color: #10b981"
                  @click="handlePublishSingle(record)"
                >
                  正式发布
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-spin>

    <!-- 教师打分与发布弹窗 -->
    <a-modal
      v-model:open="teacherModalVisible"
      title="👨‍🏫 导师综合打分与榜单发布"
      @ok="handleTeacherSubmit"
      :confirmLoading="evaluating"
      width="600px"
    >
      <a-form layout="vertical" :model="evalForm">
        <a-alert
          type="info"
          show-icon
          message="评分计算规则"
          description="最终得分 = AI模型诊断评分 (50%) + 导师综合评审分 (50%)。打分后可直接对外公示进入官方排行榜。"
          style="margin-bottom: 20px"
        />

        <a-form-item label="选择竞赛" required>
          <a-select
            v-model:value="evalForm.competitionId"
            placeholder="请选择对应的竞赛"
            @change="handleModalCompetitionChange"
          >
            <a-select-option v-for="c in competitions" :key="c.id" :value="c.id">
              {{ c.title }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="选择参评项目组" required>
          <a-select
            v-model:value="evalForm.projectId"
            placeholder="请选择参评的项目组"
            :loading="projectsLoading"
          >
            <a-select-option v-for="p in competitionProjects" :key="p.id" :value="p.id">
              {{ p.projectName }}（组长: {{ p.leaderName }}）
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="评选周期" required>
          <a-select v-model:value="evalForm.rankingPeriod">
            <a-select-option value="2026_SPRING">2026 春季联赛</a-select-option>
            <a-select-option value="2026_AUTUMN">2026 秋季赛</a-select-option>
            <a-select-option value="2025_FULL">2025 年度总决赛</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="导师评审打分 (0 - 100分)" required>
          <a-slider v-model:value="evalForm.teacherScore" :min="0" :max="100" />
          <a-input-number
            v-model:value="evalForm.teacherScore"
            :min="0"
            :max="100"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="导师评审寄语 / 评价备注">
          <a-textarea
            v-model:value="evalForm.teacherComments"
            placeholder="对团队项目架构、竞赛表现与答辩表现的综合评语..."
            :rows="3"
          />
        </a-form-item>

        <a-form-item>
          <a-checkbox v-model:checked="autoPublish">
            评分保存后，立即更新并发榜公示
          </a-checkbox>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  TrophyOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/store/user'
import portalApi from '@/api/portalApi'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const filterPeriod = ref('2026_SPRING')
const filterCompetitionId = ref(undefined)
const competitions = ref([])
const rankings = ref([])

// 导师打分弹窗
const teacherModalVisible = ref(false)
const evaluating = ref(false)
const projectsLoading = ref(false)
const competitionProjects = ref([])
const autoPublish = ref(true)

const evalForm = ref({
  competitionId: undefined,
  projectId: undefined,
  rankingPeriod: '2026_SPRING',
  teacherScore: 85,
  teacherComments: ''
})

const columns = [
  { title: '名次', key: 'rank', width: 90, align: 'center' },
  { title: '参评项目团队', key: 'projectName' },
  { title: '组长', dataIndex: 'leaderName', key: 'leaderName', width: 120 },
  { title: 'AI模型综合评分', key: 'aiScore', width: 150 },
  { title: '导师评审评分', key: 'teacherScore', width: 150 },
  { title: '综合总分', key: 'finalScore', width: 120, align: 'center' },
  { title: '公示状态', key: 'isPublished', width: 130, align: 'center' },
  { title: '操作', key: 'action', width: 140, align: 'center' }
]

const loadCompetitions = async () => {
  try {
    const res = await portalApi.getCompetitions({ current: 1, size: 50 })
    competitions.value = res.records || res.data?.records || (Array.isArray(res) ? res : [])
  } catch (e) {
    console.error(e)
  }
}

const loadRankings = async () => {
  loading.value = true
  try {
    const res = await portalApi.getRankings({
      period: filterPeriod.value || undefined,
      competitionId: filterCompetitionId.value || undefined
    })
    rankings.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    message.error('加载排行榜失败')
  } finally {
    loading.value = false
  }
}

const goToProject = (projectId) => {
  if (projectId) {
    router.push(`/project/${projectId}`)
  }
}

const openTeacherEvalModal = () => {
  teacherModalVisible.value = true
  if (competitions.value.length > 0 && !evalForm.value.competitionId) {
    evalForm.value.competitionId = competitions.value[0].id
    handleModalCompetitionChange(evalForm.value.competitionId)
  }
}

const handleModalCompetitionChange = async (compId) => {
  if (!compId) return
  projectsLoading.value = true
  try {
    const res = await portalApi.getCompetitionProjects(compId)
    competitionProjects.value = Array.isArray(res) ? res : (res?.data || [])
    if (competitionProjects.value.length > 0) {
      evalForm.value.projectId = competitionProjects.value[0].id
    } else {
      evalForm.value.projectId = undefined
    }
  } catch (e) {
    message.error('获取该竞赛项目组失败')
  } finally {
    projectsLoading.value = false
  }
}

const handleTeacherSubmit = async () => {
  if (!evalForm.value.projectId) {
    message.warning('请选择需要评分的项目组')
    return
  }
  evaluating.value = true
  try {
    const res = await portalApi.teacherEvaluateTeam(evalForm.value)
    const rankingId = typeof res === 'string' ? res : (res?.data || res?.id)
    if (autoPublish.value && rankingId) {
      await portalApi.publishRanking(rankingId)
      message.success('导师评分成功，并已正式公示上榜！')
    } else {
      message.success('导师评分已成功保存为草稿')
    }
    teacherModalVisible.value = false
    loadRankings()
  } catch (e) {
    message.error(e.message || '评分提交失败')
  } finally {
    evaluating.value = false
  }
}

const handlePublishSingle = async (record) => {
  try {
    await portalApi.publishRanking(record.id)
    message.success('该战队排名已正式公示！')
    loadRankings()
  } catch (e) {
    message.error('发布失败')
  }
}

onMounted(() => {
  loadCompetitions()
  loadRankings()
})
</script>

<style scoped>
.ranking-container {
  max-width: 1240px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.ranking-header {
  text-align: center;
  padding: 40px 24px 30px;
  background: radial-gradient(circle at 50% 20%, rgba(99, 102, 241, 0.12), transparent 70%);
  border-radius: 20px;
  margin-bottom: 30px;
}

.header-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(245, 158, 11, 0.12);
  border: 1px solid rgba(245, 158, 11, 0.25);
  padding: 4px 14px;
  border-radius: 20px;
  color: #f59e0b;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  margin-bottom: 14px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: #f59e0b;
  border-radius: 50%;
  box-shadow: 0 0 8px #f59e0b;
}

.header-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 12px;
}

.header-desc {
  max-width: 700px;
  margin: 0 auto 28px;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

.filter-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  background: #ffffff;
  padding: 16px 24px;
  border-radius: 14px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.right-controls {
  display: flex;
  gap: 12px;
}

/* 领奖台 (Top 3) */
.podium-section {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 24px;
  margin: 30px auto 40px;
  max-width: 900px;
}

.podium-card {
  flex: 1;
  background: #ffffff;
  border-radius: 20px;
  padding: 24px 20px 0;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

.podium-card:hover {
  transform: translateY(-8px);
}

.podium-card.gold {
  order: 2;
  border: 2px solid rgba(245, 158, 11, 0.3);
  box-shadow: 0 14px 35px rgba(245, 158, 11, 0.15);
}

.podium-card.silver {
  order: 1;
}

.podium-card.bronze {
  order: 3;
}

.crown {
  font-size: 26px;
  margin-bottom: -10px;
  animation: float 2s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.medal-icon {
  font-size: 38px;
  margin-bottom: 6px;
}

.podium-rank {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.gold .podium-rank { color: #f59e0b; }
.silver .podium-rank { color: #94a3b8; }
.bronze .podium-rank { color: #d97706; }

.team-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.team-leader {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 12px;
}

.score-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 2px;
  color: #4f46e5;
  margin-bottom: 8px;
}

.gold-score {
  color: #f59e0b;
}

.score-val {
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
}

.score-unit {
  font-size: 14px;
  font-weight: 600;
}

.score-breakdown {
  display: flex;
  justify-content: space-around;
  font-size: 11px;
  color: #94a3b8;
  margin-bottom: 16px;
}

.podium-pillar {
  width: 100%;
  font-size: 24px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px 12px 0 0;
}

.pillar-1 {
  height: 90px;
  background: linear-gradient(180deg, #f59e0b, #d97706);
}

.pillar-2 {
  height: 65px;
  background: linear-gradient(180deg, #94a3b8, #64748b);
}

.pillar-3 {
  height: 50px;
  background: linear-gradient(180deg, #b45309, #78350f);
}

/* 完整列表表格卡片 */
.rank-table-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.table-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.sub-text {
  font-size: 13px;
  color: #94a3b8;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-weight: 700;
  font-size: 14px;
  color: #64748b;
  background: #f1f5f9;
}

.rank-1, .rank-2, .rank-3 {
  width: 50px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 800;
}

.rank-1 {
  background: rgba(245, 158, 11, 0.15);
  color: #d97706;
}

.rank-2 {
  background: rgba(148, 163, 184, 0.2);
  color: #475569;
}

.rank-3 {
  background: rgba(180, 83, 9, 0.15);
  color: #b45309;
}

.project-cell {
  display: flex;
  flex-direction: column;
}

.project-name-link {
  font-weight: 600;
  color: #1e293b;
  transition: color 0.2s;
}

.project-name-link:hover {
  color: #4f46e5;
}

.project-desc-mini {
  font-size: 12px;
  color: #94a3b8;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.final-score-badge {
  font-size: 18px;
  font-weight: 800;
  color: #4f46e5;
}

.gradient-btn {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
}
</style>
