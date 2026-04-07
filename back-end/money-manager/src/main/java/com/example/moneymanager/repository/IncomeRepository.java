package com.example.moneymanager.repository;

import com.example.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);


    @Query("SELECT SUM (i.amount) FROM IncomeEntity i WHERE i.profile.id=:profileId")
    BigDecimal findTotalIncomePerProfileId(@Param("profileId") Long profileId);


    @Query("SELECT i FROM IncomeEntity i WHERE i.profile.id = :profileId " +
            "AND i.date BETWEEN :startDate AND :endDate " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(@Param("profileId") Long profileId,
                                                                                @Param("startDate") LocalDate startDate,
                                                                                @Param("endDate") LocalDate endDate,
                                                                                @Param("keyword") String keyword,
                                                                                Sort sort);


    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT i FROM IncomeEntity i WHERE i.profile.id = :profileId " +
            "AND i.date >= CAST(:startDate AS date) " +
            "AND i.date <= CAST(:endDate AS date) " +
            "ORDER BY i.date DESC")
    List<IncomeEntity> findByProfileIdAndDateBetween(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}