package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.service.AttributeTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attribute-options")
@RequiredArgsConstructor
public class AttributeOptionController {

    private final AttributeTemplateService templateService;

    @DeleteMapping("/{id}")
    public Result<Void> deleteOption(@PathVariable Long id) {
        templateService.deleteOption(id);
        return Result.success();
    }
}
