<template>
  <div id="app">
    <nav class="nav">
      <router-link to="/home">首页</router-link>
      <span>|</span>
      <router-link to="/publish">发布商品</router-link>
      <span>|</span>
      <router-link to="/register">注册</router-link>
      <span>|</span>
      <router-link to="/login">登录</router-link>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
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

.nav a {
  color: white;
  text-decoration: none;
  margin: 0 10px;
}

.nav a:hover {
  color: #667eea;
}

.nav span {
  color: #666;
}
</style>