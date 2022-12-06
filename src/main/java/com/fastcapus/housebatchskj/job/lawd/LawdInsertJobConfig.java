package com.fastcapus.housebatchskj.job.lawd;

import com.fastcapus.housebatchskj.core.dto.LawdDto;
import com.fastcapus.housebatchskj.core.entity.Lawd;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LawdInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

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
            @Qualifier("flatFileItemReader") ItemReader flatFileItemReader
    ){
        stepBuilderFactory.get("lawdInsertStep")
                .<LawdDto,Lawd>chunk(1000) //4만 6000개를 천 개씩 처리
                .reader(flatFileItemReader)
                .writer()
                .build(); //파일을 읽고 바로 쓰기에 프로세서 x
    }

    @Bean("flatFileItemReader")
    @StepScope
    public ItemReader<LawdDto> flatFileItemReader(@Value("#{jobParameters['filePath']}") String filePath){
        return new FlatFileItemReaderBuilder<LawdDto>()
                .name("flatFileItemReader")
                .delimited() //딜리미터 사용하기 위해서
                .delimiter("\t")// 탭으로 구분 1111000000	서울특별시 종로구   존재 탭탭탭으로 구분하는 거 맞음
                .names(LawdFieldSetMapper.LAWD_CD,LawdFieldSetMapper.LAWD_DONG,LawdFieldSetMapper.EXISTS) //mapper에서 토크나이저한 토큰들에 대해 순서대로 이름을 부여
                .linesToSkip(1)
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

}
