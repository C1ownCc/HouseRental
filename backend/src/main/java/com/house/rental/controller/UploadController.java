package com.house.rental.controller;

import com.house.rental.common.Result;
import com.house.rental.config.FileUploadConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("开始处理文件上传请求");
        if (file.isEmpty()) {
            log.warn("上传文件为空");
            return Result.error("上传文件不能为空");
        }

        try {
            // 检查文件类型
            String contentType = file.getContentType();
            log.info("文件类型: {}", contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("非法的文件类型: {}", contentType);
                return Result.error("只能上传图片文件");
            }

            // 获取文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            log.info("原始文件名: {}", originalFilename);
            if (originalFilename == null) {
                log.warn("文件名为空");
                return Result.error("文件名不能为空");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 生成新的文件名
            String newFilename = UUID.randomUUID().toString() + extension;
            log.info("新文件名: {}", newFilename);
            
            // 保存文件
            File dest = new File(fileUploadConfig.getImageUploadPath(), newFilename);
            log.info("文件保存路径: {}", dest.getAbsolutePath());
            if (!dest.getParentFile().exists()) {
                log.info("创建目录: {}", dest.getParentFile().getAbsolutePath());
                if (!dest.getParentFile().mkdirs()) {
                    log.error("创建目录失败: {}", dest.getParentFile().getAbsolutePath());
                    return Result.error("创建目录失败");
                }
            }
            file.transferTo(dest);
            log.info("文件保存成功");
            
            // 返回访问路径
            String accessPath = fileUploadConfig.getImageAccessPath() + newFilename;
            log.info("文件访问路径: {}", accessPath);
            return Result.success(accessPath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
} 