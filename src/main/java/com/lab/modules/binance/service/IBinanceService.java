package com.lab.modules.binance.service;

import com.lab.entity.CoinEntity;

import java.util.List;

public interface IBinanceService {
    List<CoinEntity> crawlAndSaveTickerPrice(String symbol);
}
