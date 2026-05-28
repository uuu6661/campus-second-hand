<template>
  <div id="app">
    <nav class="nav">
      <div class="nav-left">
        <router-link to="/home">首页</router-link>
        <span>|</span>
        <a href="javascript:void(0)" @click="handlePublish" class="nav-link">发布商品</a>
      </div>
      <div class="nav-right">
        <template v-if="isLoggedIn">
          <div class="user-menu">
            <span class="user-info" @click="toggleMenu">欢迎, {{ username }} ▼</span>
            <div class="dropdown-menu" v-if="menuOpen">
              <a href="javascript:void(0)" @click="handleProfile">个人中心</a>
              <a href="javascript:void(0)" @click="handleMyGoods">我的发布</a>
              <a href="javascript:void(0)" @click="handleLogout">退出登录</a>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/register">注册</router-link>
          <span>|</span>
          <router-link to="/login">登录</router-link>
        </template>
      </div>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(false)
const username = ref('')
const menuOpen = ref(false)

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

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value
}

const handleProfile = () => {
  menuOpen.value = false
  const userId = localStorage.getItem('userId')
  if (userId) {
    router.push('/profile')
  } else {
    localStorage.setItem('redirect', '/profile')
    router.push('/login')
  }
}

const handleMyGoods = () => {
  menuOpen.value = false
  const userId = localStorage.getItem('userId')
  if (userId) {
    router.push('/my-goods')
  } else {
    localStorage.setItem('redirect', '/my-goods')
    router.push('/login')
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
    menuOpen.value = false
    ElMessage.success('退出成功')
    router.push('/home')
  } catch {
    // 用户点击取消，不做任何操作
  }
}

const handleClickOutside = (event) => {
  const userMenu = document.querySelector('.user-menu')
  if (userMenu && !userMenu.contains(event.target)) {
    menuOpen.value = false
  }
}

const handleStorageChange = () => {
  checkLoginStatus()
}

onMounted(() => {
  checkLoginStatus()
  window.addEventListener('storage', handleStorageChange)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  document.removeEventListener('click', handleClickOutside)
})

watch(() => route.path, () => {
  checkLoginStatus()
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left, .nav-right {
  display: flex;
  align-items: center;
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
  cursor: pointer;
}

.user-menu {
  position: relative;
  display: inline-block;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 5px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-width: 120px;
  z-index: 100;
}

.dropdown-menu a {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  white-space: nowrap;
  margin: 0;
}

.dropdown-menu a:hover {
  background: #f5f5f5;
}
</style>