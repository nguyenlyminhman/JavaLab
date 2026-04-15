package com.lab.modules.binance.controller;

import com.lab.entity.CoinEntity;
import com.lab.modules.binance.service.IBinanceService;
import com.lab.modules.lock.DeadlockExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/binance")
public class BinanceController {

    @Autowired
    private IBinanceService iBinanceService;

    @GetMapping("/ticker/price")
    public List<CoinEntity> getTickerPrice() {
        List<CoinEntity> rs = iBinanceService.crawlAndSaveTickerPrice(null);
        return rs;
    }
}
