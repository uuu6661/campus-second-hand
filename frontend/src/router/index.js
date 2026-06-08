import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import Publish from '../views/Publish.vue'
import Detail from '../views/Detail.vue'
import GoodsList from '../views/GoodsList.vue'
import MyGoods from '../views/MyGoods.vue'
import Profile from '../views/Profile.vue'
import AdminLogin from '../views/admin/AdminLogin.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminGoods from '../views/admin/AdminGoods.vue'
import Withdrawals from '../views/admin/Withdrawals.vue'
import Announcements from '../views/admin/Announcements.vue'
import AdminReports from '../views/admin/AdminReports.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import Messages from '../views/Messages.vue'
import Chat from '../views/Chat.vue'
import OrderConfirm from '../views/OrderConfirm.vue'
import MyOrders from '../views/MyOrders.vue'
import MyReports from '../views/MyReports.vue'
import AnnouncementsPage from '../views/Announcements.vue'

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
    path: '/goods',
    name: 'GoodsList',
    component: GoodsList
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
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: OrderConfirm
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: MyOrders
  },
  {
    path: '/my-reports',
    name: 'MyReports',
    component: MyReports
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: AnnouncementsPage
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
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard
      },
      {
        path: '',
        redirect: '/admin/dashboard'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ==========================================
// 路由守卫 - 全局前置守卫
// ==========================================
// beforeEach：在路由跳转前执行的钩子函数
// 功能：控制用户访问权限，实现登录验证和权限管理
//
// 工作流程：
// 1. 获取存储的Token和角色信息
// 2. 判断目标路由是否在白名单中
// 3. 白名单页面：已登录则重定向，未登录则放行
// 4. 受保护页面：未登录则跳转登录页，已登录则检查权限
// 5. 管理后台：额外检查是否为管理员

/**
 * 白名单列表
 * 
 * 白名单说明：
 * 这些页面不需要登录就可以访问
 * 用于公开页面：登录、注册、管理员登录
 * 
 * 白名单页面：
 * - /login: 用户登录页
 * - /register: 用户注册页
 * - /admin/login: 管理员登录页
 */
const whiteList = ['/login', '/register', '/admin/login']

/**
 * 全局前置守卫
 * 
 * 触发时机：每次路由跳转之前
 * 执行顺序：早于路由组件的创建和渲染
 * 
 * @param {Route} to - 目标路由对象
 *   to.path: 目标路由路径
 *   to.name: 目标路由名称
 * @param {Route} from - 来源路由对象
 * @param {Function} next - 确认导航的函数
 *   next(): 确认跳转，继续执行
 *   next('/path'): 跳转到指定路径
 *   next(false): 取消跳转
 */
router.beforeEach((to, from, next) => {
  // 1️⃣ 获取存储的认证信息
  // token: JWT Token，用于身份认证
  // role: 用户角色，'0'=普通用户，'1'=管理员
  
  const token = sessionStorage.getItem('token')
  const role = sessionStorage.getItem('role')
  
  // 2️⃣ 判断目标路由是否在白名单中
  // 白名单页面：登录、注册、管理员登录
  // 这些页面不需要登录验证
  if (whiteList.includes(to.path)) {
    
    // 3️⃣ 如果已登录，智能重定向
    // 已登录用户访问登录页 → 重定向到首页
    // 已登录管理员访问管理员登录页 → 重定向到管理后台
    if (token) {
      // 管理员登录页 → 管理员重定向到管理后台
      if (to.path === '/admin/login' && role === '1') {
        return next('/admin')
      }
      
      // 用户登录/注册页 → 已登录用户重定向到首页
      if (to.path === '/login' || to.path === '/register') {
        return next('/home')
      }
    }
    
    // 未登录用户访问白名单页面，直接放行
    return next()
  }
  
  // 4️⃣ 非白名单页面：需要登录才能访问
  // 检查Token是否存在
  if (!token) {
    // Token不存在，说明未登录
    
    // 尝试访问管理后台 → 跳转到管理员登录页
    if (to.path.startsWith('/admin')) {
      return next('/admin/login')
    }
    
    // 尝试访问普通页面 → 跳转到用户登录页
    return next('/login')
  }
  
  // 5️⃣ 已登录用户：检查管理后台权限
  // 如果访问管理后台路径（/admin/...）
  if (to.path.startsWith('/admin')) {
    // 检查是否为管理员（role='1'）
    if (role !== '1') {
      // 非管理员访问管理后台，显示提示并跳转到首页
      alert('无权访问')
      return next('/')
    }
  }
  
  // 6️⃣ 通过所有检查，确认导航
  // - Token有效
  // - 权限验证通过
  next()
})

export default router
