package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptops);

        Brand savedBrand = brandRepository.save(acer);

        Assertions.assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(7);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(apple);

        Assertions.assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testCreateBrand3() {
        Brand samsung = new Brand("Samsung");
        samsung.getCategories().add(new Category(29)); // category memory
        samsung.getCategories().add(new Category(24)); // category internal hard drive

        Brand savedBrand = brandRepository.save(samsung);

        Assertions.assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        Assertions.assertThat(brands).isNotEmpty();
    }

    @Test
    void testGetById() {
        Brand brand = brandRepository.findById(1).get();

        Assertions.assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    void testUpdateName() {
        String newName = "Samsung Electronics";
        Brand samsung = brandRepository.findById(3).get();
        samsung.setName(newName);

        Brand savedBrand = brandRepository.save(samsung);

        Assertions.assertThat(savedBrand.getName()).isEqualTo(newName);
    }

    @Test
    void testDelete() {
        Integer id = 2;
        brandRepository.deleteById(id);

        Optional<Brand> brand = brandRepository.findById(id);

        Assertions.assertThat(brand).isEmpty();
    }

}
