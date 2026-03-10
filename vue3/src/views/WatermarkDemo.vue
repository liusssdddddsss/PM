<template>
  <div class="watermark-demo">
    <h1>水印组件演示</h1>
    <p>这是一个水印组件的演示页面，展示如何在系统中添加动态水印。</p>
    
    <div class="demo-section">
      <h2>默认水印</h2>
      <p>使用默认配置的水印效果</p>
    </div>
    
    <div class="demo-section">
      <h2>自定义水印</h2>
      <p>可以通过props自定义水印的文本、大小、颜色等属性</p>
    </div>
  </div>
</template>

<script setup>
// 水印组件已在App.vue中全局引入
</script>

<style scoped>
.watermark-demo {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.demo-section {
  margin: 30px 0;
  padding: 20px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}

h2 {
  font-size: 18px;
  color: #409EFF;
  margin-bottom: 10px;
}

p {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}
</style>