<template>
  <div class="home-container">
    <!-- Hero 视觉 Banner -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-left">
          <div class="hero-tag">
            <span class="pulse-dot"></span> {{ portalStore.siteInfo?.subTitle || '全新学科竞赛与算法实训协作门户' }}
          </div>
          <h1 class="hero-title">
            <span v-if="portalStore.siteInfo?.bannerTitle">{{ portalStore.siteInfo.bannerTitle }}</span>
            <span v-else>释放创新潜能，<br /><span class="gradient-text">协同组队·算法冲榜·AI精准赋能</span></span>
          </h1>
          <p class="hero-desc">
            {{ portalStore.siteInfo?.bannerDesc || '汇聚全校学科竞赛规程、跨专业组长智能招募、算法作业多维批改、社区智能审核与团队AI诊断评估。' }}
          </p>

          <div class="hero-actions">
            <a-button type="primary" size="large" shape="round" class="action-btn-main" @click="$router.push('/competition')">
              🏆 探索竞赛广场
            </a-button>
            <a-button size="large" shape="round" class="action-btn-sub" @click="$router.push('/project')">
              👥 查看团队与招募
            </a-button>
          </div>

          <!-- 核心统计指标 -->
          <div class="hero-stats">
            <div class="stat-card">
              <span class="stat-num">{{ stats.competitionCount }}</span>
              <span class="stat-label">进行中赛事</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-card">
              <span class="stat-num">{{ stats.projectCount }}</span>
              <span class="stat-label">活跃项目组</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-card">
              <span class="stat-num">{{ stats.submissionCount }}</span>
              <span class="stat-label">算法提交数</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-card">
              <span class="stat-num">98.5%</span>
              <span class="stat-label">AI诊断达标率</span>
            </div>
          </div>
        </div>

        <div class="hero-right">
          <div class="hero-dashboard-card glass-panel">
            <div class="card-header">
              <span class="card-title">🤖 平台 AI 诊断引擎监控</span>
              <a-tag color="success">AI 运行正常</a-tag>
            </div>
            <div class="engine-features">
              <div class="feature-row">
                <span class="f-icon">🛡️</span>
                <div class="f-info">
                  <div class="f-title">论坛全量文本实时 AI 审查</div>
                  <div class="f-desc">敏感词扫描 + 风险智能分流（存疑转组长/老师联审）</div>
                </div>
              </div>
              <div class="feature-row">
                <span class="f-icon">📊</span>
                <div class="f-info">
                  <div class="f-title">团队两维AI评估模型</div>
                  <div class="f-desc">自动融合算法完成度(50%)与竞赛实战活跃度(50%)</div>
                </div>
              </div>
              <div class="feature-row">
                <span class="f-icon">🔒</span>
                <div class="f-info">
                  <div class="f-title">多级隐私与权限隔离</div>
                  <div class="f-desc">组长私密设限 + 成员表现评分（仅老师与组长可见）</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 运营轮播图横幅 -->
    <div v-if="portalStore.banners && portalStore.banners.length > 0" class="portal-banner-wrap glass-card" style="margin: 0 auto 32px; max-width: 1400px; padding: 0 24px;">
      <a-carousel autoplay effect="fade">
        <div v-for="b in portalStore.banners" :key="b.id" class="banner-slide" @click="handleBannerClick(b)" style="cursor: pointer; position: relative; height: 260px; border-radius: 16px; overflow: hidden;">
          <img :src="b.imgUrl" :alt="b.title" style="width: 100%; height: 100%; object-fit: cover; filter: brightness(0.85);" />
          <div class="banner-overlay" style="position: absolute; bottom: 0; left: 0; right: 0; padding: 30px 40px; background: linear-gradient(to top, rgba(15,23,42,0.9), rgba(15,23,42,0.4), transparent); color: #fff;">
            <div style="display: flex; align-items: center; justify-content: space-between;">
              <div>
                <h3 style="color: #fff; font-size: 22px; font-weight: 700; margin-bottom: 6px;">{{ b.title }}</h3>
                <p style="color: #cbd5e1; font-size: 14px; margin-bottom: 0;">{{ b.subTitle }}</p>
              </div>
              <a-button type="primary" shape="round" style="background: rgba(255,255,255,0.2); border-color: rgba(255,255,255,0.4); backdrop-filter: blur(8px);">
                立即探索 →
              </a-button>
            </div>
          </div>
        </div>
      </a-carousel>
    </div>

    <!-- 主体内容卡片网格 -->
    <div class="home-main">
      <!-- 第一行：热门竞赛 与 组长招募令 -->
      <div class="grid-two-cols">
        <!-- 热门竞赛模块 -->
        <div class="section-card glass-card">
          <div class="section-header">
            <div class="title-with-icon">
              <span class="icon">🏆</span>
              <h3>进行中竞赛</h3>
              <a-radio-group v-model:value="compTab" size="small" button-style="solid" class="comp-filter-tabs">
                <a-radio-button value="ACTIVE">火热进行 ({{ activeCompCount }})</a-radio-button>
                <a-radio-button value="ALL">全部赛事</a-radio-button>
              </a-radio-group>
            </div>
            <a-button type="link" @click="$router.push('/competition')">查看全部 ></a-button>
          </div>
          <div class="comp-list">
            <div
              v-for="comp in displayCompetitions"
              :key="comp.id"
              class="comp-item"
              :class="{ 'is-ended': isCompEnded(comp) }"
              @click="$router.push(`/competition/detail/${comp.id}`)"
            >
              <div class="comp-main">
                <div class="comp-title-row">
                  <a-tag :color="getLevelColor(comp.competitionLevel)">{{ comp.competitionLevel }}</a-tag>
                  <span class="comp-title">{{ comp.title }}</span>
                </div>
                <div class="comp-meta">
                  <span>主办：{{ comp.organizer || '教务处' }}</span>
                  <span>·</span>
                  <span>参赛形式：{{ comp.maxMembers === 1 ? '单人个人赛' : `${comp.minMembers}-${comp.maxMembers}人组队` }}</span>
                  <span v-if="!isCompEnded(comp) && comp.signupEndTime" class="meta-highlight">· 报名截止：{{ formatDate(comp.signupEndTime) }}</span>
                  <span v-else-if="isCompEnded(comp) && comp.competitionEndTime" class="meta-ended">· 已于 {{ formatDate(comp.competitionEndTime) }} 结束</span>
                </div>
              </div>
              <div class="comp-status">
                <a-tag :color="getStatusTag(comp).color">
                  {{ getStatusTag(comp).text }}
                </a-tag>
              </div>
            </div>
            <a-empty
              v-if="displayCompetitions.length === 0"
              description="暂无进行中的竞赛"
              style="padding: 24px 0;"
            />
          </div>
        </div>

        <!-- 组长招聘墙 -->
        <div class="section-card glass-card">
          <div class="section-header">
            <div class="title-with-icon">
              <span class="icon">📢</span>
              <h3>组长招聘雷达</h3>
            </div>
            <a-button type="link" @click="$router.push('/project')">更多招募 ></a-button>
          </div>
          <div class="recruit-list">
            <div v-for="rec in recruitList" :key="rec.id" class="recruit-item">
              <div class="rec-top">
                <span class="rec-team">{{ rec.projectName }}</span>
                <a-tag color="warning">拟招 {{ rec.recruitCount }} 人</a-tag>
              </div>
              <div class="rec-title">{{ rec.title }}</div>
              <div class="rec-desc">{{ rec.requirementDesc }}</div>
              <div class="rec-footer">
                <a-button type="primary" size="small" shape="round" @click="$router.push(`/project/detail/${rec.projectId}`)">
                  了解队伍 / 申请加入
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行：社区论坛动态 与 排行榜殿堂 -->
      <div class="grid-two-cols" style="margin-top: 24px;">
        <!-- 社区论坛最新动态 -->
        <div class="section-card glass-card">
          <div class="section-header">
            <div class="title-with-icon">
              <span class="icon">💬</span>
              <h3>社区交流与动态</h3>
            </div>
            <a-button type="link" @click="$router.push('/forum')">进入论坛 ></a-button>
          </div>
          <div class="post-feed">
            <div v-for="post in forumPosts" :key="post.id" class="feed-item" @click="$router.push('/forum')">
              <div class="feed-header">
                <a-tag v-if="post.isPinned === 'YES'" color="red">置顶</a-tag>
                <a-tag :color="getPostTypeColor(post.postType)">{{ getPostTypeName(post.postType) }}</a-tag>
                <span class="feed-title">{{ post.title }}</span>
              </div>
              <div class="feed-content">{{ post.content }}</div>
              <div class="feed-meta">
                <span>作者：{{ post.authorName || '学子' }}</span>
                <span>·</span>
                <span>💬 {{ post.commentCount || 0 }} 评论</span>
                <span>·</span>
                <span>👀 {{ post.viewCount || 0 }} 围观</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 荣誉榜单前三甲 -->
        <div class="section-card glass-card">
          <div class="section-header">
            <div class="title-with-icon">
              <span class="icon">🥇</span>
              <h3>项目综合排行榜</h3>
            </div>
            <a-button type="link" @click="$router.push('/ranking')">查看完整榜单 ></a-button>
          </div>
          <div class="ranking-podium">
            <div v-for="(rank, idx) in topRankings" :key="rank.id" class="ranking-row">
              <div class="rank-badge" :class="'rank-' + (idx + 1)">
                {{ idx + 1 }}
              </div>
              <div class="rank-info">
                <div class="rank-name">{{ rank.projectName }}</div>
                <div class="rank-leader">队长：{{ rank.leaderName }}</div>
              </div>
              <div class="rank-score-box">
                <span class="final-score">{{ rank.finalScore }}</span>
                <span class="score-desc">综合总分</span>
              </div>
            </div>
            <div v-if="topRankings.length === 0" style="text-align: center; color: #94a3b8; padding: 40px;">
              老师评定榜单尚未公开发布，敬请期待！
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { usePortalStore } from '@/store/portal'

const router = useRouter()
const portalStore = usePortalStore()

const handleBannerClick = (b) => {
  if (!b.link) return
  if (b.link.startsWith('http')) {
    window.open(b.link, '_blank')
  } else {
    router.push(b.link)
  }
}

const stats = ref({
  competitionCount: 0,
  projectCount: 16,
  submissionCount: 42
})

const compTab = ref('ACTIVE')
const hotCompetitions = ref([])
const recruitList = ref([])
const forumPosts = ref([])
const topRankings = ref([])

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

// 统一解析状态徽章
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
  if (status === 'ONGOING') return { text: '🚀 进行中', color: 'success' }
  if (status === 'REVIEWING') return { text: '📝 作品评审中', color: 'warning' }
  if (status === 'FINISHED') return { text: '已结束', color: 'default' }
  return { text: '🔥 报名中', color: 'processing' }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD')
}

// 进行中的赛事数量
const activeCompCount = computed(() => {
  return hotCompetitions.value.filter(c => !isCompEnded(c)).length
})

// 展示的赛事列表
const displayCompetitions = computed(() => {
  if (compTab.value === 'ACTIVE') {
    return hotCompetitions.value.filter(c => !isCompEnded(c)).slice(0, 5)
  }
  return hotCompetitions.value.slice(0, 5)
})

const loadHomeData = async () => {
  try {
    const compRes = await portalApi.getCompetitions({ current: 1, size: 50 })
    const records = compRes.records || []
    // 排序：进行中排在前面，已结束排在后面
    records.sort((a, b) => {
      const aEnded = isCompEnded(a) ? 1 : 0
      const bEnded = isCompEnded(b) ? 1 : 0
      return aEnded - bEnded
    })
    hotCompetitions.value = records
    // 真实统计进行中赛事数量
    stats.value.competitionCount = records.filter(c => !isCompEnded(c)).length
  } catch (e) {}

  try {
    const recRes = await portalApi.listRecruits()
    recruitList.value = (recRes || []).slice(0, 3)
  } catch (e) {}

  try {
    const forumRes = await portalApi.getForumPosts({ current: 1, size: 4 })
    forumPosts.value = forumRes.records || []
  } catch (e) {}

  try {
    const rankRes = await portalApi.getRankings()
    topRankings.value = (rankRes || []).slice(0, 3)
  } catch (e) {}
}

const getLevelColor = (level) => {
  if (level === 'NATIONAL') return 'red'
  if (level === 'PROVINCE') return 'orange'
  if (level === 'INTERNATIONAL') return 'purple'
  return 'blue'
}

const getPostTypeName = (type) => {
  if (type === 'COMPETITION_EVENT') return '官方竞赛'
  if (type === 'RECRUITMENT') return '组长招聘'
  if (type === 'FOLLOW_EVENT') return '关注动态'
  return '交流讨论'
}

const getPostTypeColor = (type) => {
  if (type === 'COMPETITION_EVENT') return 'purple'
  if (type === 'RECRUITMENT') return 'orange'
  if (type === 'FOLLOW_EVENT') return 'cyan'
  return 'blue'
}

onMounted(() => {
  portalStore.fetchConfigs()
  loadHomeData()
})
</script>

<style scoped>
.home-container {
  width: 100%;
}

.hero-section {
  background: radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.12) 0%, rgba(248, 250, 252, 1) 60%), #ffffff;
  border-bottom: 1px solid #e2e8f0;
  padding: 56px 24px 64px;
}

.hero-content {
  max-width: 1320px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
}

.hero-left {
  flex: 1;
  max-width: 680px;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1d4ed8;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background-color: #3b82f6;
  border-radius: 50%;
  animation: pulseGlow 1.5s infinite;
}

.hero-title {
  font-size: 42px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.25;
  letter-spacing: -1px;
  margin-bottom: 16px;
}

.hero-desc {
  font-size: 16px;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
}

.action-btn-main {
  height: 48px;
  padding: 0 28px;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 10px 20px -5px rgba(59, 130, 246, 0.4);
}

.action-btn-sub {
  height: 48px;
  padding: 0 28px;
  font-size: 15px;
  font-weight: 600;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.stat-card {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background-color: #cbd5e1;
}

.hero-right {
  width: 440px;
}

.hero-dashboard-card {
  padding: 24px;
  border-radius: 20px;
  border: 1px solid #cbd5e1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.engine-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.f-icon {
  font-size: 22px;
}

.f-title {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.f-desc {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.home-main {
  max-width: 1320px;
  margin: 36px auto;
  padding: 0 24px;
}

.grid-two-cols {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.section-card {
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 12px;
}

.title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-with-icon .icon {
  font-size: 18px;
}

.title-with-icon h3 {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.comp-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comp-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.comp-item:hover {
  background: #eff6ff;
  border-color: #93c5fd;
  transform: translateX(3px);
}

.comp-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comp-title {
  font-weight: 700;
  font-size: 14px;
  color: #1e293b;
}

.comp-filter-tabs {
  margin-left: 12px;
}

.comp-meta {
  font-size: 12px;
  color: #64748b;
  display: flex;
  gap: 6px;
  align-items: center;
}

.comp-item.is-ended {
  background: #f8fafc;
  border-color: #e2e8f0;
  opacity: 0.72;
}

.comp-item.is-ended:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.comp-item.is-ended .comp-title {
  color: #64748b;
}

.meta-highlight {
  color: #2563eb;
  font-weight: 500;
}

.meta-ended {
  color: #94a3b8;
}

.recruit-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.recruit-item {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 12px;
  padding: 16px;
}

.rec-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.rec-team {
  font-size: 13px;
  color: #b45309;
  font-weight: 700;
}

.rec-title {
  font-size: 15px;
  font-weight: 700;
  color: #78350f;
  margin-bottom: 4px;
}

.rec-desc {
  font-size: 13px;
  color: #92400e;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-footer {
  text-align: right;
}

.post-feed {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feed-item {
  padding: 12px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
}

.feed-item:hover {
  background: #eff6ff;
  border-color: #93c5fd;
}

.feed-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.feed-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.feed-content {
  font-size: 13px;
  color: #475569;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feed-meta {
  font-size: 11px;
  color: #94a3b8;
  display: flex;
  gap: 6px;
}

.ranking-podium {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.rank-badge {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 18px;
  color: #fff;
}

.rank-1 {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.rank-2 {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

.rank-3 {
  background: linear-gradient(135deg, #b45309 0%, #78350f 100%);
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.rank-leader {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.rank-score-box {
  text-align: right;
}

.final-score {
  font-size: 20px;
  font-weight: 800;
  color: #2563eb;
  display: block;
}

.score-desc {
  font-size: 10px;
  color: #94a3b8;
}
</style>
