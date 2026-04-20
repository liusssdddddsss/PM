package com.example.springboot.service;

import com.example.springboot.entity.Project;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.Task;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.AIWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AIAnalysisService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BugService bugService;

    @Autowired
    private AIWarningService aiWarningService;

    // 分析所有项目的风险
    public Map<String, Object> analyzeProjectsRisk() {
        Map<String, Object> result = new HashMap<>();
        List<Project> projects = (List<Project>) projectService.findAll();
        List<Map<String, Object>> projectRisks = projects.stream()
                .map(this::analyzeProjectRisk)
                .collect(Collectors.toList());
        result.put("projects", projectRisks);
        result.put("totalProjects", projects.size());
        result.put("highRiskProjects", projectRisks.stream().filter(p -> "高".equals(p.get("riskLevel"))).count());
        result.put("mediumRiskProjects", projectRisks.stream().filter(p -> "中".equals(p.get("riskLevel"))).count());
        result.put("lowRiskProjects", projectRisks.stream().filter(p -> "低".equals(p.get("riskLevel"))).count());
        return result;
    }

    // 分析单个项目的风险
    public Map<String, Object> analyzeProjectRisk(Project project) {
        Map<String, Object> riskData = new HashMap<>();
        riskData.put("projectId", project.getId());
        riskData.put("projectName", project.getName());
        riskData.put("startDate", project.getStart_date());
        riskData.put("endDate", project.getEnd_date());
        riskData.put("progress", project.getProgress());

        // 计算进度风险
        int progressRisk = calculateProgressRisk(project);
        riskData.put("progressRisk", progressRisk);

        // 计算任务风险
        int taskRisk = calculateTaskRisk(project);
        riskData.put("taskRisk", taskRisk);

        // 计算Bug风险
        int bugRisk = calculateBugRisk(project);
        riskData.put("bugRisk", bugRisk);

        // 计算总体风险等级
        int totalRisk = (progressRisk + taskRisk + bugRisk) / 3;
        String riskLevel = getRiskLevel(totalRisk);
        riskData.put("totalRisk", totalRisk);
        riskData.put("riskLevel", riskLevel);

        // 生成风险预警和建议
        List<String> warnings = generateWarnings(project, progressRisk, taskRisk, bugRisk);
        List<String> suggestions = generateSuggestions(project, progressRisk, taskRisk, bugRisk);
        riskData.put("warnings", warnings);
        riskData.put("suggestions", suggestions);

        // 将风险提示保存到消息表中
        if (totalRisk >= 40) { // 中风险及以上
            createRiskMessage(project, riskLevel, warnings, suggestions);
        }

        return riskData;
    }

    // 分析所有产品的风险
    public Map<String, Object> analyzeProductsRisk() {
        Map<String, Object> result = new HashMap<>();
        List<Product> products = (List<Product>) productService.findAll();
        List<Map<String, Object>> productRisks = products.stream()
                .map(this::analyzeProductRisk)
                .collect(Collectors.toList());
        result.put("products", productRisks);
        result.put("totalProducts", products.size());
        result.put("highRiskProducts", productRisks.stream().filter(p -> "高".equals(p.get("riskLevel"))).count());
        result.put("mediumRiskProducts", productRisks.stream().filter(p -> "中".equals(p.get("riskLevel"))).count());
        result.put("lowRiskProducts", productRisks.stream().filter(p -> "低".equals(p.get("riskLevel"))).count());
        return result;
    }

    // 分析单个产品的风险
    public Map<String, Object> analyzeProductRisk(Product product) {
        Map<String, Object> riskData = new HashMap<>();
        riskData.put("productId", product.getId());
        riskData.put("productName", product.getName());

        // 这里可以根据产品相关的项目、任务、Bug等数据进行分析
        // 暂时简化处理，实际项目中需要根据具体业务逻辑进行扩展
        riskData.put("riskLevel", "低");
        riskData.put("warnings", List.of("产品风险分析功能正在开发中"));
        riskData.put("suggestions", List.of("定期检查产品相关的项目进度"));

        return riskData;
    }

    // 计算进度风险
    private int calculateProgressRisk(Project project) {
        if (project.getProgress() == null) {
            return 50; // 默认风险
        }

        // 根据项目进度和剩余时间计算风险
        int progress = project.getProgress();
        Date endDate = project.getEnd_date();
        Date startDate = project.getStart_date();
        if (endDate == null || startDate == null) {
            return 50; // 默认风险
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(currentDate, endLocalDate);
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startLocalDate, endLocalDate);

        if (daysRemaining <= 0) {
            // 项目已超期
            if (progress < 100) {
                return 100; // 高风险
            } else {
                return 0; // 无风险
            }
        }

        if (totalDays <= 0) {
            return 50; // 默认风险
        }

        // 根据剩余时间和进度计算风险
        // 根据项目的实际总周期来计算预期进度
        int expectedProgress = 100 - (int) (daysRemaining * 100 / totalDays);
        if (progress < expectedProgress - 20) {
            return 80; // 高风险
        } else if (progress < expectedProgress - 10) {
            return 60; // 中风险
        } else {
            return 20; // 低风险
        }
    }

    // 计算任务风险
    private int calculateTaskRisk(Project project) {
        List<Task> tasks = taskService.findByProjectId(project.getId().intValue());
        if (tasks.isEmpty()) {
            return 20; // 无任务，低风险
        }

        // 计算未完成任务的比例
        long unfinishedTasks = tasks.stream().filter(task -> task.getStatus() != null && task.getStatus() < 2).count();
        double unfinishedRatio = (double) unfinishedTasks / tasks.size();

        // 计算任务延期情况
        long overdueTasks = tasks.stream().filter(task -> {
            if (task.getDueDate() == null) {
                return false;
            }
            LocalDate dueLocalDate = parseDateString(task.getDueDate());
            if (dueLocalDate == null) {
                return false;
            }
            LocalDate currentDate = LocalDate.now();
            return currentDate.isAfter(dueLocalDate) && (task.getStatus() == null || task.getStatus() < 2);
        }).count();

        if (overdueTasks > 0) {
            return 80; // 有延期任务，高风险
        } else if (unfinishedRatio > 0.7) {
            return 60; // 大部分任务未完成，中风险
        } else {
            return 20; // 低风险
        }
    }

    // 解析日期字符串
    private LocalDate parseDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            // 尝试解析不同格式的日期字符串
            if (dateString.contains("T")) {
                // 格式：2023-12-31T00:00:00.000Z
                return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            } else if (dateString.contains(" ")) {
                // 格式：2023-12-31 00:00:00
                return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate();
            } else {
                // 格式：2023-12-31
                return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            }
        } catch (Exception e) {
            System.out.println("解析日期字符串失败: " + dateString);
            return null;
        }
    }

    // 计算Bug风险
    private int calculateBugRisk(Project project) {
        List<Bug> projectBugs = bugService.findByProjectId(project.getId().intValue());

        if (projectBugs.isEmpty()) {
            return 0; // 无Bug，无风险
        }

        // 计算未解决Bug的数量
        long unsolvedBugs = projectBugs.stream().filter(bug -> bug.getStatus() == null || bug.getStatus() < 2).count();

        if (unsolvedBugs > 5) {
            return 80; // 大量未解决Bug，高风险
        } else if (unsolvedBugs > 2) {
            return 60; // 中等数量未解决Bug，中风险
        } else {
            return 20; // 少量未解决Bug，低风险
        }
    }

    // 获取风险等级
    private String getRiskLevel(int risk) {
        if (risk >= 70) {
            return "高";
        } else if (risk >= 40) {
            return "中";
        } else {
            return "低";
        }
    }

    // 生成风险预警
    private List<String> generateWarnings(Project project, int progressRisk, int taskRisk, int bugRisk) {
        List<String> warnings = new ArrayList<>();

        if (progressRisk >= 70) {
            warnings.add("项目进度落后，可能面临延期风险");
        }

        if (taskRisk >= 70) {
            warnings.add("存在未完成的延期任务");
        }

        if (bugRisk >= 70) {
            warnings.add("存在大量未解决的Bug");
        }

        if (warnings.isEmpty()) {
            warnings.add("未发现明显风险");
        }

        return warnings;
    }

    // 生成建议措施
    private List<String> generateSuggestions(Project project, int progressRisk, int taskRisk, int bugRisk) {
        List<String> suggestions = new ArrayList<>();

        if (progressRisk >= 70) {
            suggestions.add("增加资源投入，加快项目进度");
            suggestions.add("重新评估项目计划，调整时间节点");
        }

        if (taskRisk >= 70) {
            suggestions.add("优先处理延期任务");
            suggestions.add("检查任务分配是否合理");
        }

        if (bugRisk >= 70) {
            suggestions.add("增加测试资源，加快Bug修复");
            suggestions.add("分析Bug产生的原因，避免类似问题");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("继续保持当前的项目管理方式");
        }

        return suggestions;
    }

    // 创建风险消息并保存到ai_warnings表
    private void createRiskMessage(Project project, String riskLevel, List<String> warnings, List<String> suggestions) {
        String riskLevelCode = riskLevel.equals("高") ? "high" : riskLevel.equals("中") ? "medium" : "low";
        
        // 构建风险描述
        StringBuilder riskDescription = new StringBuilder();
        riskDescription.append("项目名称: " + project.getName() + "\n");
        riskDescription.append("风险等级: " + riskLevel + "\n");
        riskDescription.append("\n风险预警: ");
        for (String warningMsg : warnings) {
            if (!"未发现明显风险".equals(warningMsg)) {
                riskDescription.append("\n- " + warningMsg);
            }
        }
        
        // 构建建议措施
        StringBuilder suggestionText = new StringBuilder();
        for (String suggestion : suggestions) {
            suggestionText.append("\n- " + suggestion);
        }
        
        // 检查是否已经存在相同的项目风险预警
        List<AIWarning> existingWarnings = aiWarningService.findByProjectId(project.getId().intValue());
        AIWarning warning = null;
        
        for (AIWarning existingWarning : existingWarnings) {
            if ("project_risk".equals(existingWarning.getRiskType()) && riskLevelCode.equals(existingWarning.getRiskLevel())) {
                // 存在相同的预警，覆盖原来的
                warning = existingWarning;
                break;
            }
        }
        
        if (warning == null) {
            // 不存在相同的预警，创建新的
            warning = new AIWarning();
            warning.setProjectId(project.getId().intValue());
            warning.setUserId(null); // 暂时设置为null，实际项目中应该设置为相关用户ID
            warning.setRiskType("project_risk");
            warning.setRiskLevel(riskLevelCode);
            warning.setCreatedAt(new Date());
        }
        
        // 更新预警信息
        warning.setIsRead(0); // 重置为未读状态
        warning.setRiskDescription(riskDescription.toString());
        warning.setSuggestion(suggestionText.toString());
        
        aiWarningService.save(warning);
    }
}
