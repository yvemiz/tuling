<template>
  <div class="forum-page">
    <!-- 头部横幅 -->
    <div class="page-header glass-card">
      <div class="header-left">
        <h2>💬 竞赛社区与研讨论坛</h2>
        <p>学术探讨、组长招募令、关注动态全自动实时广播；AI智能合规审查 + 组长/教师多级联审</p>
      </div>
      <div class="header-right">
        <a-button type="primary" size="large" shape="round" @click="openPostModal">
          ✍️ 我要发帖交流 (AI实时审查)
        </a-button>
      </div>
    </div>

    <!-- 论坛主区域 -->
    <div class="forum-layout">
      <!-- 帖子列表与审核流 -->
      <div class="forum-main glass-card">
        <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
          <a-tab-pane key="ALL" tab="🌐 全部研讨" />
          <template v-if="portalStore.boards && portalStore.boards.length > 0">
            <a-tab-pane
              v-for="b in portalStore.boards"
              :key="b.boardCode"
              :tab="`${b.icon || '💬'} ${b.boardName}`"
            />
          </template>
          <template v-else>
            <a-tab-pane key="RECRUITMENT" tab="📢 组长招募专区" />
            <a-tab-pane key="COMPETITION_EVENT" tab="🏆 官方竞赛发布" />
            <a-tab-pane key="FOLLOW_EVENT" tab="⭐ 关注动态速递" />
            <a-tab-pane key="GENERAL" tab="💡 纯算法讨论" />
          </template>

          <!-- 老师和组长专属的审核专区 -->
          <a-tab-pane
            v-if="userStore.isTeacher || userStore.isLeader"
            key="AUDIT"
            tab="🛡️ 待审核队列 (组长/教师联审)"
          />
        </a-tabs>

        <!-- 正常已发布帖子列表 -->
        <div v-if="activeTab !== 'AUDIT'" class="post-list-container">
          <div v-if="posts.length > 0" class="post-cards">
            <div v-for="post in posts" :key="post.id" class="post-card glass-card">
              <div class="post-card-top">
                <div class="author-info">
                  <a-avatar size="default" style="background-color: #3b82f6;">
                    {{ (post.authorName || 'U').substring(0, 1) }}
                  </a-avatar>
                  <div class="author-text">
                    <span class="author-name">{{ post.authorName || '学子' }}</span>
                    <span class="post-time">{{ formatDate(post.createTime) }}</span>
                  </div>
                </div>

                <div class="post-badges">
                  <a-tag v-if="post.isPinned === 'YES'" color="red">📌 置顶帖</a-tag>
                  <a-tag :color="getPostTypeColor(post.postType)">{{ getPostTypeName(post.postType) }}</a-tag>

                  <!-- 老师可以置顶/取消置顶 -->
                  <a-button
                    v-if="userStore.isTeacher"
                    type="link"
                    size="small"
                    @click="handleTogglePin(post)"
                  >
                    {{ post.isPinned === 'YES' ? '取消置顶' : '置顶内容' }}
                  </a-button>
                </div>
              </div>

              <!-- 帖子核心主体（CSDN 风格图文并茂信息流） -->
              <div class="post-main-layout" @click="$router.push(`/forum/detail/${post.id}`)" style="cursor: pointer;">
                <div class="post-text-side">
                  <div class="post-title-row">
                    <h3 class="post-title">{{ post.title }}</h3>
                  </div>
                  <div class="post-snippet">
                    {{ formatContentSnippet(post.content) }}
                  </div>
                  <!-- 多形态内容特征小标签 -->
                  <div class="post-features-tags" style="margin-top: 10px; display: flex; gap: 6px; flex-wrap: wrap;">
                    <a-tag v-if="extractFirstImage(post.content)" color="blue" size="small">🖼️ 包含图文插图</a-tag>
                    <a-tag v-if="post.extData.codeSnippet" color="geekblue" size="small">💻 包含源码题解</a-tag>
                    <a-tag v-if="post.extData.attachments?.length > 0" color="purple" size="small">📎 包含 {{ post.extData.attachments.length }} 个附件</a-tag>
                    <a-tag v-if="post.extData.recruitRole" color="orange" size="small">📢 招募 {{ post.extData.recruitRole }}</a-tag>
                    <a-tag v-for="tag in (post.extData.tags || []).slice(0, 3)" :key="tag" color="cyan" size="small"># {{ tag }}</a-tag>
                  </div>
                </div>

                <!-- 右侧封面缩略图 (类似 CSDN/掘金/知乎) -->
                <div v-if="extractFirstImage(post.content)" class="post-cover-side">
                  <div class="cover-image-container">
                    <img :src="extractFirstImage(post.content)" alt="文章封面插图" class="cover-thumb" loading="lazy" />
                  </div>
                </div>
              </div>

              <div class="post-footer">
                <div class="post-stats">
                  <span>👀 {{ post.viewCount || 0 }} 浏览</span>
                  <span>·</span>
                  <a-button type="link" size="small" @click="$router.push(`/forum/detail/${post.id}`)">
                    💬 {{ post.commentCount || 0 }} 条研讨回复
                  </a-button>
                </div>

                <a-button type="primary" ghost size="small" shape="round" @click="$router.push(`/forum/detail/${post.id}`)">
                  阅读全文与参与研讨 >
                </a-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <a-empty description="当前分类下暂无已发布的帖子" />
          </div>
        </div>

        <!-- 审核专区：待人工联审帖子列表 -->
        <div v-else class="audit-list-container">
          <div class="audit-banner-box">
            <a-alert
              message="AI安全审核风控体系说明"
              description="所有用户发布的发帖均先经过AI智能语意扫描。若检测到敏感合规风险或异常，将自动进入本审核专区。老师拥有直接决裁权，组长联审达到 2 票通过后将自动正式上线！"
              type="info"
              show-icon
              style="margin-bottom: 20px;"
            />
          </div>

          <div v-if="pendingAuditPosts.length > 0" class="pending-cards">
            <div v-for="p in pendingAuditPosts" :key="p.id" class="pending-card glass-card">
              <div class="p-header">
                <span class="p-author">发帖人：{{ p.authorName }}</span>
                <a-tag color="error">AI标记风险：{{ p.aiAuditReason || '敏感词风控拦截' }}</a-tag>
              </div>

              <h4 class="p-title" style="cursor: pointer; color: #4f46e5;" @click="$router.push(`/forum/detail/${p.id}`)">{{ p.title }}</h4>
              <p class="p-content" style="cursor: pointer;" @click="$router.push(`/forum/detail/${p.id}`)">{{ p.content }}</p>

              <div class="p-voting-meta">
                <span>当前组长赞成票：<strong>{{ p.leaderPassCount || 0 }}</strong> / 2 票</span>
                <span>·</span>
                <span>组长反对票：{{ p.leaderRejectCount || 0 }} 票</span>
              </div>

              <div class="p-actions">
                <!-- 教师一票直审 -->
                <template v-if="userStore.isTeacher">
                  <a-space>
                    <a-button type="primary" size="small" @click="handleTeacherAudit(p.id, 'PASS')">
                      👨‍🏫 教师一票通过发布
                    </a-button>
                    <a-button danger size="small" @click="handleTeacherAudit(p.id, 'REJECT')">
                      驳回违规
                    </a-button>
                  </a-space>
                </template>

                <!-- 组长联审投票 -->
                <template v-else-if="userStore.isLeader">
                  <a-space>
                    <a-button type="primary" size="small" @click="handleLeaderAudit(p.id, 'PASS')">
                      👍 组长表决通过 (+1票)
                    </a-button>
                    <a-button danger size="small" @click="handleLeaderAudit(p.id, 'REJECT')">
                      👎 组长表决驳回
                    </a-button>
                  </a-space>
                </template>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <a-empty description="当前暂无待审核的存疑帖子，所有内容安全合规" />
          </div>
        </div>
      </div>
    </div>

    <!-- 用户发帖对话框 (内置AI实时扫描模拟) -->
    <a-modal
      v-model:open="postModalVisible"
      title="发表社区帖子 (经AI智能审查)"
      width="640px"
      ok-text="立即发表"
      cancel-text="取消"
      @ok="handleCreatePost"
    >
      <a-form :model="postForm" layout="vertical">
        <a-form-item label="发布板块" required>
          <a-select v-model:value="postForm.postType">
            <template v-if="availableBoards && availableBoards.length > 0">
              <a-select-option v-for="b in availableBoards" :key="b.boardCode" :value="b.boardCode">
                {{ b.icon || '💬' }} {{ b.boardName }}
              </a-select-option>
            </template>
            <template v-else>
              <a-select-option value="GENERAL">💡 纯学术交流与算法探讨</a-select-option>
              <a-select-option value="RECRUITMENT" v-if="userStore.isLeader">📢 组长招募组员</a-select-option>
            </template>
          </a-select>
        </a-form-item>
        <a-form-item label="标题" required>
          <a-input v-model:value="postForm.title" placeholder="请输入清晰概括的研讨标题" />
        </a-form-item>
        <a-form-item label="正文内容" required>
          <a-textarea v-model:value="postForm.content" :rows="6" placeholder="请阐述您的观点、解题思路或团队优势（请遵守社区文明规范）" />
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
import { usePortalStore } from '@/store/portal'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const portalStore = usePortalStore()

const activeTab = ref('ALL')
const posts = ref([])
const pendingAuditPosts = ref([])

const availableBoards = computed(() => {
  if (!portalStore.boards || portalStore.boards.length === 0) return []
  return portalStore.boards.filter(b => {
    if (b.status !== 'ENABLE') return false
    if (b.postPermission === 'TEACHER') return userStore.isTeacher
    if (b.postPermission === 'LEADER') return userStore.isTeacher || userStore.isLeader
    return true
  })
})

const postModalVisible = ref(false)
const postForm = reactive({
  title: '',
  content: '',
  postType: 'GENERAL'
})

const activeCommentPostId = ref(null)
const comments = ref([])
const commentInput = ref('')

const loadPosts = async () => {
  try {
    const res = await portalApi.getForumPosts({
      postType: activeTab.value,
      current: 1,
      size: 50
    })
    posts.value = res.records || res.data?.records || (Array.isArray(res) ? res : [])
  } catch (e) {
    console.error(e)
  }
}

const loadPendingAudits = async () => {
  if (userStore.isTeacher || userStore.isLeader) {
    try {
      const res = await portalApi.getPendingAuditPosts({ current: 1, size: 50 })
      pendingAuditPosts.value = res.records || res.data?.records || (Array.isArray(res) ? res : [])
    } catch (e) {}
  }
}

const handleTabChange = (key) => {
  if (key === 'AUDIT') {
    loadPendingAudits()
  } else {
    loadPosts()
  }
}

const openPostModal = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再发表帖子')
    router.push('/login')
    return
  }
  router.push('/forum/publish')
}

const handleCreatePost = async () => {
  if (!postForm.title || !postForm.content) {
    message.warning('请填写标题和内容')
    return
  }
  try {
    const res = await portalApi.createForumPost(postForm)
    message.info(res)
    postModalVisible.value = false
    postForm.title = ''
    postForm.content = ''
    loadPosts()
    loadPendingAudits()
  } catch (e) {}
}

const handleTogglePin = async (post) => {
  const isPin = post.isPinned === 'YES' ? 'NO' : 'YES'
  try {
    const res = await portalApi.togglePinPost({ postId: post.id, isPinned: isPin })
    message.success(res)
    loadPosts()
  } catch (e) {}
}

// 展开并加载评论
const toggleComments = async (post) => {
  if (activeCommentPostId.value === post.id) {
    activeCommentPostId.value = null
    return
  }
  activeCommentPostId.value = post.id
  try {
    const res = await portalApi.getPostComments(post.id)
    comments.value = res || []
  } catch (e) {}
}

const handleSendComment = async (postId) => {
  if (!commentInput.value.trim()) return
  try {
    await portalApi.addComment({
      postId,
      content: commentInput.value
    })
    message.success('评论发表成功')
    commentInput.value = ''
    const res = await portalApi.getPostComments(postId)
    comments.value = res || []
    loadPosts()
  } catch (e) {}
}

// 老师审核
const handleTeacherAudit = async (postId, result) => {
  try {
    const res = await portalApi.teacherAuditPost({
      postId,
      auditResult: result,
      auditReason: result === 'PASS' ? '教师审核合规通过' : '教师判定内容存疑驳回'
    })
    message.success(res)
    loadPendingAudits()
    loadPosts()
  } catch (e) {}
}

// 组长审核
const handleLeaderAudit = async (postId, result) => {
  try {
    const res = await portalApi.leaderAuditPost({
      postId,
      auditResult: result,
      auditReason: result === 'PASS' ? '组长联审投票赞成' : '组长投票驳回'
    })
    message.info(res)
    loadPendingAudits()
    loadPosts()
  } catch (e) {}
}

// 提取文章中的第一张图片作为封面缩略图 (自动清洗本地端口)
const extractFirstImage = (content) => {
  if (!content) return null
  const match = content.match(/!\[.*?\]\((.*?)\)/)
  if (match && match[1]) {
    return match[1].replace(/^http:\/\/localhost:\d+\/api/, '/api')
  }
  return null
}

// 提炼优雅的纯文本摘要（彻底剔除 raw Markdown 符号与表格乱码）
const formatContentSnippet = (text) => {
  if (!text) return ''
  let cleaned = text
    .replace(/!\[.*?\]\(.*?\)/g, '')
    .replace(/\[(.*?)\]\(.*?\)/g, '$1')
    .replace(/```[\s\S]*?```/g, ' [代码示例] ')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/\|[\s-:]+\|[\s-:]+\|?/g, ' ')
    .replace(/\|/g, ' ')
    .replace(/^[#>-]+\s*/gm, '')
    .replace(/[*_~]/g, '')
    .replace(/\s+/g, ' ')
    .trim()
  if (!cleaned) return '点击进入阅读详细图文研讨与代码方案...'
  return cleaned.length > 130 ? cleaned.slice(0, 130) + '...' : cleaned
}

const getPostTypeName = (t) => {
  const b = portalStore.boards?.find((item) => item.boardCode === t)
  if (b) return b.boardName
  if (t === 'COMPETITION_EVENT') return '官方竞赛'
  if (t === 'RECRUITMENT') return '组长招聘'
  if (t === 'FOLLOW_EVENT') return '关注动态'
  return '通用研讨'
}

const getPostTypeColor = (t) => {
  const b = portalStore.boards?.find((item) => item.boardCode === t)
  if (b && b.color) return b.color
  if (t === 'COMPETITION_EVENT') return 'purple'
  if (t === 'RECRUITMENT') return 'orange'
  if (t === 'FOLLOW_EVENT') return 'cyan'
  return 'blue'
}

const formatDate = (d) => {
  if (!d) return ''
  return dayjs(d).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  portalStore.fetchBoards()
  if (route.query.tab === 'audit' && (userStore.isTeacher || userStore.isLeader)) {
    activeTab.value = 'AUDIT'
    loadPendingAudits()
  } else {
    loadPosts()
  }
})
</script>

<style scoped>
.forum-page {
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

.forum-main {
  padding: 24px 32px;
}

.post-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 16px;
}

.post-card {
  padding: 24px;
}

.post-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-name {
  font-weight: 700;
  font-size: 14.5px;
  color: #0f172a;
}

.post-time {
  font-size: 12px;
  color: #94a3b8;
  display: block;
}

.post-badges {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-title {
  font-size: 17px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 6px;
  transition: color 0.2s ease;
}

.post-main-layout:hover .post-title {
  color: #3b82f6;
}

/* CSDN 级左文右图卡片布局 */
.post-main-layout {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin: 8px 0 14px 0;
}

.post-text-side {
  flex: 1;
  min-width: 0;
}

.post-snippet {
  font-size: 13.5px;
  color: #64748b;
  line-height: 1.6;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-cover-side {
  flex-shrink: 0;
  width: 156px;
  height: 98px;
}

.cover-image-container {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.cover-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.3s ease;
}

.post-main-layout:hover .cover-thumb {
  transform: scale(1.05);
}

@media (max-width: 640px) {
  .post-main-layout {
    flex-direction: column-reverse;
  }
  .post-cover-side {
    width: 100%;
    height: 140px;
  }
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
  font-size: 12px;
  color: #94a3b8;
}

.comment-drawer {
  margin-top: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.comment-input-row {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}

.comment-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.c-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  font-size: 13px;
}

.c-user {
  font-weight: 700;
  color: #1e293b;
}

.c-text {
  color: #475569;
}

.pending-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.pending-card {
  padding: 20px;
  border: 1px solid #fecaca;
  background: #fff5f5;
}

.p-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.p-author {
  font-weight: 700;
  font-size: 13.5px;
}

.p-title {
  font-size: 15px;
  font-weight: 700;
  color: #991b1b;
  margin-bottom: 4px;
}

.p-content {
  font-size: 13.5px;
  color: #7f1d1d;
  margin-bottom: 12px;
}

.p-voting-meta {
  font-size: 12px;
  color: #b91c1c;
  margin-bottom: 12px;
}

.p-actions {
  text-align: right;
  border-top: 1px solid #fee2e2;
  padding-top: 10px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}
</style>
