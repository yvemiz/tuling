import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/login.vue'),
    meta: { title: '前台登录与注册' }
  },
  {
    path: '/',
    component: AppLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '平台首页' }
      },
      {
        path: 'competition',
        name: 'Competition',
        component: () => import('@/views/competition/index.vue'),
        meta: { title: '竞赛广场' }
      },
      {
        path: 'competition/detail/:id',
        alias: 'competition/:id',
        name: 'CompetitionDetail',
        component: () => import('@/views/competition/detail.vue'),
        meta: { title: '竞赛详情与参赛' }
      },
      {
        path: 'project',
        name: 'Project',
        component: () => import('@/views/project/index.vue'),
        meta: { title: '团队大厅与招募' }
      },
      {
        path: 'project/detail/:id',
        alias: 'project/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail.vue'),
        meta: { title: '团队工作台' }
      },
      {
        path: 'algorithm',
        name: 'Algorithm',
        component: () => import('@/views/algorithm/index.vue'),
        meta: { title: '算法训练中心' }
      },
      {
        path: 'algorithm/detail/:id',
        alias: 'algorithm/:id',
        name: 'AlgorithmDetail',
        component: () => import('@/views/algorithm/detail.vue'),
        meta: { title: '算法任务详情' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/forum/index.vue'),
        meta: { title: '社区动态与论坛' }
      },
      {
        path: 'forum/publish',
        name: 'ForumPublish',
        component: () => import('@/views/forum/publish.vue'),
        meta: { title: '发布社区研讨' }
      },
      {
        path: 'forum/detail/:id',
        alias: 'forum/:id',
        name: 'ForumDetail',
        component: () => import('@/views/forum/detail.vue'),
        meta: { title: '研讨详情与交流' }
      },
      {
        path: 'ranking',
        name: 'Ranking',
        component: () => import('@/views/ranking/index.vue'),
        meta: { title: '项目综合排行榜' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人工作台' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? `${to.meta.title} - ` : '') + '大学生学科竞赛与实训协作平台'
  next()
})

export default router
