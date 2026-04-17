package com.lab.modules.binance.controller;

import com.lab.modules.binance.dto.CrawledProduct;
import com.lab.modules.binance.service.IBatchJob;
import com.lab.modules.binance.service.IBinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/thread")
public class VirtualThreadController {

    @Autowired
    private IBinanceService iBinanceService;

    @PostMapping("/virtualThread")
    public String updateVirtual() throws Exception {
        return iBinanceService.updateTickerPrice();
    }

    @PostMapping("/virtualThread/completableFuture")
    public String updateVirtualFut() throws Exception {
        return iBinanceService.updateTickerPriceWithCompletableFuture();
    }

}
