import {onMounted, onUnmounted, ref, watch} from "vue";
import * as echarts from 'echarts';

export function useEcharts(){
    const chartRef = ref(null);
    let chartInstance = null;
    let resizeObserver = null;

    const initChart = (options) => {
        if (chartRef.value){
            chartInstance = echarts.init(chartRef.value);
            chartInstance.setOption(options);
            resizeObserver = new ResizeObserver(() => chartInstance.resize());
            resizeObserver.observe(chartRef.value);
        }
    };

    const updateChart = (options) => {
        if (chartInstance) {
            chartInstance.setOption(options);
        }
    };

    onUnmounted(() => {
        chartInstance?.dispose();
        resizeObserver?.disconnect();
    });

    return {chartRef, initChart, updateChart};
}