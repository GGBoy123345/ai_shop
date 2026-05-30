package com.sxpi.pan.aimallproduct.service;

import com.sxpi.pan.aimallproduct.dto.AttributeOptionDTO;
import com.sxpi.pan.aimallproduct.dto.AttributeTemplateDTO;
import com.sxpi.pan.aimallproduct.vo.AttributeTemplateVO;

import java.util.List;

public interface AttributeTemplateService {
    List<AttributeTemplateVO> getList(Integer page, Integer size);
    List<AttributeTemplateVO> getByCategoryId(Long categoryId);
    void add(AttributeTemplateDTO dto);
    void update(Long id, AttributeTemplateDTO dto);
    void delete(Long id);
    void addOption(Long templateId, AttributeOptionDTO dto);
    void deleteOption(Long optionId);
}
