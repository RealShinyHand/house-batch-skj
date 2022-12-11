package com.fastcapus.housebatchskj.core.repository;

import com.fastcapus.housebatchskj.core.entity.Apart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AptRepository extends JpaRepository<Apart,Long> {
    Optional<Apart> findApartByAptNameAndJibun(String aprName,String jibun);
}
