package com.lab.modules.binance.service.impl;

import com.lab.core.client.CommonClient;
import com.lab.core.client.ExternalApiClient;
import com.lab.entity.CoinEntity;
import com.lab.modules.binance.dto.CrawledProduct;
import com.lab.modules.binance.service.IBinanceClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class BinanceClient implements IBinanceClient {

    @Autowired
    private ExternalApiClient apiExecutor;
    private String BASE_URL = "https://api.binance.com";

    @Override
    public List<CrawledProduct> getTickerPrice(String symbol) {
        String url = BASE_URL + "/api/v3/ticker/price";
        System.out.println(url);
        Map<String, Object> queryParams = new HashMap<>();
        CommonClient.addParamIfValid(queryParams, "symbol", symbol);
        return apiExecutor.get(url, null, queryParams, new ParameterizedTypeReference<List<CrawledProduct>>() {});
    }

}
