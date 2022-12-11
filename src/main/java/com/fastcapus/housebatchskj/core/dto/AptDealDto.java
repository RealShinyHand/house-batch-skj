package com.fastcapus.housebatchskj.core.dto;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@ToString
@Getter
@XmlRootElement(name="item")
public class AptDealDto {

    //거래 금액
    @XmlElement(name="거래금액")
    private String dealAmount;
    //건축년도
    @XmlElement(name="건축년도")
    private Integer builtYear;
    //년
    @XmlElement(name="년")
    private Integer year;
    //법정동
    @XmlElement(name="법정동")
    private String dong;
    //아파트
    @XmlElement(name="아파트")
    private String aptName;
    //월
    @XmlElement(name="월")
    private Integer month;
    //일
    @XmlElement(name="일")
    private Integer day;
    //전용 면적
    @XmlElement(name="전용면적")
    private Double exclusiveArea;
    //지번
    @XmlElement(name="지번")
    private String jibun;
    //지역 코드
    @XmlElement(name="지역코드")
    private String regionalCode;
    //층
    @XmlElement(name="층")
    private Integer floor;
    //해제 사유 발생일
    @XmlElement(name="해제사유발생일")
    private String dealCanceldDate;
    //해제 여부
    @XmlElement(name="해제여부")
    private String dealCanceled;

    public LocalDate getDealDate(){
        return LocalDate.of(this.year,this.month,this.day);
    }

    public Long getNumberDealAmount(){
        String amount = this.dealAmount.replaceAll(",","").trim();
        return Long.parseLong(amount);
    }

    public Boolean getDealCanceled(){
        return "O".equals(this.dealCanceled);
    }

    public LocalDate getDealCanceledDate(){
        if(StringUtils.isBlank(this.dealCanceldDate)){
            return null;
        }


        return LocalDate.parse(this.dealCanceldDate.trim(), DateTimeFormatter.ofPattern("yy.MM.dd"));
    }

    public String getJibun(){
        return Optional.ofNullable(jibun).orElse("");
    }
}
