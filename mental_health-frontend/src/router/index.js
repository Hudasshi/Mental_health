import { createRouter, createWebHashHistory } from "vue-router"
import Home from '../components/Home.vue'

//路由配置
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: Home,
      children: [] // 无子路由就留空数组，不要写空对象{}
    }
  ]
})

export default router