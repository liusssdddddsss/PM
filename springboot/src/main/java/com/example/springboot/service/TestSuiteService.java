package com.example.springboot.service;

import com.example.springboot.entity.TestSuite;
import com.example.springboot.repository.TestSuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestSuiteService {

    @Autowired
    private TestSuiteRepository testSuiteRepository;

    // 获取所有测试套件
    public List<TestSuite> findAll() {
        return testSuiteRepository.findAll();
    }

    // 根据ID获取测试套件
    public Optional<TestSuite> findById(Long id) {
        return testSuiteRepository.findById(id);
    }

    // 保存测试套件
    public TestSuite save(TestSuite testSuite) {
        return testSuiteRepository.save(testSuite);
    }

    // 删除测试套件
    public void deleteById(Long id) {
        testSuiteRepository.deleteById(id);
    }

}
