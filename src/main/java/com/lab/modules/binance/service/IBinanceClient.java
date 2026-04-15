package com.lab.modules.binance.service;

import com.lab.entity.CoinEntity;

import java.util.List;
import java.util.Map;

public interface IBinanceClient {
    List<CoinEntity> getTickerPrice(String symbol);

}
