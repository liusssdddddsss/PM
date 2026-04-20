package com.example.springboot.service;

import com.example.springboot.entity.Project;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.AIWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AIAnalysisEngine {

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @Autowired
    private AIWarningService aiWarningService;

    // 扫描状态
    private final AtomicBoolean isScanning = new AtomicBoolean(false);
    
    // 上次扫描时间
    private AtomicLong lastScanTime = new AtomicLong(0);
    
    // 扫描结果统计
    private int totalProjectsScanned = 0;
    private int highRiskProjects = 0;
    private int mediumRiskProjects = 0;
    private int lowRiskProjects = 0;
    
    private int totalProductsScanned = 0;
    private int highRiskProducts = 0;
    private int mediumRiskProducts = 0;
    private int lowRiskProducts = 0;

    // 定时扫描，每6小时执行一次
    @Scheduled(cron = "0 0 */6 * * *")
    public void scheduledScan() {
        if (!isScanning.get()) {
            scanAll();
        }
    }

    // 手动触发扫描
    public synchronized void scanAll() {
        if (isScanning.get()) {
            System.out.println("AI分析引擎：扫描任务正在进行中，请勿重复触发");
            return;
        }

        System.out.println("AI分析引擎：开始全量扫描...");
        isScanning.set(true);
        lastScanTime.set(System.currentTimeMillis());

        try {
            // 分析项目风险
            Map<String, Object> projectResult = aiAnalysisService.analyzeProjectsRisk();
            totalProjectsScanned = (int) projectResult.get("totalProjects");
            highRiskProjects = (int) projectResult.get("highRiskProjects");
            mediumRiskProjects = (int) projectResult.get("mediumRiskProjects");
            lowRiskProjects = (int) projectResult.get("lowRiskProjects");

            // 分析产品风险
            Map<String, Object> productResult = aiAnalysisService.analyzeProductsRisk();
            totalProductsScanned = (int) productResult.get("totalProducts");
            highRiskProducts = (int) productResult.get("highRiskProducts");
            mediumRiskProducts = (int) productResult.get("mediumRiskProducts");
            lowRiskProducts = (int) productResult.get("lowRiskProducts");

            // 生成扫描报告消息
            createScanReport();

            System.out.println("AI分析引擎：全量扫描完成");
            System.out.println("项目扫描结果：总计" + totalProjectsScanned + "个项目，高风险" + highRiskProjects + "个，中风险" + mediumRiskProjects + "个，低风险" + lowRiskProjects + "个");
            System.out.println("产品扫描结果：总计" + totalProductsScanned + "个产品，高风险" + highRiskProducts + "个，中风险" + mediumRiskProducts + "个，低风险" + lowRiskProducts + "个");

        } catch (Exception e) {
            System.err.println("AI分析引擎：扫描过程中发生错误");
            e.printStackTrace();
        } finally {
            isScanning.set(false);
        }
    }

    // 扫描单个项目
    public void scanProject(Long projectId) {
        System.out.println("AI分析引擎：开始扫描项目ID: " + projectId);
        try {
            // 这里可以调用AIAnalysisService的方法来分析单个项目
            // 暂时使用现有的分析方法
            aiAnalysisService.analyzeProjectsRisk();
            System.out.println("AI分析引擎：项目扫描完成");
        } catch (Exception e) {
            System.err.println("AI分析引擎：项目扫描错误");
            e.printStackTrace();
        }
    }

    // 扫描单个产品
    public void scanProduct(Long productId) {
        System.out.println("AI分析引擎：开始扫描产品ID: " + productId);
        try {
            // 这里可以调用AIAnalysisService的方法来分析单个产品
            // 暂时使用现有的分析方法
            aiAnalysisService.analyzeProductsRisk();
            System.out.println("AI分析引擎：产品扫描完成");
        } catch (Exception e) {
            System.err.println("AI分析引擎：产品扫描错误");
            e.printStackTrace();
        }
    }

    // 创建扫描报告并保存到ai_warnings表
    private void createScanReport() {
        AIWarning warning = new AIWarning();
        warning.setProjectId(null);
        warning.setUserId(null);
        warning.setRiskType("scan_report");
        warning.setRiskLevel(highRiskProjects > 0 || highRiskProducts > 0 ? "high" : "low");
        warning.setCreatedAt(new Date());

        StringBuilder riskDescription = new StringBuilder();
        riskDescription.append("扫描时间: " + new Date(lastScanTime.get()) + "\n");
        riskDescription.append("\n项目扫描结果:\n");
        riskDescription.append("- 总计: " + totalProjectsScanned + "个项目\n");
        riskDescription.append("- 高风险: " + highRiskProjects + "个 (" + (totalProjectsScanned > 0 ? (highRiskProjects * 100 / totalProjectsScanned) : 0) + "%)\n");
        riskDescription.append("- 中风险: " + mediumRiskProjects + "个 (" + (totalProjectsScanned > 0 ? (mediumRiskProjects * 100 / totalProjectsScanned) : 0) + "%)\n");
        riskDescription.append("- 低风险: " + lowRiskProjects + "个 (" + (totalProjectsScanned > 0 ? (lowRiskProjects * 100 / totalProjectsScanned) : 0) + "%)\n");
        
        riskDescription.append("\n产品扫描结果:\n");
        riskDescription.append("- 总计: " + totalProductsScanned + "个产品\n");
        riskDescription.append("- 高风险: " + highRiskProducts + "个 (" + (totalProductsScanned > 0 ? (highRiskProducts * 100 / totalProductsScanned) : 0) + "%)\n");
        riskDescription.append("- 中风险: " + mediumRiskProducts + "个 (" + (totalProductsScanned > 0 ? (mediumRiskProducts * 100 / totalProductsScanned) : 0) + "%)\n");
        riskDescription.append("- 低风险: " + lowRiskProducts + "个 (" + (totalProductsScanned > 0 ? (lowRiskProducts * 100 / totalProductsScanned) : 0) + "%)\n");

        if (highRiskProjects > 0 || highRiskProducts > 0) {
            riskDescription.append("\n【警告】发现高风险项目或产品，建议及时处理！\n");
        }

        warning.setRiskDescription(riskDescription.toString());
        warning.setSuggestion("定期检查高风险项目，及时处理相关问题。");
        warning.setIsRead(0);
        aiWarningService.save(warning);
    }

    // 获取引擎状态
    public Map<String, Object> getEngineStatus() {
        Map<String, Object> status = new java.util.HashMap<>();
        status.put("isScanning", isScanning.get());
        status.put("lastScanTime", lastScanTime.get() > 0 ? new Date(lastScanTime.get()) : null);
        status.put("totalProjectsScanned", totalProjectsScanned);
        status.put("highRiskProjects", highRiskProjects);
        status.put("mediumRiskProjects", mediumRiskProjects);
        status.put("lowRiskProjects", lowRiskProjects);
        status.put("totalProductsScanned", totalProductsScanned);
        status.put("highRiskProducts", highRiskProducts);
        status.put("mediumRiskProducts", mediumRiskProducts);
        status.put("lowRiskProducts", lowRiskProducts);
        return status;
    }

    // 立即执行扫描
    public void executeScan() {
        scanAll();
    }
}