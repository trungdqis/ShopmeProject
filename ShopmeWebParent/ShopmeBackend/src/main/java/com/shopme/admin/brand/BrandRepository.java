package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

    Long countById(Integer id);

    Brand findByName(String name);

    @Query("SELECT b FROM Brand  b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);
}
