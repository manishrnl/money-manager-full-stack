package com.example.moneymanager.repository;

import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByActivationToken(String activationToken);


    Optional<ProfileEntity> findByEmail(String name);

    boolean existsByEmail(String email);

    @Query("SELECT p FROM ProfileEntity  p WHERE p.email =:email")
    List<IncomeEntity> findAllByEmail(String email);

    @Query("select p from ProfileEntity p where p.id=:userId AND p.email=:email")
    Optional<ProfileEntity> findByIdAndEmail(@Param("userId") Long userId,
                                             @Param("email") String email);
}