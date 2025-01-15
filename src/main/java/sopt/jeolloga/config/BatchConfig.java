/*
package sopt.jeolloga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .<String, Void>chunk(100) // 청크 크기를 100으로 설정
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
                return null; // 모든 데이터가 처리되었음을 나타냄
            }
        };
    }

    @Bean
    public ItemProcessor<String, Void> reviewProcessor() {
        return templeName -> {
            reviewApiService.processReviewsByTempleName(templeName);
            return null;
        };
    }

    @Bean
    public ItemWriter<Void> reviewWriter() {
        return items -> {
        };
    }
}
*/
