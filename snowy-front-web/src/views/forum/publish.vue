<template>
  <div class="forum-publish-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav-bar glass-card">
      <div class="nav-left">
        <a-button type="link" class="back-btn" @click="handleGoBack">
          ← 返回社区论坛
        </a-button>
        <span class="divider">|</span>
        <span class="page-title">✍️ 发布社区研讨 (通用模板)</span>
        <a-tag color="blue" class="ai-tag">AI 智能合规风控已就绪</a-tag>
      </div>

      <div class="nav-right">
        <!-- 视图切换模式 -->
        <a-radio-group v-model:value="viewMode" size="small" button-style="solid" class="view-mode-group">
          <a-radio-button value="split">
            <template #icon><ColumnWidthOutlined /></template>
            双栏对照
          </a-radio-button>
          <a-radio-button value="edit">
            <template #icon><EditOutlined /></template>
            纯编辑
          </a-radio-button>
          <a-radio-button value="preview">
            <template #icon><EyeOutlined /></template>
            纯预览
          </a-radio-button>
        </a-radio-group>

        <a-button
          type="primary"
          shape="round"
          class="publish-submit-btn"
          :loading="submitting"
          @click="handleSubmitPost"
        >
          🚀 确认发布研讨
        </a-button>
      </div>
    </div>

    <!-- 主编辑工作区 -->
    <div class="publish-container">
      <!-- 标题输入卡片 -->
      <div class="title-card glass-card">
        <a-input
          ref="titleInputRef"
          v-model:value="form.title"
          class="article-title-input"
          placeholder="请输入研讨标题（5 - 100个字符）..."
          :maxlength="100"
          allow-clear
        />
        <div class="title-count-tip">{{ form.title.length }} / 100</div>
      </div>

      <!-- 分类与元信息工具栏 -->
      <div class="meta-card glass-card">
        <a-row :gutter="16" align="middle">
          <!-- 话题标签 -->
          <a-col :xs="24" :sm="24" :md="10" :lg="10">
            <div class="meta-item">
              <span class="meta-label">🏷️ 话题标签：</span>
              <a-select
                v-model:value="form.tags"
                mode="tags"
                placeholder="选择或输入标签后回车（最多5个）"
                style="width: 100%"
                :max-tag-count="4"
                :options="popularTagOptions"
              />
            </div>
          </a-col>

          <!-- 研讨板块 -->
          <a-col :xs="24" :sm="12" :md="7" :lg="7">
            <div class="meta-item">
              <span class="meta-label">📁 归属板块：</span>
              <a-select
                v-model:value="form.boardCode"
                placeholder="通用研讨专区 (默认)"
                style="width: 100%"
                allow-clear
              >
                <a-select-option value="GENERAL">💡 综合学术探讨 (通用)</a-select-option>
                <a-select-option v-for="b in filteredBoards" :key="b.boardCode" :value="b.boardCode">
                  {{ b.icon || '💬' }} {{ b.boardName }}
                </a-select-option>
              </a-select>
            </div>
          </a-col>

          <!-- 关联项目/竞赛 (可选扩展) -->
          <a-col :xs="24" :sm="12" :md="7" :lg="7">
            <div class="meta-item">
              <span class="meta-label">🔗 关联实体：</span>
              <a-select
                v-model:value="selectedRefKey"
                placeholder="无关联 (纯技术交流)"
                style="width: 100%"
                allow-clear
                @change="onRefChange"
              >
                <a-select-option value="NONE">无关联 (通用研讨)</a-select-option>
                <a-select-opt-group label="我带领/参与的项目团队" v-if="myProjects.length > 0">
                  <a-select-option v-for="p in myProjects" :key="'PROJECT:' + p.id" :value="'PROJECT:' + p.id">
                    🚀 {{ p.projectName }}
                  </a-select-option>
                </a-select-opt-group>
                <a-select-opt-group label="学科竞赛" v-if="competitions.length > 0">
                  <a-select-option v-for="c in competitions" :key="'COMPETITION:' + c.id" :value="'COMPETITION:' + c.id">
                    🏆 {{ c.title }}
                  </a-select-option>
                </a-select-opt-group>
              </a-select>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- CSDN 级编辑器卡片 -->
      <div class="editor-card glass-card">
        <!-- 编辑器顶部工具条 (Toolbar) -->
        <div class="editor-toolbar">
          <div class="toolbar-group">
            <a-tooltip title="一级标题 (H1)">
              <button type="button" class="tool-btn" @click="insertHeader(1)">H1</button>
            </a-tooltip>
            <a-tooltip title="二级标题 (H2)">
              <button type="button" class="tool-btn" @click="insertHeader(2)">H2</button>
            </a-tooltip>
            <a-tooltip title="三级标题 (H3)">
              <button type="button" class="tool-btn" @click="insertHeader(3)">H3</button>
            </a-tooltip>
            <span class="tool-divider"></span>
            <a-tooltip title="粗体 (Ctrl+B)">
              <button type="button" class="tool-btn" @click="insertFormat('**', '**', '加粗文字')">
                <BoldOutlined />
              </button>
            </a-tooltip>
            <a-tooltip title="斜体 (Ctrl+I)">
              <button type="button" class="tool-btn" @click="insertFormat('*', '*', '斜体文字')">
                <ItalicOutlined />
              </button>
            </a-tooltip>
            <a-tooltip title="引用文本">
              <button type="button" class="tool-btn" @click="insertPrefix('> ', '引用一段核心见解或规范')">
                <FileTextOutlined />
              </button>
            </a-tooltip>
            <span class="tool-divider"></span>
            <a-tooltip title="无序列表">
              <button type="button" class="tool-btn" @click="insertPrefix('- ', '列表项目')">
                <UnorderedListOutlined />
              </button>
            </a-tooltip>
            <a-tooltip title="有序列表">
              <button type="button" class="tool-btn" @click="insertPrefix('1. ', '步骤一')">
                <OrderedListOutlined />
              </button>
            </a-tooltip>
            <span class="tool-divider"></span>
            <a-tooltip title="插入代码块">
              <button type="button" class="tool-btn" @click="insertCodeBlock">
                <CodeOutlined />
              </button>
            </a-tooltip>
            <a-tooltip title="插入超链接">
              <button type="button" class="tool-btn" @click="insertLink">
                <LinkOutlined />
              </button>
            </a-tooltip>
            <a-tooltip title="插入图片 (支持本地上传/网络URL/剪贴板截图粘贴)">
              <button type="button" class="tool-btn highlight" @click="openImageModal">
                <PictureOutlined /> 插入图片
              </button>
            </a-tooltip>
            <a-tooltip title="插入标准表格">
              <button type="button" class="tool-btn" @click="insertTable">
                📊 表格
              </button>
            </a-tooltip>
            <a-tooltip title="分割线">
              <button type="button" class="tool-btn" @click="insertText('\n---\n')">
                — 分割线
              </button>
            </a-tooltip>
          </div>

          <div class="toolbar-group right">
            <span class="paste-tip">
              💡 提示：在正文中按 <strong>Ctrl+V</strong> 可直接粘贴截图并自动上传！
            </span>
            <a-button type="link" size="small" @click="clearDraft">清空草稿</a-button>
          </div>
        </div>

        <!-- 编辑与实时预览双栏主体 -->
        <div class="editor-workspace" :class="'mode-' + viewMode">
          <!-- 左侧：编辑区 -->
          <div v-show="viewMode !== 'preview'" class="editor-pane">
            <textarea
              ref="editorTextareaRef"
              v-model="form.content"
              class="markdown-textarea"
              placeholder="正文内容（支持图文混排）：在此输入研讨正文、算法解题破局点、技术架构思考...

• 支持直接按 Ctrl+V 粘贴截图，系统全自动极速上传并插入图片！
• 支持将本地图片直接拖拽至此区域自动上传！
• 支持标准 Markdown 语法，右侧实时双栏对照渲染呈现。"
              @paste="handlePaste"
              @drop="handleDrop"
              @keydown.tab.prevent="handleTabKey"
              @input="handleContentInput"
            ></textarea>
          </div>

          <!-- 右侧：实时预览区 -->
          <div v-show="viewMode !== 'edit'" class="preview-pane">
            <div class="preview-header">
              <span>👁️ 实时预览渲染效果</span>
              <span class="preview-badge">Markdown + 图片混排</span>
            </div>
            <div
              v-if="renderedContent"
              class="preview-content markdown-body"
              v-html="renderedContent"
            ></div>
            <div v-else class="preview-empty">
              <p>暂无内容，请在左侧编辑器中输入文字或粘贴图片，此处将实时渲染...</p>
            </div>
          </div>
        </div>

        <!-- 编辑器底部状态条 -->
        <div class="editor-statusbar">
          <div class="status-left">
            <span>📝 字数统计：<strong>{{ stats.charCount }}</strong> 字</span>
            <span class="dot">·</span>
            <span>📑 行数：<strong>{{ stats.lineCount }}</strong> 行</span>
            <span class="dot">·</span>
            <span>🖼️ 包含图片：<strong>{{ stats.imageCount }}</strong> 张</span>
            <span class="dot">·</span>
            <span>⏱️ 预计阅读时长：约 <strong>{{ stats.readTime }}</strong> 分钟</span>
          </div>
          <div class="status-right">
            <span class="draft-tip">💾 {{ draftSavedText }}</span>
          </div>
        </div>
      </div>

      <!-- 附件上传折叠区域 (可选方案附件) -->
      <div class="attachment-card glass-card">
        <div class="att-header">
          <div class="att-title">
            <span>📎 方案附件与数据集 / 架构方案 (可选)</span>
            <span class="att-sub">（支持 PDF 文档、ZIP 压缩包、项目架构图等）</span>
          </div>
          <a-upload
            :show-upload-list="false"
            :customRequest="handleFileUpload"
            accept=".pdf,.zip,.rar,.png,.jpg,.jpeg,.doc,.docx"
          >
            <a-button size="small" :loading="uploadingFile">
              <template #icon><UploadOutlined /></template>
              上传附件材料
            </a-button>
          </a-upload>
        </div>

        <div v-if="form.attachments.length > 0" class="att-list">
          <div v-for="(file, idx) in form.attachments" :key="idx" class="att-item">
            <span class="att-icon">📄</span>
            <a :href="file.url" target="_blank" class="att-name">{{ file.name }}</a>
            <span class="att-size">{{ file.size }}</span>
            <a-button type="link" danger size="small" @click="removeAttachment(idx)">移除</a-button>
          </div>
        </div>
      </div>

      <!-- 底部安全与发布横幅 -->
      <div class="bottom-action-bar glass-card">
        <div class="ai-notice">
          <span class="pulse-dot"></span>
          <span>🛡️ <strong>AI 智能安全风控网关守护</strong>：健康学术探讨、技术答疑与项目研讨将秒级自动上线发布；若包含异常词汇将转入人工联审专区。</span>
        </div>
        <div class="action-buttons">
          <a-button size="large" shape="round" @click="handleGoBack">取消发帖</a-button>
          <a-button
            type="primary"
            size="large"
            shape="round"
            class="submit-btn"
            :loading="submitting"
            @click="handleSubmitPost"
          >
            🚀 确认发布研讨 (AI实时审查)
          </a-button>
        </div>
      </div>
    </div>

    <!-- 弹窗：插入图片 -->
    <a-modal
      v-model:open="imageModalOpen"
      title="🖼️ 插入图片至正文"
      :footer="null"
      :width="520"
      destroy-on-close
    >
      <a-tabs v-model:activeKey="imageTabKey" centered>
        <!-- 本地上传 -->
        <a-tab-pane key="upload" tab="本地文件上传">
          <div class="image-upload-box">
            <a-upload-dragger
              name="file"
              :show-upload-list="false"
              :customRequest="handleImageModalUpload"
              accept="image/*"
            >
              <p class="ant-upload-drag-icon">
                <PictureOutlined style="font-size: 40px; color: #3b82f6;" />
              </p>
              <p class="ant-upload-text">点击或将本地图片拖拽至此区域上传</p>
              <p class="ant-upload-hint">支持 PNG, JPG, JPEG, GIF, WEBP 格式图片，自动压缩优化</p>
            </a-upload-dragger>
            <div v-if="uploadingImage" style="text-align: center; margin-top: 12px; color: #3b82f6;">
              <a-spin /> 正在上传图片并生成链接...
            </div>
          </div>
        </a-tab-pane>

        <!-- 网络图片URL -->
        <a-tab-pane key="url" tab="输入网络图片地址">
          <div style="padding: 16px 0;">
            <a-form layout="vertical">
              <a-form-item label="图片 URL 地址" required>
                <a-input v-model:value="networkImageUrl" placeholder="https://example.com/image.png" allow-clear />
              </a-form-item>
              <a-form-item label="图片描述 (Alt Text)">
                <a-input v-model:value="networkImageAlt" placeholder="请输入图片描述（可选）" allow-clear />
              </a-form-item>
              <a-button type="primary" block @click="confirmInsertNetworkImage">
                确认插入图片
              </a-button>
            </a-form>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  UploadOutlined,
  PictureOutlined,
  BoldOutlined,
  ItalicOutlined,
  LinkOutlined,
  CodeOutlined,
  UnorderedListOutlined,
  OrderedListOutlined,
  FileTextOutlined,
  EyeOutlined,
  EditOutlined,
  ColumnWidthOutlined
} from '@ant-design/icons-vue'
import { marked } from 'marked'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'
import { usePortalStore } from '@/store/portal'

const router = useRouter()
const userStore = useUserStore()
const portalStore = usePortalStore()

// 编辑器 DOM 引用
const editorTextareaRef = ref(null)
const titleInputRef = ref(null)

// 视图模式: split (双栏), edit (纯编辑), preview (纯预览)
const viewMode = ref('split')
const submitting = ref(false)
const uploadingFile = ref(false)
const uploadingImage = ref(false)
const draftSavedText = ref('草稿自动保存中')

// 关联下拉选中的复合 Key: "NONE" | "PROJECT:id" | "COMPETITION:id"
const selectedRefKey = ref('NONE')
const competitions = ref([])
const myProjects = ref([])

// 弹窗插入图片状态
const imageModalOpen = ref(false)
const imageTabKey = ref('upload')
const networkImageUrl = ref('')
const networkImageAlt = ref('')

// 常用推荐标签
const popularTagOptions = [
  { value: '动态规划' },
  { value: '贪心算法' },
  { value: '二叉树与图论' },
  { value: 'ACM/ICPC' },
  { value: '蓝桥杯实战' },
  { value: '全栈架构设计' },
  { value: 'Vue3实训心得' },
  { value: '国家级学科竞赛' },
  { value: '组队引才' },
  { value: '答辩避坑指南' }
]

// 统一发帖表单（通用研讨模板）
const form = reactive({
  title: '',
  content: '',
  postType: 'GENERAL',
  boardCode: undefined,
  refType: 'NONE',
  refId: undefined,
  tags: ['动态规划', '实训心得'],
  attachments: []
})

// 归属板块
const filteredBoards = computed(() => {
  if (!portalStore.boards || portalStore.boards.length === 0) return []
  return portalStore.boards.filter(b => {
    if (b.status !== 'ENABLE') return false
    if (b.postPermission === 'TEACHER') return userStore.isTeacher
    if (b.postPermission === 'LEADER') return userStore.isTeacher || userStore.isLeader
    return true
  })
})

// 统一将带有特定端口的本地图片 URL 转换为同源安全相对路径 /api
const normalizeImageUrl = (url) => {
  if (!url || typeof url !== 'string') return ''
  return url.replace(/^http:\/\/localhost:\d+\/api/, '/api')
}

// 实时渲染 Markdown，自动规范化图片链接支持实时预览
const renderedContent = computed(() => {
  if (!form.content || !form.content.trim()) return ''
  try {
    const safeContent = form.content.replace(/http:\/\/localhost:\d+\/api/g, '/api')
    return marked.parse(safeContent)
  } catch (err) {
    return form.content
  }
})

// 文章统计
const stats = computed(() => {
  const text = form.content || ''
  const charCount = text.length
  const lineCount = text ? text.split('\n').length : 0
  const imageMatches = text.match(/!\[.*?\]\(.*?\)/g)
  const imageCount = imageMatches ? imageMatches.length : 0
  const readTime = Math.max(1, Math.ceil(charCount / 400))
  return { charCount, lineCount, imageCount, readTime }
})

onMounted(async () => {
  // 加载竞赛列表与用户参与的项目
  loadContextData()
  // 恢复本地草稿
  restoreDraft()
  // 加载板块数据
  if (portalStore.getBoards) {
    portalStore.getBoards()
  }
})

const loadContextData = async () => {
  try {
    const compRes = await portalApi.getCompetitions({ current: 1, size: 50 })
    competitions.value = compRes?.records || compRes || []
  } catch (ignored) {}

  try {
    const projRes = await portalApi.getMyProjects()
    myProjects.value = projRes || []
  } catch (ignored) {}
}

const onRefChange = (val) => {
  if (!val || val === 'NONE') {
    form.refType = 'NONE'
    form.refId = undefined
  } else {
    const [type, id] = val.split(':')
    form.refType = type
    form.refId = id
  }
}

// ==================== 编辑器辅助工具函数 ====================
// 在光标位置插入文本
const insertAtCursor = (textToInsert, selectOffset = 0, selectLength = 0) => {
  const textarea = editorTextareaRef.value
  if (!textarea) {
    form.content += textToInsert
    return
  }

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const content = form.content

  form.content = content.substring(0, start) + textToInsert + content.substring(end)

  nextTick(() => {
    textarea.focus()
    const newPos = start + selectOffset
    textarea.setSelectionRange(newPos, newPos + selectLength)
    saveDraft()
  })
}

// 插入标题
const insertHeader = (level) => {
  const prefix = '#'.repeat(level) + ' '
  insertAtCursor(`\n${prefix}标题文字\n`, prefix.length + 1, 4)
}

// 包裹格式（如 **粗体**）
const insertFormat = (before, after, defaultText) => {
  const textarea = editorTextareaRef.value
  if (!textarea) return
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = form.content.substring(start, end) || defaultText
  const replacement = before + selected + after
  insertAtCursor(replacement, before.length, selected.length)
}

// 插入前缀（如 > 引用，- 列表）
const insertPrefix = (prefix, defaultText) => {
  insertAtCursor(`\n${prefix}${defaultText}\n`, prefix.length + 1, defaultText.length)
}

// 插入代码块
const insertCodeBlock = () => {
  const sample = '\n```java\n// 在此编写算法实现代码\npublic class Solution {\n    public static void main(String[] args) {\n        \n    }\n}\n```\n'
  insertAtCursor(sample, 9, 14)
}

// 插入超链接
const insertLink = () => {
  insertAtCursor('[链接描述文字](https://example.com)', 1, 6)
}

// 插入表格
const insertTable = () => {
  const table = '\n| 列名 1 | 列名 2 | 说明 |\n| :--- | :--- | :--- |\n| 数据项 A | 数据项 B | 备注信息 |\n| 数据项 C | 数据项 D | 备注信息 |\n'
  insertAtCursor(table)
}

const insertText = (str) => {
  insertAtCursor(str)
}

// Tab 键输入 2 个空格缩进
const handleTabKey = () => {
  insertAtCursor('  ', 2, 0)
}

// ==================== 核心：图片上传与剪贴板粘贴 ====================
// 打开插入图片弹窗
const openImageModal = () => {
  imageModalOpen.value = true
  networkImageUrl.value = ''
  networkImageAlt.value = ''
}

// 确认插入网络图片
const confirmInsertNetworkImage = () => {
  if (!networkImageUrl.value.trim()) {
    message.warning('请输入有效的图片 URL 地址')
    return
  }
  const alt = networkImageAlt.value.trim() || '网络图片'
  insertAtCursor(`\n![${alt}](${networkImageUrl.value.trim()})\n`)
  imageModalOpen.value = false
  message.success('图片已插入至正文')
}

// 弹窗中本地上传图片
const handleImageModalUpload = async ({ file }) => {
  uploadingImage.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const rawUrl = typeof res === 'string' ? res : (res?.data || res)
    const url = normalizeImageUrl(rawUrl)
    const alt = file.name.replace(/\.[^/.]+$/, '') || '插图'
    insertAtCursor(`\n![${alt}](${url})\n`)
    imageModalOpen.value = false
    message.success('图片上传成功并已插入正文！')
  } catch (e) {
    message.error('图片上传失败，请重试')
  } finally {
    uploadingImage.value = false
  }
}

// 核心：监听剪贴板粘贴（Ctrl+V 截图直接上传插入）
const handlePaste = async (event) => {
  const items = (event.clipboardData || event.originalEvent?.clipboardData)?.items
  if (!items) return

  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    if (item.type.indexOf('image') !== -1) {
      event.preventDefault()
      const blob = item.getAsFile()
      if (!blob) return

      const hideMsg = message.loading('检测到剪贴板截图，正在全速自动上传...', 0)
      try {
        const formData = new FormData()
        formData.append('file', blob, `screenshot_${Date.now()}.png`)
        const res = await portalApi.uploadFile(formData)
        const rawUrl = typeof res === 'string' ? res : (res?.data || res)
        const url = normalizeImageUrl(rawUrl)
        insertAtCursor(`\n![截图](${url})\n`)
        message.success('截图已自动上传并插入至光标位置！')
      } catch (err) {
        message.error('截图自动上传失败')
      } finally {
        hideMsg()
      }
      break
    }
  }
}

// 核心：监听拖拽图片直接上传
const handleDrop = async (event) => {
  const files = event.dataTransfer?.files
  if (!files || files.length === 0) return

  const imageFiles = Array.from(files).filter(f => f.type.startsWith('image/'))
  if (imageFiles.length === 0) return

  event.preventDefault()
  const hideMsg = message.loading(`正在自动上传拖入的 ${imageFiles.length} 张图片...`, 0)
  try {
    for (const file of imageFiles) {
      const formData = new FormData()
      formData.append('file', file)
      const res = await portalApi.uploadFile(formData)
      const rawUrl = typeof res === 'string' ? res : (res?.data || res)
      const url = normalizeImageUrl(rawUrl)
      const alt = file.name.replace(/\.[^/.]+$/, '') || '拖拽图片'
      insertAtCursor(`\n![${alt}](${url})\n`)
    }
    message.success('图片拖拽上传成功！')
  } catch (e) {
    message.error('图片拖拽上传失败')
  } finally {
    hideMsg()
  }
}

// ==================== 附件上传 ====================
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
    message.success('附件材料上传成功！')
    saveDraft()
  } catch (e) {
    message.error('附件上传失败')
  } finally {
    uploadingFile.value = false
  }
}

const removeAttachment = (index) => {
  form.attachments.splice(index, 1)
  saveDraft()
}

// ==================== 草稿自动保存机制 ====================
let saveTimer = null
const handleContentInput = () => {
  clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    saveDraft()
  }, 800)
}

const saveDraft = () => {
  try {
    const draft = {
      title: form.title,
      content: form.content,
      tags: form.tags,
      boardCode: form.boardCode,
      attachments: form.attachments,
      time: new Date().toLocaleTimeString()
    }
    localStorage.setItem('TULING_FORUM_DRAFT', JSON.stringify(draft))
    draftSavedText.value = `已自动保存于 ${draft.time}`
  } catch (ignored) {}
}

const restoreDraft = () => {
  try {
    const raw = localStorage.getItem('TULING_FORUM_DRAFT')
    if (raw) {
      const draft = JSON.parse(raw)
      if (draft.title) form.title = draft.title
      if (draft.content) form.content = draft.content
      if (draft.tags) form.tags = draft.tags
      if (draft.boardCode) form.boardCode = draft.boardCode
      if (draft.attachments) form.attachments = draft.attachments
      draftSavedText.value = `已恢复本地草稿 (${draft.time || ''})`
    }
  } catch (ignored) {}
}

const clearDraft = () => {
  Modal.confirm({
    title: '确定清空当前草稿吗？',
    content: '清空后编辑中的正文和标题将无法恢复。',
    onOk() {
      form.title = ''
      form.content = ''
      form.attachments = []
      localStorage.removeItem('TULING_FORUM_DRAFT')
      draftSavedText.value = '草稿已清空'
      message.info('草稿已清空')
    }
  })
}

const handleGoBack = () => {
  router.push('/forum')
}

// ==================== 提交发布研讨 ====================
const handleSubmitPost = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再发布研讨')
    router.push('/login')
    return
  }
  if (!form.title.trim()) {
    message.warning('请填写文章研讨标题')
    titleInputRef.value?.focus()
    return
  }
  if (!form.content.trim()) {
    message.warning('正文内容不能为空，请分享您的核心见解或代码思路')
    editorTextareaRef.value?.focus()
    return
  }

  submitting.value = true
  try {
    // 构造通用 extJson
    const extObj = {
      tags: form.tags,
      attachments: form.attachments,
      charCount: stats.value.charCount,
      imageCount: stats.value.imageCount
    }

    const finalPostType = form.boardCode || 'GENERAL'
    // 提交前清洗图片 URL：将写死端口的绝对路径转化为标准的相对代理路径 /api/...
    const sanitizedContent = (form.content || '').replace(/http:\/\/localhost:\d+\/api/g, '/api')

    const res = await portalApi.createForumPost({
      title: form.title.trim(),
      content: sanitizedContent,
      postType: finalPostType,
      refType: form.refType,
      refId: form.refId,
      extJson: JSON.stringify(extObj)
    })

    // 清除草稿
    localStorage.removeItem('TULING_FORUM_DRAFT')
    message.success(res || '发帖成功！')
    router.push('/forum')
  } catch (e) {
    message.error(e.message || '发布研讨失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.forum-publish-page {
  min-height: 100vh;
  padding: 16px 24px 48px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.top-nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  border-radius: 16px;
  margin-bottom: 16px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  padding: 0;
  font-weight: 600;
  color: #4f46e5;
}

.divider {
  color: #cbd5e1;
}

.page-title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

.ai-tag {
  border-radius: 12px;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.publish-submit-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
  font-weight: 700;
  padding: 0 20px;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25);
}

.publish-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 标题卡片 */
.title-card {
  position: relative;
  padding: 16px 20px;
  border-radius: 16px;
}

.article-title-input {
  font-size: 20px;
  font-weight: 700;
  border: none;
  box-shadow: none;
  padding: 8px 0;
  color: #0f172a;
}

.article-title-input:focus {
  border: none;
  box-shadow: none;
}

.title-count-tip {
  position: absolute;
  right: 20px;
  bottom: 16px;
  font-size: 12px;
  color: #94a3b8;
}

/* 分类与元信息卡片 */
.meta-card {
  padding: 14px 20px;
  border-radius: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  white-space: nowrap;
}

/* CSDN 级编辑器 */
.editor-card {
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 编辑器工具条 */
.editor-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 4px;
}

.tool-btn {
  height: 30px;
  padding: 0 8px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 6px;
  color: #334155;
  font-size: 13px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.15s ease;
}

.tool-btn:hover {
  background: #e2e8f0;
  color: #0f172a;
}

.tool-btn.highlight {
  background: #eff6ff;
  color: #2563eb;
  border-color: #bfdbfe;
  font-weight: 600;
}

.tool-btn.highlight:hover {
  background: #dbeafe;
}

.tool-divider {
  width: 1px;
  height: 16px;
  background: #cbd5e1;
  margin: 0 4px;
}

.paste-tip {
  font-size: 12px;
  color: #64748b;
}

/* 双栏工作区 */
.editor-workspace {
  display: flex;
  min-height: 580px;
  background: #ffffff;
}

.editor-workspace.mode-split .editor-pane {
  flex: 1;
  border-right: 1px solid #e2e8f0;
}

.editor-workspace.mode-split .preview-pane {
  flex: 1;
}

.editor-workspace.mode-edit .editor-pane {
  flex: 1;
}

.editor-workspace.mode-preview .preview-pane {
  flex: 1;
}

/* 编辑输入域 */
.editor-pane {
  display: flex;
  flex-direction: column;
}

.markdown-textarea {
  flex: 1;
  width: 100%;
  min-height: 580px;
  padding: 20px;
  border: none;
  outline: none;
  resize: vertical;
  font-family: Consolas, Monaco, "Courier New", monospace;
  font-size: 14.5px;
  line-height: 1.7;
  color: #1e293b;
  background: #ffffff;
}

/* 预览域 */
.preview-pane {
  display: flex;
  flex-direction: column;
  background: #fafafa;
  overflow-y: auto;
  max-height: 700px;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background: #f1f5f9;
  border-bottom: 1px solid #e2e8f0;
  font-size: 12px;
  font-weight: 700;
  color: #475569;
}

.preview-badge {
  color: #3b82f6;
  font-size: 11px;
}

.preview-content {
  padding: 24px;
  flex: 1;
  background: #ffffff;
}

.preview-empty {
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}

/* 状态栏 */
.editor-statusbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  font-size: 12px;
  color: #64748b;
}

.status-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  color: #cbd5e1;
}

/* 附件卡片 */
.attachment-card {
  padding: 16px 20px;
  border-radius: 16px;
}

.att-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.att-title {
  font-weight: 700;
  color: #1e293b;
}

.att-sub {
  font-size: 12px;
  color: #94a3b8;
  margin-left: 6px;
  font-weight: normal;
}

.att-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.att-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 13px;
}

.att-name {
  color: #2563eb;
  font-weight: 500;
}

.att-size {
  color: #94a3b8;
  font-size: 12px;
}

/* 底部发布栏 */
.bottom-action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-radius: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.ai-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #475569;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submit-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
  font-weight: 700;
  box-shadow: 0 4px 14px rgba(79, 70, 229, 0.3);
}

.image-upload-box {
  padding: 16px 0;
}

/* Markdown 内容样式定义 */
:deep(.markdown-body) {
  font-size: 14.5px;
  line-height: 1.8;
  color: #1e293b;
}

:deep(.markdown-body h1) {
  font-size: 24px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 8px;
  margin-top: 16px;
  margin-bottom: 12px;
}

:deep(.markdown-body h2) {
  font-size: 20px;
  border-left: 4px solid #4f46e5;
  padding-left: 10px;
  margin-top: 18px;
  margin-bottom: 10px;
}

:deep(.markdown-body h3) {
  font-size: 16.5px;
  margin-top: 14px;
  margin-bottom: 8px;
}

:deep(.markdown-body p) {
  margin-bottom: 12px;
}

:deep(.markdown-body img) {
  max-width: 96%;
  height: auto;
  border-radius: 8px;
  margin: 14px auto;
  display: block;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

:deep(.markdown-body pre) {
  background: #0f172a;
  color: #38bdf8;
  padding: 14px 18px;
  border-radius: 8px;
  overflow-x: auto;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  line-height: 1.5;
  margin: 14px 0;
}

:deep(.markdown-body code) {
  background: #f1f5f9;
  color: #0f172a;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: Consolas, monospace;
  font-size: 12.5px;
}

:deep(.markdown-body pre code) {
  background: transparent;
  color: inherit;
  padding: 0;
}

:deep(.markdown-body blockquote) {
  margin: 12px 0;
  padding: 10px 16px;
  background: #f8fafc;
  border-left: 4px solid #6366f1;
  color: #475569;
  border-radius: 0 8px 8px 0;
}

:deep(.markdown-body table) {
  width: 100%;
  border-collapse: collapse;
  margin: 14px 0;
  font-size: 13px;
}

:deep(.markdown-body th), :deep(.markdown-body td) {
  border: 1px solid #e2e8f0;
  padding: 8px 12px;
  text-align: left;
}

:deep(.markdown-body th) {
  background: #f1f5f9;
  font-weight: 600;
}

:deep(.markdown-body ul), :deep(.markdown-body ol) {
  padding-left: 24px;
  margin-bottom: 12px;
}
</style>
