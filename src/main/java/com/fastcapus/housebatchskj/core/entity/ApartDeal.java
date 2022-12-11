package com.fastcapus.housebatchskj.core.entity;

import com.fastcapus.housebatchskj.core.dto.AptDealDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "table_apt_deal")
@EntityListeners(AuditingEntityListener.class)
public class ApartDeal {
    //apt_deal_id int auto_increment primary key,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptDealId;


    //apt_id bigint not null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id",nullable = false)
    private Apart apart;

    //exclusive_area double not null,
    @Column(nullable = false)
    private Double exclusiveArea;

    //deal_data date not null,
    @Column(nullable = false)
    private LocalDate dealDate;

    //deal_amount bigint not null,
    @Column(nullable = false)
    private Long dealAmount;

    //floor int not null,
    @Column(nullable = false)
    private Integer floor;

    //deal_canceled tinyint(1) default 0 not null,
    @Column(nullable = false)
    private Boolean dealCanceled;

    //deal_canceled_date date null,
    @Column(nullable = true)
    private LocalDate dealCanceledDate;

    //created_at datetime not null,
    @CreatedDate
    private LocalDateTime createdAt;

    //updated_at datetime not null
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static ApartDeal of(AptDealDto dto){
        ApartDeal apartDeal = new ApartDeal();
        apartDeal.setExclusiveArea(dto.getExclusiveArea());
        apartDeal.setDealDate(dto.getDealDate());
        apartDeal.setDealAmount(dto.getNumberDealAmount());
        apartDeal.setFloor(dto.getFloor());
        apartDeal.setDealCanceled(dto.getDealCanceled());
        apartDeal.setDealCanceledDate(dto.getDealCanceledDate());
        return apartDeal;
    }
}
