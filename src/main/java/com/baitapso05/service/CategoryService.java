package com.baitapso05.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.baitapso05.entitiy.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    long count();

    void deleteById(Long id);

    List<Category> findAll();

    List<Category> findAll(Sort sort);

    Page<Category> findAll(Pageable pageable);

    Page<Category> findByCategoryNameContaining(String categoryName, Pageable pageable);

    Optional<Category> findByCategoryName(String categoryName);

    <S extends Category> S save(S entity);

    Optional<Category> findById(Long aLong);

}
