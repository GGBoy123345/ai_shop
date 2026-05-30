package com.sxpi.pan.aimallsearch.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.service.SearchService;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/search")
@RequiredArgsConstructor
public class InternalSearchController {

    private final SearchService searchService;

    @GetMapping
    public Result<SearchResultVO> search(SearchQueryDTO query) {
        return Result.success(searchService.search(null, query));
    }
}
