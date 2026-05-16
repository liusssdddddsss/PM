package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.Iteration;
import com.example.springboot.repository.ProductRepository;
import com.example.springboot.repository.IterationRepository;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private IterationRepository iterationRepository;
    
    @Autowired
    private com.example.springboot.service.TeamMemberService teamMemberService;
    
    @Autowired
    private com.example.springboot.service.ProjectMemberService projectMemberService;

    // 获取产品列表
    @GetMapping("/products")
    public Result getProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }
    
    // 搜索产品
    @GetMapping("/productResearch/products")
    public Result searchProducts(@RequestParam(required = false) String search) {
        try {
            List<Product> products = productRepository.findAll();
            if (search != null && !search.isEmpty()) {
                products = products.stream()
                    .filter(product -> product.getName() != null && product.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
            }
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索产品失败: " + e.getMessage());
        }
    }

    // 创建产品
    @PostMapping("/products")
    public Result createProduct(@RequestBody Product product) {
        try {
            // 设置创建时间
            product.setCreated_at(new Date());
            // 保存产品
            Product savedProduct = productRepository.save(product);
            return Result.success(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建产品失败: " + e.getMessage());
        }
    }
    
    // 获取产品详情及关联项目信息
    @GetMapping("/products/{id}/detail")
    public Result getProductDetail(@PathVariable Long id, @RequestParam(required = false) String username) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            System.out.println("========== 调用 /api/products/" + id + "/detail 接口 ==========");
            System.out.println("  产品ID参数: " + id);
            System.out.println("  用户名参数: " + username);
            
            // 获取产品基本信息
            var productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return Result.error("产品不存在");
            }
            
            Product product = productOpt.get();
            System.out.println("  产品名称: " + product.getName());
            result.put("id", product.getId());
            result.put("name", product.getName());
            result.put("code", product.getCode());
            result.put("status", product.getStatus());
            result.put("releaseStatus", product.getReleaseStatus());
            result.put("owner_id", product.getOwner_id());
            
            // 获取产品负责人信息
            String ownerName = "未知";
            if (product.getOwner_id() != null) {
                try {
                    User user = userService.findByUsername(product.getOwner_id().toString());
                    if (user != null) {
                        ownerName = user.getName() != null ? user.getName() : user.getUsername();
                    }
                } catch (Exception e) {
                    System.out.println("获取产品负责人信息失败: " + e.getMessage());
                }
            }
            result.put("manager", ownerName);
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                try {
                    Long userId = Long.parseLong(username);
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        if (member.getProjectId() != null) {
                            userProjectIds.add(member.getProjectId());
                        }
                    }
                } catch (Exception e) {
                    // 如果获取项目成员失败，继续执行
                }
            }
            
            // 获取产品关联的项目列表
            List<Map<String, Object>> projects = new ArrayList<>();
            List<Map<String, Object>> iterations = new ArrayList<>();
            Iterable<Project> allProjects = projectService.findAll();
            System.out.println("  系统中项目总数: " + ((List<?>)allProjects).size());
            
            int matchedCount = 0;
            for (Project project : allProjects) {
                System.out.println("  检查项目: " + project.getName() + ", product_id: " + project.getProduct_id() + ", 目标产品ID: " + id);
                if (project.getProduct_id() != null && project.getProduct_id().longValue() == id.longValue()) {
                    // 如果指定了用户名，只返回与该用户有关的项目
                    boolean isProjectRelated = true;
                    if (username != null) {
                        isProjectRelated = false;
                        // 检查项目是否由用户管理
                        if (project.getManagerId() != null) {
                            Optional<User> userOptional = userService.findById(project.getManagerId().toString());
                            if (userOptional.isPresent() && username.equals(userOptional.get().getUsername())) {
                                isProjectRelated = true;
                                System.out.println("  -> 用户是项目管理员");
                            }
                        }
                        // 检查用户是否是项目成员
                        if (!isProjectRelated && userProjectIds.contains(project.getId())) {
                            isProjectRelated = true;
                            System.out.println("  -> 用户是项目成员");
                        }
                    }
                    
                    if (isProjectRelated) {
                        matchedCount++;
                        System.out.println("  -> 匹配到产品ID: " + id);
                        Map<String, Object> projectMap = new HashMap<>();
                        projectMap.put("id", project.getId());
                        projectMap.put("name", project.getName());
                        
                        // 获取项目负责人信息
                        String projectManager = "未知";
                        if (project.getManagerId() != null) {
                            try {
                                User user = userService.findByUsername(project.getManagerId().toString());
                                if (user != null) {
                                    projectManager = user.getName() != null ? user.getName() : user.getUsername();
                                }
                            } catch (Exception e) {
                                System.out.println("获取项目负责人信息失败: " + e.getMessage());
                            }
                        }
                        projectMap.put("manager", projectManager);
                        
                        // 获取项目所属团队信息
                        String teamName = "未知";
                        if (project.getTeam_id() != null) {
                            try {
                                var teamOpt = teamService.findById(project.getTeam_id().intValue());
                                if (teamOpt.isPresent()) {
                                    teamName = teamOpt.get().getName();
                                }
                            } catch (Exception e) {
                                System.out.println("获取团队信息失败: " + e.getMessage());
                            }
                        }
                        projectMap.put("team", teamName);
                        
                        projects.add(projectMap);
                        
                        // 获取项目关联的迭代
                        try {
                            List<Iteration> projectIterations = iterationRepository.findByProjectId(project.getId().intValue());
                            if (projectIterations != null && !projectIterations.isEmpty()) {
                                for (Iteration iteration : projectIterations) {
                                    Map<String, Object> iterationMap = new HashMap<>();
                                    iterationMap.put("id", iteration.getId());
                                    iterationMap.put("name", iteration.getName());
                                    iterationMap.put("projectId", iteration.getProjectId());
                                    iterationMap.put("projectName", project.getName());
                                    iterations.add(iterationMap);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("获取迭代信息失败: " + e.getMessage());
                        }
                    }
                }
            }
            System.out.println("  匹配到的项目数量: " + matchedCount);
            System.out.println("  返回的项目列表大小: " + projects.size());
            System.out.println("  返回的迭代列表大小: " + iterations.size());
            result.put("projects", projects);
            result.put("iterations", iterations);
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品详情失败: " + e.getMessage());
        }
    }
    
    // 更新产品信息
    @PutMapping("/products/{id}")
    @Transactional
    public Result updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        try {
            System.out.println("========== 调用 /api/products/" + id + " 更新接口 ==========");
            System.out.println("  更新的产品数据: " + updatedProduct);
            
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return Result.error("产品不存在");
            }
            
            Product product = productOpt.get();
            
            // 更新字段
            if (updatedProduct.getName() != null) {
                product.setName(updatedProduct.getName());
            }
            if (updatedProduct.getCode() != null) {
                product.setCode(updatedProduct.getCode());
            }
            if (updatedProduct.getOwner_id() != null) {
                product.setOwner_id(updatedProduct.getOwner_id());
            }
            if (updatedProduct.getStatus() != null) {
                product.setStatus(updatedProduct.getStatus());
            }
            if (updatedProduct.getReleaseStatus() != null) {
                product.setReleaseStatus(updatedProduct.getReleaseStatus());
            }
            if (updatedProduct.getTeamId() != null) {
                product.setTeamId(updatedProduct.getTeamId());
            }
            if (updatedProduct.getDescription() != null) {
                product.setDescription(updatedProduct.getDescription());
            }
            
            Product savedProduct = productRepository.save(product);
            System.out.println("  产品更新成功: " + savedProduct.getName());
            
            return Result.success(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新产品失败: " + e.getMessage());
        }
    }
}
