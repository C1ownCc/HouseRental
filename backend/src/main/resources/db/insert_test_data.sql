USE `houseRental`;

-- 清空表数据
TRUNCATE TABLE `user`;
TRUNCATE TABLE `house`;
TRUNCATE TABLE `booking`;
TRUNCATE TABLE `favorite`;
TRUNCATE TABLE `message`;

-- 插入测试用户数据
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin', 1),
('landlord', 'e10adc3949ba59abbe56e057f20f883e', '房东', 'landlord', 1),
('user', 'e10adc3949ba59abbe56e057f20f883e', '用户', 'user', 1);

-- 插入测试房源数据
INSERT INTO `house` (
  `title`, `description`, `province`, `city`, `district`, `address`,
  `room_count`, `hall_count`, `bathroom_count`, `area`, `price`, `deposit`,
  `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`,
  `facilities`, `images`, `contact_name`, `contact_phone`, `owner_id`, `status`
)
VALUES
(
  '整租·南山区精装两房·近地铁',
  '房源位于南山区，临近地铁1号线，周边配套设施齐全，交通便利。房间采光好，布局合理，配套设施齐全。',
  '广东省', '深圳市', '南山区', '南山大道123号',
  2, 1, 1, 75.50, 4500.00, 4500.00,
  12, 30, 1, '朝南', '精装',
  '["wifi","tv","ac","washer","fridge"]',
  'https://example.com/images/house1.jpg,https://example.com/images/house1-1.jpg',
  '张先生', '13800138001', 2, 1
),
(
  '整租·福田区豪华三房·江景房',
  '房源位于福田区，临江而建，视野开阔，可观江景。精装修，家具家电齐全，拎包入住。',
  '广东省', '深圳市', '福田区', '福田路456号',
  3, 2, 2, 120.00, 8000.00, 8000.00,
  25, 35, 1, '东南', '豪装',
  '["wifi","tv","ac","washer","fridge","microwave","desk","wardrobe"]',
  'https://example.com/images/house2.jpg,https://example.com/images/house2-1.jpg',
  '李先生', '13800138002', 2, 1
),
(
  '整租·罗湖区温馨一房·近地铁',
  '房源位于罗湖区，临近地铁2号线，周边商圈成熟，生活便利。房间温馨舒适，适合单身人士居住。',
  '广东省', '深圳市', '罗湖区', '罗湖路789号',
  1, 1, 1, 45.00, 3000.00, 3000.00,
  8, 20, 1, '朝南', '简装',
  '["wifi","tv","ac","washer"]',
  'https://example.com/images/house3.jpg,https://example.com/images/house3-1.jpg',
  '王先生', '13800138003', 2, 0
); 

-- 插入预约数据
INSERT INTO `booking` (`house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`) VALUES
(1, 2, '2024-01-05 10:00:00', '张三', '13800138001', '希望能在周末看房', 0),
(2, 2, '2024-01-06 14:30:00', '张三', '13800138001', '最好能提前联系', 1),
(1, 3, '2024-01-07 15:00:00', '李四', '13800138002', '想详细了解房屋情况', 2),
(3, 2, '2024-01-08 11:00:00', '张三', '13800138001', '关注采光问题', 3);

-- 插入收藏数据
INSERT INTO `favorite` (`user_id`, `house_id`) VALUES
(2, 1),
(2, 3),
(3, 2);

-- 插入消息数据
INSERT INTO `message` (`user_id`, `title`, `content`, `type`, `read_status`)
VALUES (2, '预约确认通知', '您预约查看的房源已确认，请准时到达。', 2, 0),
       (2, '系统通知', '欢迎使用房屋租赁平台！', 1, 1),
       (2, '预约取消通知', '很抱歉，您预约的房源因特殊原因已被取消。', 2, 0),
       (3, '系统通知', '欢迎使用房屋租赁平台！', 1, 0),
       (3, '预约提醒', '您有一个预约即将开始，请准时到达。', 2, 0);