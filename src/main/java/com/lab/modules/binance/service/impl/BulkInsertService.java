package com.lab.modules.binance.service.impl;

import com.lab.modules.binance.dto.CrawledProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkInsertService {

    private final JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 100;

    public void insert(List<CrawledProduct> data) {
        if (data == null || data.isEmpty()) return;

        List<List<CrawledProduct>> batches = partition(data, BATCH_SIZE);
        long start = System.nanoTime();
//      batches.parallelStream().forEach(this::insertBatch); // cái này chậm hơn khi dùng for
        for (List<CrawledProduct> batch : batches) {
            this.insertBatch(batch);
        }
        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("Execution time: " + duration + " ns");
        System.out.println("Execution time: " + (duration / 1_000_000.0) + " ms");
    }

    @Transactional
    protected void insertBatch(List<CrawledProduct> batch) {
        String sql = """
            INSERT INTO COIN ( symbol, pricing, created_by, created_dt)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (symbol) DO NOTHING
        """;

        try {
            jdbcTemplate.batchUpdate(sql, batch, batch.size(),
                    (ps, record) -> {
                        ps.setString(1, record.getSymbol());
                        ps.setString(2, record.getPrice());
                        ps.setString(3, "SYS");
                    }
            );
        } catch (Exception e) {
            log.warn("Batch insert failed -> fallback to single insert. Size={}", batch.size(), e);
//          handleBatchFailure(batch);
        }
    }

    // Fallback: insert từng record để skip lỗi
    protected void handleBatchFailure(List<CrawledProduct> batch) {
        String sql = """
            INSERT INTO your_table (business_key, col1, col2)
            VALUES (?, ?, ?)
            ON CONFLICT (business_key) DO NOTHING
        """;

        for (CrawledProduct record : batch) {
            try {
                jdbcTemplate.update(sql,
                        record.getSymbol(),
                        record.getPrice()
                );
            } catch (Exception ex) {
                log.error("Insert failed for record: {}", record, ex);
                saveErrorLog(record, ex);
            }
        }
    }

    // Log lỗi (có thể lưu DB hoặc file)
    protected void saveErrorLog(CrawledProduct record, Exception ex) {
        String sql = """
            INSERT INTO error_log (business_key, payload, error_message)
            VALUES (?, ?, ?)
        """;

        try {
            jdbcTemplate.update(sql,
                    record.getSymbol(),
                    record.toString(),
                    ex.getMessage()
            );
        } catch (Exception logEx) {
            log.error("Failed to save error log: {}", record, logEx);
        }
    }

    // Helper chia batch
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
