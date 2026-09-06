<template>
  <div class="backend-layout">
    <el-container>
      <el-header>
        <Navbar></Navbar>
      </el-header>

      <el-container>
        <el-aside width="400px" height="auto">
            <Sidebar></Sidebar>
        </el-aside>
        <el-main>
            <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

</script>

<style scoped>
.backend-layout {
  height: 100vh;
}
</style>

