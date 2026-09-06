import { createRouter, createWebHistory } from "vue-router"

//路由配置
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/back',
      component: () => import('../components/BackendLayout.vue'),
      children: [
        {
          path: 'dashboard',
          component: () => import('../views/dashboard.vue'),
          meta:{
            title:'数据分析',
            icon:'PieChart',
          }
        },
        {
          path: 'emotional',
          component: () => import('../views/emotional.vue'),
          meta:{
            title:'情绪日志',
            icon:'User',
          }
        },
        {
          path: 'knowledge',
          component: () => import('../views/knowledge.vue'),
          meta:{
            title:'知识文章',
            icon:'ChatLineSquare',
          }
        },
        {
          path: 'consultation',
          component: () => import('../views/consultation.vue'),
          meta:{
            title:'咨询记录',
            icon:'Message',
          }
        }
      ]
    },
  ]
})

export default router