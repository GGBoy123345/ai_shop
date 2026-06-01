package com.sxpi.pan.aimallproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxpi.pan.aimallproduct.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 物理删除超过指定天数的已逻辑删除记录（绕过 @TableLogic）
     */
    @Delete("DELETE FROM product WHERE deleted = 1 AND update_time < #{threshold}")
    int physicalDeleteExpired(@Param("threshold") LocalDateTime threshold);
}
