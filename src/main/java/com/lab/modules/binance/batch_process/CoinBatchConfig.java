package com.lab.modules.binance.batch_process;

import com.google.gson.Gson;
import com.lab.entity.CoinEntity;
import com.lab.modules.binance.dto.CrawledProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CoinBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    // ================= READER =================
    @Bean
    @StepScope
    public ItemReader<CrawledProduct> reader(BatchDataHolder holder) {
        System.out.println("DATA SIZE = " + holder.getData().size());
        return new ListItemReader<>(holder.getData());
    }

    // ================= PROCESSOR =================
    @Bean
    public ItemProcessor<CrawledProduct, CoinEntity> processor() {
        return item -> {
            CoinEntity entity = new CoinEntity();
            entity.setSymbol(item.getSymbol());
            entity.setPricing(item.getPrice());
            entity.setCreatedBy("SYSTEM");
            return entity;
        };
    }

    // ================= WRITER =================
    @Bean
    public JdbcBatchItemWriter<CoinEntity> writer() {
        JdbcBatchItemWriter<CoinEntity> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);

        writer.setSql("""
            INSERT INTO coin ( symbol, pricing, created_dt, created_by)
            VALUES ( :symbol, :pricing,CURRENT_TIMESTAMP, :createdBy)
            ON CONFLICT (symbol) DO NOTHING
        """);

        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<>()
        );

        return writer;
    }

    // ================= STEP =================
    @Bean
    public Step step(ItemReader<CrawledProduct> reader) {
        return new StepBuilder("coin-step", jobRepository)
                .<CrawledProduct, CoinEntity>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(skipListener())
                .build();
    }

    // ================= JOB =================
    @Bean
    public Job job(Step step) {
        return new JobBuilder("coin-job", jobRepository)
                .start(step)
                .build();
    }

    // ================= SKIP LISTENER =================
    @Bean
    public SkipListener<CrawledProduct, CoinEntity> skipListener() {
        return new SkipListener<>() {

            @Override
            public void onSkipInWrite(CoinEntity item, Throwable t) {
                System.err.println("WRITE ERROR: " + item + " | " + t.getMessage());
                saveError(item, t);
            }

            @Override
            public void onSkipInProcess(CrawledProduct item, Throwable t) {
                System.err.println("PROCESS ERROR: " + item + " | " + t.getMessage());
                saveErrorRaw(item, t);
            }

            @Override
            public void onSkipInRead(Throwable t) {
                System.err.println("READ ERROR: " + t.getMessage());
            }

            private void saveError(CoinEntity item, Throwable t) {
                jdbcTemplate.update("""
                INSERT INTO coin_error ( symbol, pricing, error_msg)
                VALUES ( ?, ?, ?)
            """,
                        item.getSymbol(),
                        item.getPricing(),
                        t.getMessage()
                );
            }

            private void saveErrorRaw(CrawledProduct item, Throwable t) {
                jdbcTemplate.update("""
                    INSERT INTO coin_error (symbol, pricing, error_msg, raw_data)
                    VALUES (?, ?, ?, ?)
                """,
                        item.getSymbol(),
                        item.getPrice(),
                        t.getMessage(),
                        new Gson().toJson(item)
                );
            }
        };
    }
}
