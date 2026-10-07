<template>
  <div class="layout-container">
    <!-- 顶部透明磨砂导航栏 -->
    <header class="header-nav glass-panel">
      <div class="header-content">
        <!-- 品牌 LOGO -->
        <router-link to="/home" class="brand-link">
          <div class="logo-box">
            <img v-if="portalStore.siteInfo?.logo" :src="portalStore.siteInfo.logo" alt="logo" style="width: 28px; height: 28px; object-fit: contain;" />
            <span v-else>🏆</span>
          </div>
          <div class="brand-text">
            <span class="brand-title gradient-text">{{ portalStore.siteInfo?.title || 'Smart Competition Hub' }}</span>
            <span class="brand-sub">{{ portalStore.siteInfo?.subTitle || '学科竞赛与实训协作前台' }}</span>
          </div>
        </router-link>

        <!-- 主导航菜单 -->
        <nav class="nav-links">
          <router-link to="/home" class="nav-item" active-class="active">首页</router-link>
          <router-link to="/competition" class="nav-item" active-class="active">竞赛广场</router-link>
          <router-link to="/project" class="nav-item" active-class="active">团队与招募</router-link>
          <router-link to="/algorithm" class="nav-item" active-class="active">算法训练</router-link>
          <router-link to="/forum" class="nav-item" active-class="active">
            社区论坛
            <span class="nav-dot"></span>
          </router-link>
          <router-link to="/ranking" class="nav-item" active-class="active">荣誉榜单</router-link>
          <router-link to="/profile" class="nav-item" active-class="active">个人工作台</router-link>
        </nav>

        <!-- 右侧用户状态 -->
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <!-- 用户信息下拉 -->
            <a-dropdown :trigger="['hover']">
              <div class="user-profile-trigger">
                <a-avatar
                  :size="36"
                  :style="{ backgroundColor: avatarBgColor, verticalAlign: 'middle', fontWeight: 600 }"
                >
                  {{ (userStore.userInfo?.name || userStore.userInfo?.account || 'U').substring(0, 1) }}
                </a-avatar>
                <span class="user-name">{{ userStore.userInfo?.name || userStore.userInfo?.account }}</span>
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="$router.push('/profile')">
                    <span>📌 个人工作台</span>
                  </a-menu-item>
                  <a-menu-item @click="$router.push('/competition')">
                    <span>🏆 参与竞赛</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item @click="handleLogout">
                    <span style="color: #ef4444;">🚪 退出登录</span>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>

          <template v-else>
            <a-button type="primary" shape="round" @click="$router.push('/login')">
              登录 / 注册
            </a-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 页面内容主体 -->
    <main class="main-body">
      <router-view />
    </main>

    <!-- 底部 Footer -->
    <footer class="footer-section">
      <div class="footer-content">
        <div class="footer-top">
          <div class="footer-brand">
            <h3>🏆 {{ portalStore.siteInfo?.title || '大学生学科竞赛与实训协作平台' }}</h3>
            <p>{{ portalStore.siteInfo?.subTitle || '基于小诺（Snowy 3.0）微内核架构，赋能高校“赛教融合、项目孵化、算法实战与AI辅助诊断”全生态。' }}</p>
          </div>
          <div class="footer-links">
            <div class="link-col">
              <h4>竞赛与实训</h4>
              <router-link to="/competition">竞赛大厅</router-link>
              <router-link to="/project">团队招募</router-link>
              <router-link to="/ranking">荣誉排行</router-link>
            </div>
            <div class="link-col">
              <h4>训练与社区</h4>
              <router-link to="/algorithm">算法中心</router-link>
              <router-link to="/forum">论坛圈子</router-link>
              <router-link to="/profile">个人工作台</router-link>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <span>{{ portalStore.siteInfo?.copyright || '© 2026 Smart Competition Hub. Powered by Snowy 3.0 & Vue 3.' }}</span>
          <span v-if="portalStore.siteInfo?.icp" style="margin-left: 16px; color: #64748b;">{{ portalStore.siteInfo.icp }}</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { usePortalStore } from '@/store/portal'

const router = useRouter()
const userStore = useUserStore()
const portalStore = usePortalStore()

onMounted(() => {
  portalStore.fetchAllData()
})

const roleLabel = computed(() => {
  if (userStore.activeRolePerspective === 'TEACHER') return '老师'
  if (userStore.activeRolePerspective === 'LEADER') return '组长'
  return '学生'
})

const roleBadgeClass = computed(() => {
  if (userStore.activeRolePerspective === 'TEACHER') return 'gradient-badge-teacher'
  if (userStore.activeRolePerspective === 'LEADER') return 'gradient-badge-leader'
  return 'gradient-badge-student'
})

const avatarBgColor = computed(() => {
  if (userStore.activeRolePerspective === 'TEACHER') return '#8b5cf6'
  if (userStore.activeRolePerspective === 'LEADER') return '#f59e0b'
  return '#3b82f6'
})

const handleRoleChange = ({ key }) => {
  userStore.switchPerspective(key)
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header-nav {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 68px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid #e2e8f0;
}

.header-content {
  max-width: 1320px;
  margin: 0 auto;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo-box {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.3px;
  line-height: 1.2;
}

.brand-sub {
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  position: relative;
  padding: 8px 16px;
  color: #475569;
  text-decoration: none;
  font-size: 14.5px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #2563eb;
  background: #f1f5f9;
}

.nav-item.active {
  color: #2563eb;
  background: #eff6ff;
}

.nav-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  background-color: #ef4444;
  border-radius: 50%;
  margin-left: 3px;
  vertical-align: super;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-switch-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 20px;
}

.role-badge {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

.user-profile-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-profile-trigger:hover {
  background: #f1f5f9;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.check-icon {
  float: right;
  color: #2563eb;
  font-weight: bold;
}

.main-body {
  flex: 1;
  width: 100%;
}

.footer-section {
  background: #0f172a;
  color: #94a3b8;
  padding: 48px 0 24px;
  margin-top: 60px;
}

.footer-content {
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #1e293b;
  padding-bottom: 36px;
  gap: 40px;
}

.footer-brand h3 {
  color: #f8fafc;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 10px;
}

.footer-brand p {
  max-width: 480px;
  font-size: 13px;
  line-height: 1.6;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-col h4 {
  color: #f8fafc;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 14px;
}

.link-col a {
  display: block;
  color: #94a3b8;
  text-decoration: none;
  font-size: 13px;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.link-col a:hover {
  color: #60a5fa;
}

.footer-bottom {
  text-align: center;
  padding-top: 24px;
  font-size: 12px;
}
</style>
