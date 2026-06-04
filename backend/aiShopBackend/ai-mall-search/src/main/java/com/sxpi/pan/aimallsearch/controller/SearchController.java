package com.sxpi.pan.aimallsearch.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.service.SearchService;
import com.sxpi.pan.aimallsearch.vo.HotKeywordVO;
import com.sxpi.pan.aimallsearch.vo.SearchHistoryVO;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Result<SearchResultVO> search(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                         SearchQueryDTO query) {
        return Result.success(searchService.search(userId, query));
    }

    @GetMapping("/hot")
    public Result<List<HotKeywordVO>> getHotKeywords() {
        return Result.success(searchService.getHotKeywords());
    }

    @GetMapping("/suggest")
    public Result<List<String>> suggest(@RequestParam String keyword) {
        return Result.success(searchService.suggest(keyword));
    }

    @GetMapping("/history")
    public Result<List<SearchHistoryVO>> getHistory(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(searchService.getHistory(userId));
    }

    @DeleteMapping("/history")
    public Result<Void> clearHistory(@RequestHeader("X-User-Id") Long userId) {
        searchService.clearHistory(userId);
        return Result.success(null);
    }

    @PostMapping("/sync")
    public Result<String> syncToEs() {
        searchService.fullSync();
        return Result.success("同步完成");
    }

    @PostMapping("/rebuild")
    public Result<String> rebuildIndex() {
        searchService.rebuildIndex();
        return Result.success("重建完成");
    }
}
