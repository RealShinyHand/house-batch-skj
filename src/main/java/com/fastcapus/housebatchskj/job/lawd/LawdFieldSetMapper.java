package com.fastcapus.housebatchskj.job.lawd;

import com.fastcapus.housebatchskj.core.dto.LawdDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class LawdFieldSetMapper implements FieldSetMapper<LawdDto> {
    public static final String LAWD_CD = "lawdCd";
    public static final String LAWD_DONG = "lawdDong";
    public static final String EXISTS = "exist";
    private static final String EXISTS_TRUE_VALUE = "존재";

    @Override
    public LawdDto mapFieldSet(FieldSet fieldSet) throws BindException {
        LawdDto lawdDto = new LawdDto();

        lawdDto.setLawdCd(fieldSet.readString(LAWD_CD));
        lawdDto.setLawdDong(fieldSet.readString(LAWD_DONG));
        lawdDto.setExist(fieldSet.readBoolean(EXISTS,EXISTS_TRUE_VALUE)); //값이 "존재" 면 트루 반환

        return lawdDto;
    }
}
