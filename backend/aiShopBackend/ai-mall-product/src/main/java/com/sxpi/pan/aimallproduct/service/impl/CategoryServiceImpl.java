package com.sxpi.pan.aimallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.CategoryDTO;
import com.sxpi.pan.aimallproduct.entity.Category;
import com.sxpi.pan.aimallproduct.mapper.CategoryMapper;
import com.sxpi.pan.aimallproduct.service.CategoryService;
import com.sxpi.pan.aimallproduct.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        List<CategoryVO> voList = all.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(c, vo);
            vo.setChildren(new ArrayList<>());
            return vo;
        }).toList();

        Map<Long, CategoryVO> map = voList.stream()
                .collect(Collectors.toMap(CategoryVO::getId, v -> v));
        List<CategoryVO> tree = new ArrayList<>();
        for (CategoryVO vo : voList) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                tree.add(vo);
            } else {
                CategoryVO parent = map.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }
        return tree;
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40410, "分类不存在");
        }
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    @Override
    public void addCategory(CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        category.setStatus(1);
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40410, "分类不存在");
        }
        BeanUtils.copyProperties(dto, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40410, "分类不存在");
        }
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(40030, "该分类下有子分类，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
