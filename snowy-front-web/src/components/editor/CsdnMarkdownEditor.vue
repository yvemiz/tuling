<template>
  <div class="csdn-markdown-editor" :class="{ 'is-fullscreen': isFullscreen }">
    <!-- 顶部 CSDN 风格科技工具栏 -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <!-- 标题组 -->
        <a-dropdown>
          <a-button size="small" class="tool-btn">
            <strong>H</strong> 标题 ▼
          </a-button>
          <template #overlay>
            <a-menu @click="handleHeadingSelect">
              <a-menu-item key="1"><h1>H1 一级标题</h1></a-menu-item>
              <a-menu-item key="2"><h2>H2 二级标题</h2></a-menu-item>
              <a-menu-item key="3"><h3>H3 三级标题</h3></a-menu-item>
              <a-menu-item key="4"><h4>H4 四级标题</h4></a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <span class="tool-divider"></span>

        <!-- 基础文本格式 -->
        <a-tooltip title="粗体 (Ctrl+B)">
          <button type="button" class="tool-icon-btn" @click="insertWrap('**', '**', '加粗文本')">
            <BoldOutlined />
          </button>
        </a-tooltip>
        <a-tooltip title="斜体 (Ctrl+I)">
          <button type="button" class="tool-icon-btn" @click="insertWrap('*', '*', '斜体文本')">
            <ItalicOutlined />
          </button>
        </a-tooltip>
        <a-tooltip title="删除线">
          <button type="button" class="tool-icon-btn" @click="insertWrap('~~', '~~', '删除线文本')">
            <StrikethroughOutlined />
          </button>
        </a-tooltip>

        <span class="tool-divider"></span>

        <!-- 结构与排版 -->
        <a-tooltip title="引用块">
          <button type="button" class="tool-icon-btn" @click="insertPrefix('> ', '引用核心见解或文献')">
            <span style="font-size: 15px; font-weight: bold;">“</span>
          </button>
        </a-tooltip>
        <a-tooltip title="无序列表">
          <button type="button" class="tool-icon-btn" @click="insertPrefix('- ', '列表要点')">
            <span style="font-size: 14px;">• ≡</span>
          </button>
        </a-tooltip>
        <a-tooltip title="有序列表">
          <button type="button" class="tool-icon-btn" @click="insertPrefix('1. ', '步骤事项')">
            <span style="font-size: 13px; font-weight: bold;">1.</span>
          </button>
        </a-tooltip>
        <a-tooltip title="插入表格">
          <button type="button" class="tool-icon-btn" @click="insertTable">
            <TableOutlined />
          </button>
        </a-tooltip>

        <span class="tool-divider"></span>

        <!-- 代码与链接 -->
        <a-tooltip title="行内代码">
          <button type="button" class="tool-icon-btn" @click="insertWrap('`', '`', 'code')">
            <span style="font-family: monospace; font-size: 13px;">&lt;/&gt;</span>
          </button>
        </a-tooltip>
        <a-tooltip title="代码块">
          <button type="button" class="tool-icon-btn" @click="insertCodeBlock">
            <CodeOutlined />
          </button>
        </a-tooltip>
        <a-tooltip title="添加链接">
          <button type="button" class="tool-icon-btn" @click="insertLink">
            <LinkOutlined />
          </button>
        </a-tooltip>

        <span class="tool-divider"></span>

        <!-- 核心图文功能：本地图片上传与外链插入 -->
        <a-tooltip title="从本地上传图片（支持拖拽或直接 Ctrl+V 粘贴截图）">
          <button type="button" class="tool-icon-btn highlight-btn" :disabled="uploadingImage" @click="triggerUpload">
            <PictureOutlined />
            <span class="btn-text">{{ uploadingImage ? '上传中...' : '上传图片' }}</span>
          </button>
        </a-tooltip>

        <a-tooltip title="插入网络图片链接">
          <button type="button" class="tool-icon-btn" @click="openImageModal">
            <LinkOutlined />
            <span class="btn-text">外链图片</span>
          </button>
        </a-tooltip>

        <span class="tool-divider"></span>

        <!-- 学术研讨框架模板一键套用 -->
        <a-tooltip title="一键填入学术研讨标准五步结构模板">
          <a-button type="dashed" size="small" class="template-btn" @click="insertAcademicTemplate">
            💡 学术研讨模板
          </a-button>
        </a-tooltip>
      </div>

      <!-- 右侧：视窗视图模式切换与全屏 -->
      <div class="toolbar-right">
        <a-radio-group v-model:value="viewMode" size="small" button-style="solid">
          <a-radio-button value="edit">
            <EditOutlined /> 纯编辑
          </a-radio-button>
          <a-radio-button value="split">
            ⚡ 实时分屏
          </a-radio-button>
          <a-radio-button value="preview">
            <EyeOutlined /> 纯预览
          </a-radio-button>
        </a-radio-group>

        <a-tooltip title="清空内容">
          <button type="button" class="tool-icon-btn" @click="clearContent">
            <DeleteOutlined />
          </button>
        </a-tooltip>
      </div>
    </div>

    <!-- 隐藏的文件上传 input -->
    <input
      type="file"
      ref="fileInputRef"
      style="display: none"
      accept="image/*"
      multiple
      @change="handleFileInputChange"
    />

    <!-- 编辑器核心主体（分屏或单栏） -->
    <div class="editor-body" :class="`mode-${viewMode}`">
      <!-- 左侧：Markdown 源码编辑输入区 -->
      <div
        v-show="viewMode === 'edit' || viewMode === 'split'"
        class="editor-pane source-pane"
        @dragover.prevent="dragOver = true"
        @dragleave.prevent="dragOver = false"
        @drop.prevent="handleDrop"
      >
        <div v-if="dragOver" class="drop-overlay">
          <div class="drop-tip">
            <PictureOutlined style="font-size: 32px; color: #3b82f6" />
            <p>松开鼠标即可自动上传并插入该图片</p>
          </div>
        </div>

        <textarea
          ref="textareaRef"
          class="markdown-textarea"
          :value="modelValue"
          :placeholder="placeholderText"
          @input="onInput"
          @keydown="handleKeyDown"
          @paste="handlePaste"
        ></textarea>
      </div>

      <!-- 右侧：实时 Markdown 渲染预览区 -->
      <div
        v-show="viewMode === 'preview' || viewMode === 'split'"
        class="editor-pane preview-pane"
      >
        <div class="preview-header">
          <span class="preview-title">
            <EyeOutlined style="color: #3b82f6; margin-right: 6px;" />
            CSDN 效果实时预览
          </span>
          <span class="preview-badge">Markdown HTML 实时映射</span>
        </div>

        <div v-if="!modelValue || !modelValue.trim()" class="preview-empty">
          <div class="empty-guide">
            <div class="guide-icon">✍️</div>
            <h4>欢迎开始学术研讨图文创作</h4>
            <p>• 支持直接在左侧键入 Markdown 格式文字与学术理论推导</p>
            <p>• <strong>快捷插图</strong>：点击工具栏【上传图片】、将图片拖入编辑框、或使用快捷键 <strong>Ctrl+V</strong> 直接粘贴剪贴板截图！</p>
            <p>• <strong>代码高亮</strong>：支持 ```java / ```python / ```cpp 代码块展示</p>
            <p>• <strong>表格公式</strong>：支持标准学术数据对比表格与引用标注</p>
          </div>
        </div>

        <div v-else class="preview-content markdown-rendered-content" v-html="renderedHtml"></div>
      </div>
    </div>

    <!-- 底部状态与图库统计栏 -->
    <div class="editor-footer">
      <div class="footer-left">
        <span class="stat-item">字数：<strong>{{ wordCount }}</strong> 字符</span>
        <span class="stat-divider">|</span>
        <span class="stat-item">插图：<strong>{{ insertedImages.length }}</strong> 张</span>
        <span class="stat-divider">|</span>
        <span class="hint-text">💡 提示：支持截图后直接在编辑器内 <strong>Ctrl+V</strong> 粘贴上传图片</span>
      </div>

      <!-- 已插入图片预览抽屉开关 -->
      <div class="footer-right" v-if="insertedImages.length > 0">
        <a-popover title="本文已插入的图片列表" trigger="click" placement="topRight">
          <template #content>
            <div class="inserted-images-shelf">
              <div
                v-for="(img, idx) in insertedImages"
                :key="idx"
                class="shelf-img-card"
              >
                <img :src="img.url" :alt="img.alt" class="shelf-thumb" />
                <div class="shelf-info">
                  <div class="shelf-title">{{ img.alt || '未命名插图' }}</div>
                  <a-button type="link" size="small" @click="copyImageUrl(img.url)">
                    复制链接
                  </a-button>
                </div>
              </div>
            </div>
          </template>
          <a-button type="link" size="small" class="shelf-btn">
            📸 查看正文插图 ({{ insertedImages.length }})
          </a-button>
        </a-popover>
      </div>
    </div>

    <!-- 插入网络图片弹窗 -->
    <a-modal
      v-model:open="imageModalVisible"
      title="🌐 插入网络图片"
      ok-text="确定插入"
      cancel-text="取消"
      @ok="handleInsertExternalImage"
    >
      <a-form layout="vertical" style="margin-top: 16px;">
        <a-form-item label="图片直链 URL" required>
          <a-input v-model:value="externalImageUrl" placeholder="https://example.com/image.png" />
        </a-form-item>
        <a-form-item label="图片说明 / Caption (可选)">
          <a-input v-model:value="externalImageAlt" placeholder="如：系统整体架构图 / 实验对比收敛曲线" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  PictureOutlined,
  UploadOutlined,
  LinkOutlined,
  BoldOutlined,
  ItalicOutlined,
  StrikethroughOutlined,
  CodeOutlined,
  TableOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { marked } from 'marked'
import portalApi from '@/api/portalApi'

// 配置 marked 解析规则
marked.setOptions({
  gfm: true,
  breaks: true
})

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请在此输入学术研讨正文，支持图文混排、Markdown 语法排版，支持截图直接 Ctrl+V 粘贴上传...'
  }
})

const emit = defineEmits(['update:modelValue'])

const textareaRef = ref(null)
const fileInputRef = ref(null)
const viewMode = ref('split') // 'split' | 'edit' | 'preview'
const isFullscreen = ref(false)
const dragOver = ref(false)
const uploadingImage = ref(false)

// 外链图片弹窗状态
const imageModalVisible = ref(false)
const externalImageUrl = ref('')
const externalImageAlt = ref('')

const placeholderText = computed(() => props.placeholder)

// 字符数
const wordCount = computed(() => {
  return props.modelValue ? props.modelValue.length : 0
})

// 提取正文中已包含的所有图片
const insertedImages = computed(() => {
  if (!props.modelValue) return []
  const imgRegex = /!\[(.*?)\]\((.*?)\)/g
  const list = []
  let match
  while ((match = imgRegex.exec(props.modelValue)) !== null) {
    list.push({
      alt: match[1] || '图片',
      url: match[2]
    })
  }
  return list
})

// 实时渲染 Markdown HTML
const renderedHtml = computed(() => {
  if (!props.modelValue) return ''
  try {
    return marked.parse(props.modelValue)
  } catch (err) {
    return props.modelValue
  }
})

// 输入变化通知父组件
const onInput = (e) => {
  emit('update:modelValue', e.target.value)
}

// 触发隐藏文件输入
const triggerUpload = () => {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 文件选择上传
const handleFileInputChange = async (e) => {
  const files = e.target.files
  if (!files || files.length === 0) return
  for (let i = 0; i < files.length; i++) {
    await uploadAndInsertImage(files[i], files[i].name)
  }
  // 清理以便再次选择相同文件
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

// 核心上传与插入逻辑
const uploadAndInsertImage = async (file, defaultAlt = '插图') => {
  if (!file.type.startsWith('image/')) {
    message.warning('请选择图片类型文件（JPG / PNG / GIF / WebP / SVG）')
    return
  }
  uploadingImage.value = true
  const hideMsg = message.loading(`正在上传图片【${file.name || defaultAlt}】...`, 0)
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await portalApi.uploadFile(formData)
    const url = typeof res === 'string' ? res : (res?.data || res?.url || res)
    if (!url) {
      throw new Error('服务器未返回有效图片地址')
    }
    const cleanAlt = (file.name || defaultAlt).replace(/\.[^/.]+$/, '')
    const markdownImg = `\n![${cleanAlt}](${url})\n`
    insertAtCursor(markdownImg)
    message.success('图片上传成功并已插入正文！')
  } catch (err) {
    message.error(err.message || '图片上传失败，请重试')
  } finally {
    hideMsg()
    uploadingImage.value = false
  }
}

// 剪贴板图片直接粘贴 (Ctrl+V)
const handlePaste = async (e) => {
  const clipboardData = e.clipboardData || window.clipboardData
  if (!clipboardData) return
  const items = clipboardData.items
  if (!items) return

  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    if (item.type.indexOf('image') !== -1) {
      e.preventDefault()
      const file = item.getAsFile()
      if (file) {
        await uploadAndInsertImage(file, '剪贴板截图')
      }
      return
    }
  }
}

// 拖拽图片松开上传
const handleDrop = async (e) => {
  dragOver.value = false
  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      if (file.type.startsWith('image/')) {
        await uploadAndInsertImage(file, file.name)
      }
    }
  }
}

// 键盘快捷键处理 (Tab 缩进, Ctrl+B, Ctrl+I)
const handleKeyDown = (e) => {
  if (e.key === 'Tab') {
    e.preventDefault()
    insertAtCursor('  ')
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'b') {
    e.preventDefault()
    insertWrap('**', '**', '加粗文本')
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'i') {
    e.preventDefault()
    insertWrap('*', '*', '斜体文本')
  }
}

// 光标位置精确插入文本
const insertAtCursor = (text) => {
  const textarea = textareaRef.value
  if (!textarea) {
    emit('update:modelValue', (props.modelValue || '') + text)
    return
  }
  const start = textarea.selectionStart || 0
  const end = textarea.selectionEnd || 0
  const currentValue = props.modelValue || ''
  const newValue = currentValue.substring(0, start) + text + currentValue.substring(end)
  emit('update:modelValue', newValue)

  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start + text.length, start + text.length)
  })
}

// 包裹选中文本或插入占位符
const insertWrap = (prefix, suffix, defaultText = '') => {
  const textarea = textareaRef.value
  if (!textarea) return
  const start = textarea.selectionStart || 0
  const end = textarea.selectionEnd || 0
  const currentValue = props.modelValue || ''
  const selected = currentValue.substring(start, end)

  const textToWrap = selected || defaultText
  const replacement = `${prefix}${textToWrap}${suffix}`
  const newValue = currentValue.substring(0, start) + replacement + currentValue.substring(end)
  emit('update:modelValue', newValue)

  nextTick(() => {
    textarea.focus()
    const newPos = start + prefix.length + textToWrap.length
    textarea.setSelectionRange(start + prefix.length, newPos)
  })
}

// 前缀行插入（如 > 引用、- 列表、# 标题）
const insertPrefix = (prefix, defaultText = '') => {
  const textarea = textareaRef.value
  if (!textarea) return
  const start = textarea.selectionStart || 0
  const end = textarea.selectionEnd || 0
  const currentValue = props.modelValue || ''
  const selected = currentValue.substring(start, end)
  const lineText = selected || defaultText
  const replacement = `\n${prefix}${lineText}\n`
  const newValue = currentValue.substring(0, start) + replacement + currentValue.substring(end)
  emit('update:modelValue', newValue)

  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start + replacement.length, start + replacement.length)
  })
}

// 标题选择
const handleHeadingSelect = ({ key }) => {
  const level = parseInt(key) || 1
  const hashes = '#'.repeat(level) + ' '
  insertPrefix(hashes, `${level}级学术标题`)
}

// 插入表格
const insertTable = () => {
  const tableMd = `\n| 指标/维度 | 核心参数/算法方案 | 实验对比结论 |\n| :--- | :--- | :--- |\n| 时间复杂度 | O(N log N) | 相比暴力法提升 98% |\n| 内存开销 | 128 MB | 峰值平稳未发生 OOM |\n`
  insertAtCursor(tableMd)
}

// 插入代码块
const insertCodeBlock = () => {
  const codeMd = `\n\`\`\`java\n// 在此编写算法实现或架构核心代码\npublic void solve() {\n    System.out.println("Academic Solution");\n}\n\`\`\`\n`
  insertAtCursor(codeMd)
}

// 插入超链接
const insertLink = () => {
  insertAtCursor(`[链接描述文字](https://example.com)`)
}

// 打开外链图片弹窗
const openImageModal = () => {
  externalImageUrl.value = ''
  externalImageAlt.value = ''
  imageModalVisible.value = true
}

// 插入外链图片
const handleInsertExternalImage = () => {
  if (!externalImageUrl.value.trim()) {
    message.warning('请输入图片直链 URL')
    return
  }
  const alt = externalImageAlt.value.trim() || '学术图解'
  insertAtCursor(`\n![${alt}](${externalImageUrl.value.trim()})\n`)
  imageModalVisible.value = false
  message.success('已插入网络图片！')
}

// 复制图片链接
const copyImageUrl = (url) => {
  navigator.clipboard.writeText(url)
  message.success('已复制图片直链到剪贴板！')
}

// 插入学术研讨模板
const insertAcademicTemplate = () => {
  const template = `## 一、研究背景与实训痛点
在此简要阐述算法或竞赛项目的背景及当前技术面临的核心挑战...

## 二、核心理论推导与算法架构
在此阐述核心架构、数学模型及关键推导步骤...

## 三、工程实现与系统架构图解
在此插入系统架构图、核心流程图或关键代码片段：
> 提示：您可以直接按快捷键 **Ctrl+V** 或点击上方【上传图片】在此处插入架构图！

## 四、实测效果与实验对比
汇总实训评测数据、时间/空间复杂度对比：

| 测试用例规模 | 基础实现耗时 | 优化后耗时 | 性能提升幅度 |
| :--- | :--- | :--- | :--- |
| N = 10,000 | 142 ms | 12 ms | 11.8x |
| N = 1,000,000 | 超时 (TLE) | 38 ms | 极限通过 |

## 五、学术总结与未来展望
在此总结本次学术探讨与实训经验心得...
`
  if (props.modelValue && props.modelValue.trim()) {
    if (confirm('当前正文已有内容，是否将学术研讨模板追加至正文末尾？')) {
      insertAtCursor(`\n\n${template}`)
    }
  } else {
    emit('update:modelValue', template)
    message.success('已成功填入学术研讨标准结构模板！')
  }
}

// 清空正文
const clearContent = () => {
  if (confirm('确定清空当前编辑器内的全部内容吗？')) {
    emit('update:modelValue', '')
  }
}
</script>

<style scoped>
.csdn-markdown-editor {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.csdn-markdown-editor:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
}

/* 顶部工具栏 */
.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 14px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  user-select: none;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 5px;
}

.tool-divider {
  width: 1px;
  height: 18px;
  background: #cbd5e1;
  margin: 0 4px;
}

.tool-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 6px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 6px;
  color: #475569;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.15s ease;
}

.tool-icon-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.tool-icon-btn:active {
  transform: scale(0.96);
}

.tool-icon-btn.highlight-btn {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #2563eb;
  font-weight: 600;
  padding: 0 8px;
}

.tool-icon-btn.highlight-btn:hover {
  background: #dbeafe;
  color: #1d4ed8;
}

.btn-text {
  margin-left: 4px;
  font-size: 12px;
}

.template-btn {
  color: #d97706;
  border-color: #fcd34d;
  background: #fffbeb;
  font-size: 12px;
  font-weight: 600;
}

.template-btn:hover {
  background: #fef3c7;
  color: #b45309;
}

/* 编辑器主体容器 */
.editor-body {
  display: flex;
  height: 520px;
  position: relative;
  background: #ffffff;
}

.editor-pane {
  flex: 1;
  height: 100%;
  overflow-y: auto;
  position: relative;
}

.source-pane {
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.mode-edit .source-pane {
  border-right: none;
}

.markdown-textarea {
  flex: 1;
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  padding: 16px 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', monospace;
  font-size: 14.5px;
  line-height: 1.75;
  color: #1e293b;
  resize: none;
  background: transparent;
}

.markdown-textarea::placeholder {
  color: #94a3b8;
  font-size: 14px;
}

/* 拖拽悬停遮罩 */
.drop-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(239, 246, 255, 0.92);
  border: 2px dashed #3b82f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.drop-tip {
  text-align: center;
  color: #2563eb;
  font-weight: 600;
  font-size: 14px;
}

/* 实时预览面板 */
.preview-pane {
  background: #fafafa;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: #f1f5f9;
  border-bottom: 1px solid #e2e8f0;
  font-size: 12px;
  color: #64748b;
}

.preview-title {
  font-weight: 600;
  color: #334155;
  display: flex;
  align-items: center;
}

.preview-badge {
  background: #e2e8f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.preview-content {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  background: #ffffff;
}

.preview-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.empty-guide {
  max-width: 420px;
  padding: 24px;
  background: #ffffff;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.8;
}

.guide-icon {
  font-size: 32px;
  margin-bottom: 8px;
  text-align: center;
}

.empty-guide h4 {
  color: #1e293b;
  font-size: 16px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 12px;
}

/* 底部状态与图库统计栏 */
.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  font-size: 12px;
  color: #64748b;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-divider {
  color: #cbd5e1;
}

.hint-text {
  color: #3b82f6;
  font-size: 11.5px;
  margin-left: 6px;
}

/* 已插入图片抽屉 */
.inserted-images-shelf {
  max-width: 320px;
  max-height: 280px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.shelf-img-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
}

.shelf-thumb {
  width: 52px;
  height: 52px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
}

.shelf-info {
  flex: 1;
  overflow: hidden;
}

.shelf-title {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Markdown 渲染统一科技学术样式 (与详情页一致) */
:deep(.markdown-rendered-content) {
  font-size: 14.5px;
  line-height: 1.8;
  color: #334155;
  word-break: break-word;
}

:deep(.markdown-rendered-content h1) {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 8px;
  margin-top: 18px;
  margin-bottom: 12px;
}

:deep(.markdown-rendered-content h2) {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  border-left: 4px solid #3b82f6;
  padding-left: 10px;
  margin-top: 16px;
  margin-bottom: 10px;
}

:deep(.markdown-rendered-content h3) {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  margin-top: 14px;
  margin-bottom: 8px;
}

:deep(.markdown-rendered-content p) {
  margin-bottom: 12px;
}

:deep(.markdown-rendered-content blockquote) {
  margin: 12px 0;
  padding: 10px 16px;
  background: #f8fafc;
  border-left: 4px solid #6366f1;
  border-radius: 0 8px 8px 0;
  color: #475569;
  font-size: 13.5px;
}

:deep(.markdown-rendered-content img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 16px auto;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.07);
}

:deep(.markdown-rendered-content table) {
  width: 100%;
  border-collapse: collapse;
  margin: 14px 0;
  font-size: 13px;
}

:deep(.markdown-rendered-content th),
:deep(.markdown-rendered-content td) {
  border: 1px solid #e2e8f0;
  padding: 8px 12px;
  text-align: left;
}

:deep(.markdown-rendered-content th) {
  background-color: #f1f5f9;
  color: #0f172a;
  font-weight: 600;
}

:deep(.markdown-rendered-content tr:nth-child(even)) {
  background-color: #f8fafc;
}

:deep(.markdown-rendered-content pre) {
  background: #0f172a;
  color: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  margin: 12px 0;
}

:deep(.markdown-rendered-content code) {
  background: #f1f5f9;
  color: #0f172a;
  padding: 2px 5px;
  border-radius: 4px;
  font-family: Consolas, Monaco, monospace;
  font-size: 12.5px;
}

:deep(.markdown-rendered-content pre code) {
  background: transparent;
  color: #38bdf8;
  padding: 0;
}
</style>
