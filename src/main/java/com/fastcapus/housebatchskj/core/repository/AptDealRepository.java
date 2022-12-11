package com.fastcapus.housebatchskj.core.repository;


import com.fastcapus.housebatchskj.core.entity.Apart;
import com.fastcapus.housebatchskj.core.entity.ApartDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AptDealRepository extends JpaRepository<ApartDeal,Long> {

    Optional<ApartDeal> findApartDealByApartAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apart apart, Double exclusiveArea, LocalDate dealDate, Long dealAmount,Integer floor
            );
}
