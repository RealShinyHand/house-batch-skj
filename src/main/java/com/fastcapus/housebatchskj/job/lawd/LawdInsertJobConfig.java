package com.fastcapus.housebatchskj.job.lawd;

import com.fastcapus.housebatchskj.core.dto.LawdDto;
import com.fastcapus.housebatchskj.core.entity.Lawd;
import com.fastcapus.housebatchskj.core.service.LawdService;
import com.fastcapus.housebatchskj.job.validator.FilePathParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LawdInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final LawdService lawdService;

    @Bean(name="lawdInsertJob")
    public Job lawdInsertJob(
            @Qualifier("lawdInsertStep") Step lawdInsertStep
    ){
        return jobBuilderFactory.get("lawdInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathParameterValidator())
                .start(lawdInsertStep)
                .build();
    }
    @Bean("lawdInsertStep")
    @JobScope
    public Step lawdInsertStep(
            @Qualifier("flatFileItemReader") FlatFileItemReader flatFileItemReader,
            @Qualifier("flatFileItemWriter") ItemWriter flatFileItemWriter
    ){
        return stepBuilderFactory.get("lawdInsertStep")
                .<LawdDto,LawdDto>chunk(1000) //4??? 6000?????? ??? ?????? ??????
                .reader(flatFileItemReader)
                .writer(flatFileItemWriter)
                .build(); //????????? ?????? ?????? ????????? ???????????? x
    }

    @Bean("flatFileItemReader")
    @StepScope
    public FlatFileItemReader<LawdDto> flatFileItemReader(@Value("#{jobParameters['filePath']}") String filePath){

        return new FlatFileItemReaderBuilder<LawdDto>()
                .name("flatFileItemReader")
                .delimited() //???????????? ???????????? ?????????
                .delimiter("\t")// ????????? ?????? 1111000000	??????????????? ?????????   ?????? ??????????????? ???????????? ??? ??????
                .names(LawdFieldSetMapper.LAWD_CD,LawdFieldSetMapper.LAWD_DONG,LawdFieldSetMapper.EXISTS) //mapper?????? ?????????????????? ???????????? ?????? ???????????? ????????? ??????
                .linesToSkip(1)
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

    @Bean("flatFileItemWriter")
    @StepScope
    public ItemWriter<LawdDto> flatFileItemWriter(){
        return new ItemWriter<LawdDto>() {
            @Override
            public void write(List<? extends LawdDto> items) throws Exception {
                items.forEach(lawdService::upsert);
            }
        };
    }
}
