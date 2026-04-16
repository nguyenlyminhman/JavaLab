package com.lab.modules.binance.batch_process;

import com.lab.modules.binance.dto.CrawledProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinBatchService {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final BatchDataHolder holder;

    public void runBatch(List<CrawledProduct> data) throws Exception {

        if (data == null || data.isEmpty()) {
            System.out.println("NO DATA -> SKIP BATCH");
            return;
        }

        // set data vào holder
        holder.setData(data);

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, params);
    }
}