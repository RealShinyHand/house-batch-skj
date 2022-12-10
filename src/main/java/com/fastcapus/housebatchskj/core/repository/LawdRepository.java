package com.fastcapus.housebatchskj.core.repository;

import com.fastcapus.housebatchskj.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LawdRepository extends JpaRepository<Lawd,Long> {
    //난 명시적으로 하는게 좋다 ㅎㅎ,,,, 안티패턴인가?
    @Query("SELECT l FROM Lawd l WHERE l.lawdCd = :lawdCd")
    Optional<Lawd> findByLawdCd(@Param("lawdCd") String lawdCd);

    @Query("SELECT DISTINCT substring(l.lawdCd,1,5) FROM Lawd l WHERE l.lawdCd NOT like '%00000000' AND l.exist = 1")
    List<String> findDistinctGuLawdCd();
}
