<template>
  <div class="watermark-container" ref="watermarkContainer"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  fontSize: {
    type: Number,
    default: 14
  },
  color: {
    type: String,
    default: '#000000'
  },
  opacity: {
    type: Number,
    default: 0.2
  },
  rotate: {
    type: Number,
    default: -45
  },
  spacing: {
    type: Number,
    default: 100
  }
});

const watermarkContainer = ref(null);

const addWatermark = () => {
  console.log('执行addWatermark函数');
  if (!watermarkContainer.value) {
    console.log('watermarkContainer未找到');
    return;
  }

  const canvas = document.createElement('canvas');
  const ctx = canvas.getContext('2d');
  
  // 设置canvas尺寸，增加宽度以容纳完整文本
  canvas.width = 300;
  canvas.height = 200;
  
  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  
  // 设置水印样式
  ctx.font = `${props.fontSize}px Arial`;
  ctx.fillStyle = props.color;
  ctx.globalAlpha = props.opacity;
  
  // 保存当前状态
  ctx.save();
  
  // 移动到画布中心并旋转
  ctx.translate(canvas.width / 2, canvas.height / 2);
  ctx.rotate(props.rotate * Math.PI / 180);
  ctx.translate(-canvas.width / 2, -canvas.height / 2);
  
  // 生成水印文本
  console.log('获取到的用户信息:', props.userInfo);
  const { username, name } = props.userInfo;
  const watermarkText = username && name ? `${username} - ${name}` : '系统水印';
  console.log('生成的水印文本:', watermarkText);
  
  // 绘制水印文本
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(watermarkText, canvas.width / 2, canvas.height / 2);
  
  // 恢复状态
  ctx.restore();
  
  // 将canvas转换为base64图片
  const base64Url = canvas.toDataURL('image/png');
  console.log('生成的base64图片:', base64Url.substring(0, 100) + '...');
  
  // 设置为背景图
  watermarkContainer.value.style.backgroundImage = `url(${base64Url})`;
  watermarkContainer.value.style.backgroundSize = `${props.spacing}px ${props.spacing}px`;
  console.log('水印样式已设置');
};

onMounted(() => {
  addWatermark();
});

watch(
  () => [props.userInfo, props.fontSize, props.color, props.opacity, props.rotate, props.spacing],
  () => {
    addWatermark();
  },
  { deep: true }
);
</script>

<style scoped>
.watermark-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  background-repeat: repeat;
  background-color: transparent;
}
</style>