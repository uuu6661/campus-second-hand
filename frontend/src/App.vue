<template>
  <div id="app">
    <nav class="nav">
      <router-link to="/home">首页</router-link>
      <span>|</span>
      <a href="javascript:void(0)" @click="handlePublish" class="nav-link">发布商品</a>
      <span>|</span>
      <template v-if="isLoggedIn">
        <span class="user-info">欢迎, {{ username }}</span>
        <span>|</span>
        <a href="javascript:void(0)" @click="handleLogout" class="nav-link">退出</a>
      </template>
      <template v-else>
        <router-link to="/register">注册</router-link>
        <span>|</span>
        <router-link to="/login">登录</router-link>
      </template>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const isLoggedIn = ref(false)
const username = ref('')

const checkLoginStatus = () => {
  const userId = localStorage.getItem('userId')
  const user = localStorage.getItem('username')
  isLoggedIn.value = !!userId
  username.value = user || '用户'
}

const handlePublish = async () => {
  const userId = localStorage.getItem('userId')
  
  if (!userId) {
    try {
      await ElMessageBox.confirm(
        '请先登录',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      router.push('/login')
    } catch {
      // 用户点击取消，不做任何操作
    }
  } else {
    router.push('/publish')
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    isLoggedIn.value = false
    username.value = ''
    ElMessage.success('退出成功')
    router.push('/home')
  } catch {
    // 用户点击取消，不做任何操作
  }
}

const handleStorageChange = () => {
  checkLoginStatus()
}

onMounted(() => {
  checkLoginStatus()
  window.addEventListener('storage', handleStorageChange)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.nav {
  background: #333;
  padding: 15px 20px;
}

.nav a, .nav-link {
  color: white;
  text-decoration: none;
  margin: 0 10px;
  cursor: pointer;
}

.nav a:hover, .nav-link:hover {
  color: #667eea;
}

.nav span {
  color: #666;
}

.user-info {
  color: #667eea;
  font-weight: bold;
}
</style>