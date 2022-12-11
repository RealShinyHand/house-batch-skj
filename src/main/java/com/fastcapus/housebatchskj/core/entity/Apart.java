package com.fastcapus.housebatchskj.core.entity;

import com.fastcapus.housebatchskj.core.dto.AptDealDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="table_apt")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Apart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptId;

    //apt_name varchar(40) not null,
    @Column(nullable = false,length = 40)
    private String aptName;

    //jibun varchar(20) not null,
    @Column(nullable = false,length = 20)
    private String jibun;

    //dong varchar(40) not null,
    @Column(nullable = false,length = 40)
    private String dong;
    //gu_lawd_cd char(5) not null,
    @Column(nullable = false,length = 5)
    private String guLawdCd;

    //built_year int not null,
    @Column(nullable = false)
    private Integer builtYear;

    //created_at datetime not null,
    @CreatedDate
    private LocalDateTime createdAt;
    //updated_at datetime not null
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Apart of(AptDealDto dto){
        Apart apart = new Apart();

        apart.setAptName(dto.getAptName().trim());
        apart.setBuiltYear(dto.getBuiltYear());
        apart.setDong(dto.getDong().trim());
        apart.setJibun(dto.getJibun().trim());
        apart.setGuLawdCd(dto.getRegionalCode().trim());

        return apart;
    }
}
