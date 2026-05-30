package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.CategoryDTO;
import com.sxpi.pan.aimallproduct.entity.Category;
import com.sxpi.pan.aimallproduct.mapper.CategoryMapper;
import com.sxpi.pan.aimallproduct.service.impl.CategoryServiceImpl;
import com.sxpi.pan.aimallproduct.vo.CategoryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 单元测试")
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("服装");
        parentCategory.setParentId(0L);
        parentCategory.setSort(1);
        parentCategory.setStatus(1);

        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("男装");
        childCategory.setParentId(1L);
        childCategory.setSort(1);
        childCategory.setStatus(1);
    }

    @Test
    @DisplayName("获取分类树-成功")
    void getCategoryTree_success() {
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(parentCategory, childCategory));

        List<CategoryVO> tree = categoryService.getCategoryTree();

        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("服装", tree.get(0).getName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("男装", tree.get(0).getChildren().get(0).getName());
    }

    @Test
    @DisplayName("获取分类树-空列表")
    void getCategoryTree_empty() {
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        List<CategoryVO> tree = categoryService.getCategoryTree();
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("根据ID查询分类-成功")
    void getCategoryById_success() {
        when(categoryMapper.selectById(1L)).thenReturn(parentCategory);

        CategoryVO vo = categoryService.getCategoryById(1L);

        assertNotNull(vo);
        assertEquals("服装", vo.getName());
    }

    @Test
    @DisplayName("根据ID查询分类-不存在")
    void getCategoryById_notFound() {
        when(categoryMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> categoryService.getCategoryById(999L));
    }

    @Test
    @DisplayName("添加分类-成功")
    void addCategory_success() {
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("食品");
        dto.setSort(3);

        assertDoesNotThrow(() -> categoryService.addCategory(dto));
        verify(categoryMapper).insert(any(Category.class));
    }

    @Test
    @DisplayName("更新分类-成功")
    void updateCategory_success() {
        when(categoryMapper.selectById(1L)).thenReturn(parentCategory);
        when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("服装鞋帽");
        dto.setSort(1);

        assertDoesNotThrow(() -> categoryService.updateCategory(1L, dto));
        verify(categoryMapper).updateById(any(Category.class));
    }

    @Test
    @DisplayName("更新分类-不存在")
    void updateCategory_notFound() {
        when(categoryMapper.selectById(999L)).thenReturn(null);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("测试");

        assertThrows(BusinessException.class, () -> categoryService.updateCategory(999L, dto));
    }

    @Test
    @DisplayName("删除分类-成功")
    void deleteCategory_success() {
        when(categoryMapper.selectById(1L)).thenReturn(parentCategory);
        when(categoryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(categoryMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除分类-有子分类")
    void deleteCategory_hasChildren() {
        when(categoryMapper.selectById(1L)).thenReturn(parentCategory);
        when(categoryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.deleteCategory(1L));
        assertEquals(40030, ex.getCode());
    }

    @Test
    @DisplayName("删除分类-不存在")
    void deleteCategory_notFound() {
        when(categoryMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> categoryService.deleteCategory(999L));
    }
}
