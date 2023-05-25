package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class BrandServiceTests {

    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void testCheckUniqueInNewModeReturnDuplicate() {
        Integer id = null;
        String name = "Canon";

        Brand brand = new Brand(name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(id, name);

        Assertions.assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "AMD";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUnique(id, name);

        Assertions.assertThat(result).isEqualTo("OK");
    }

    @Test
    void testCheckUniqueInEditModeReturnDuplicate() {
        Integer id = 1;
        String name = "Canon";

        Brand brand = new Brand(id, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(2, "Canon");

        Assertions.assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "Acer";

        Brand brand = new Brand(id, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(id, name);

        Assertions.assertThat(result).isEqualTo("OK");
    }
}
