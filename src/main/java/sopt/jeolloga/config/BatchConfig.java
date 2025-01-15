package sopt.jeolloga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

package sopt.jeolloga.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sopt.jeolloga.domain.templestay.api.service.ReviewApiService;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ReviewApiService reviewApiService;

    @Bean
    public Job processReviewsJob() {
        return jobBuilderFactory.get("processReviewsJob")
                .start(processReviewsStep())
                .build();
    }

    @Bean
    public Step processReviewsStep() {
        return stepBuilderFactory.get("processReviewsStep")
                .<String, String>chunk(10) // Process 10 temple names at a time
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
                return null; // All temple names processed
            }
        };
    }

    @Bean
    public ItemProcessor<String, String> reviewProcessor() {
        return templeName -> {
            reviewApiService.processReviewsByTempleName(templeName);
            return templeName; // Returning the processed temple name for logging
        };
    }

    @Bean
    public ItemWriter<String> reviewWriter() {
        return templeNames -> templeNames.forEach(templeName ->
                System.out.println("Processed temple reviews for: " + templeName));
    }
}
