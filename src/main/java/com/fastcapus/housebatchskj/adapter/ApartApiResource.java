package com.fastcapus.housebatchskj.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 아파트 실거래가 API를 호출하기 위한 파리미터
 * 1. serviceKey - API 호출 시 필용한 키
 * 2. LAWD_CD - 지역 코드 값, 법정동 코드중 앞에 5자리
 * 3. DEAL_YMD - 거래가 발생한 년월
 */
@Slf4j
@Component
public class ApartApiResource {

    @Value("${external.apartment-api.path}")
    private String path;
    @Value("${external.apartment-api.service-key}")
    private String serviceKey;

    //1. 서비스키
    public Resource getResources(String lawdCd , YearMonth yearMonth) {

        String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s",path,serviceKey,lawdCd,yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));

        log.info("Resource URL = " + url);

        try {
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("UrlResource 인스턴스를 얻지 못하였습니다. Caused by 비정상적 url");
        }
    }
}
