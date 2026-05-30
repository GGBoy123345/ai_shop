package com.sxpi.pan.aimallproduct.service;

import com.sxpi.pan.aimallproduct.dto.CategoryDTO;
import com.sxpi.pan.aimallproduct.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<CategoryVO> getCategoryTree();
    CategoryVO getCategoryById(Long id);
    void addCategory(CategoryDTO dto);
    void updateCategory(Long id, CategoryDTO dto);
    void deleteCategory(Long id);
}
