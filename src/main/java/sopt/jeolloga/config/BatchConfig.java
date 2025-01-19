package sopt.jeolloga.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import sopt.jeolloga.domain.templestay.api.service.ReviewApiService;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReviewApiService reviewApiService;

    @Bean
    public Job processReviewsJob() {
        return new JobBuilder("processReviewsJob", jobRepository)
                .start(processReviewsStep())
                .build();
    }

    @Bean
    public Step processReviewsStep() {
        return new StepBuilder("processReviewsStep", jobRepository)
                .<String, String>chunk(100, transactionManager) // 한 번에 100개의 데이터를 처리
                .reader(templeNamesReader())
                .processor(reviewProcessor())
                .writer(reviewWriter())
                .build();
    }

    @Bean
    public ItemReader<String> templeNamesReader() {
        return new ItemReader<>() {
            private final List<String> templeNames = reviewApiService.getDistinctTempleNames();
            private int currentIndex = 0;

            @Override
            public String read() {
                if (currentIndex < templeNames.size()) {
                    return templeNames.get(currentIndex++);
                }
                return null;
            }
        };
    }

    @Bean
    public ItemProcessor<String, String> reviewProcessor() {
        return templeName -> {
            reviewApiService.processReviewsByTempleName(templeName);
            return templeName;
        };
    }

    @Bean
    public ItemWriter<String> reviewWriter() {
        return templeNames -> templeNames.forEach(templeName ->
                System.out.println("Processed reviews for temple: " + templeName));
    }
}
