<template>
  <div class="task-add">
    <h3>新增任务</h3>
    <div class="form-container">
      <el-form :model="taskForm" label-width="120px" class="add-task-form">
        <div class="form-row">
          <el-form-item label="所属项目">
            <el-select v-model="taskForm.project" placeholder="请选择">
              <el-option label="智慧教室" value="智慧教室" />
              <el-option label="电子班牌" value="电子班牌" />
              <el-option label="数据大屏" value="数据大屏" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务名称">
            <el-input v-model="taskForm.name" placeholder="请输入" />
          </el-form-item>
          
          <el-form-item label="任务类型">
            <div class="task-type-buttons">
              <el-button 
                :type="taskForm.taskType === '短期' ? 'primary' : 'default'"
                @click="taskForm.taskType = '短期'"
              >
                短期
              </el-button>
              <el-button 
                :type="taskForm.taskType === '中期' ? 'primary' : 'default'"
                @click="taskForm.taskType = '中期'"
              >
                中期
              </el-button>
              <el-button 
                :type="taskForm.taskType === '长期' ? 'primary' : 'default'"
                @click="taskForm.taskType = '长期'"
              >
                长期
              </el-button>
            </div>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="预计开始日期">
            <el-date-picker
              v-model="taskForm.startDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="预计完成日期">
            <el-date-picker
              v-model="taskForm.endDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="预计完成时长">
            <el-input v-model="taskForm.duration" placeholder="请输入" />
          </el-form-item>
          
          <el-form-item label="关联项目">
            <el-select v-model="taskForm.relatedProject" placeholder="请选择">
              <el-option label="无" value="" />
              <el-option label="项目1" value="project1" />
              <el-option label="项目2" value="project2" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="产品负责人">
            <el-select v-model="taskForm.productLeader" placeholder="请选择">
              <el-option label="张三" value="张三" />
              <el-option label="李四" value="李四" />
              <el-option label="王五" value="王五" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="测试负责人">
            <el-select v-model="taskForm.testLeader" placeholder="请选择">
              <el-option label="赵六" value="赵六" />
              <el-option label="孙七" value="孙七" />
              <el-option label="周八" value="周八" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="团队成员">
            <el-select v-model="taskForm.teamMembers" multiple placeholder="请选择">
              <el-option label="张三" value="张三" />
              <el-option label="李四" value="李四" />
              <el-option label="王五" value="王五" />
              <el-option label="赵六" value="赵六" />
              <el-option label="孙七" value="孙七" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="访问控制">
            <div class="access-control">
              <el-checkbox v-model="taskForm.inheritProjectAccess" checked>
                继承项目访问权（能访问当前项目，即可访问）
              </el-checkbox>
              <br>
              <el-radio-group v-model="taskForm.accessType" class="mt-2">
                <el-radio label="private">私有（团队成员和项目负责人、干系人可访问）</el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务描述">
            <el-input
              v-model="taskForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
            />
          </el-form-item>
        </div>
        
        <div class="form-buttons">
          <el-button type="primary" @click="saveTask">保存</el-button>
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 任务表单数据
const taskForm = ref({
  project: '',
  name: '',
  taskType: '短期',
  startDate: '',
  endDate: '',
  duration: '',
  relatedProject: '',
  productLeader: '',
  testLeader: '',
  teamMembers: [],
  inheritProjectAccess: true,
  accessType: 'private',
  description: ''
});

// 保存任务
const saveTask = () => {
  console.log('保存任务:', taskForm.value);
  // 这里可以添加表单验证和保存逻辑
  // 保存成功后跳转回任务列表
  router.push('/task/taskList');
};

// 返回上一页
const goBack = () => {
  router.push('/task/taskList');
};
</script>

<style scoped>
.task-add {
  padding: 10px;
  background-color: #fff;
  min-height: 100vh;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.add-task-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-row .el-form-item {
  flex: 1;
  margin-bottom: 0;
}

.task-type-buttons {
  display: flex;
  gap: 10px;
}

.access-control {
  margin-top: 4px;
}

.mt-2 {
  margin-top: 8px;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  gap: 12px;
  justify-content: center;
}
</style>