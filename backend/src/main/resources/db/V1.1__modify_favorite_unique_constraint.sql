-- 删除原有的唯一约束
ALTER TABLE favorite DROP INDEX uk_user_house;

-- 添加新的唯一约束，包含deleted字段
ALTER TABLE favorite ADD CONSTRAINT uk_user_house UNIQUE KEY (user_id, house_id, deleted); 