package com.lab.modules.binance.service.impl;

import com.lab.entity.CoinEntity;
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

    @Override
    public List<CrawledProduct> crawlAndSaveTickerPrice(String symbol) throws Exception {
        List<CrawledProduct> coinEntityList = iBinanceClient.getTickerPrice(symbol);

        bulkInsertService.insert(coinEntityList);

        return coinEntityList;
    }
}
