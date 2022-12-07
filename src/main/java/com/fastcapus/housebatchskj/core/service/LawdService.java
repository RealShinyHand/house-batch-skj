package com.fastcapus.housebatchskj.core.service;

import com.fastcapus.housebatchskj.core.dto.LawdDto;
import com.fastcapus.housebatchskj.core.entity.Lawd;
import com.fastcapus.housebatchskj.core.repository.LawdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LawdService {
    private final LawdRepository lawdRepository;

    /**
     * @implSpec
     * lawdDto가 존재하는 값이면 update
     * 새로운 값이면 insert
     * 판별 기준은 lawdDto.lawdCd
     * @param lawdDto
     */
    @Transactional
    public void upsert(LawdDto lawdDto){
        Lawd saved = lawdRepository.findByLawdCd(lawdDto.getLawdCd())
                //.orElseGet(()->new Lawd());
                .orElse(mapLawdDtoToLawd(lawdDto));
        lawdRepository.save(saved);
    }

    private Lawd mapLawdDtoToLawd(LawdDto lawdDto){
        Lawd lawd = new Lawd();
        lawd.setLawdCd(lawdDto.getLawdCd());
        lawd.setLawdDong(lawdDto.getLawdDong());
        lawd.setExist(lawdDto.getExist());
        return lawd;
    }
}
