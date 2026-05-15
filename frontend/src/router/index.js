import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import Publish from '../views/Publish.vue'
import Detail from '../views/Detail.vue'
import MyGoods from '../views/MyGoods.vue'

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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router