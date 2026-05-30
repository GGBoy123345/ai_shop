package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.dto.AttributeOptionDTO;
import com.sxpi.pan.aimallproduct.dto.AttributeTemplateDTO;
import com.sxpi.pan.aimallproduct.service.AttributeTemplateService;
import com.sxpi.pan.aimallproduct.vo.AttributeTemplateVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attribute-templates")
@RequiredArgsConstructor
public class AttributeTemplateController {

    private final AttributeTemplateService templateService;

    @GetMapping
    public Result<List<AttributeTemplateVO>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(templateService.getList(page, size));
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<AttributeTemplateVO>> getByCategoryId(@PathVariable Long categoryId) {
        return Result.success(templateService.getByCategoryId(categoryId));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody AttributeTemplateDTO dto) {
        templateService.add(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttributeTemplateDTO dto) {
        templateService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    @PostMapping("/{templateId}/options")
    public Result<Void> addOption(@PathVariable Long templateId, @Valid @RequestBody AttributeOptionDTO dto) {
        templateService.addOption(templateId, dto);
        return Result.success();
    }

    @DeleteMapping("/options/{id}")
    public Result<Void> deleteOption(@PathVariable Long id) {
        templateService.deleteOption(id);
        return Result.success();
    }
}
