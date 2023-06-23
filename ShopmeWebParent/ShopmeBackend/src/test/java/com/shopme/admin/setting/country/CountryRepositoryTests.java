package com.shopme.admin.setting.country;

import com.shopme.common.entity.Country;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CountryRepositoryTests {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void testCreateCountry() {
        Country country = countryRepository.save(new Country("Singapore", "SP"));

        Assertions.assertThat(country).isNotNull();
        Assertions.assertThat(country.getId()).isPositive();
    }

    @Test
    void testListCountries() {
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        countries.forEach(System.out::println);

        Assertions.assertThat(countries).isNotEmpty();
    }

    @Test
    void testUpdateCountry() {
        Integer id = 1;
        String name = "Replublic of India";

        Country country = countryRepository.findById(id).get();
        country.setName(name);

        Country updatedCountry = countryRepository.save(country);

        Assertions.assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    void testGetCountry() {
        Integer id = 3;
        Country country = countryRepository.findById(id).get();

        Assertions.assertThat(country).isNotNull();
    }

    @Test
    void testDeleteCountry() {
        Integer id = 2;
        countryRepository.deleteById(id);

        Optional<Country> foundCountry = countryRepository.findById(id);

        Assertions.assertThat(foundCountry).isEmpty();
    }
}
