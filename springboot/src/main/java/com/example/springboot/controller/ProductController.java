package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
