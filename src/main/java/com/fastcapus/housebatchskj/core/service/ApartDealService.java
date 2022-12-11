package com.fastcapus.housebatchskj.core.service;

import com.fastcapus.housebatchskj.core.dto.AptDealDto;
import com.fastcapus.housebatchskj.core.entity.Apart;
import com.fastcapus.housebatchskj.core.entity.ApartDeal;
import com.fastcapus.housebatchskj.core.repository.AptDealRepository;
import com.fastcapus.housebatchskj.core.repository.AptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApartDealService {
    private  final AptDealRepository aptDealRepository;
    private final AptRepository aptRepository;

    @Transactional
    public void upsert(AptDealDto dto){
        //아파트 유니크를 네임과 지번으로 가정
        //아파트가 없으면 저장
        Apart apart = getApartOrNew(dto);
        saveAptDeal(dto,apart);



    }

    //아파트 이름과 지번이 같은 아파트를 반환, 없을 시 새로 생성 후 반환
    private Apart getApartOrNew(AptDealDto dto){
        Apart apart =  aptRepository.findApartByAptNameAndJibun(dto.getAptName(),dto.getJibun())
                .orElse(Apart.of(dto));
        return aptRepository.save(apart);
    }
    //아파트 거래내역을 저장, 아파트, 전용면적, 거래일, 거래 금액, 층이 같으면 같은 아파트로 보고 수정을 한다.
    private void saveAptDeal(AptDealDto dto,Apart apart){
        ApartDeal apartDeal = aptDealRepository.findApartDealByApartAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apart,
                dto.getExclusiveArea(),
                dto.getDealDate(),
                dto.getNumberDealAmount(),
                dto.getFloor()).orElse(ApartDeal.of(dto));

        apartDeal.setApart(apart);
        apartDeal.setDealCanceled(dto.getDealCanceled());
        apartDeal.setDealCanceledDate(dto.getDealCanceledDate());
        aptDealRepository.save(apartDeal);
    }
}
