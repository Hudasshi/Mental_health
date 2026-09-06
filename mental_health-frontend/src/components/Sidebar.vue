<template>
  <el-row class="tac">
    <el-col :span="12">
      <h5 class="mb-2">Default colors</h5>
      <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
      >
        <el-menu-item v-for="item in router.options.routes[0].children" :key="item.path" :index="item.path">
          <el-icon><component :is="item.meta.icon"></component></el-icon>
          <span>{{item.meta.title}}</span>
        </el-menu-item>
      </el-menu>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
const router = useRouter()


const handleOpen = () => {
  console.log("open menu")
}
const handleClose = () => {
  console.log("close menu")
}
</script>
