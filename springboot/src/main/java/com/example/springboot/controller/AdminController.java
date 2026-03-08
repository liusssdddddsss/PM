package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "用户登录",description = "根据用户名和密码进行登录验证")
    @PostMapping("/login")
    public Result login(@RequestParam(required = false) String username, @RequestParam(required = false) String password) {
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            return Result.success(admin);
        } else {
            return Result.error("用户名或密码错误");
        }
    }
}
