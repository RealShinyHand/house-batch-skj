package com.fastcapus.housebatchskj.job.apt;

import com.fastcapus.housebatchskj.adapter.ApartApiResource;
import com.fastcapus.housebatchskj.config.HouseBatchJpaConfig;
import com.fastcapus.housebatchskj.core.repository.LawdRepository;
import com.fastcapus.housebatchskj.core.service.ApartDealService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBatchTest
@SpringBootTest(classes = {AptDealInsertJobConfig.class, HouseBatchJpaConfig.class})
@ActiveProfiles("test")
@EnableBatchProcessing
@EnableAutoConfiguration
public class ApartDealInsertJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private ApartDealService apartDealService;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private ApartApiResource apartApiResource;

    @Test
    public void givenResourceWhenApartDealInsertJobThenSuccess() throws Exception {
        //given
        when(lawdRepository.findDistinctGuLawdCd()).thenReturn(List.of("41135"));
        //lawdRepository 목빈에서 해당 메서드 호출시 위 결과를 리턴하게한다.
        when(apartApiResource.getResources(anyString(),any())).thenReturn(
                new ClassPathResource("test-api-response.xml")
        );
        //when
        JobExecution execution = jobLauncherTestUtils.launchJob(new JobParameters(Map.of("yearMonth",new JobParameter("2021-07"))));
        //then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        verify(apartDealService,times(1)).upsert(any());
        //해당 인스턴스의 메서드가 몇번 호출되었는지 확인
    }
}
