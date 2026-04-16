package com.lab.modules.binance.service;

import com.lab.modules.binance.dto.CrawledProduct;

import java.util.List;

public interface IBinanceClient {
    List<CrawledProduct> getTickerPrice(String symbol);

}
