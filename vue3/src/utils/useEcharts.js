import {onMounted, onUnmounted, ref, watch} from "vue";
import * as echarts from 'echarts';

export function useEcharts(){
    const chartRef = ref(null);
    let chartInstance = null;
    let resizeObserver = null;

    const initChart = (options, callback) => {
        if (!chartRef.value) return;

        // 反复 initChart 时，先清理旧实例，避免多重 init/多重 ResizeObserver
        if (chartInstance) {
            chartInstance.dispose();
            chartInstance = null;
        }
        resizeObserver?.disconnect();
        resizeObserver = null;

        chartInstance = echarts.init(chartRef.value);
        chartInstance.setOption(options);
        if (callback && typeof callback === 'function') {
            callback(chartInstance);
        }
        resizeObserver = new ResizeObserver(() => chartInstance?.resize());
        resizeObserver.observe(chartRef.value);
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