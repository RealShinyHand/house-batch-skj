package com.fastcapus.housebatchskj.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class FilePathParameterValidator implements JobParametersValidator {
    private final String FILE_PATH = "filePath";
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String filePath = parameters.getString("filePath");
        if(!StringUtils.hasText(filePath)){
            throw new JobParametersInvalidException(String.format("실행 변수 %s가 빈 문자열이거나 존재 하지 않습니다.",FILE_PATH));
        }

        Resource resource = new ClassPathResource(filePath);
        if(!resource.exists()){
            throw new JobParametersInvalidException(String.format("%s가 class Path에 존재하지 않습니다. 경로를 확인해 주세요",FILE_PATH));
        }
    }
}
