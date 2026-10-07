import { defineStore } from 'pinia'
import portalApi from '@/api/portalApi'

export const usePortalStore = defineStore('portal', {
  state: () => ({
    siteInfo: {
      title: 'Smart Competition Hub',
      subTitle: '大学生学科竞赛与实训协作前台',
      logo: '',
      bannerTitle: '释放创新潜能 · 协同组队 · 算法冲榜 · AI精准赋能',
      bannerDesc: '汇聚全校学科竞赛规程、跨专业组长智能招募、算法作业多维批改、社区智能审核与团队AI诊断评估。',
      copyright: 'Copyright © 2026 大学生学科竞赛与实训协作智能平台. All rights reserved.',
      icp: '京ICP备20261001号-1'
    },
    banners: [],
    pageSettings: {},
    boards: [],
    loaded: false
  }),
  actions: {
    async fetchAllData() {
      await Promise.all([this.fetchConfigs(), this.fetchBoards()])
      this.loaded = true
    },
    async fetchConfigs() {
      try {
        const res = await portalApi.getPortalConfigs()
        const data = res?.data || res
        if (data) {
          if (data.PORTAL_SITE_INFO) {
            this.siteInfo = { ...this.siteInfo, ...data.PORTAL_SITE_INFO }
          }
          if (Array.isArray(data.PORTAL_BANNERS)) {
            this.banners = data.PORTAL_BANNERS
          }
          if (data.PORTAL_PAGE_SETTINGS) {
            this.pageSettings = data.PORTAL_PAGE_SETTINGS
          }
        }
      } catch (err) {
        console.warn('获取前台门户动态配置失败，使用默认配置:', err)
      }
    },
    async fetchBoards() {
      try {
        const res = await portalApi.getForumBoards()
        const list = res?.data || res
        if (Array.isArray(list)) {
          this.boards = list.filter((b) => b.status === 'ENABLE')
        }
      } catch (err) {
        console.warn('获取社区子板块列表失败，使用默认板块:', err)
      }
    }
  }
})
