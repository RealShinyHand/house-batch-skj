package com.fastcapus.housebatchskj.job.apt;

import com.fastcapus.housebatchskj.adapter.ApartApiResource;
import com.fastcapus.housebatchskj.core.dto.AptDealDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.time.YearMonth;

@Configuration
@RequiredArgsConstructor
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApartApiResource apartApiResource;
    @Bean("aptDealInsertJob")
    public Job aptDealInsertJob(
            @Qualifier("aptDealInsertStep") Step aptDealInsertStep){
        return jobBuilderFactory.get("aptDealInsertJob")
                .start(aptDealInsertStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @JobScope
    @Bean("aptDealInsertStep")
    public Step aptDealInsertStep(
            @Qualifier("aptDealResourceReader") StaxEventItemReader aptDealResourceReader,
            @Qualifier("aptDealWriter") ItemWriter aptDealWriter
    ){
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto,AptDealDto>chunk(10)
                .reader(aptDealResourceReader)
                .writer(aptDealWriter)
                .build();
    }

    @StepScope
    @Bean("aptDealResourceReader")
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
            Jaxb2Marshaller aptDealDtoMarshaller,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobParameters['lawdCd']}") String lawdCd
    ){
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartApiResource.getResources(lawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean("aptDealWriter")
    public ItemWriter<AptDealDto> aptDealWriter(){
        return (items)->{
          items.forEach(System.out::println);
        };
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptDealDtoMarshaller(){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }




}
