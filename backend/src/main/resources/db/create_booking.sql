USE house_rental;

-- 预约表
CREATE TABLE IF NOT EXISTS booking (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    house_id BIGINT NOT NULL COMMENT '房源ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    booking_time DATETIME NOT NULL COMMENT '预约看房时间',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    remark TEXT COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已取消，3-已完成',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    KEY idx_house (house_id),
    KEY idx_user (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';