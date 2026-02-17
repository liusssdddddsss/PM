package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name="管理员模块")
public class AdminController {
    @Resource
    AdminService adminService;

//    查询所有的数据
    @Operation(summary = "查询所有管理员",description = "返回管理员列表")
    @GetMapping("/findAll")
    public Result findAll(){
        List<Admin> list = adminService.findall();
        return  Result.success(list);
    }
}
