<template>
  <div class="home-page fade-in">
    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="container">
        <div class="search-box">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索二手商品..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 公告栏 -->
    <div v-if="latestAnnouncement" class="announcement-banner" @click="goAnnouncements">
      <div class="announcement-icon">📢</div>
      <div class="announcement-content">
        <span class="announcement-label">最新公告：</span>
        <span class="announcement-title">{{ latestAnnouncement.title }}</span>
      </div>
      <div class="announcement-arrow">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 18l6-6-6-6"/>
        </svg>
      </div>
    </div>

    <!-- 分类入口 -->
    <div class="category-section">
      <div class="container">
        <h2 class="section-title">分类浏览</h2>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            @click="handleCategoryClick(cat.id)"
          >
            <div class="category-icon">{{ cat.icon }}</div>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="goods-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">精选推荐</h2>
          <a href="/goods" class="view-more">查看更多 →</a>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="skeleton-grid">
          <SkeletonCard v-for="i in 6" :key="i" />
        </div>

        <!-- 商品列表 -->
        <div v-else-if="goodsList.length > 0" class="goods-grid">
          <GoodsCard v-for="goods in goodsList" :key="goods.id" :goods="goods" />
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-container">
          <EmptyState
            title="暂无商品"
            description="快来发布第一件二手商品吧"
            button-text="发布商品"
            @action="goPublish"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import GoodsCard from '../components/GoodsCard.vue'
import SkeletonCard from '../components/SkeletonCard.vue'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const searchKeyword = ref('')
const goodsList = ref([])
const loading = ref(true)
const latestAnnouncement = ref(null)

// 分类映射，包含图标
const categoryIcons = {
  1: '📱', // 数码产品
  2: '📚', // 图书教材
  3: '🏠', // 生活用品
  4: '👔', // 服装鞋帽
  5: '⚽', // 运动户外
  6: '💄', // 美妆护肤
  7: '🎁'  // 其他
}
const categories = ref([])

const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories')
    if (response.data.code === 200) {
      // 添加图标
      categories.value = (response.data.data || []).map(cat => ({
        ...cat,
        icon: categoryIcons[cat.id] || '📦'
      }))
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchGoods = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/goods')
    if (response.data.code === 200) {
      // 后端返回的是List，不是分页对象
      goodsList.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/goods?keyword=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const handleCategoryClick = (categoryId) => {
  router.push(`/goods?categoryId=${categoryId}`)
}

const goPublish = () => {
  router.push('/publish')
}

const fetchLatestAnnouncement = async () => {
  try {
    const response = await axios.get('/api/announcements')
    if (response.data.code === 200 && response.data.data.length > 0) {
      // 获取最新的置顶公告或第一个公告
      latestAnnouncement.value = response.data.data.find(a => a.isPinned === 1) || response.data.data[0]
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

const goAnnouncements = () => {
  router.push('/announcements')
}

onMounted(() => {
  fetchCategories()
  fetchGoods()
  fetchLatestAnnouncement()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.search-section {
  background: linear-gradient(135deg, #667eea 0%, #f59e0b 100%);
  padding: 24px 0;
}

.search-box {
  display: flex;
  max-width: 500px;
  margin: 0 auto;
  background: white;
  border-radius: 40px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  overflow: hidden;
}

.search-input {
  flex: 1;
  padding: 14px 20px;
  border: none;
  outline: none;
  font-size: 15px;
  background: transparent;
}

.search-btn {
  padding: 14px 20px;
  background: linear-gradient(135deg, #667eea 0%, #f59e0b 100%);
  border: none;
  color: white;
  cursor: pointer;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.announcement-banner {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(245, 158, 11, 0.1) 100%);
  border-bottom: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(245, 158, 11, 0.15) 100%);
  }
}

.announcement-icon {
  font-size: 24px;
  margin-right: 12px;
}

.announcement-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.announcement-label {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.announcement-title {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.announcement-arrow {
  color: #667eea;
  transition: transform 0.25s ease;
}

.announcement-banner:hover .announcement-arrow {
  transform: translateX(4px);
}

.category-section {
  padding: 24px 0;
  background: white;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    background: #f5f7fa;
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
  color: #4b5563;
}

.goods-section {
  padding: 24px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-more {
  font-size: 14px;
  color: #667eea;
  text-decoration: none;
  transition: color 0.2s ease;

  &:hover {
    color: #5a6fd6;
  }
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.empty-container {
  padding: 40px 0;
}

@media (max-width: 768px) {
  .search-section {
    padding: 16px 0;
  }

  .search-box {
    margin: 0 16px;
  }

  .search-input {
    padding: 12px 16px;
    font-size: 14px;
  }

  .search-btn {
    padding: 12px 16px;
  }

  .category-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
  }

  .category-item {
    padding: 12px 8px;
  }

  .category-icon {
    font-size: 24px;
  }

  .category-name {
    font-size: 12px;
  }

  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .section-title {
    font-size: 18px;
  }
}
</style>
