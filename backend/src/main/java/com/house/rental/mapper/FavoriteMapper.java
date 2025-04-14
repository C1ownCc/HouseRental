package com.house.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.rental.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    // 获取收藏列表（包含房源信息）
    IPage<Favorite> getFavoriteList(Page<Favorite> page, @Param("userId") Long userId);
    
    @Update("UPDATE favorite SET deleted = 1 WHERE id = #{id}")
    int softDelete(@Param("id") Long id);
    
    @Update("UPDATE favorite SET deleted = 0 WHERE id = #{id}")
    int restore(@Param("id") Long id);
    
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND house_id = #{houseId}")
    Favorite findByUserIdAndHouseId(@Param("userId") Long userId, @Param("houseId") Long houseId);
} 