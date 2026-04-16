<template>
  <div class="add-product">
    <h3>编辑产品</h3>
    <div class="form-container">
      <el-form :model="productForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称">
              <el-input v-model="productForm.name" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品类型">
              <el-select v-model="productForm.type" placeholder="请选择">
                <el-option label="产品型" value="product" />
                <el-option label="项目型" value="project" />
                <el-option label="服务型" value="service" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品流程">
              <el-select v-model="productForm.flow" placeholder="请选择">
                <el-option label="流程1" value="flow1" />
                <el-option label="流程2" value="flow2" />
                <el-option label="流程3" value="flow3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="productForm.leader" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评审人">
              <el-select v-model="productForm.reviewer" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品描述">
          <el-input
              v-model="productForm.description"
              type="textarea"
              placeholder="请输入产品描述"
              :rows="4"
          />
        </el-form-item>

        <el-form-item label="访问控制">
          <el-radio-group v-model="productForm.accessControl">
            <el-radio label="public">公开(有项目视图权限即可访问)</el-radio>
            <el-radio label="private">私有(只有项目负责人、团队成员和干系人可访问)</el-radio>
            <el-radio label="group">项目集内公开(所有上级项目集负责人和干系人、项目负责人、团队成员和干系人可访问)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <div class="form-buttons">
            <el-button type="primary" @click="saveProduct">保存</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { recordOperationLog } from '@/utils/operationLog.js';

const router = useRouter();

// 产品表单数据
const productForm = ref({
  name: '',
  type: '',
  flow: 'flow1',
  leader: '',
  reviewer: '',
  description: '',
  accessControl: 'public'
});

// 保存产品
const saveProduct = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 构建产品数据
      const productData = {
        name: productForm.value.name,
        description: productForm.value.description,
        manager_id: user.username, // 使用当前登录用户作为产品负责人
        status: 0, // 默认状态为未开始
        start_date: new Date(),
        end_date: null
      };
      // 调用后端API保存产品
      const response = await request.post('/workbench/projects', productData);
      if (response.data.code === 200) {
        // 记录操作日志
        await recordOperationLog('创建了', '产品', null, productForm.value.name);
        ElMessage.success('产品保存成功');
        // 保存成功后返回
        goBack();
      } else {
        ElMessage.error(response.data.msg || '产品保存失败');
      }
    }
  } catch (error) {
    console.error('保存产品失败:', error);
    ElMessage.error('产品保存失败');
  }
};

// 返回上一页
const goBack = () => {
  router.push('/productResearch/productList');
};
</script>

<style scoped>
.add-product {
  background-color: white;
  padding-top: 10px;
  padding-left: 10px;
}

.form-container {
  padding: 20px;
}

.form-buttons {
  margin-top: 10px;
  width: 100%;
  text-align: center;
}
</style>