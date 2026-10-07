<template>
  <div class="competition-page">
    <!-- 头部横幅与操作 -->
    <div class="page-header glass-card">
      <div class="header-left">
        <h2>🏆 学科竞赛广场</h2>
        <p>探索前沿学科赛事，跨专业组队实训，挑战国家级与国际级荣誉</p>
      </div>
      <div class="header-right">
        <!-- 只有指导老师拥有发布竞赛权限 -->
        <a-button
          v-if="userStore.isTeacher"
          type="primary"
          size="large"
          shape="round"
          @click="openPublishModal"
        >
          ➕ 发布新竞赛 (教师特权)
        </a-button>
      </div>
    </div>

    <!-- 筛选过滤栏 -->
    <div class="filter-bar glass-card">
      <div class="filter-row">
        <div class="filter-item">
          <span class="label">竞赛级别：</span>
          <a-radio-group v-model:value="filters.level" button-style="solid" @change="loadCompetitions">
            <a-radio-button value="">全部</a-radio-button>
            <a-radio-button value="SCHOOL">校级</a-radio-button>
            <a-radio-button value="PROVINCE">省级</a-radio-button>
            <a-radio-button value="NATIONAL">国家级</a-radio-button>
            <a-radio-button value="INTERNATIONAL">国际级</a-radio-button>
          </a-radio-group>
        </div>

        <div class="filter-item">
          <span class="label">竞赛状态：</span>
          <a-radio-group v-model:value="filters.status" button-style="solid" @change="loadCompetitions">
            <a-radio-button value="">全部</a-radio-button>
            <a-radio-button value="ENROLLING">报名中</a-radio-button>
            <a-radio-button value="ONGOING">进行中</a-radio-button>
            <a-radio-button value="FINISHED">已结束</a-radio-button>
          </a-radio-group>
        </div>

        <div class="filter-search">
          <a-input-search
            v-model:value="filters.keyword"
            placeholder="搜索竞赛名称、承办方..."
            enter-button="搜索"
            @search="loadCompetitions"
            style="width: 280px;"
          />
        </div>
      </div>
    </div>

    <!-- 竞赛卡片网格列表 -->
    <div class="competition-grid" v-if="competitions.length > 0">
      <div
        v-for="comp in competitions"
        :key="comp.id"
        class="comp-card glass-card"
        @click="$router.push(`/competition/detail/${comp.id}`)"
      >
        <div class="card-banner" :class="'banner-' + comp.competitionLevel.toLowerCase()">
          <span class="level-badge">{{ getLevelText(comp.competitionLevel) }}</span>
          <a-tag :color="getStatusTag(comp).color">{{ getStatusTag(comp).text }}</a-tag>
        </div>

        <div class="card-body">
          <h3 class="comp-card-title">{{ comp.title }}</h3>
          <div class="comp-organizer">主办：{{ comp.organizer || '教务处/学院' }}</div>

          <div class="comp-specs">
            <div class="spec-row">
              <span class="spec-icon">👥</span>
              <span>
                {{ comp.maxMembers === 1 ? '单人单项赛 (无需组队)' : `团队赛 (${comp.minMembers}-${comp.maxMembers}人/组)` }}
              </span>
            </div>
            <div class="spec-row">
              <span class="spec-icon">📅</span>
              <span>报名截止：{{ formatDate(comp.signupEndTime) }}</span>
            </div>
          </div>

          <div class="comp-card-footer">
            <div class="stats-mini">
              <span>👀 {{ comp.viewCount || 0 }} 围观</span>
              <span>⭐ {{ comp.followCount || 0 }} 关注</span>
            </div>
            <a-space>
              <a-button
                v-if="userStore.isTeacher"
                size="small"
                shape="round"
                type="dashed"
                @click.stop="openEditModal(comp)"
              >
                ✏️ 修改规程
              </a-button>
              <a-button
                :type="isCompEnded(comp) ? 'default' : (isSignupClosed(comp) ? 'dashed' : 'primary')"
                size="small"
                shape="round"
                :danger="isCompEnded(comp)"
              >
                {{ isCompEnded(comp) ? '🏁 赛事已结束' : (isSignupClosed(comp) ? '⏳ 报名已截止' : '立即参赛 >') }}
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state glass-card">
      <a-empty description="暂无符合条件的竞赛赛事" />
    </div>

    <!-- 老师发布/修改竞赛对话框 -->
    <a-modal
      v-model:open="publishModalVisible"
      :title="publishModalTitle"
      width="680px"
      :ok-text="publishForm.id ? '确认修改' : '确认发布并广播至论坛'"
      cancel-text="取消"
      @ok="handlePublishCompetition"
    >
      <a-form :model="publishForm" layout="vertical" style="margin-top: 16px;">
        <a-form-item label="竞赛名称" required>
          <a-input v-model:value="publishForm.title" placeholder="如：第十一届全国大学生算法设计与编程实训大赛" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="竞赛级别" required>
              <a-select v-model:value="publishForm.competitionLevel">
                <a-select-option value="SCHOOL">校级竞赛</a-select-option>
                <a-select-option value="PROVINCE">省级竞赛</a-select-option>
                <a-select-option value="NATIONAL">国家级竞赛</a-select-option>
                <a-select-option value="INTERNATIONAL">国际级赛事</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="主办单位/承办方" required>
              <a-input v-model:value="publishForm.organizer" placeholder="如：计算机学院学术委员会" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="最少参赛人数" required>
              <a-input-number v-model:value="publishForm.minMembers" :min="1" :max="10" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="最多参赛人数（1表示单人赛，大于1须建组）" required>
              <a-input-number v-model:value="publishForm.maxMembers" :min="1" :max="10" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="报名开始时间">
              <a-date-picker v-model:value="publishForm.signupStartTime" show-time style="width: 100%" placeholder="选择报名开始时间" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="报名截止时间">
              <a-date-picker v-model:value="publishForm.signupEndTime" show-time style="width: 100%" placeholder="选择报名截止时间" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="竞赛开始时间">
              <a-date-picker v-model:value="publishForm.competitionStartTime" show-time style="width: 100%" placeholder="选择竞赛开始时间" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="竞赛结束时间">
              <a-date-picker v-model:value="publishForm.competitionEndTime" show-time style="width: 100%" placeholder="选择竞赛结束时间" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="竞赛要求与参赛规程" required>
          <a-textarea v-model:value="publishForm.requirements" :rows="4" placeholder="请输入竞赛要求、评审要点与团队招募规范" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const filters = reactive({
  keyword: '',
  level: '',
  status: ''
})

const competitions = ref([])
const publishModalVisible = ref(false)
const publishModalTitle = ref('发布全新学科竞赛')

const publishForm = reactive({
  id: null,
  title: '',
  competitionLevel: 'SCHOOL',
  organizer: '',
  category: '计算机与算法',
  minMembers: 1,
  maxMembers: 5,
  signupStartTime: null,
  signupEndTime: null,
  competitionStartTime: null,
  competitionEndTime: null,
  requirements: '',
  status: 'SIGNUP'
})

const loadCompetitions = async () => {
  try {
    const res = await portalApi.getCompetitions({
      keyword: filters.keyword,
      level: filters.level,
      status: filters.status,
      current: 1,
      size: 50
    })
    competitions.value = res.records || []
  } catch (e) {
    console.error(e)
  }
}

const openPublishModal = () => {
  publishModalTitle.value = '发布全新学科竞赛'
  publishForm.id = null
  publishForm.title = ''
  publishForm.competitionLevel = 'SCHOOL'
  publishForm.organizer = ''
  publishForm.category = '计算机与算法'
  publishForm.minMembers = 1
  publishForm.maxMembers = 5
  publishForm.signupStartTime = null
  publishForm.signupEndTime = null
  publishForm.competitionStartTime = null
  publishForm.competitionEndTime = null
  publishForm.requirements = ''
  publishForm.status = 'SIGNUP'
  publishModalVisible.value = true
}

const openEditModal = (comp) => {
  publishModalTitle.value = '修改竞赛规程与时间'
  publishForm.id = comp.id
  publishForm.title = comp.title
  publishForm.competitionLevel = comp.competitionLevel || 'SCHOOL'
  publishForm.organizer = comp.organizer || ''
  publishForm.category = comp.category || '计算机与算法'
  publishForm.minMembers = comp.minMembers || 1
  publishForm.maxMembers = comp.maxMembers || 5
  publishForm.signupStartTime = comp.signupStartTime ? dayjs(comp.signupStartTime) : null
  publishForm.signupEndTime = comp.signupEndTime ? dayjs(comp.signupEndTime) : null
  publishForm.competitionStartTime = comp.competitionStartTime ? dayjs(comp.competitionStartTime) : null
  publishForm.competitionEndTime = comp.competitionEndTime ? dayjs(comp.competitionEndTime) : null
  publishForm.requirements = comp.requirements || ''
  publishForm.status = comp.status || 'SIGNUP'
  publishModalVisible.value = true
}

const handlePublishCompetition = async () => {
  if (!publishForm.title || !publishForm.requirements) {
    message.warning('请填写竞赛名称及参赛要求')
    return
  }
  try {
    const payload = {
      ...publishForm,
      signupStartTime: publishForm.signupStartTime ? dayjs(publishForm.signupStartTime).format('YYYY-MM-DD HH:mm:ss') : null,
      signupEndTime: publishForm.signupEndTime ? dayjs(publishForm.signupEndTime).format('YYYY-MM-DD HH:mm:ss') : null,
      competitionStartTime: publishForm.competitionStartTime ? dayjs(publishForm.competitionStartTime).format('YYYY-MM-DD HH:mm:ss') : null,
      competitionEndTime: publishForm.competitionEndTime ? dayjs(publishForm.competitionEndTime).format('YYYY-MM-DD HH:mm:ss') : null
    }
    if (publishForm.id) {
      await portalApi.editCompetition(payload)
      message.success('竞赛规程与时间修改成功！')
    } else {
      await portalApi.publishCompetition(payload)
      message.success('竞赛发布成功，已自动同步置顶广播至论坛社区！')
    }
    publishModalVisible.value = false
    loadCompetitions()
  } catch (e) {
    console.error(e)
  }
}

const getLevelText = (lvl) => {
  const map = {
    SCHOOL: '校级赛事',
    PROVINCE: '省级选拔赛',
    NATIONAL: '国家级大赛',
    INTERNATIONAL: '国际级挑战赛'
  }
  return map[lvl] || lvl
}

const isCompEnded = (comp) => {
  if (!comp) return false
  if (comp.isEnded) return true
  if (comp.status === 'FINISHED') return true
  if (comp.competitionEndTime && dayjs().isAfter(dayjs(comp.competitionEndTime))) return true
  return false
}

const isSignupClosed = (comp) => {
  if (!comp) return false
  if (isCompEnded(comp)) return true
  if (comp.isSignupClosed) return true
  if (comp.status === 'ONGOING' || comp.status === 'REVIEWING' || comp.status === 'FINISHED') return true
  if (comp.signupEndTime && dayjs().isAfter(dayjs(comp.signupEndTime))) return true
  return false
}

const getStatusTag = (comp) => {
  if (!comp) return { text: '🔥 报名中', color: 'processing' }
  const status = typeof comp === 'string' ? comp : comp.status
  if (typeof comp === 'object') {
    if (isCompEnded(comp)) {
      return { text: '已结束', color: 'default' }
    }
    if (isSignupClosed(comp) && status !== 'FINISHED') {
      return { text: '进行中/评审中', color: 'warning' }
    }
  }
  if (status === 'ENROLLING' || status === 'SIGNUP') return { text: '🔥 报名中', color: 'processing' }
  if (status === 'ONGOING') return { text: '进行中', color: 'success' }
  if (status === 'REVIEWING') return { text: '作品评审中', color: 'warning' }
  if (status === 'FINISHED') return { text: '已结束', color: 'default' }
  return { text: '🔥 报名中', color: 'processing' }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '即日起至赛事结束'
  return dayjs(dateStr).format('YYYY-MM-DD')
}

onMounted(() => {
  loadCompetitions()
})
</script>

<style scoped>
.competition-page {
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
  letter-spacing: -0.5px;
}

.header-left p {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}

.filter-bar {
  padding: 18px 24px;
  margin-bottom: 28px;
}

.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-item .label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.competition-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
}

.comp-card {
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.card-banner {
  height: 90px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
}

.banner-school {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.banner-province {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
}

.banner-national {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
}

.banner-international {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
}

.level-badge {
  font-size: 13px;
  font-weight: 800;
  color: #1e293b;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 10px;
  border-radius: 8px;
}

.card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.comp-card-title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.comp-organizer {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 16px;
}

.comp-specs {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
  margin-bottom: 20px;
}

.spec-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #334155;
  font-weight: 500;
}

.comp-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.stats-mini {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #94a3b8;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}
</style>
