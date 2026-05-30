package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.AttributeOptionDTO;
import com.sxpi.pan.aimallproduct.dto.AttributeTemplateDTO;
import com.sxpi.pan.aimallproduct.entity.AttributeOption;
import com.sxpi.pan.aimallproduct.entity.AttributeTemplate;
import com.sxpi.pan.aimallproduct.mapper.AttributeOptionMapper;
import com.sxpi.pan.aimallproduct.mapper.AttributeTemplateMapper;
import com.sxpi.pan.aimallproduct.service.impl.AttributeTemplateServiceImpl;
import com.sxpi.pan.aimallproduct.vo.AttributeTemplateVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttributeTemplateService 单元测试")
class AttributeTemplateServiceImplTest {

    @InjectMocks
    private AttributeTemplateServiceImpl templateService;

    @Mock
    private AttributeTemplateMapper templateMapper;
    @Mock
    private AttributeOptionMapper optionMapper;

    private AttributeTemplate testTemplate;
    private AttributeOption testOption;

    @BeforeEach
    void setUp() {
        testTemplate = new AttributeTemplate();
        testTemplate.setId(1L);
        testTemplate.setCategoryId(1L);
        testTemplate.setName("颜色");
        testTemplate.setInputType("select");
        testTemplate.setRequired(1);
        testTemplate.setSort(1);

        testOption = new AttributeOption();
        testOption.setId(1L);
        testOption.setTemplateId(1L);
        testOption.setValue("红色");
        testOption.setSort(1);
    }

    @Test
    @DisplayName("获取模板列表-成功")
    void getList_success() {
        Page<AttributeTemplate> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testTemplate));
        mockPage.setTotal(1);

        when(templateMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);
        when(optionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testOption));

        List<AttributeTemplateVO> result = templateService.getList(1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("颜色", result.get(0).getName());
        assertEquals(1, result.get(0).getOptions().size());
    }

    @Test
    @DisplayName("根据分类ID查询模板-成功")
    void getByCategoryId_success() {
        when(templateMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testTemplate));
        when(optionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testOption));

        List<AttributeTemplateVO> result = templateService.getByCategoryId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("添加模板-成功")
    void add_success() {
        when(templateMapper.insert(any(AttributeTemplate.class))).thenReturn(1);

        AttributeTemplateDTO dto = new AttributeTemplateDTO();
        dto.setCategoryId(1L);
        dto.setName("尺码");
        dto.setInputType("select");

        assertDoesNotThrow(() -> templateService.add(dto));
        verify(templateMapper).insert(any(AttributeTemplate.class));
    }

    @Test
    @DisplayName("更新模板-成功")
    void update_success() {
        when(templateMapper.selectById(1L)).thenReturn(testTemplate);
        when(templateMapper.updateById(any(AttributeTemplate.class))).thenReturn(1);

        AttributeTemplateDTO dto = new AttributeTemplateDTO();
        dto.setCategoryId(1L);
        dto.setName("颜色分类");

        assertDoesNotThrow(() -> templateService.update(1L, dto));
        verify(templateMapper).updateById(any(AttributeTemplate.class));
    }

    @Test
    @DisplayName("更新模板-不存在")
    void update_notFound() {
        when(templateMapper.selectById(999L)).thenReturn(null);

        AttributeTemplateDTO dto = new AttributeTemplateDTO();
        dto.setName("测试");

        assertThrows(BusinessException.class, () -> templateService.update(999L, dto));
    }

    @Test
    @DisplayName("删除模板-成功")
    void delete_success() {
        when(templateMapper.selectById(1L)).thenReturn(testTemplate);
        when(templateMapper.deleteById(1L)).thenReturn(1);
        when(optionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

        assertDoesNotThrow(() -> templateService.delete(1L));
        verify(templateMapper).deleteById(1L);
    }

    @Test
    @DisplayName("添加选项-成功")
    void addOption_success() {
        when(templateMapper.selectById(1L)).thenReturn(testTemplate);
        when(optionMapper.insert(any(AttributeOption.class))).thenReturn(1);

        AttributeOptionDTO dto = new AttributeOptionDTO();
        dto.setValue("蓝色");
        dto.setSort(2);

        assertDoesNotThrow(() -> templateService.addOption(1L, dto));
        verify(optionMapper).insert(any(AttributeOption.class));
    }

    @Test
    @DisplayName("添加选项-模板不存在")
    void addOption_templateNotFound() {
        when(templateMapper.selectById(999L)).thenReturn(null);

        AttributeOptionDTO dto = new AttributeOptionDTO();
        dto.setValue("蓝色");

        assertThrows(BusinessException.class, () -> templateService.addOption(999L, dto));
    }

    @Test
    @DisplayName("删除选项-成功")
    void deleteOption_success() {
        when(optionMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> templateService.deleteOption(1L));
        verify(optionMapper).deleteById(1L);
    }
}
