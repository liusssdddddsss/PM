<template>
  <div class="product-research">
    <div class="tou">
      <div class="left">
        <h3>产品总览</h3>
        <div class="kuai">
          <span
              v-for="(item,index) in ovCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <dvi>{{item.count}}</dvi>
          </span>
        </div>
      </div>

      <el-divider direction="vertical" style="height: auto;align-self: stretch"/>

      <div class="right">
        <h3 style="display: inline-block">产品年度推进统计</h3>
<!--        下拉菜单待改-->
        <el-select v-model="selectedYear" placeholder="请选择年份" size="small" style="display: inline-block;width: 80px;">
          <el-option label="2023" value="2023"/>
          <el-option label="2024" value="2024" />
          <el-option label="2025" value="2025" />
        </el-select>
        <div class="kuai">
          <span
              v-for="(item,index) in tuiJinCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <dvi>{{item.count}}</dvi>
          </span>
        </div>
      </div>
    </div>
    <div class="comment">
      <div class="com-left" style="width: 65%">
<!--        产品年度统计工作-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="yearWorkStatistic">
            <h3>产品年度工作量统计</h3>
            <el-divider/>
            <div class="container">
              <div class="demand-size">
                <h3>完成需求规模</h3>
                <ul>
                  <li
                      v-for="(item,index) in demandSizeList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
              <div class="demand-count">
                <h3>完成需求数</h3>
                <ul>
                  <li
                      v-for="(item,index) in demandCountList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
              <div class="repair-bug">
                <h3>修复Bug数</h3>
                <ul>
                  <li
                      v-for="(item,index) in repairBugList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </el-card>

<!--        未关闭的产品统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>未关闭的产品统计</h3>
          <div class="product-statistics">
            <ProductList style="width: 100px;"/>
            <div class="product-statistics-echart">
              <el-row>
                <el-col :span="8" class="jiao-fu">
                  <h3>需求交付率</h3>
                  <el-progress type="circle" :percentage="25" />
                </el-col>
                <el-col :span="8" class="needs-statistic">
                  <h3>需求统计</h3>
                  <p>本月完成统计{{thisMonthFinish}}</p>
                  <p>本月新增{{thisMonthAdd}}</p>
                  <div ref="needsDom" style="width: 200px; height: 250px;"></div>
                </el-col>
                <el-col :span="8" class="project-latest">
                  <h3>产品最新推进</h3>
                  最新任务
                  最新发布
                </el-col>
              </el-row>
            </div>
          </div>
        </el-card>

<!--        未关闭的产品列表-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>未关闭的产品列表</h3>
          <ProductList/>
        </el-card>

<!--        产品发布列表-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品发布列表</h3>
          <ProductList/>
        </el-card>
      </div>

      <div class="com-right" style="width: 35%;">
<!--        产品发布统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品发布统计</h3>
          <el-divider/>
          <div ref="faBuDom" style="width: 100%; height: 250px;"></div>
          <div class="bangDan">
            <h4>年度发布榜({{currentYear}})</h4>
<!--            进度条那个感觉应该另外写个页面-->
            <div class="chart-container">
              <div ref="rankingChart" style="width: 100%; height: 300px;"></div>
            </div>
          </div>
        </el-card>

<!--        指派研发需求-->
        <el-card style="max-width: 98%;margin-top: 10px">
        <div class="assigned-rd-requirements">
          <h3>指派给我的研发需求</h3>
          <div class="table-container">
            <el-table :data="requirements" stripe style="width: 100%">
              <el-table-column prop="id" label="序号" width="80" />
              <el-table-column prop="name" label="研发需求名称" />
              <el-table-column prop="priority" label="优先级">
                <template #default="scope">
            <span :class="getPriorityClass(scope.row.priority)">
              {{ scope.row.priority }}
            </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        </el-card>
        <!--      测试统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="measure">
            <h3>测试统计</h3>
            <el-divider/>
            <div>
              <div class="shang">
                <div class="system-list">
                  <ProjectList/>
                </div>
                <div class="bug-statistics">
                  <h3>Bug统计</h3>
                  <div class="Add">
                    昨天新增{{yAdd}} <el-progress :percentage="50" status="exception" />
                    今日新增{{tAdd}} <el-progress :percentage="50" status="exception" />
                  </div>
                  <div class="Deal">
                    昨天新增{{yDeal}} <el-progress :percentage="50" status="exception" />
                    今日新增{{tDeal}} <el-progress :percentage="50" status="exception" />
                  </div>
                </div>
              </div>
              <div class="xia">
                <div class="bug-repair">
                  <h3>Bug修复率</h3>
                  <el-progress type="circle" :percentage="25" />
                  <span>
                  {{youXiaoBug}}
                  <p>有效Bug</p>
                </span>
                  <span>
                  {{yiRepair}}
                  已修复
                </span>
                  <span>
                  {{noClose}}
                  未关闭
                </span>
                </div>
                <div class="staying-test-list">
                  <h3>待测试的测试单</h3>
                  <StayTestList/>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
// 产品总览数据
import {ref} from "vue";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import {useEcharts} from "@/utils/useEcharts.js";
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";

const ovCount = ref([
  { name: '产品线总量', count: 10 },
  { name: '产品总数', count: 10 },
  { name: '未完成计划数', count: 6 },
  { name: '未关闭需求数', count: 4 },
  { name: '激活Bug数', count: 10 },
]);
// 产品年度推进统计数据
const tuiJinCount = ref([
  { name: '已完成发布数', count: 120 },
  { name: '已完成需求数', count: 80 },
  { name: '已完成Bug数', count: 543}
]);
// 选择的年份
const selectedYear = ref('2023');

// 格式化进度条显示
const formatProgress = (percentage) => {
  return `${percentage}`;
};

// 完成需求规模数据
const demandSizeList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

// 完成需求数数据
const demandCountList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

// 修复Bug数数据
const repairBugList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

const {chartRef: faBuDom} = useEcharts({
  title: {
    text:'月度发布次数趋势图'
  },
  xAxis:{
    type: 'category',
    data:['6月','7月','8月','9月','10月','11月']
  },
  yAxis:{
    type: 'value',
  },
  series:[{
    type:'line',
    data:[20,100,30,12,22,11]
  }]
});
const { chartRef: rankingChart } = useEcharts({
  title: { text: '年度发布榜' },
  xAxis: {
    type: 'category',
    data: ['实践教学管理平台', '电子班牌管理系统', '智慧校园(中学版)', '宿舍管理系统', '教务考试系统']
  },
  yAxis: { type: 'value' },
  series: [{
    type: 'bar',
    data: [123, 101, 86, 71, 66],
    label: { show: true, position: 'top' }
  }]
});
</script>

<style scoped>
.tou{
  display: flex;
  background-color: #fff;
}
.left{
  width: 60%;
}
.right{
  width: 40%;
}
.kuai{
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 150px;
  text-align: center;
}
.kuai span{
  flex: 1;
}
h3{
  padding: 10px;
}

.comment{
  display: flex;
}
/*产品年度工作量统计样式*/
.container{
  display: flex;
}
.container div{
  flex: 1;
}

.assigned-rd-requirements{
  background-color: #fff;
}


/*测量统计样式*/
.shang{
  height: 150px;
  background-color: pink;
  display: flex;
}
.system-list{
  flex:1;
}
.bug-statistics{
  flex: 2;
}
.xia{
  height: 150px;
  background-color: green;
  display: flex;
}
.bug-repair{
  flex: 1;
}
.staying-test-list{
  flex:1;
}
</style>