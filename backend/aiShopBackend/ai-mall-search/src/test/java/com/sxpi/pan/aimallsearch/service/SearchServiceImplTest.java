package com.sxpi.pan.aimallsearch.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.entity.SearchHistory;
import com.sxpi.pan.aimallsearch.mapper.ProductIndexMapper;
import com.sxpi.pan.aimallsearch.mapper.SearchHistoryMapper;
import com.sxpi.pan.aimallsearch.service.impl.SearchServiceImpl;
import com.sxpi.pan.aimallsearch.vo.HotKeywordVO;
import com.sxpi.pan.aimallsearch.vo.SearchHistoryVO;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchService 单元测试")
class SearchServiceImplTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private ProductIndexMapper productIndexMapper;
    @Mock
    private SearchHistoryMapper searchHistoryMapper;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;

    private final Long userId = 1L;

    @Test
    @DisplayName("搜索商品-成功")
    void search_success() {
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "测试商品");
        item.put("subtitle", "副标题");
        item.put("main_image", "http://img.jpg");
        item.put("price", new BigDecimal("99.00"));
        item.put("market_price", new BigDecimal("199.00"));
        item.put("sales", 100);
        item.put("category_id", 1L);

        when(productIndexMapper.searchProducts(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(item));
        when(productIndexMapper.countSearch(any(), any(), any(), any()))
                .thenReturn(1L);
        when(searchHistoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(searchHistoryMapper.insert(any(SearchHistory.class))).thenReturn(1);
        when(searchHistoryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        SearchQueryDTO query = new SearchQueryDTO();
        query.setKeyword("测试");
        query.setPage(1);
        query.setSize(10);

        SearchResultVO result = searchService.search(userId, query);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("测试商品", result.getList().get(0).getName());
    }

    @Test
    @DisplayName("搜索商品-无关键词")
    void search_noKeyword() {
        when(productIndexMapper.searchProducts(isNull(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(productIndexMapper.countSearch(isNull(), isNull(), isNull(), isNull()))
                .thenReturn(0L);

        SearchQueryDTO query = new SearchQueryDTO();
        query.setPage(1);
        query.setSize(10);

        SearchResultVO result = searchService.search(null, query);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("搜索商品-带分类筛选")
    void search_withCategory() {
        when(productIndexMapper.searchProducts(any(), eq(5L), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(productIndexMapper.countSearch(any(), eq(5L), any(), any()))
                .thenReturn(0L);

        SearchQueryDTO query = new SearchQueryDTO();
        query.setKeyword("裙子");
        query.setCategoryId(5L);
        query.setPage(1);
        query.setSize(10);

        SearchResultVO result = searchService.search(null, query);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
    }

    @Test
    @DisplayName("获取热门搜索-有Redis数据")
    void getHotKeywords_withRedis() {
        Set<String> keys = new LinkedHashSet<>();
        keys.add("连衣裙");
        keys.add("T恤");

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange("search:hot_keywords", 0, 9)).thenReturn(keys);
        when(zSetOperations.score("search:hot_keywords", "连衣裙")).thenReturn(100.0);
        when(zSetOperations.score("search:hot_keywords", "T恤")).thenReturn(80.0);

        List<HotKeywordVO> result = searchService.getHotKeywords();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("连衣裙", result.get(0).getKeyword());
        assertEquals(100, result.get(0).getScore());
    }

    @Test
    @DisplayName("获取热门搜索-无Redis数据返回默认")
    void getHotKeywords_default() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange("search:hot_keywords", 0, 9)).thenReturn(Collections.emptySet());

        List<HotKeywordVO> result = searchService.getHotKeywords();

        assertNotNull(result);
        assertEquals(10, result.size());
    }

    @Test
    @DisplayName("搜索建议-成功")
    void suggest_success() {
        when(productIndexMapper.suggestByPrefix("连衣")).thenReturn(
                Arrays.asList("连衣裙", "连衣裙女夏", "连衣裙长款"));

        List<String> result = searchService.suggest("连衣");

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("搜索建议-空关键词")
    void suggest_empty() {
        List<String> result = searchService.suggest("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取搜索历史-成功")
    void getHistory_success() {
        SearchHistory history = new SearchHistory();
        history.setId(1L);
        history.setUserId(userId);
        history.setKeyword("测试");

        when(searchHistoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(history));

        List<SearchHistoryVO> result = searchService.getHistory(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试", result.get(0).getKeyword());
    }

    @Test
    @DisplayName("清空搜索历史-成功")
    void clearHistory_success() {
        when(searchHistoryMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(5);

        assertDoesNotThrow(() -> searchService.clearHistory(userId));
        verify(searchHistoryMapper).delete(any(LambdaQueryWrapper.class));
    }
}
