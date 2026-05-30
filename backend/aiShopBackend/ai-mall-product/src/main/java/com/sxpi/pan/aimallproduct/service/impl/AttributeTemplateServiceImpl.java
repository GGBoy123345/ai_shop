package com.sxpi.pan.aimallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.AttributeOptionDTO;
import com.sxpi.pan.aimallproduct.dto.AttributeTemplateDTO;
import com.sxpi.pan.aimallproduct.entity.AttributeOption;
import com.sxpi.pan.aimallproduct.entity.AttributeTemplate;
import com.sxpi.pan.aimallproduct.mapper.AttributeOptionMapper;
import com.sxpi.pan.aimallproduct.mapper.AttributeTemplateMapper;
import com.sxpi.pan.aimallproduct.service.AttributeTemplateService;
import com.sxpi.pan.aimallproduct.vo.AttributeTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeTemplateServiceImpl implements AttributeTemplateService {

    private final AttributeTemplateMapper templateMapper;
    private final AttributeOptionMapper optionMapper;

    @Override
    public List<AttributeTemplateVO> getList(Integer page, Integer size) {
        Page<AttributeTemplate> pageParam = new Page<>(page, size);
        Page<AttributeTemplate> result = templateMapper.selectPage(pageParam,
                new LambdaQueryWrapper<AttributeTemplate>().orderByAsc(AttributeTemplate::getSort));
        return result.getRecords().stream().map(this::toVO).toList();
    }

    @Override
    public List<AttributeTemplateVO> getByCategoryId(Long categoryId) {
        List<AttributeTemplate> list = templateMapper.selectList(
                new LambdaQueryWrapper<AttributeTemplate>()
                        .eq(AttributeTemplate::getCategoryId, categoryId)
                        .orderByAsc(AttributeTemplate::getSort));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public void add(AttributeTemplateDTO dto) {
        AttributeTemplate template = new AttributeTemplate();
        BeanUtils.copyProperties(dto, template);
        if (template.getInputType() == null) {
            template.setInputType("text");
        }
        templateMapper.insert(template);
    }

    @Override
    public void update(Long id, AttributeTemplateDTO dto) {
        AttributeTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(40412, "属性模板不存在");
        }
        BeanUtils.copyProperties(dto, template);
        templateMapper.updateById(template);
    }

    @Override
    public void delete(Long id) {
        AttributeTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(40412, "属性模板不存在");
        }
        templateMapper.deleteById(id);
        // 同时删除关联的选项
        optionMapper.delete(new LambdaQueryWrapper<AttributeOption>()
                .eq(AttributeOption::getTemplateId, id));
    }

    @Override
    public void addOption(Long templateId, AttributeOptionDTO dto) {
        AttributeTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(40412, "属性模板不存在");
        }
        AttributeOption option = new AttributeOption();
        BeanUtils.copyProperties(dto, option);
        option.setTemplateId(templateId);
        optionMapper.insert(option);
    }

    @Override
    public void deleteOption(Long optionId) {
        optionMapper.deleteById(optionId);
    }

    private AttributeTemplateVO toVO(AttributeTemplate template) {
        AttributeTemplateVO vo = new AttributeTemplateVO();
        BeanUtils.copyProperties(template, vo);
        List<AttributeOption> options = optionMapper.selectList(
                new LambdaQueryWrapper<AttributeOption>()
                        .eq(AttributeOption::getTemplateId, template.getId())
                        .orderByAsc(AttributeOption::getSort));
        vo.setOptions(options.stream().map(o -> {
            AttributeTemplateVO.OptionVO opt = new AttributeTemplateVO.OptionVO();
            opt.setId(o.getId());
            opt.setValue(o.getValue());
            opt.setSort(o.getSort());
            return opt;
        }).toList());
        return vo;
    }
}
