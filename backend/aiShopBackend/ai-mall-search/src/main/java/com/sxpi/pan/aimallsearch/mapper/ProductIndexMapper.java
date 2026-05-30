package com.sxpi.pan.aimallsearch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxpi.pan.aimallsearch.entity.ProductIndex;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ProductIndexMapper extends BaseMapper<ProductIndex> {

    @Select("<script>" +
            "SELECT p.id, p.name, p.subtitle, p.main_image, p.price, p.market_price, p.sales, p.category_id " +
            "FROM product p " +
            "WHERE p.deleted = 0 AND p.status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (p.name LIKE CONCAT('%',#{keyword},'%') OR p.subtitle LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND p.category_id = #{categoryId}" +
            "</if>" +
            "<if test='minPrice != null'>" +
            "AND p.price &gt;= #{minPrice}" +
            "</if>" +
            "<if test='maxPrice != null'>" +
            "AND p.price &lt;= #{maxPrice}" +
            "</if>" +
            "<choose>" +
            "<when test='sort == \"price_asc\"'>ORDER BY p.price ASC</when>" +
            "<when test='sort == \"price_desc\"'>ORDER BY p.price DESC</when>" +
            "<when test='sort == \"sales_desc\"'>ORDER BY p.sales DESC</when>" +
            "<when test='sort == \"newest\"'>ORDER BY p.create_time DESC</when>" +
            "<otherwise>ORDER BY p.sales DESC, p.id DESC</otherwise>" +
            "</choose>" +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> searchProducts(@Param("keyword") String keyword,
                                              @Param("categoryId") Long categoryId,
                                              @Param("minPrice") java.math.BigDecimal minPrice,
                                              @Param("maxPrice") java.math.BigDecimal maxPrice,
                                              @Param("sort") String sort,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM product p " +
            "WHERE p.deleted = 0 AND p.status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (p.name LIKE CONCAT('%',#{keyword},'%') OR p.subtitle LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND p.category_id = #{categoryId}" +
            "</if>" +
            "<if test='minPrice != null'>" +
            "AND p.price &gt;= #{minPrice}" +
            "</if>" +
            "<if test='maxPrice != null'>" +
            "AND p.price &lt;= #{maxPrice}" +
            "</if>" +
            "</script>")
    long countSearch(@Param("keyword") String keyword,
                     @Param("categoryId") Long categoryId,
                     @Param("minPrice") java.math.BigDecimal minPrice,
                     @Param("maxPrice") java.math.BigDecimal maxPrice);

    @Select("SELECT name FROM product WHERE deleted = 0 AND status = 1 AND name LIKE CONCAT('%',#{prefix},'%') LIMIT 10")
    List<String> suggestByPrefix(@Param("prefix") String prefix);
}
