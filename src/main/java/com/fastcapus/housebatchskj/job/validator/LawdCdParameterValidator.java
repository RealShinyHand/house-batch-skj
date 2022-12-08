package com.fastcapus.housebatchskj.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class LawdCdParameterValidator implements JobParametersValidator {
    private static final String LAWD_CD = "lawdCd";
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String lawdCd = parameters.getString(LAWD_CD);
        if(!StringUtils.hasText(lawdCd) || lawdCd.length() != 5){
            //빈문자열이거나 5자리가 아니면
            throw new JobParametersInvalidException(LAWD_CD + "문자열이 5자리여야 합니다.");
        }

    }
}
