package com.sxpi.pan.aimallsearch.service;

import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.vo.HotKeywordVO;
import com.sxpi.pan.aimallsearch.vo.SearchHistoryVO;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;

import java.util.List;

public interface SearchService {
    SearchResultVO search(Long userId, SearchQueryDTO query);
    List<HotKeywordVO> getHotKeywords();
    List<String> suggest(String keyword);
    List<SearchHistoryVO> getHistory(Long userId);
    void clearHistory(Long userId);
}
