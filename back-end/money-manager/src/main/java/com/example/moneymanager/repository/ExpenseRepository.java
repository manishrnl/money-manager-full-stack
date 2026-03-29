package com.example.moneymanager.repository;

import com.example.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM (e.amount) FROM ExpenseEntity e WHERE e.profile.id=:profileId")
    BigDecimal findTotalExpensePerProfileId(@Param("profileId") Long profileId);

    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "AND LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(@Param("profileId") Long profileId,
                                                                                 @Param("startDate") LocalDate startDate,
                                                                                 @Param("endDate") LocalDate endDate,
                                                                                 @Param("keyword") String keyword,
                                                                                 Sort sort);

    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);


    @Query("SELECT e FROM ExpenseEntity e JOIN FETCH e.category WHERE e.profile.id = :profileId AND e.date = :date")
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}
