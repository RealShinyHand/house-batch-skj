package com.fastcapus.housebatchskj.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Table(name = "table_lawd")
public class Lawd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lawdId;

    //법정동 코드
    @Column(nullable = false)
    private String lawdCd;

    //법정동의 내용
    @Column(nullable = false)
    private String lawdDong;

    //존재 여부
    @Column(nullable = false)
    private Boolean exist;

    @CreatedDate //jpa에서 인설트 연산할 떄 시간 기록
    private LocalDateTime createdAt;

    @LastModifiedDate //update 시간 기록
    private LocalDateTime updatedAt;
}

/*
	lawd_id bigint auto_increment primary key,
    lawd_cd char(10) not null,
    lawd_dong varchar(100) not null,
    exist tinyint(1) not null,
    created_at datetime not null,
    updated_at datetime not null,
    constraint uk_lawdcd unique(lawd_cd)
 */
