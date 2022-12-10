package com.fastcapus.housebatchskj.job.apt;

import com.fastcapus.housebatchskj.adapter.ApartApiResource;
import com.fastcapus.housebatchskj.core.dto.AptDealDto;
import com.fastcapus.housebatchskj.core.repository.LawdRepository;
import com.fastcapus.housebatchskj.job.validator.LawdCdParameterValidator;
import com.fastcapus.housebatchskj.job.validator.YearMonthParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.time.YearMonth;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApartApiResource apartApiResource;
    private final LawdRepository lawdRepository;
    @Bean("aptDealInsertJob")
    public Job aptDealInsertJob(
            @Qualifier("aptDealInsertStep") Step aptDealInsertStep,
            @Qualifier("guLawdStep") Step guLawdStep
    ){
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(validator())
                .start(guLawdStep)
                .on("CONTINUABLE").to(aptDealInsertStep).next(guLawdStep)
                .on(ExitStatus.COMPLETED.getExitCode()).end()
                .end()
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
            @Value("#{jobExecutionContext['guLawdCd']}") String lawdCd
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

    private JobParametersValidator validator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(new YearMonthParameterValidator()));
        return validator;
    }


    @JobScope
    @Bean("guLawdStep")
    public Step guLawdStep(@Qualifier("guLawdTasklet") Tasklet guLawdTasklet){
        return stepBuilderFactory.get("guLawdStep")
                .tasklet(guLawdTasklet)
               /* .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {

                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("?????????"+stepExecution.getExitStatus());
                        return null;
                    }
                })*/
                .build();
    }

    @StepScope
    @Bean("guLawdTasklet")
    public Tasklet guLawdTasklet(LawdRepository lawdRepository){
        return new GuLawdTaskLet(lawdRepository);
    }
}
