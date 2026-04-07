package com.example.moneymanager.repository;

import com.example.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByProfileId(Long profileId);

    CategoryEntity findByIdAndProfileId(Long id, Long profileId);

    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long ProfileId);

    Boolean existsByNameAndTypeAndProfileId(String name, String type, Long id);

    @Transactional
    int deleteByIdAndProfileId(Long categoryId, Long profileId);
}
