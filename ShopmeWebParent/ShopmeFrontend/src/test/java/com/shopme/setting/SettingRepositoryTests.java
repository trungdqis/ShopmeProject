package com.shopme.setting;

import com.shopme.common.entity.Product;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SettingRepositoryTests {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    void testFindByTwoCategories() {
        List<Setting> settings = settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

        settings.forEach(System.out::println);
    }
}
