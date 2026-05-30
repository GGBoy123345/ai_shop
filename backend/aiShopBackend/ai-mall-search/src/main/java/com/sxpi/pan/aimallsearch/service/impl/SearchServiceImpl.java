package com.sxpi.pan.aimallsearch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.entity.SearchHistory;
import com.sxpi.pan.aimallsearch.mapper.ProductIndexMapper;
import com.sxpi.pan.aimallsearch.mapper.SearchHistoryMapper;
import com.sxpi.pan.aimallsearch.service.SearchService;
import com.sxpi.pan.aimallsearch.vo.HotKeywordVO;
import com.sxpi.pan.aimallsearch.vo.SearchHistoryVO;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductIndexMapper productIndexMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String HOT_KEYWORD_KEY = "search:hot_keywords";

    @Override
    public SearchResultVO search(Long userId, SearchQueryDTO query) {
        String keyword = query.getKeyword();
        int offset = (query.getPage() - 1) * query.getSize();

        List<Map<String, Object>> records = productIndexMapper.searchProducts(
                keyword, query.getCategoryId(), query.getMinPrice(), query.getMaxPrice(),
                query.getSort(), offset, query.getSize());
        long total = productIndexMapper.countSearch(
                keyword, query.getCategoryId(), query.getMinPrice(), query.getMaxPrice());

        // 记录搜索历史
        if (userId != null && keyword != null && !keyword.isBlank()) {
            saveSearchHistory(userId, keyword.trim());
            // 更新热词
            redisTemplate.opsForZSet().incrementScore(HOT_KEYWORD_KEY, keyword.trim(), 1);
        }

        SearchResultVO result = new SearchResultVO();
        result.setTotal(total);
        result.setPage(query.getPage());
        result.setSize(query.getSize());
        result.setKeyword(keyword);
        result.setList(records.stream().map(this::toSearchItem).toList());
        return result;
    }

    @Override
    public List<HotKeywordVO> getHotKeywords() {
        Set<String> topKeys = redisTemplate.opsForZSet().reverseRange(HOT_KEYWORD_KEY, 0, 9);
        if (topKeys == null || topKeys.isEmpty()) {
            // 返回默认热词
            return List.of(
                    hotItem("连衣裙", 9856), hotItem("T恤", 8720), hotItem("运动鞋", 7500),
                    hotItem("手机壳", 6890), hotItem("蓝牙耳机", 6200), hotItem("牛仔裤", 5800),
                    hotItem("防晒霜", 5100), hotItem("背包", 4700), hotItem("短裤", 4200), hotItem("拖鞋", 3900)
            );
        }
        List<HotKeywordVO> result = new ArrayList<>();
        for (String key : topKeys) {
            Double score = redisTemplate.opsForZSet().score(HOT_KEYWORD_KEY, key);
            result.add(hotItem(key, score != null ? score.intValue() : 0));
        }
        return result;
    }

    @Override
    public List<String> suggest(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        List<String> suggestions = productIndexMapper.suggestByPrefix(keyword.trim());
        // 去重
        return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
    }

    @Override
    public List<SearchHistoryVO> getHistory(Long userId) {
        List<SearchHistory> list = searchHistoryMapper.selectList(
                new LambdaQueryWrapper<SearchHistory>()
                        .eq(SearchHistory::getUserId, userId)
                        .orderByDesc(SearchHistory::getCreateTime)
                        .last("LIMIT 20"));
        return list.stream().map(h -> {
            SearchHistoryVO vo = new SearchHistoryVO();
            BeanUtils.copyProperties(h, vo);
            return vo;
        }).toList();
    }

    @Override
    public void clearHistory(Long userId) {
        searchHistoryMapper.delete(
                new LambdaQueryWrapper<SearchHistory>().eq(SearchHistory::getUserId, userId));
    }

    private void saveSearchHistory(Long userId, String keyword) {
        // 查找是否已存在
        SearchHistory existing = searchHistoryMapper.selectOne(
                new LambdaQueryWrapper<SearchHistory>()
                        .eq(SearchHistory::getUserId, userId)
                        .eq(SearchHistory::getKeyword, keyword));
        if (existing != null) {
            existing.setCreateTime(java.time.LocalDateTime.now());
            searchHistoryMapper.updateById(existing);
        } else {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword);
            searchHistoryMapper.insert(history);
            // 保留最近20条
            Long count = searchHistoryMapper.selectCount(
                    new LambdaQueryWrapper<SearchHistory>().eq(SearchHistory::getUserId, userId));
            if (count > 20) {
                List<SearchHistory> oldest = searchHistoryMapper.selectList(
                        new LambdaQueryWrapper<SearchHistory>()
                                .eq(SearchHistory::getUserId, userId)
                                .orderByAsc(SearchHistory::getCreateTime)
                                .last("LIMIT " + (count - 20)));
                for (SearchHistory h : oldest) {
                    searchHistoryMapper.deleteById(h.getId());
                }
            }
        }
    }

    private SearchResultVO.SearchItemVO toSearchItem(Map<String, Object> map) {
        SearchResultVO.SearchItemVO item = new SearchResultVO.SearchItemVO();
        item.setId(toLong(map.get("id")));
        item.setName((String) map.get("name"));
        item.setSubtitle((String) map.get("subtitle"));
        item.setMainImage((String) map.get("main_image"));
        item.setPrice(toBigDecimal(map.get("price")));
        item.setMarketPrice(toBigDecimal(map.get("market_price")));
        item.setSales(toInt(map.get("sales")));
        item.setCategoryId(toLong(map.get("category_id")));
        return item;
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long l) return l;
        if (obj instanceof Number n) return n.longValue();
        return null;
    }

    private Integer toInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer i) return i;
        if (obj instanceof Number n) return n.intValue();
        return null;
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal b) return b;
        if (obj instanceof Number n) return new BigDecimal(n.toString());
        return null;
    }

    private HotKeywordVO hotItem(String keyword, int score) {
        HotKeywordVO vo = new HotKeywordVO();
        vo.setKeyword(keyword);
        vo.setScore(score);
        return vo;
    }
}
