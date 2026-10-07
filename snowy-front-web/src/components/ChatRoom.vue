<template>
  <div class="chat-room-container glass-card">
    <!-- 顶部状态栏 -->
    <div class="chat-header">
      <div class="header-info">
        <div class="title-row">
          <span class="chat-icon">💬</span>
          <h3 class="chat-title">{{ title }}</h3>
          <a-tag v-if="chatType === 'PROJECT'" color="blue">项目小组研讨群</a-tag>
          <a-tag v-else color="purple">师生领航·组长与教师大群</a-tag>
        </div>
        <p class="chat-subtitle">{{ subtitle || '群内消息实时加密同步，支持技术研讨与团队协同' }}</p>
      </div>

      <div class="header-actions">
        <a-button type="dashed" size="small" shape="round" @click="memberDrawerVisible = true">
          👥 查看群成员 ({{ groupMembers.length }})
        </a-button>
        <a-button size="small" shape="round" @click="loadMessages" :loading="loading">
          🔄 刷新消息
        </a-button>
      </div>
    </div>

    <!-- 消息列表滚动区 -->
    <div class="chat-messages-area" ref="messagesContainer">
      <div v-if="messages.length === 0" class="chat-empty">
        <a-empty description="暂无历史交流消息，快发送第一条消息开启探讨吧！" />
      </div>

      <div v-else class="messages-flow">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ 'is-me': msg.senderId === userStore.userInfo?.id }"
        >
          <!-- 头像 -->
          <div class="msg-avatar">
            <a-avatar
              :size="36"
              :style="{
                backgroundColor:
                  msg.senderRole === 'TEACHER'
                    ? '#8b5cf6'
                    : msg.senderRole === 'LEADER'
                    ? '#f59e0b'
                    : '#3b82f6'
              }"
            >
              {{ (msg.senderName || 'U').substring(0, 1) }}
            </a-avatar>
          </div>

          <!-- 消息体 -->
          <div class="msg-content-wrap">
            <div class="msg-meta">
              <span class="msg-sender">{{ msg.senderName }}</span>
              <a-tag
                :color="
                  msg.senderRole === 'TEACHER'
                    ? 'purple'
                    : msg.senderRole === 'LEADER'
                    ? 'orange'
                    : 'blue'
                "
                class="role-mini-tag"
              >
                {{
                  msg.senderRole === 'TEACHER'
                    ? '👨‍🏫 指导教师'
                    : msg.senderRole === 'LEADER'
                    ? '👑 组长'
                    : '组员'
                }}
              </a-tag>
              <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
            </div>

            <div class="msg-bubble">
              {{ msg.content }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入工具栏 -->
    <div class="chat-input-bar">
      <a-textarea
        v-model:value="inputText"
        :rows="3"
        :placeholder="`在此输入研讨消息内容... (按 Enter 发送，Shift + Enter 换行)`"
        @keydown.enter.exact.prevent="handleSend"
        class="custom-chat-textarea"
      />
      <div class="input-actions">
        <span class="hint-text">Enter 发送 / Shift + Enter 换行</span>
        <a-button
          type="primary"
          shape="round"
          size="middle"
          :loading="sending"
          @click="handleSend"
          class="send-btn"
        >
          ✈️ 发送消息
        </a-button>
      </div>
    </div>

    <!-- 群成员查看抽屉 -->
    <a-drawer
      v-model:open="memberDrawerVisible"
      title="群聊成员名录"
      placement="right"
      width="360"
    >
      <div class="drawer-members-list">
        <div class="drawer-header-tip" style="margin-bottom: 16px; font-size: 13px; color: #64748b;">
          <span v-if="chatType === 'PROJECT'">
            包含指导教师、项目组长及已入驻的所有正式参赛组员。
          </span>
          <span v-else>
            涵盖全校学科竞赛指导教师与各项目组长。系统已自动合并去重多组组长身份；不再担任组长时自动移除。
          </span>
        </div>

        <div v-for="m in groupMembers" :key="m.userId" class="member-drawer-item glass-card">
          <a-avatar
            :size="40"
            :style="{
              backgroundColor:
                m.role === 'TEACHER'
                  ? '#8b5cf6'
                  : m.role === 'LEADER'
                  ? '#f59e0b'
                  : '#3b82f6'
            }"
          >
            {{ (m.name || 'U').substring(0, 1) }}
          </a-avatar>
          <div class="m-info">
            <div class="m-name-line">
              <span class="m-name">{{ m.name }}</span>
              <a-tag
                :color="m.role === 'TEACHER' ? 'purple' : m.role === 'LEADER' ? 'orange' : 'blue'"
                size="small"
              >
                {{ m.roleLabel || m.role }}
              </a-tag>
            </div>
            <!-- 如果是组长且带领多个项目，显示所带项目 -->
            <div v-if="m.projects && m.projects.length > 0" class="m-projects-desc">
              带领 {{ m.projectCount }} 个项目：{{ m.projects.join('、') }}
            </div>
          </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const props = defineProps({
  chatType: {
    type: String,
    required: true // 'PROJECT' 或 'LEADER_TEACHER'
  },
  chatTargetId: {
    type: String,
    required: true // 项目ID 或 'GLOBAL_LEADER_TEACHER'
  },
  title: {
    type: String,
    default: '研讨交流群'
  },
  subtitle: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['messageSent', 'readMarked'])

const userStore = useUserStore()
const messages = ref([])
const groupMembers = ref([])
const inputText = ref('')
const loading = ref(false)
const sending = ref(false)
const memberDrawerVisible = ref(false)
const messagesContainer = ref(null)

let pollTimer = null

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return dayjs(timeStr).format('YYYY-MM-DD HH:mm')
}

// 加载聊天历史记录
const loadMessages = async (silent = false) => {
  if (!silent) loading.value = true
  try {
    const res = await portalApi.getChatHistory({
      chatType: props.chatType,
      chatTargetId: props.chatTargetId,
      limit: 100
    })
    const list = Array.isArray(res) ? res : (res?.data || [])
    const isFirst = messages.value.length === 0
    const hasNew = list.length !== messages.value.length
    messages.value = list
    if (isFirst || hasNew) {
      scrollToBottom()
    }
    emit('readMarked')
  } catch (err) {
    if (!silent) console.error(err)
  } finally {
    if (!silent) loading.value = false
  }
}

// 加载群成员
const loadMembers = async () => {
  try {
    const res = await portalApi.getChatGroupMembers({
      chatType: props.chatType,
      chatTargetId: props.chatTargetId
    })
    groupMembers.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (err) {
    console.error(err)
  }
}

// 发送消息
const handleSend = async () => {
  const content = inputText.value.trim()
  if (!content) {
    message.warning('请输入消息内容')
    return
  }
  sending.value = true
  try {
    const newMsg = await portalApi.sendChatMessage({
      chatType: props.chatType,
      chatTargetId: props.chatTargetId,
      content,
      messageType: 'TEXT'
    })
    inputText.value = ''
    messages.value.push(newMsg?.data || newMsg)
    scrollToBottom()
    emit('messageSent', newMsg)
  } catch (err) {
    console.error(err)
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  loadMessages()
  loadMembers()
  // 每 4 秒轮询一次同步新消息
  pollTimer = setInterval(() => {
    if (typeof document !== 'undefined' && document.hidden) return
    loadMessages(true)
  }, 4000)
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
})
</script>

<style scoped>
.chat-room-container {
  display: flex;
  flex-direction: column;
  height: 680px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04);
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: rgba(248, 250, 252, 0.7);
  backdrop-filter: blur(8px);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-icon {
  font-size: 20px;
}

.chat-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.chat-subtitle {
  font-size: 12px;
  color: #64748b;
  margin: 4px 0 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 消息流区域 */
.chat-messages-area {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  background: #f8fafc;
}

.chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.messages-flow {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 75%;
}

.message-item.is-me {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-content-wrap {
  display: flex;
  flex-direction: column;
}

.message-item.is-me .msg-content-wrap {
  align-items: flex-end;
}

.msg-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.msg-sender {
  font-size: 12px;
  font-weight: 600;
  color: #334155;
}

.role-mini-tag {
  font-size: 10px;
  padding: 0 4px;
  line-height: 16px;
  border-radius: 4px;
}

.msg-time {
  font-size: 11px;
  color: #94a3b8;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  white-space: pre-wrap;
  background: #ffffff;
  color: #1e293b;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.message-item.is-me .msg-bubble {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

/* 输入区域 */
.chat-input-bar {
  padding: 16px 20px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
}

.custom-chat-textarea {
  border-radius: 10px;
  border: 1px solid #cbd5e1;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.hint-text {
  font-size: 12px;
  color: #94a3b8;
}

.send-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  font-weight: 600;
}

/* 抽屉成员卡片 */
.drawer-members-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.member-drawer-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.m-info {
  flex: 1;
}

.m-name-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2px;
}

.m-name {
  font-weight: 600;
  font-size: 13px;
  color: #0f172a;
}

.m-projects-desc {
  font-size: 11px;
  color: #64748b;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
