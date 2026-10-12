<template>
  <div class="forum-detail-page" v-if="post">
    <!-- 头部导航与操作 -->
    <div class="detail-header glass-card">
      <div class="header-top-row">
        <a-button type="link" @click="$router.push('/forum')">← 返回社区论坛</a-button>
        <div class="badges-row">
          <a-tag v-if="post.isPinned === 'YES'" color="red">📌 官方置顶</a-tag>
          <a-tag :color="getPostTypeColor(post.postType)">{{ getPostTypeName(post.postType) }}</a-tag>
          <a-tag :color="post.auditStatus === 'PUBLISHED' ? 'success' : 'warning'">
            {{ post.auditStatus === 'PUBLISHED' ? '已通过AI审核' : '人工联审中' }}
          </a-tag>
        </div>
      </div>

      <h1 class="post-title">{{ post.title }}</h1>

      <!-- 作者名片与元信息 -->
      <div class="author-meta-bar">
        <div class="author-info">
          <a-avatar size="large" style="background-color: #4f46e5; font-weight: 700;">
            {{ (post.authorName || 'U').substring(0, 1) }}
          </a-avatar>
          <div class="author-texts">
            <div class="name-line">
              <span class="name">{{ post.authorName || '学子' }}</span>
              <a-tag v-if="post.authorRoleInfo?.isTeacher" color="purple">👨‍🏫 指导教师</a-tag>
              <a-tag v-else-if="post.authorRoleInfo?.isLeader" color="orange">👑 团队组长</a-tag>
              <a-tag v-else color="blue">👨‍🎓 认证学子</a-tag>
            </div>
            <div class="meta-line">
              <span>发表于 {{ formatDate(post.createTime) }}</span>
              <span>·</span>
              <span>👀 {{ post.viewCount || 0 }} 次浏览</span>
              <span>·</span>
              <span>💬 {{ comments.length }} 条探讨</span>
            </div>
          </div>
        </div>

        <!-- 导师/组长特权操作 -->
        <div class="author-actions">
          <a-space>
            <!-- 导师置顶控制 -->
            <a-button
              v-if="userStore.isTeacher && post.auditStatus === 'PUBLISHED'"
              :type="post.isPinned === 'YES' ? 'default' : 'dashed'"
              shape="round"
              @click="handleTogglePin"
            >
              {{ post.isPinned === 'YES' ? '取消置顶' : '📌 置顶本帖' }}
            </a-button>

            <!-- 待审核时老师一票直审 -->
            <template v-if="userStore.isTeacher && post.auditStatus === 'MANUAL_PENDING'">
              <a-button type="primary" shape="round" @click="handleTeacherAudit('PASS')">
                一票通过
              </a-button>
              <a-button danger shape="round" @click="handleTeacherAudit('REJECT')">
                驳回违规
              </a-button>
            </template>
          </a-space>
        </div>
      </div>
    </div>

    <!-- 帖子主体多模态内容卡片 -->
    <div class="post-main-content glass-card">
      <!-- 话题标签 -->
      <div class="tags-row" v-if="post.extData?.tags?.length > 0">
        <a-tag v-for="tag in post.extData.tags" :key="tag" color="cyan" style="border-radius: 12px; padding: 2px 10px;">
          # {{ tag }}
        </a-tag>
      </div>

      <!-- 核心见解文字正文 (支持 Markdown 与图文混排) -->
      <div class="article-body markdown-body" v-html="renderedContent"></div>

      <!-- 多模态展示 1: 算法核心代码精析框 -->
      <div v-if="post.extData?.codeSnippet" class="code-showcase-box">
        <div class="code-box-header">
          <div class="lang-tag">
            <span class="dot"></span>
            <span>{{ post.extData.codeLanguage || 'Java' }} 源码实现</span>
          </div>
          <a-button type="link" size="small" @click="copyCode" style="color: #94a3b8;">
            📋 复制代码
          </a-button>
        </div>
        <pre class="code-pre"><code>{{ post.extData.codeSnippet }}</code></pre>

        <!-- 复杂度指标卡片 -->
        <div class="complexity-row" v-if="post.extData?.timeComplexity || post.extData?.spaceComplexity">
          <div class="comp-item" v-if="post.extData.timeComplexity">
            <span class="comp-label">⏱️ 时间复杂度：</span>
            <span class="comp-val">{{ post.extData.timeComplexity }}</span>
          </div>
          <div class="comp-item" v-if="post.extData.spaceComplexity">
            <span class="comp-label">💾 空间复杂度：</span>
            <span class="comp-val">{{ post.extData.spaceComplexity }}</span>
          </div>
        </div>

        <!-- 样例输入输出 -->
        <div class="sample-grid" v-if="post.extData?.sampleInput || post.extData?.sampleOutput">
          <div class="sample-box" v-if="post.extData.sampleInput">
            <div class="s-title">📥 输入样例：</div>
            <pre>{{ post.extData.sampleInput }}</pre>
          </div>
          <div class="sample-box" v-if="post.extData.sampleOutput">
            <div class="s-title">📤 输出样例：</div>
            <pre>{{ post.extData.sampleOutput }}</pre>
          </div>
        </div>
      </div>

      <!-- 多模态展示 2: 组长团队引才招募令卡片 -->
      <div v-if="post.postType === 'RECRUITMENT' || post.extData?.recruitRole" class="recruit-showcase-box">
        <div class="recruit-badge">
          <span>🚀 团队战队引才直通车</span>
        </div>
        <div class="recruit-meta-grid">
          <div class="r-item">
            <span class="r-label">拟招募角色：</span>
            <span class="r-val" style="color: #f59e0b; font-weight: 700;">{{ post.extData?.recruitRole || '核心技术研发' }}</span>
          </div>
          <div class="r-item">
            <span class="r-label">招募名额：</span>
            <span class="r-val">{{ post.extData?.recruitCount || 1 }} 位</span>
          </div>
          <div class="r-item" v-if="post.extData?.timeRequirement">
            <span class="r-label">实训投入期望：</span>
            <span class="r-val">{{ post.extData.timeRequirement }}</span>
          </div>
        </div>
        <div class="r-contact" v-if="post.extData?.contactInfo">
          <span>投递对接方式：<strong>{{ post.extData.contactInfo }}</strong></span>
        </div>
        <div class="r-action" v-if="post.refId">
          <a-button type="primary" shape="round" @click="$router.push(`/project/detail/${post.refId}`)">
            查看关联团队主页并申请加入 >
          </a-button>
        </div>
      </div>

      <!-- 多模态展示 3: 文件方案与图文附件 -->
      <div v-if="post.extData?.attachments?.length > 0" class="attachments-box">
        <div class="att-title">📎 技术方案与数据集附件下载：</div>
        <div class="att-grid">
          <div v-for="(att, i) in post.extData.attachments" :key="i" class="att-card">
            <span class="file-icon">📄</span>
            <div class="file-info">
              <div class="file-name">{{ att.name }}</div>
              <div class="file-size">{{ att.size || '文件附件' }}</div>
            </div>
            <a :href="att.url" target="_blank" class="download-link">
              下载 / 查验 ↗
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部：全功能评论与互动研讨专区 -->
    <div class="comments-section glass-card">
      <div class="comments-header">
        <h3>💬 社区研讨交流区 ({{ comments.length }})</h3>
        <span class="hint">理性发言，共同精进算法与实训工程水平</span>
      </div>

      <!-- 发送评论框 -->
      <div class="comment-input-area">
        <a-textarea
          v-model:value="commentInput"
          :rows="3"
          placeholder="撰写您的见解、疑问或协作邀请，按 Ctrl+Enter 快捷发送..."
          @keydown.ctrl.enter="handleSendComment"
        />
        <div class="input-actions">
          <span class="tip">支持友善交流探讨</span>
          <a-button type="primary" shape="round" :loading="sendingComment" @click="handleSendComment">
            发表研讨回复
          </a-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list" v-if="comments.length > 0">
        <div v-for="c in comments" :key="c.id" class="comment-row">
          <a-avatar size="default" style="background-color: #3b82f6;">
            {{ (c.userName || 'U').substring(0, 1) }}
          </a-avatar>
          <div class="c-content-col">
            <div class="c-user-row">
              <span class="c-user-name">{{ c.userName }}</span>
              <span class="c-time">{{ formatDate(c.createTime) }}</span>
            </div>
            <div class="c-text">{{ c.content }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty-comments">
        <a-empty description="当前暂无评论回复，留下您的第一条真知灼见吧！" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { marked } from 'marked'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const postId = route.params.id
const post = ref(null)
const comments = ref([])
const commentInput = ref('')
const sendingComment = ref(false)

// 实时渲染 Markdown，支持图片与代码格式（自动规范化图片 URL）
const renderedContent = computed(() => {
  if (!post.value?.content) return ''
  try {
    const safeContent = post.value.content.replace(/http:\/\/localhost:\d+\/api/g, '/api')
    return marked.parse(safeContent)
  } catch (e) {
    return post.value.content
  }
})

const loadDetail = async () => {
  try {
    const res = await portalApi.getPostDetail(postId)
    post.value = res
  } catch (e) {
    message.error('加载帖子详情失败')
  }
}

const loadComments = async () => {
  try {
    const res = await portalApi.getPostComments(postId)
    comments.value = res || []
  } catch (e) {}
}

const handleSendComment = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再发表评论')
    router.push('/login')
    return
  }
  if (!commentInput.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  sendingComment.value = true
  try {
    await portalApi.addComment({
      postId,
      content: commentInput.value.trim()
    })
    message.success('评论发表成功！')
    commentInput.value = ''
    loadComments()
    if (post.value) {
      post.value.commentCount = (post.value.commentCount || 0) + 1
    }
  } catch (e) {
    message.error('发送评论失败')
  } finally {
    sendingComment.value = false
  }
}

const handleTogglePin = async () => {
  const isPin = post.value.isPinned === 'YES' ? 'NO' : 'YES'
  try {
    const res = await portalApi.togglePinPost({ postId, isPinned: isPin })
    post.value.isPinned = isPin
    message.success(res)
  } catch (e) {}
}

const handleTeacherAudit = async (result) => {
  try {
    const res = await portalApi.teacherAuditPost({
      postId,
      auditResult: result,
      auditReason: result === 'PASS' ? '教师审核合规通过' : '教师判定内容存疑驳回'
    })
    message.success(res)
    loadDetail()
  } catch (e) {}
}

const copyCode = () => {
  if (post.value?.extData?.codeSnippet) {
    navigator.clipboard.writeText(post.value.extData.codeSnippet)
    message.success('算法源码已复制到剪贴板！')
  }
}

const getPostTypeName = (t) => {
  if (t === 'COMPETITION_EVENT') return '🏆 赛事历程'
  if (t === 'RECRUITMENT') return '📢 组长招募'
  if (t === 'FOLLOW_EVENT') return '⭐ 关注动态'
  return '💡 学术探讨'
}

const getPostTypeColor = (t) => {
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
  loadDetail()
  loadComments()
})
</script>

<style scoped>
.forum-detail-page {
  max-width: 1040px;
  margin: 32px auto;
  padding: 0 20px 60px;
}

.detail-header {
  padding: 28px 36px;
  margin-bottom: 24px;
}

.header-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.badges-row {
  display: flex;
  gap: 8px;
}

.post-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.4;
  margin-bottom: 20px;
}

.author-meta-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.author-texts .name-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.author-texts .name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.meta-line {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #94a3b8;
}

.post-main-content {
  padding: 36px;
  margin-bottom: 24px;
}

.tags-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.article-body {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  white-space: pre-wrap;
  margin-bottom: 24px;
}

/* 算法代码块暗黑设计 */
.code-showcase-box {
  background: #0f172a;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid #1e293b;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.2);
}

.code-box-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #334155;
  margin-bottom: 14px;
}

.lang-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #38bdf8;
  font-weight: 700;
  font-size: 14px;
}

.lang-tag .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
}

.code-pre {
  margin: 0;
  font-family: 'Fira Code', monospace;
  font-size: 14px;
  color: #f1f5f9;
  line-height: 1.6;
  overflow-x: auto;
}

.complexity-row {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #1e293b;
}

.comp-item {
  font-size: 13px;
}

.comp-label {
  color: #94a3b8;
}

.comp-val {
  color: #a78bfa;
  font-weight: 700;
}

.sample-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.sample-box {
  background: #1e293b;
  border-radius: 8px;
  padding: 12px 16px;
}

.sample-box .s-title {
  color: #94a3b8;
  font-size: 12px;
  margin-bottom: 6px;
}

.sample-box pre {
  margin: 0;
  color: #e2e8f0;
  font-size: 13px;
}

/* 招募令卡片 */
.recruit-showcase-box {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.08), rgba(249, 115, 22, 0.04));
  border: 1px solid rgba(245, 158, 11, 0.25);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.recruit-badge {
  font-size: 16px;
  font-weight: 800;
  color: #d97706;
  margin-bottom: 16px;
}

.recruit-meta-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.r-label {
  color: #64748b;
  font-size: 13px;
}

.r-val {
  font-size: 14px;
  font-weight: 600;
}

.r-contact {
  padding: 10px 14px;
  background: #ffffff;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #334155;
}

/* 附件下载卡片 */
.attachments-box {
  padding: 20px;
  background: #f8fafc;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.att-title {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.att-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.att-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #ffffff;
  padding: 10px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.file-icon {
  font-size: 20px;
}

.file-info {
  flex: 1;
}

.file-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.file-size {
  font-size: 12px;
  color: #94a3b8;
}

.download-link {
  color: #4f46e5;
  font-weight: 600;
  font-size: 13px;
}

/* 评论区 */
.comments-section {
  padding: 32px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.comments-header h3 {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.comments-header .hint {
  font-size: 12px;
  color: #94a3b8;
}

.comment-input-area {
  margin-bottom: 32px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.input-actions .tip {
  font-size: 12px;
  color: #94a3b8;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-row {
  display: flex;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.c-content-col {
  flex: 1;
}

.c-user-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.c-user-name {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.c-time {
  font-size: 12px;
  color: #94a3b8;
}

.c-text {
  font-size: 14px;
  line-height: 1.6;
  color: #475569;
}

.empty-comments {
  padding: 32px 0;
}

/* Markdown 图文排版样式 */
:deep(.markdown-body) {
  font-size: 15px;
  line-height: 1.8;
  color: #1e293b;
  margin-bottom: 24px;
}

:deep(.markdown-body h1) {
  font-size: 24px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 8px;
  margin-top: 24px;
  margin-bottom: 14px;
  color: #0f172a;
}

:deep(.markdown-body h2) {
  font-size: 20px;
  border-left: 4px solid #4f46e5;
  padding-left: 10px;
  margin-top: 20px;
  margin-bottom: 12px;
  color: #1e293b;
}

:deep(.markdown-body h3) {
  font-size: 17px;
  margin-top: 16px;
  margin-bottom: 10px;
  color: #334155;
}

:deep(.markdown-body p) {
  margin-bottom: 14px;
  color: #334155;
}

:deep(.markdown-body img) {
  max-width: 96%;
  height: auto;
  border-radius: 8px;
  margin: 18px auto;
  display: block;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

:deep(.markdown-body pre) {
  background: #0f172a;
  color: #38bdf8;
  padding: 16px 20px;
  border-radius: 8px;
  overflow-x: auto;
  font-family: Consolas, Monaco, monospace;
  font-size: 13.5px;
  line-height: 1.55;
  margin: 16px 0;
}

:deep(.markdown-body code) {
  background: #f1f5f9;
  color: #0f172a;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: Consolas, monospace;
  font-size: 13px;
}

:deep(.markdown-body pre code) {
  background: transparent;
  color: inherit;
  padding: 0;
}

:deep(.markdown-body blockquote) {
  margin: 14px 0;
  padding: 12px 18px;
  background: #f8fafc;
  border-left: 4px solid #6366f1;
  color: #475569;
  border-radius: 0 8px 8px 0;
}

:deep(.markdown-body table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
  font-size: 13.5px;
}

:deep(.markdown-body th), :deep(.markdown-body td) {
  border: 1px solid #e2e8f0;
  padding: 10px 14px;
  text-align: left;
}

:deep(.markdown-body th) {
  background: #f1f5f9;
  font-weight: 600;
}

:deep(.markdown-body ul), :deep(.markdown-body ol) {
  padding-left: 24px;
  margin-bottom: 14px;
}
</style>
