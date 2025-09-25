package com.sweet.backend.repository;

import com.sweet.backend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SweetRepository extends JpaRepository<Sweet, Long> {
    @Query("SELECT s FROM Sweet s WHERE " +
           "(:name IS NULL OR s.name LIKE %:name%) AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<Sweet> searchSweets(@Param("name") String name,
                             @Param("category") String category,
                             @Param("minPrice") Double minPrice,
                             @Param("maxPrice") Double maxPrice);
}