<template>
  <div class="forum-publish-page">
    <!-- 头部横幅与返回 -->
    <div class="publish-header glass-card">
      <div class="header-top">
        <a-button type="link" @click="$router.push('/forum')">← 返回社区论坛</a-button>
        <a-tag color="blue">AI 智能合规风控体系</a-tag>
      </div>
      <div class="header-main">
        <h2>✍️ 发布社区研讨与发帖中心</h2>
        <p>支持学术研讨、算法题解精析、组长招募令与备赛答辩总结等多模态内容形式</p>
      </div>
    </div>

    <!-- 主表单卡片 -->
    <div class="publish-content glass-card">
      <a-form layout="vertical" :model="form">
        <!-- 内容分类选择 -->
        <a-form-item label="研讨内容形态 / 模板" required>
          <a-radio-group v-model:value="form.postType" button-style="solid" size="large" @change="onPostTypeChange">
            <a-radio-button value="GENERAL">💡 学术交流与探讨</a-radio-button>
            <a-radio-button value="CODE">💻 算法题解与源码精析</a-radio-button>
            <a-radio-button value="RECRUITMENT">📢 组长团队引才招募</a-radio-button>
            <a-radio-button value="COMPETITION_EVENT">🏆 赛事历程与通关秘籍</a-radio-button>
          </a-radio-group>
        </a-form-item>

        <!-- 社区板块选择 (后台动态配置) -->
        <a-form-item label="归属研讨板块 (后台配置)" v-if="filteredBoards.length > 0">
          <a-select v-model:value="form.boardCode" placeholder="选择归属的具体板块（留空则默认按上述形态发布）" allow-clear>
            <a-select-option v-for="b in filteredBoards" :key="b.boardCode" :value="b.boardCode">
              {{ b.icon || '💬' }} {{ b.boardName }} ({{ b.boardCode }})
            </a-select-option>
          </a-select>
        </a-form-item>

        <!-- 帖子标题 -->
        <a-form-item label="研讨标题" required>
          <a-input
            v-model:value="form.title"
            size="large"
            :placeholder="getTitlePlaceholder()"
            :maxlength="100"
            show-count
          />
        </a-form-item>

        <!-- 标签选择与关联竞赛/项目 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="话题标签 (可直接键入自定义标签)">
              <a-select
                v-model:value="form.tags"
                mode="tags"
                placeholder="选择或输入标签，回车确认"
                style="width: 100%"
                :options="popularTags"
              />
            </a-form-item>
          </a-col>

          <!-- 招募令时关联项目团队 -->
          <a-col :span="12" v-if="form.postType === 'RECRUITMENT'">
            <a-form-item label="所属参赛项目组" required>
              <a-select v-model:value="form.refId" placeholder="选择您名下的项目组">
                <a-select-option v-for="p in myLeaderProjects" :key="p.id" :value="p.id">
                  {{ p.projectName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <!-- 赛事相关时关联竞赛 -->
          <a-col :span="12" v-else-if="form.postType === 'COMPETITION_EVENT'">
            <a-form-item label="关联学科竞赛">
              <a-select v-model:value="form.refId" placeholder="请选择关联赛事" allow-clear>
                <a-select-option v-for="c in competitions" :key="c.id" :value="c.id">
                  {{ c.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 帖子正文描述 -->
        <a-form-item label="正文描述与核心见解" required>
          <a-textarea
            v-model:value="form.content"
            :rows="6"
            placeholder="支持输入详尽的实训技术探讨、解题思维破局点、战队愿景或答辩评审避坑经验..."
            style="font-size: 14px; line-height: 1.6;"
          />
        </a-form-item>

        <!-- 多内容形态 1: 算法题解与核心代码精析专用区域 -->
        <div v-if="form.postType === 'CODE'" class="addon-section glass-card">
          <div class="addon-title">
            <span>💻 算法代码与复杂度分析配置</span>
            <span class="sub">（发布后将以暗黑高亮代码器向全校展示）</span>
          </div>

          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item label="编程语言">
                <a-select v-model:value="form.codeLanguage">
                  <a-select-option value="Java">Java 17 / 21</a-select-option>
                  <a-select-option value="Python">Python 3.x</a-select-option>
                  <a-select-option value="C++">C++ 17 / 20</a-select-option>
                  <a-select-option value="Go">Go 1.22</a-select-option>
                  <a-select-option value="TypeScript">TypeScript / Vue3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="时间复杂度">
                <a-input v-model:value="form.timeComplexity" placeholder="如：O(N log N) / O(V+E)" />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="空间复杂度">
                <a-input v-model:value="form.spaceComplexity" placeholder="如：O(1) 辅助空间 / O(N)" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="核心算法源码" required>
            <a-textarea
              v-model:value="form.codeSnippet"
              :rows="10"
              placeholder="// 在此粘贴算法核心实现代码..."
              style="font-family: monospace; font-size: 13px; background: #0f172a; color: #38bdf8;"
            />
          </a-form-item>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="输入样例 (Sample Input)">
                <a-textarea v-model:value="form.sampleInput" :rows="3" placeholder="例如：[3, 1, 4, 1, 5, 9, 2, 6]" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="输出样例 (Sample Output)">
                <a-textarea v-model:value="form.sampleOutput" :rows="3" placeholder="例如：21" />
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- 多内容形态 2: 组长团队引才招募令专用区域 -->
        <div v-if="form.postType === 'RECRUITMENT'" class="addon-section glass-card">
          <div class="addon-title">
            <span>📢 组长招募指标与技能需求</span>
          </div>

          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item label="拟招募重点角色" required>
                <a-input v-model:value="form.recruitRole" placeholder="如：动态规划算法攻关手 / PPT主讲人" />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="拟招人数上限">
                <a-input-number v-model:value="form.recruitCount" :min="1" :max="5" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="每周实训投入时间">
                <a-input v-model:value="form.timeRequirement" placeholder="如：每周不低于12小时，支持周末答辩" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="联系方式或投递指引">
            <a-input v-model:value="form.contactInfo" placeholder="如：组长QQ：12345678 或直接站内点击申请加入" />
          </a-form-item>
        </div>

        <!-- 附件上传（支持图文方案、PDF、ZIP数据集等） -->
        <div class="attachment-section">
          <div class="attachment-header">
            <span>📎 方案附件与数据集 / 架构图 (可选)</span>
            <a-upload
              :show-upload-list="false"
              :customRequest="handleFileUpload"
              accept=".pdf,.zip,.rar,.png,.jpg,.jpeg,.doc,.docx"
            >
              <a-button size="small" :loading="uploadingFile">
                <template #icon><UploadOutlined /></template>
                上传本地附件/架构图
              </a-button>
            </a-upload>
          </div>

          <div v-if="form.attachments.length > 0" class="attachments-list">
            <div v-for="(file, idx) in form.attachments" :key="idx" class="att-item">
              <span class="att-icon">📄</span>
              <a :href="file.url" target="_blank" class="att-name">{{ file.name }}</a>
              <a-button type="link" danger size="small" @click="removeAttachment(idx)">移除</a-button>
            </div>
          </div>
        </div>

        <!-- AI 安全风控提示条 -->
        <div class="ai-banner">
          <a-alert
            message="AI 大模型全自动化语意风控守护"
            description="本平台已启用 AI 实时审查网关。健康学术探讨、高质量解题与合法招募将秒级发布上线；若检测到敏感或违规内容，系统将自动留存并转入导师/组长联审专区。"
            type="info"
            show-icon
          />
        </div>

        <!-- 提交控制栏 -->
        <div class="form-actions">
          <a-button size="large" shape="round" @click="$router.push('/forum')">取消</a-button>
          <a-button
            type="primary"
            size="large"
            shape="round"
            class="submit-btn"
            :loading="submitting"
            @click="handleSubmitPost"
          >
            🚀 确认发布研讨帖 (AI实时审查)
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'
import { usePortalStore } from '@/store/portal'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const portalStore = usePortalStore()

const submitting = ref(false)
const uploadingFile = ref(false)
const competitions = ref([])
const myLeaderProjects = ref([])

const filteredBoards = computed(() => {
  if (!portalStore.boards || portalStore.boards.length === 0) return []
  return portalStore.boards.filter(b => {
    if (b.status !== 'ENABLE') return false
    if (b.postPermission === 'TEACHER') return userStore.isTeacher
    if (b.postPermission === 'LEADER') return userStore.isTeacher || userStore.isLeader
    return true
  })
})

const popularTags = [
  { value: '动态规划' },
  { value: '贪心算法' },
  { value: '图论与搜索' },
  { value: 'ACM/ICPC' },
  { value: '天池实战' },
  { value: '全栈架构' },
  { value: 'Vue3实训' },
  { value: '国家级竞赛' },
  { value: '组队引才' }
]

const form = reactive({
  title: '',
  content: '',
  postType: 'GENERAL',
  boardCode: undefined,
  refType: 'NONE',
  refId: undefined,
  tags: ['动态规划', '实训心得'],
  // 算法代码扩展
  codeLanguage: 'Java',
  codeSnippet: '',
  timeComplexity: '',
  spaceComplexity: '',
  sampleInput: '',
  sampleOutput: '',
  // 招募令扩展
  recruitRole: '',
  recruitCount: 2,
  timeRequirement: '',
  contactInfo: '',
  // 附件列表
  attachments: []
})

const getTitlePlaceholder = () => {
  if (form.postType === 'CODE') return '如：【算法精析】动态规划最长递增子序列从 O(N^2) 到 O(N log N) 极限优化'
  if (form.postType === 'RECRUITMENT') return '如：【创客实训队引才】诚招1名Vue3前端交互与1名算法答辩核心成员'
  if (form.postType === 'COMPETITION_EVENT') return '如：【国赛通关秘籍】第十一届算法实训挑战赛备赛答辩复盘与避坑建议'
  return '输入研讨帖标题，简明扼要阐述研讨核心内容...'
}

const onPostTypeChange = () => {
  if (form.postType === 'RECRUITMENT') {
    form.refType = 'PROJECT'
    if (myLeaderProjects.value.length > 0) {
      form.refId = myLeaderProjects.value[0].id
    }
  } else if (form.postType === 'COMPETITION_EVENT') {
    form.refType = 'COMPETITION'
    if (competitions.value.length > 0) {
      form.refId = competitions.value[0].id
    }
  } else {
    form.refType = 'NONE'
    form.refId = undefined
  }
}

// 附件上传
const handleFileUpload = async ({ file }) => {
  uploadingFile.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const url = typeof res === 'string' ? res : (res?.data || res)
    form.attachments.push({
      name: file.name,
      url,
      size: (file.size / 1024 / 1024).toFixed(2) + ' MB'
    })
    message.success('附件上传成功！')
  } catch (e) {
    message.error('文件上传失败')
  } finally {
    uploadingFile.value = false
  }
}

const removeAttachment = (index) => {
  form.attachments.splice(index, 1)
}

// 提交发布
const handleSubmitPost = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (!form.title.trim() || !form.content.trim()) {
    message.warning('请填写研讨标题与描述正文')
    return
  }
  if (form.postType === 'CODE' && !form.codeSnippet.trim()) {
    message.warning('您选择了算法题解形态，请填入核心算法源码')
    return
  }

  submitting.value = true
  try {
    // 构造 extJson 支持多模态内容形式
    const extObj = {
      contentType: form.postType,
      tags: form.tags,
      attachments: form.attachments,
      codeLanguage: form.codeLanguage,
      codeSnippet: form.codeSnippet,
      timeComplexity: form.timeComplexity,
      spaceComplexity: form.spaceComplexity,
      sampleInput: form.sampleInput,
      sampleOutput: form.sampleOutput,
      recruitRole: form.recruitRole,
      recruitCount: form.recruitCount,
      timeRequirement: form.timeRequirement,
      contactInfo: form.contactInfo
    }

    // 后端规范分类映射（若用户选了具体的社区板块，则归入该板块；否则映射形态分类）
    const finalPostType = form.boardCode || (form.postType === 'CODE' ? 'GENERAL' : form.postType)

    const res = await portalApi.createForumPost({
      title: form.title,
      content: form.content,
      postType: finalPostType,
      refType: form.refType,
      refId: form.refId,
      extJson: JSON.stringify(extObj)
    })

    message.success(res || '发帖成功！')
    router.push('/forum')
  } catch (e) {
    message.error(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

// 加载基础数据
const loadInitialData = async () => {
  try {
    portalStore.fetchBoards()
    const compRes = await portalApi.getCompetitions({ current: 1, size: 50 })
    competitions.value = compRes.records || compRes.data?.records || []

    if (userStore.isLoggedIn) {
      const projRes = await portalApi.getMyProjects()
      const list = Array.isArray(projRes) ? projRes : (projRes?.data || [])
      let led = []
      if (projRes && (projRes.ledProjects || projRes.joinedProjects)) {
        led = projRes.ledProjects || []
      } else {
        led = list.filter(p => p.isLeader)
      }
      myLeaderProjects.value = led
    }
  } catch (e) {}

  // 支持预置类型从 query 传入，如 /forum/publish?type=CODE
  if (route.query.type) {
    form.postType = route.query.type
    onPostTypeChange()
  }
}

onMounted(() => {
  loadInitialData()
})
</script>

<style scoped>
.forum-publish-page {
  max-width: 1040px;
  margin: 32px auto;
  padding: 0 20px;
}

.publish-header {
  padding: 24px 32px;
  margin-bottom: 24px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.header-main h2 {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.header-main p {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}

.publish-content {
  padding: 32px;
}

.addon-section {
  padding: 24px;
  margin-bottom: 24px;
  background: #f8fafc;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.addon-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
}

.addon-title .sub {
  font-size: 12px;
  color: #94a3b8;
  font-weight: normal;
}

.attachment-section {
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
}

.attachment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #475569;
}

.attachments-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.att-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.att-name {
  flex: 1;
  color: #4f46e5;
  text-decoration: underline;
  font-size: 13px;
}

.ai-banner {
  margin-bottom: 28px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.submit-btn {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
  font-weight: 700;
  padding: 0 32px;
}
</style>
