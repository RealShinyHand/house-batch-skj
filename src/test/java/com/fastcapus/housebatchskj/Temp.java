package com.fastcapus.housebatchskj;

import com.fastcapus.housebatchskj.core.entity.Apart;
import com.fastcapus.housebatchskj.core.entity.ApartDeal;
import com.fastcapus.housebatchskj.core.repository.AptDealRepository;
import com.fastcapus.housebatchskj.core.repository.AptRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@DataJpaTest
@ActiveProfiles("test")
public class Temp {

    @Autowired
    AptDealRepository aptDealRepository;
    @Autowired
    AptRepository aptRepository;

    @Test
    void booleanPrimitiveBooleanReferenceTest(){
        //given
        Apart apart = new Apart();
        apart.setGuLawdCd("11");
        apart.setDong("테스트동");
        apart.setJibun("테스트 테스트");
        apart.setAptName("테스트아파트");
        apart.setBuiltYear(3000);
        aptRepository.save(apart);

        ApartDeal apartDeal = new ApartDeal();
        apartDeal.setApart(apart);
        apartDeal.setDealCanceled(true);
        apartDeal.setDealDate(LocalDate.of(3000,11,11));
        apartDeal.setDealAmount(110000L);
        apartDeal.setDealCanceledDate(LocalDate.of(3000,11,11));
        apartDeal.setFloor(2);
        apartDeal.setExclusiveArea(3.123);
        aptDealRepository.save(apartDeal);
        System.out.println(apartDeal.getDealCanceled());
        Assertions.assertEquals(true,apartDeal.getDealCanceled());
    }
}
