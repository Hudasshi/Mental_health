import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router/index.js'
// 把所有图标全部导入，全部打包到 ElementPlusIconsVue 对象
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)
// Object.entries：把对象转成 [[键名,组件],[键名,组件]] 数组
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  // 全局注册图标组件，key就是图标名字，如 PieChart、User
  app.component(key, component)
}
app.use(ElementPlus).use(router).mount('#app')
