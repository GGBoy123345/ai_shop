package com.sxpi.pan.aimallproduct.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallproduct.dto.CategoryDTO;
import com.sxpi.pan.aimallproduct.service.CategoryService;
import com.sxpi.pan.aimallproduct.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @PostMapping
    public Result<Void> addCategory(@Valid @RequestBody CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(categoryService.getCategoryStats());
    }
}
