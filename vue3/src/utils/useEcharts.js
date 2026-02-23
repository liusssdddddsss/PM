import {onMounted, onUnmounted,ref} from "vue";
import * as echarts from 'echarts';

export function useEcharts(option){
    const chartRef = ref(null);
    let chartInstance = null;
    let resizeObserver = null;

    onMounted(() => {
        if (chartRef.value){
            chartInstance=echarts.init(chartRef.value);
            chartInstance.setOption(option);
            resizeObserver = new ResizeObserver(() => chartInstance.resize());
            resizeObserver.observe(chartRef.value);
        }
    });
    onUnmounted(() => {
        chartInstance?.dispose();
        chartInstance?.dispose();
    });

    return {chartRef};
}