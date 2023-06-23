package com.shopme.admin.setting.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CountryRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        Assertions.assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testCreateCountry() throws Exception {
        String url = "/countries/save";
        String countryCode = "CA";
        String countryName = "Canada";
        Country country = new Country(countryName, countryCode);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(response);
        Optional<Country> foundCountry = countryRepository.findById(countryId);

        Assertions.assertThat(foundCountry).isPresent();
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Integer countryId = 5;
        String countryCode = "GE";
        String countryName = "Germany";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)))
                .andReturn();

        Optional<Country> foundCountry = countryRepository.findById(countryId);

        Assertions.assertThat(foundCountry).isPresent();
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testDeleteCountry() throws Exception {
        Integer countryId = 4;
        String url = "/countries/delete/" + countryId;

        mockMvc.perform(get(url))
                .andExpect(status().isOk());

        Optional<Country> foundCountry = countryRepository.findById(countryId);

        Assertions.assertThat(foundCountry).isNotPresent();
    }
}
