import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import Publish from '../views/Publish.vue'
import Detail from '../views/Detail.vue'
import MyGoods from '../views/MyGoods.vue'
import Profile from '../views/Profile.vue'
import AdminLogin from '../views/admin/AdminLogin.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminGoods from '../views/admin/AdminGoods.vue'
import Withdrawals from '../views/admin/Withdrawals.vue'
import Announcements from '../views/admin/Announcements.vue'
import AdminReports from '../views/admin/AdminReports.vue'
import Messages from '../views/Messages.vue'
import Chat from '../views/Chat.vue'

const routes = [
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  },
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/publish',
    name: 'Publish',
    component: Publish
  },
  {
    path: '/goods/:id',
    name: 'Detail',
    component: Detail
  },
  {
    path: '/my-goods',
    name: 'MyGoods',
    component: MyGoods
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/messages',
    name: 'Messages',
    component: Messages
  },
  {
    path: '/chat/:userId',
    name: 'Chat',
    component: Chat
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers
      },
      {
        path: 'goods',
        name: 'AdminGoods',
        component: AdminGoods
      },
      {
        path: 'withdrawals',
        name: 'Withdrawals',
        component: Withdrawals
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: Announcements
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: AdminReports
      },
      {
        path: '',
        redirect: '/admin/users'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/login', '/register', '/admin/login']

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
  const role = sessionStorage.getItem('role')
  
  if (whiteList.includes(to.path)) {
    if (token) {
      if (to.path === '/admin/login' && role === '1') {
        return next('/admin')
      }
      if (to.path === '/login' && role === '0') {
        return next('/')
      }
    }
    return next()
  }
  
  if (!token) {
    if (to.path.startsWith('/admin')) {
      return next('/admin/login')
    }
    return next('/login')
  }
  
  if (to.path.startsWith('/admin')) {
    if (role !== '1') {
      alert('无权访问')
      return next('/')
    }
    return next()
  }
  
  next()
})

export default router
