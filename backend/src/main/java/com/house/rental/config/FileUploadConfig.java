package com.house.rental.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    private static final String PROJECT_ROOT = new File(System.getProperty("user.dir")).getParent();
    private static final String UPLOAD_DIR = PROJECT_ROOT + "/frontend/public/uploads";
    private static final String IMAGE_DIR = UPLOAD_DIR + "/images";
    
    @PostConstruct
    public void init() {
        log.info("初始化文件上传配置");
        log.info("项目根目录: {}", PROJECT_ROOT);
        // 确保上传目录存在
        createDirectory(UPLOAD_DIR);
        createDirectory(IMAGE_DIR);
        log.info("文件上传目录初始化完成");
    }
    
    private void createDirectory(String path) {
        File directory = new File(path);
        log.info("检查目录是否存在: {}", directory.getAbsolutePath());
        if (!directory.exists()) {
            log.info("创建目录: {}", directory.getAbsolutePath());
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("无法创建目录: {}", directory.getAbsolutePath());
                throw new RuntimeException("无法创建目录：" + directory.getAbsolutePath());
            }
            log.info("目录创建成功: {}", directory.getAbsolutePath());
        } else {
            log.info("目录已存在: {}", directory.getAbsolutePath());
        }
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("配置静态资源映射");
        // 配置静态资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + PROJECT_ROOT + "/frontend/public/uploads/")
                .setCachePeriod(3600);
        log.info("静态资源映射配置完成");
    }
    
    public String getImageUploadPath() {
        log.info("获取图片上传路径: {}", IMAGE_DIR);
        return IMAGE_DIR;
    }
    
    public String getImageAccessPath() {
        // 由于文件保存在前端public目录下，直接返回相对路径
        String path = "/uploads/images/";
        log.info("获取图片访问路径: {}", path);
        return path;
    }
} 