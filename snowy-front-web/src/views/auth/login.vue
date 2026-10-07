<template>
  <div class="auth-container">
    <div class="auth-box glass-panel">
      <!-- 头部介绍 -->
      <div class="auth-header">
        <div class="brand-badge">
          <span style="font-size: 24px;">🏆</span>
        </div>
        <h2 class="title">学科竞赛与实训协作平台</h2>
        <p class="subtitle">赛教融合 · 智能组队 · 算法打榜 · AI 诊断</p>
      </div>

      

      <!-- 登录 / 注册 Tabs -->
      <a-tabs v-model:activeKey="activeTab" centered>
        <!-- 登录 -->
        <a-tab-pane key="login" tab="账号密码登录">
          <a-form :model="loginForm" layout="vertical" @finish="handleLogin">
            <a-form-item label="登录账号" required>
              <a-input v-model:value="loginForm.account" placeholder="学号/工号/用户名" size="large" />
            </a-form-item>
            <a-form-item label="密码" required>
              <a-input-password v-model:value="loginForm.password" placeholder="请输入密码" size="large" />
            </a-form-item>
            <a-button type="primary" html-type="submit" block size="large" :loading="loading" style="margin-top: 8px;">
              立即登录
            </a-button>
          </a-form>
        </a-tab-pane>

        <!-- 注册 -->
        <a-tab-pane key="register" tab="学生注册激活">
          <a-form :model="regForm" layout="vertical" @finish="handleRegister">
            <div class="reg-tip-box glass-panel" style="padding: 12px 14px; margin-bottom: 16px; border-radius: 8px; background: rgba(59, 130, 246, 0.08); border: 1px solid rgba(59, 130, 246, 0.2); font-size: 13px; color: #3b82f6; line-height: 1.5;">
              💡 <strong>学号自动建档</strong>：管理员已在后台预先录入全校学籍名册。您只需输入<strong>【学号】</strong>并设置密码，系统将全自动匹配您的真实姓名与班级完成账号激活！
            </div>
            <a-form-item label="学生学号" required>
              <a-input v-model:value="regForm.userNo" size="large" placeholder="请输入您的学号（如：202308001 或 ST20261001）" />
            </a-form-item>
            <a-form-item label="设置登录密码" required>
              <a-input-password v-model:value="regForm.password" size="large" placeholder="请输入密码（不少于6位）" />
            </a-form-item>
            <a-form-item label="确认登录密码" required>
              <a-input-password v-model:value="regForm.confirmPassword" size="large" placeholder="请再次输入设置的密码" />
            </a-form-item>

            <a-button type="primary" html-type="submit" block size="large" :loading="loading" style="margin-top: 12px; height: 42px; border-radius: 8px;">
              🚀 验证学号并激活账号
            </a-button>

            <div class="teacher-notice-box" style="margin-top: 16px; padding: 10px 12px; background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; font-size: 12px; color: #64748b; line-height: 1.6;">
              📌 <strong>指导教师说明</strong>：教师账号由系统管理员在后台统一录入与分配，不支持前台自主注册。教师请直接在【账号密码登录】使用管理员分配的工号账号登录。
            </div>
          </a-form>
        </a-tab-pane>
      </a-tabs>

      <!-- 底部辅助 -->
      <div class="auth-footer">
        <span>平台支持多角色身份隔离与动态组长工作台</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import portalApi from '@/api/portalApi'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({
  account: '',
  password: ''
})

const regForm = reactive({
  role: 'STUDENT',
  account: '',
  password: '',
  name: '',
  userNo: '',
  major: '',
  skillTags: ''
})

// 账号密码登录
const handleLogin = async () => {
  if (!loginForm.account || !loginForm.password) {
    message.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await portalApi.login(loginForm)
    userStore.setLoginData(res)
    message.success('登录成功，欢迎进入竞赛协作大厅！')
    router.push('/home')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}



// 学生自主注册激活
const handleRegister = async () => {
  if (!regForm.userNo) {
    message.warning('请输入学号')
    return
  }
  if (!regForm.password) {
    message.warning('请设置登录密码')
    return
  }
  if (regForm.password.length < 6) {
    message.warning('密码长度不能少于6位')
    return
  }
  if (regForm.password !== regForm.confirmPassword) {
    message.warning('两次输入的密码不一致，请重新核对')
    return
  }
  regForm.role = 'STUDENT'
  regForm.account = regForm.userNo.trim()

  loading.value = true
  try {
    const regMsg = await portalApi.register(regForm)
    message.success(regMsg || '学号验证匹配成功，正在为您自动登录...')
    const loginAccount = regForm.userNo.trim()
    const res = await portalApi.login({ account: loginAccount, password: regForm.password })
    userStore.setLoginData(res)
    router.push('/home')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 10% 20%, rgba(59, 130, 246, 0.15) 0%, rgba(147, 51, 234, 0.12) 50%, rgba(244, 63, 94, 0.08) 100%), #f8fafc;
  padding: 24px;
}

.auth-box {
  width: 100%;
  max-width: 460px;
  padding: 36px 32px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 20px 40px -15px rgba(0, 0, 0, 0.08);
}

.auth-header {
  text-align: center;
  margin-bottom: 20px;
}

.brand-badge {
  width: 52px;
  height: 52px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}



.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #94a3b8;
}
</style>
