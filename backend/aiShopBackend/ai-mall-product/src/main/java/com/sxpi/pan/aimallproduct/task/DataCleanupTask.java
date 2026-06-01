package com.sxpi.pan.aimallproduct.task;

import com.sxpi.pan.aimallproduct.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时清理已逻辑删除的数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataCleanupTask {

    private final ProductMapper productMapper;

    private static final int RETENTION_DAYS = 90;

    /**
     * 每天凌晨3点执行，物理删除90天前被逻辑删除的商品
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupDeletedProducts() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(RETENTION_DAYS);
        int count = productMapper.physicalDeleteExpired(threshold);
        if (count > 0) {
            log.info("[数据清理] 已物理删除 {} 条超过 {} 天的商品记录", count, RETENTION_DAYS);
        }
    }
}
