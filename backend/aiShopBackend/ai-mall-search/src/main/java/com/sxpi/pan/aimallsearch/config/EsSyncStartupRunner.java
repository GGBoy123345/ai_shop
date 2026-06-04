package com.sxpi.pan.aimallsearch.config;

import com.sxpi.pan.aimallsearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsSyncStartupRunner implements CommandLineRunner {

    private final SearchService searchService;

    @Override
    public void run(String... args) {
        log.info("应用启动，开始全量同步商品数据到 ES...");
        try {
            searchService.fullSync();
        } catch (Exception e) {
            log.error("启动同步失败，将由定时任务重试", e);
        }
    }
}
