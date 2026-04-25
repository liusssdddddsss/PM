package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

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
}
