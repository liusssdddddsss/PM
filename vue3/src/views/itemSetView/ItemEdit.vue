<template>
  <div class="edit">
    <h3>编辑项目</h3>
    <el-divider/>
    <div class="form-container">
      <el-form :model="projectForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型">
              <el-select v-model="projectForm.type" placeholder="请选择项目类型">
                <el-option label="产品型" value="product" />
                <el-option label="项目型" value="project" />
                <el-option label="服务型" value="service" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始日期">
              <el-date-picker
                  v-model="projectForm.startDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划完成日期">
              <el-date-picker
                  v-model="projectForm.endDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="projectForm.leader" placeholder="请选择负责人">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参与人">
              <el-select v-model="projectForm.participants" placeholder="请选择参与人">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
              v-model="projectForm.remark"
              type="textarea"
              placeholder="请输入备注"
              :rows="4"
          />
        </el-form-item>

        <el-form-item label="访问控制">
          <el-radio-group v-model="projectForm.accessControl">
            <el-radio label="public">公开(有项目权限即可访问)</el-radio>
            <el-radio label="private">私有(只项目负责人、团队成员和干系人可访问)</el-radio>
            <el-radio label="group">项目组内公开(所有上级项目负责人和干系人、项目负责人、团队成员和干系人可访问)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="权限控制">
          <el-radio-group v-model="projectForm.permissionControl">
            <el-radio label="inherit">继承(继承系统与项目级权限的合轴)</el-radio>
            <el-radio label="redefine">重新定义(只项目权限)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="关联需求概念">
          <el-checkbox-group v-model="projectForm.requirements">
            <el-checkbox label="business">业务需求</el-checkbox>
            <el-checkbox label="user">用户需求</el-checkbox>
            <el-checkbox label="development">研发需求</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item>
          <div class="form-buttons">
            <el-button type="primary" @click="saveProject">保存</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

// 项目表单数据
const projectForm = ref({
  name: '',
  type: '',
  startDate: '',
  endDate: '',
  leader: '',
  participants: '',
  remark: '',
  accessControl: 'public',
  permissionControl: 'inherit',
  requirements: []
});

// 获取项目详情
const fetchProjectDetail = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  try {
    const response = await request.get(`/workbench/projects/${projectId}`);
    if (response.data.code === 200) {
      const project = response.data.data;
      // 填充表单数据
      projectForm.value = {
        name: project.title || '',
        type: project.type || '',
        startDate: project.startTime ? new Date(project.startTime) : '',
        endDate: project.finishTime ? new Date(project.finishTime) : '',
        leader: project.person || '',
        participants: project.participants || '',
        remark: project.remark || '',
        accessControl: project.accessControl || 'public',
        permissionControl: project.permissionControl || 'inherit',
        requirements: project.requirements || []
      };
    }
  } catch (error) {
    console.error('获取项目详情失败:', error);
    ElMessage.error('获取项目详情失败');
  }
};

// 保存项目
const saveProject = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  try {
    // 构建保存数据
    const saveData = {
      title: projectForm.value.name,
      type: projectForm.value.type,
      startTime: projectForm.value.startDate,
      finishTime: projectForm.value.endDate,
      person: projectForm.value.leader,
      participants: projectForm.value.participants,
      remark: projectForm.value.remark,
      accessControl: projectForm.value.accessControl,
      permissionControl: projectForm.value.permissionControl,
      requirements: projectForm.value.requirements
    };
    
    // 调用后端API保存项目
    const response = await request.put(`/workbench/projects/${projectId}`, saveData);
    if (response.data.code === 200) {
      ElMessage.success('项目保存成功');
      // 保存成功后返回
      goBack();
    }
  } catch (error) {
    console.error('保存项目失败:', error);
    ElMessage.error('保存项目失败');
  }
};

// 返回上一页
const goBack = () => {
  router.push('/itemSet/itemList');
};

// 页面加载时获取项目详情
onMounted(() => {
  fetchProjectDetail();
});
</script>

<style scoped>
.edit {
  background-color: #fff;
  padding: 20px;
  min-height: 100vh;
}
.el-divider{
  margin-top: 10px;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

.form-buttons {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-radio,
.el-checkbox {
  display: block;
  margin-bottom: 10px;
}
</style>