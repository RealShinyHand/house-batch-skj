package com.fastcapus.housebatchskj.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class YearMonthParameterValidator implements JobParametersValidator {
    private static final String YEAR_MONTH = "yearMonth";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String yearMonth = parameters.getString(YEAR_MONTH);
        if(!StringUtils.hasText(yearMonth)){
            throw new JobParametersInvalidException(YEAR_MONTH + "is not valid , input = "+ yearMonth);
        }
        try{
            YearMonth.parse(yearMonth);
        }catch (DateTimeParseException dpe){
            throw new JobParametersInvalidException(YEAR_MONTH + "can't parse so \"yyyy-MM\" checking format, input = " + yearMonth);
        }

    }
}
