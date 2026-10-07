import { defineStore } from 'pinia'
import portalApi from '@/api/portalApi'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('CLIENT_TOKEN') || '',
    userInfo: JSON.parse(localStorage.getItem('CLIENT_USER_INFO') || '{}'),
    roleInfo: JSON.parse(localStorage.getItem('CLIENT_ROLE_INFO') || '{}'),
    // 当前界面选中的角色视角 (STUDENT, LEADER, TEACHER)
    activeRolePerspective: localStorage.getItem('CLIENT_ACTIVE_ROLE') || 'STUDENT'
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isTeacher: (state) => !!state.roleInfo?.isTeacher,
    isLeader: (state) => !!state.roleInfo?.isLeader,
    isStudent: (state) => !!state.roleInfo?.isStudent,
    currentPerspective: (state) => state.activeRolePerspective
  },
  actions: {
    setLoginData(data) {
      this.token = data.token
      this.userInfo = data.userInfo
      this.roleInfo = data.roleInfo

      // 默认视角
      if (data.roleInfo.isTeacher) {
        this.activeRolePerspective = 'TEACHER'
      } else if (data.roleInfo.isLeader) {
        this.activeRolePerspective = 'LEADER'
      } else {
        this.activeRolePerspective = 'STUDENT'
      }

      localStorage.setItem('CLIENT_TOKEN', data.token)
      localStorage.setItem('CLIENT_USER_INFO', JSON.stringify(data.userInfo))
      localStorage.setItem('CLIENT_ROLE_INFO', JSON.stringify(data.roleInfo))
      localStorage.setItem('CLIENT_ACTIVE_ROLE', this.activeRolePerspective)
    },
    switchPerspective(newRole) {
      if (newRole === 'TEACHER' && !this.isTeacher) {
        message.warning('您当前账号非指导教师身份')
        return
      }
      if (newRole === 'LEADER' && !this.isLeader) {
        message.warning('您名下暂无担任组长的项目，请先创建竞赛项目')
        return
      }
      this.activeRolePerspective = newRole
      localStorage.setItem('CLIENT_ACTIVE_ROLE', newRole)
      message.success(`已切换至【${newRole === 'TEACHER' ? '老师' : newRole === 'LEADER' ? '组长' : '学生'}】视角工作台`)
    },
    async fetchProfile() {
      if (!this.token) return
      try {
        const res = await portalApi.getMyProfile()
        this.userInfo = res.user
        this.roleInfo = res.roleInfo
        localStorage.setItem('CLIENT_USER_INFO', JSON.stringify(res.user))
        localStorage.setItem('CLIENT_ROLE_INFO', JSON.stringify(res.roleInfo))
      } catch (e) {
        console.error('Fetch profile error:', e)
      }
    },
    async logout() {
      try {
        await portalApi.logout()
      } catch (e) {}
      this.token = ''
      this.userInfo = {}
      this.roleInfo = {}
      this.activeRolePerspective = 'STUDENT'
      localStorage.removeItem('CLIENT_TOKEN')
      localStorage.removeItem('CLIENT_USER_INFO')
      localStorage.removeItem('CLIENT_ROLE_INFO')
      localStorage.removeItem('CLIENT_ACTIVE_ROLE')
      message.success('已安全退出')
    }
  }
})
