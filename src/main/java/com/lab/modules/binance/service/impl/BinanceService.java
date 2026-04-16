package com.lab.modules.binance.service.impl;

import com.lab.modules.binance.batch_process.CoinBatchService;
import com.lab.modules.binance.dto.CrawledProduct;
import com.lab.modules.binance.service.IBinanceClient;
import com.lab.modules.binance.service.IBinanceService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class BinanceService implements IBinanceService {

    @Autowired
    private IBinanceClient iBinanceClient;

    @Autowired
    private BulkInsertService bulkInsertService;

    @Autowired
    private CoinBatchService coinBatchService;

    @Override
    public List<CrawledProduct> crawlAndSaveTickerPrice(String symbol) throws Exception {
        List<CrawledProduct> coinEntityList = iBinanceClient.getTickerPrice(symbol);
        long start = System.nanoTime();

        bulkInsertService.insert(coinEntityList);

        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("Execution time: " + duration + " ns");
        System.out.println("Execution time: " + (duration / 1_000_000.0) + " ms");

        return List.of();
    }

    @Override
    public List<CrawledProduct> crawlAndSaveTickerPriceSpringBatch(String symbol) throws Exception {
        List<CrawledProduct> coinEntityList = iBinanceClient.getTickerPrice(symbol);

        long start = System.nanoTime();

        coinBatchService.runBatch(coinEntityList);

        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("Execution time: " + duration + " ns");
        System.out.println("Execution time: " + (duration / 1_000_000.0) + " ms");

        return List.of();
    }
}
