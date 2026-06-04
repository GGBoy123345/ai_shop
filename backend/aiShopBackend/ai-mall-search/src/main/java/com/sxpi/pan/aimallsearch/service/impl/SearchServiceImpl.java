package com.sxpi.pan.aimallsearch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sxpi.pan.aimallsearch.dto.SearchQueryDTO;
import com.sxpi.pan.aimallsearch.entity.SearchHistory;
import com.sxpi.pan.aimallsearch.mapper.ProductIndexMapper;
import com.sxpi.pan.aimallsearch.mapper.SearchHistoryMapper;
import com.sxpi.pan.aimallsearch.service.SearchService;
import com.sxpi.pan.aimallsearch.vo.HotKeywordVO;
import com.sxpi.pan.aimallsearch.vo.SearchHistoryVO;
import com.sxpi.pan.aimallsearch.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductIndexMapper productIndexMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final StringRedisTemplate redisTemplate;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String HOT_KEYWORD_KEY = "search:hot_keywords";
    private static final String INDEX_NAME = "product";

    /** 上次同步时间，用于增量同步 */
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusYears(10);

    @Override
    public SearchResultVO search(Long userId, SearchQueryDTO query) {
        String keyword = query.getKeyword();

        if (userId != null && keyword != null && !keyword.isBlank()) {
            saveSearchHistory(userId, keyword.trim());
            redisTemplate.opsForZSet().incrementScore(HOT_KEYWORD_KEY, keyword.trim(), 1);
        }

        try {
            ObjectNode body = buildSearchQuery(keyword, query);
            Request request = new Request("POST", "/" + INDEX_NAME + "/_search");
            request.setJsonEntity(body.toString());
            Response response = restClient.performRequest(request);
            JsonNode result = objectMapper.readTree(EntityUtils.toByteArray(response.getEntity()));

            long total = result.path("hits").path("total").path("value").asLong(0);
            JsonNode hits = result.path("hits").path("hits");

            List<SearchResultVO.SearchItemVO> items = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                SearchResultVO.SearchItemVO item = new SearchResultVO.SearchItemVO();
                item.setId(source.path("id").asLong());
                item.setName(source.path("title").asText(null));
                item.setSubtitle(source.path("subtitle").asText(null));
                item.setMainImage(source.path("mainImage").asText(null));
                item.setPrice(new BigDecimal(source.path("price").asText("0")));
                item.setMarketPrice(new BigDecimal(source.path("marketPrice").asText("0")));
                item.setSales(source.path("sales").asInt(0));
                item.setCategoryId(source.path("categoryId").asLong());

                // 高亮
                JsonNode highlight = hit.path("highlight");
                if (highlight.has("title")) {
                    item.setName(highlight.path("title").path(0).asText());
                }
                if (highlight.has("subtitle")) {
                    item.setSubtitle(highlight.path("subtitle").path(0).asText());
                }
                items.add(item);
            }

            int page = query.getPage() != null ? query.getPage() : 1;
            int size = query.getSize() != null ? query.getSize() : 10;

            SearchResultVO resultVO = new SearchResultVO();
            resultVO.setTotal(total);
            resultVO.setPage(page);
            resultVO.setSize(size);
            resultVO.setKeyword(keyword);
            resultVO.setList(items);
            return resultVO;

        } catch (Exception e) {
            log.error("ES 搜索异常，降级到 MySQL", e);
            return searchFromMySQL(userId, query);
        }
    }

    private ObjectNode buildSearchQuery(String keyword, SearchQueryDTO query) {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode boolNode = objectMapper.createObjectNode();

        // must: 状态过滤
        ArrayNode mustArray = objectMapper.createArrayNode();
        mustArray.add(objectMapper.createObjectNode().set("term",
                objectMapper.createObjectNode().put("status", 1)));
        mustArray.add(objectMapper.createObjectNode().set("term",
                objectMapper.createObjectNode().put("deleted", 0)));

        // 关键词搜索
        if (keyword != null && !keyword.isBlank()) {
            ObjectNode boolQuery = objectMapper.createObjectNode();
            ArrayNode should = objectMapper.createArrayNode();

            ObjectNode titleMatch = objectMapper.createObjectNode();
            ObjectNode titleField = objectMapper.createObjectNode();
            titleField.put("query", keyword);
            titleField.put("boost", 2.0);
            titleMatch.set("title", titleField);
            should.add(objectMapper.createObjectNode().set("match", titleMatch));

            ObjectNode subtitleMatch = objectMapper.createObjectNode();
            ObjectNode subtitleField = objectMapper.createObjectNode();
            subtitleField.put("query", keyword);
            subtitleMatch.set("subtitle", subtitleField);
            should.add(objectMapper.createObjectNode().set("match", subtitleMatch));

            boolQuery.set("should", should);
            boolQuery.put("minimum_should_match", 1);
            mustArray.add(objectMapper.createObjectNode().set("bool", boolQuery));
        }

        boolNode.set("must", mustArray);

        // filter: 分类 + 价格
        ArrayNode filterArray = objectMapper.createArrayNode();
        if (query.getCategoryId() != null) {
            filterArray.add(objectMapper.createObjectNode().set("term",
                    objectMapper.createObjectNode().put("categoryId", query.getCategoryId())));
        }
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            ObjectNode range = objectMapper.createObjectNode();
            ObjectNode priceRange = objectMapper.createObjectNode();
            if (query.getMinPrice() != null) priceRange.put("gte", query.getMinPrice());
            if (query.getMaxPrice() != null) priceRange.put("lte", query.getMaxPrice());
            range.set("price", priceRange);
            filterArray.add(objectMapper.createObjectNode().set("range", range));
        }
        if (filterArray.size() > 0) {
            boolNode.set("filter", filterArray);
        }

        ObjectNode queryNode = objectMapper.createObjectNode();
        queryNode.set("bool", boolNode);
        root.set("query", queryNode);

        // 排序
        String sort = query.getSort();
        ArrayNode sortArray = objectMapper.createArrayNode();
        if ("sales_desc".equals(sort)) {
            sortArray.add(objectMapper.createObjectNode().put("sales", "desc"));
        } else if ("price_asc".equals(sort)) {
            sortArray.add(objectMapper.createObjectNode().put("price", "asc"));
        } else if ("price_desc".equals(sort)) {
            sortArray.add(objectMapper.createObjectNode().put("price", "desc"));
        } else if (keyword == null || keyword.isBlank()) {
            sortArray.add(objectMapper.createObjectNode().put("sales", "desc"));
        }
        if (sortArray.size() > 0) {
            root.set("sort", sortArray);
        }

        // 高亮
        if (keyword != null && !keyword.isBlank()) {
            ObjectNode highlight = objectMapper.createObjectNode();
            ObjectNode fields = objectMapper.createObjectNode();
            fields.set("title", objectMapper.createObjectNode());
            fields.set("subtitle", objectMapper.createObjectNode());
            highlight.set("fields", fields);
            highlight.putArray("pre_tags").add("<em>");
            highlight.putArray("post_tags").add("</em>");
            root.set("highlight", highlight);
        }

        // 分页
        int page = query.getPage() != null ? query.getPage() : 1;
        int size = query.getSize() != null ? query.getSize() : 10;
        root.put("from", (page - 1) * size);
        root.put("size", size);

        return root;
    }

    private SearchResultVO searchFromMySQL(Long userId, SearchQueryDTO query) {
        String keyword = query.getKeyword();
        int offset = (query.getPage() - 1) * query.getSize();
        List<Map<String, Object>> records = productIndexMapper.searchProducts(
                keyword, query.getCategoryId(), query.getMinPrice(), query.getMaxPrice(),
                query.getSort(), offset, query.getSize());
        long total = productIndexMapper.countSearch(
                keyword, query.getCategoryId(), query.getMinPrice(), query.getMaxPrice());

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
        try {
            ObjectNode body = objectMapper.createObjectNode();
            ObjectNode queryNode = objectMapper.createObjectNode();
            ObjectNode mpp = objectMapper.createObjectNode();
            mpp.put("query", keyword);
            mpp.put("max_expansions", 10);
            queryNode.set("match_phrase_prefix", objectMapper.createObjectNode().set("title", mpp));
            body.set("query", queryNode);
            body.put("size", 10);
            body.set("_source", objectMapper.createArrayNode().add("title"));

            Request request = new Request("POST", "/" + INDEX_NAME + "/_search");
            request.setJsonEntity(body.toString());
            Response response = restClient.performRequest(request);
            JsonNode result = objectMapper.readTree(EntityUtils.toByteArray(response.getEntity()));

            List<String> suggestions = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (JsonNode hit : result.path("hits").path("hits")) {
                String title = hit.path("_source").path("title").asText(null);
                if (title != null && seen.add(title)) {
                    suggestions.add(title);
                }
            }
            return suggestions;
        } catch (Exception e) {
            log.error("ES 建议查询异常，降级到 MySQL", e);
            return productIndexMapper.suggestByPrefix(keyword.trim()).stream()
                    .distinct().limit(10).collect(Collectors.toList());
        }
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

    // ========== ES 同步 ==========

    /**
     * 启动同步：索引不存在则创建并全量写入，已存在则跳过
     */
    public void fullSync() {
        log.info("检查 ES 索引状态...");
        try {
            if (indexExists()) {
                log.info("索引 [{}] 已存在，跳过创建，等待增量同步", INDEX_NAME);
                return;
            }

            // 首次部署：创建索引 + 全量写入
            log.info("索引 [{}] 不存在，创建并全量同步...", INDEX_NAME);
            createIndex();

            List<Map<String, Object>> products = productIndexMapper.fetchAllProducts();
            if (products.isEmpty()) {
                log.info("没有商品数据需要同步");
                return;
            }

            indexToEs(products);
            lastSyncTime = LocalDateTime.now();
            log.info("全量同步完成，共同步 {} 条商品", products.size());
        } catch (Exception e) {
            log.error("全量同步失败", e);
        }
    }

    /**
     * 重建索引：删除旧索引 → 创建新索引 → 全量写入
     * 仅在 mapping 变更时手动调用
     */
    public void rebuildIndex() {
        log.info("开始重建索引...");
        try {
            deleteIndexIfExists();
            createIndex();

            List<Map<String, Object>> products = productIndexMapper.fetchAllProducts();
            indexToEs(products);
            lastSyncTime = LocalDateTime.now();
            log.info("重建索引完成，共同步 {} 条商品", products.size());
        } catch (Exception e) {
            log.error("重建索引失败", e);
        }
    }

    /**
     * 检查索引是否存在
     */
    private boolean indexExists() {
        try {
            Response response = restClient.performRequest(new Request("HEAD", "/" + INDEX_NAME));
            return response.getStatusLine().getStatusCode() == 200;
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return false;
            }
            throw new RuntimeException("检查索引状态失败", e);
        } catch (IOException e) {
            throw new RuntimeException("检查索引状态失败", e);
        }
    }

    /**
     * 增量同步：每分钟执行一次，只处理上次同步后变更的商品
     * - 新增/修改的商品 → 写入 ES
     * - 下架/删除的商品 → 从 ES 删除
     * - 无变更 → 跳过
     */
    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void incrementalSync() {
        try {
            // 查询上次同步后有变更的商品（包括新增、修改、删除、下架）
            List<Map<String, Object>> changed = productIndexMapper.fetchIncrementalProducts(lastSyncTime);

            if (changed.isEmpty()) {
                return; // 无变更，静默跳过
            }

            log.info("增量同步：检测到 {} 条变更商品", changed.size());

            // 分离：需要写入 ES 的 vs 需要从 ES 删除的
            List<Map<String, Object>> toIndex = new ArrayList<>();
            List<String> toDelete = new ArrayList<>();

            for (Map<String, Object> product : changed) {
                Integer deleted = toInt(product.get("deleted"));
                Integer status = toInt(product.get("status"));
                // 上架且未删除 → 写入 ES；下架或删除 → 从 ES 删除
                if (deleted != null && deleted == 0 && status != null && status == 1) {
                    toIndex.add(product);
                } else {
                    toDelete.add(String.valueOf(product.get("id")));
                }
            }

            // 写入新增/修改的商品
            if (!toIndex.isEmpty()) {
                indexToEs(toIndex);
                log.info("增量同步：写入 {} 条商品到 ES", toIndex.size());
            }

            // 删除下架/删除的商品
            if (!toDelete.isEmpty()) {
                deleteFromEs(toDelete);
                log.info("增量同步：从 ES 删除 {} 条商品", toDelete.size());
            }

            lastSyncTime = LocalDateTime.now();
        } catch (Exception e) {
            log.error("增量同步失败", e);
        }
    }

    /**
     * 每天凌晨 3 点执行一次全量校准，防止增量遗漏
     * 不删索引，直接全量覆盖写入（bulk index 会覆盖同 ID 文档）
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyFullSync() {
        log.info("开始每日全量校准...");
        try {
            List<Map<String, Object>> products = productIndexMapper.fetchAllProducts();
            if (products.isEmpty()) {
                log.info("没有商品数据需要校准");
                return;
            }
            indexToEs(products);
            lastSyncTime = LocalDateTime.now();
            log.info("每日校准完成，共同步 {} 条商品", products.size());
        } catch (Exception e) {
            log.error("每日校准失败", e);
        }
    }

    /**
     * 检查索引是否存在，存在则删除
     */
    private void deleteIndexIfExists() throws IOException {
        // 第一步：HEAD 检查索引是否存在
        boolean exists;
        try {
            Response headResponse = restClient.performRequest(new Request("HEAD", "/" + INDEX_NAME));
            exists = headResponse.getStatusLine().getStatusCode() == 200;
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                log.info("索引 [{}] 不存在，跳过删除", INDEX_NAME);
                return;
            }
            throw e;
        }

        if (!exists) {
            log.info("索引 [{}] 不存在，跳过删除", INDEX_NAME);
            return;
        }

        // 第二步：索引存在，执行删除
        log.info("索引 [{}] 存在，执行删除...", INDEX_NAME);
        try {
            Response deleteResponse = restClient.performRequest(new Request("DELETE", "/" + INDEX_NAME));
            int deleteStatus = deleteResponse.getStatusLine().getStatusCode();
            if (deleteStatus != 200) {
                throw new IOException("删除索引失败，状态码: " + deleteStatus);
            }
            log.info("索引 [{}] 删除成功", INDEX_NAME);
        } catch (ResponseException e) {
            // 并发场景下，其他线程可能已经删掉了索引，404 视为删除成功
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                log.info("索引 [{}] 已被删除（其他线程），跳过", INDEX_NAME);
            } else {
                throw e;
            }
        }
    }

    private void createIndex() throws IOException {
        String mapping = """
                {
                  "mappings": {
                    "properties": {
                      "id": { "type": "long" },
                      "merchantId": { "type": "long" },
                      "categoryId": { "type": "long" },
                      "title": { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart" },
                      "subtitle": { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart" },
                      "mainImage": { "type": "keyword" },
                      "price": { "type": "double" },
                      "marketPrice": { "type": "double" },
                      "stock": { "type": "integer" },
                      "sales": { "type": "integer" },
                      "status": { "type": "integer" },
                      "deleted": { "type": "integer" }
                    }
                  }
                }
                """;
        Request request = new Request("PUT", "/" + INDEX_NAME);
        request.setJsonEntity(mapping);
        restClient.performRequest(request);
    }

    private void indexToEs(List<Map<String, Object>> products) throws IOException {
        StringBuilder bulkBody = new StringBuilder();
        for (Map<String, Object> product : products) {
            // action 行
            bulkBody.append("{\"index\":{\"_index\":\"").append(INDEX_NAME)
                    .append("\",\"_id\":\"").append(product.get("id")).append("\"}}\n");
            // 数据行
            ObjectNode doc = objectMapper.createObjectNode();
            doc.put("id", toLong(product.get("id")));
            doc.put("merchantId", toLong(product.get("merchant_id")));
            doc.put("categoryId", toLong(product.get("category_id")));
            doc.put("title", (String) product.get("title"));
            doc.put("subtitle", (String) product.get("subtitle"));
            doc.put("mainImage", (String) product.get("main_image"));
            doc.put("price", toBigDecimal(product.get("price")).toPlainString());
            doc.put("marketPrice", toBigDecimal(product.get("market_price")).toPlainString());
            doc.put("stock", toInt(product.get("stock")));
            doc.put("sales", toInt(product.get("sales")));
            doc.put("status", toInt(product.get("status")));
            doc.put("deleted", toInt(product.get("deleted")));
            bulkBody.append(doc.toString()).append("\n");
        }

        Request request = new Request("POST", "/_bulk");
        request.setJsonEntity(bulkBody.toString());
        Response response = restClient.performRequest(request);
        JsonNode result = objectMapper.readTree(EntityUtils.toByteArray(response.getEntity()));
        if (result.path("errors").asBoolean()) {
            log.error("批量同步部分失败: {}", result.toString());
        }
    }

    /**
     * 批量删除 ES 中的文档
     */
    private void deleteFromEs(List<String> ids) throws IOException {
        StringBuilder bulkBody = new StringBuilder();
        for (String id : ids) {
            bulkBody.append("{\"delete\":{\"_index\":\"").append(INDEX_NAME)
                    .append("\",\"_id\":\"").append(id).append("\"}}\n");
        }

        Request request = new Request("POST", "/_bulk");
        request.setJsonEntity(bulkBody.toString());
        Response response = restClient.performRequest(request);
        JsonNode result = objectMapper.readTree(EntityUtils.toByteArray(response.getEntity()));
        if (result.path("errors").asBoolean()) {
            log.error("批量删除部分失败: {}", result.toString());
        }
    }

    // ========== 辅助方法 ==========

    private void saveSearchHistory(Long userId, String keyword) {
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
        item.setName((String) map.get("title"));
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
