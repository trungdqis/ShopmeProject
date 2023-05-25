package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

    Long countById(Integer id);

    Brand findByName(String name);
}
