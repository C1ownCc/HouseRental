-- 更新深圳南山区房源
UPDATE house 
SET latitude = 22.535023, 
    longitude = 113.932466
WHERE id = 1;

-- 更新深圳福田区房源
UPDATE house 
SET latitude = 22.543097, 
    longitude = 114.057868
WHERE id = 2;

-- 更新深圳罗湖区房源
UPDATE house 
SET latitude = 22.555341, 
    longitude = 114.137674
WHERE id = 3;

-- 更新南京溧水区房源
UPDATE house 
SET latitude = 31.653061, 
    longitude = 119.028319
WHERE id = 4;

-- 更新北京东城区房源
UPDATE house 
SET latitude = 39.928353, 
    longitude = 116.416357
WHERE id = 5;

-- 更新天津和平区房源
UPDATE house 
SET latitude = 39.117196, 
    longitude = 117.195907
WHERE id = 6;

-- 更新所有房源的update_time字段为当前时间
UPDATE house 
SET update_time = CURRENT_TIMESTAMP 
WHERE update_time IS NULL; 