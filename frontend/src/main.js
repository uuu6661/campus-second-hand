import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

axios.defaults.baseURL = 'http://localhost:8080'

axios.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      const status = error.response.status
      const path = router.currentRoute.value.path
      
      if (status === 401 || status === 403) {
        sessionStorage.clear()
        if (path.startsWith('/admin')) {
          router.push('/admin/login')
        } else {
          router.push('/login')
        }
      }
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.config.globalProperties.$axios = axios
app.mount('#app')