package com.house.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.rental.entity.House;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseMapper extends BaseMapper<House> {
} 