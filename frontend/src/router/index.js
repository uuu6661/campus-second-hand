import { createRouter, createWebHistory } from 'vue-router'
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

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
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
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/admin',
    component: AdminLayout,
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

export default router
