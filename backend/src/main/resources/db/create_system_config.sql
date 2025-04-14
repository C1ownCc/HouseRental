USE house_rental;

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(50) NOT NULL COMMENT '配置键',
    config_value TEXT NOT NULL COMMENT '配置值',
    description VARCHAR(255) COMMENT '描述',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化系统配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('website_name', '房屋租赁平台', '网站名称'),
('website_title', '房屋租赁平台 - 专业的租房平台', '网站标题'),
('website_description', '专业的房屋租赁平台，提供海量房源信息', '网站描述'),
('website_keywords', '租房,房屋租赁,租房平台', '网站关键词'),
('website_logo', '', '网站Logo'),
('website_icp', '', 'ICP备案号'),
('website_phone', '', '客服电话'),
('website_email', '', '客服邮箱'),
('upload_type', 'local', '上传方式：local-本地存储，oss-阿里云OSS，cos-腾讯云COS'),
('upload_path', '/uploads', '上传目录'),
('upload_url', '', '访问域名'),
('email_host', '', 'SMTP服务器'),
('email_port', '', 'SMTP端口'),
('email_username', '', '发件人邮箱'),
('email_password', '', '邮箱密码'),
('email_from', '', '发件人'),
('email_ssl', '0', '是否启用SSL：0-否，1-是'); 