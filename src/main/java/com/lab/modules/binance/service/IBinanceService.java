package com.lab.modules.binance.service;

import com.lab.modules.binance.dto.CrawledProduct;

import java.util.List;

public interface IBinanceService {
    List<CrawledProduct> crawlAndSaveTickerPrice(String symbol) throws Exception;
    List<CrawledProduct> crawlAndSaveTickerPriceSpringBatch(String symbol) throws Exception;
}
