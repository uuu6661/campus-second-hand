<template>
  <div class="home-container">
    <header class="header">
      <h1>校园二手交易平台</h1>
      <p class="subtitle">让闲置物品焕发新生</p>
      <el-button type="primary" size="large" @click="handlePublish">发布商品</el-button>
    </header>

    <main class="main-content">
      <div class="search-bar">
        <div class="category-tabs">
          <span 
            v-for="cat in categories" 
            :key="cat.id"
            :class="['tab-item', { active: selectedCategory === cat.id }]"
            @click="selectCategory(cat.id)"
          >
            {{ cat.name }}
          </span>
        </div>
        <div class="search-input-wrapper">
          <el-input 
            v-model="keyword" 
            placeholder="搜索商品..." 
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <div v-if="goodsList.length === 0" class="empty-state">
        <div class="empty-icon">📦</div>
        <p>{{ getEmptyMessage() }}</p>
        <el-button type="primary" @click="handlePublish">去发布</el-button>
      </div>

      <div v-else class="goods-grid" v-loading="loading">
        <el-card 
          v-for="goods in goodsList" 
          :key="goods.id" 
          class="goods-card"
          hover
          @click="goToDetail(goods.id)"
        >
          <div class="goods-image">
            <img 
              :src="goods.image || 'https://via.placeholder.com/200x200?text=暂无图片'" 
              :alt="goods.title"
              class="product-img"
            />
          </div>
          <div class="goods-info">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <div class="price-row">
              <span class="price">¥{{ goods.price }}</span>
              <span v-if="goods.originalPrice" class="original-price">¥{{ goods.originalPrice }}</span>
            </div>
            <div class="meta-row">
              <span class="category">{{ goods.categoryName }}</span>
            </div>
            <div class="footer-row">
              <span class="nickname">{{ goods.nickname }}</span>
              <span class="time">{{ formatTime(goods.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const goodsList = ref([])
const loading = ref(false)
const selectedCategory = ref(null)
const keyword = ref('')

const categories = ref([
  { id: null, name: '全部' },
  { id: 1, name: '数码产品' },
  { id: 2, name: '图书教材' },
  { id: 3, name: '生活用品' },
  { id: 4, name: '服装鞋帽' },
  { id: 5, name: '运动户外' },
  { id: 6, name: '美妆护肤' },
  { id: 7, name: '其他' }
])

onMounted(async () => {
  await loadGoodsList()
})

const loadGoodsList = async () => {
  loading.value = true
  try {
    const params = {}
    if (selectedCategory.value !== null) {
      params.categoryId = selectedCategory.value
    }
    if (keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    const response = await axios.get('/api/goods', { params })
    if (response.data.code === 200) {
      goodsList.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const selectCategory = async (categoryId) => {
  selectedCategory.value = categoryId
  await loadGoodsList()
}

const handleSearch = async () => {
  await loadGoodsList()
}

const getEmptyMessage = () => {
  if (keyword.value.trim()) {
    return '没有找到相关商品'
  }
  if (selectedCategory.value !== null) {
    return '该分类暂无商品'
  }
  return '暂无商品，去发布一个吧'
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

const goToDetail = (id) => {
  router.push(`/goods/${id}`)
}

const formatTime = (createTime) => {
  if (!createTime) return ''
  
  const now = new Date()
  const create = new Date(createTime)
  const diff = now.getTime() - create.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  
  return create.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 20px;
  text-align: center;
}

.header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 20px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.category-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input-wrapper {
  display: flex;
  gap: 8px;
}

.search-input {
  width: 200px;
}

.tab-item {
  padding: 8px 16px;
  border-radius: 20px;
  background: #fff;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.tab-item:hover {
  background: #f0f0f0;
}

.tab-item.active {
  background: #667eea;
  color: white;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 6rem;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 20px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.goods-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.goods-card:hover {
  transform: translateY(-4px);
}

.goods-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  padding: 15px;
}

.goods-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 8px;
}

.price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 0.9rem;
  color: #999;
  text-decoration: line-through;
}

.meta-row {
  margin-bottom: 8px;
}

.category {
  font-size: 0.85rem;
  color: #666;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
}

.footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #999;
  font-size: 0.85rem;
}

.nickname {
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>