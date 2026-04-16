package com.lab.modules.binance.batch_process;

import com.lab.modules.binance.dto.CrawledProduct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchDataHolder {
    private List<CrawledProduct> data;

    public List<CrawledProduct> getData() {
        return data;
    }

    public void setData(List<CrawledProduct> data) {
        this.data = data;
    }
}
