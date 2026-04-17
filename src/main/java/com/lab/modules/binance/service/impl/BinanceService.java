package com.lab.modules.binance.service.impl;

import com.lab.entity.CoinEntity;
import com.lab.modules.binance.batch_process.CoinBatchService;
import com.lab.modules.binance.dto.CrawledProduct;
import com.lab.modules.binance.repository.CoinRepository;
import com.lab.modules.binance.service.IBinanceClient;
import com.lab.modules.binance.service.IBinanceService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
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

    @Autowired
    private CoinRepository coinRepository;

    private final Semaphore dbSemaphore = new Semaphore(50);

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

    @Override
    public String updateTickerPrice() {
        long start = System.nanoTime();
        List<CoinEntity> coinEntityList = coinRepository.findAll();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (CoinEntity update : coinEntityList) {
                executor.submit(() -> updateCoinPrice(update));
            }
        }
        long end = System.nanoTime();
        long duration = end - start;
        return "Execution time: " + (duration / 1_000_000.0) + " ms";
    }



    @Transactional
    public void updateCoinPrice(CoinEntity update) {
        try {
            coinRepository.findBySymbol(update.getSymbol()).ifPresentOrElse(coin -> {
                coin.setPricing( (Double.parseDouble(update.getPricing()) * 2 ) + "");
                coin.setUpdatedDt(new Date());
                coin.setUpdatedBy("VT_WORKER");

                coinRepository.save(coin);
            }, () -> System.out.println("Không tìm thấy symbol: " + update.getSymbol()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String updateTickerPriceWithCompletableFuture() {
        long start = System.nanoTime();
        List<CoinEntity> coinEntityList = coinRepository.findAll();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Chuyển danh sách update thành một mảng các CompletableFuture
            List<CompletableFuture<Void>> futures = coinEntityList.stream()
                    .map(update -> CompletableFuture.runAsync(() -> {
                        updateCoinPriceWithCompletableFuture(update);
                    }, executor))
                    .toList();

            // Đợi cho đến khi tất cả 3.800 luồng hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        long end = System.nanoTime();
        long duration = end - start;

        return "Execution time: " + (duration / 1_000_000.0) + " ms";
    }

    @Transactional
    public void updateCoinPriceWithCompletableFuture(CoinEntity update) {
        try {
            coinRepository.findBySymbol(update.getSymbol()).ifPresent(coin -> {
                coin.setPricing( (Double.parseDouble(update.getPricing()) * 2 ) + "");
                coin.setUpdatedDt(new Date());
                coinRepository.save(coin);
            });
        } catch (Exception e) {
            System.out.println("sdsd");
        }
    }


}
