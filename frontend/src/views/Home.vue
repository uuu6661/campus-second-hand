<template>
  <div class="container">
    <header class="header">
      <h1>校园二手交易平台</h1>
      <p class="subtitle">让闲置物品焕发新生</p>
    </header>
    <main class="main-content">
      <div class="welcome-card">
        <h2>欢迎来到校园二手交易平台</h2>
        <p>在这里，你可以发布和浏览各类二手商品</p>
      </div>
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 3rem;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
}

.main-content {
  max-width: 800px;
  margin: 0 auto;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.welcome-card h2 {
  color: #333;
  font-size: 1.8rem;
  margin-bottom: 15px;
}

.welcome-card p {
  color: #666;
  font-size: 1.1rem;
}
</style>