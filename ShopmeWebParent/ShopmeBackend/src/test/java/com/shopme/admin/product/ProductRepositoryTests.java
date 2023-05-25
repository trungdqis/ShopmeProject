package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.Optional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short Descriptuon for Acer Aspire Desktop");
        product.setFullDescription("Full Descriptuon for Acer Aspire Desktop");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isPositive();
    }

    @Test
    void testListAllProducts() {
        Iterable<Product> products = productRepository.findAll();

        products.forEach(System.out::println);
    }

    @Test
    void testGetProduct() {
        Integer id = 2;

        Optional<Product> product = productRepository.findById(id);
        System.out.println(product.get());

        Assertions.assertThat(product).isNotNull();
    }

    @Test
    void testUpdateProduct() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        product.setPrice(499);

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void testDeleteProduct() {
        Integer id = 3;
        productRepository.deleteById(3);

        Optional<Product> product = productRepository.findById(id);

        Assertions.assertThat(product).isNotPresent();
    }

    @Test
    void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.setMainImage("main image.jsp");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra_image_2.png");
        product.addExtraImage("extra-image3.png");

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }
}
