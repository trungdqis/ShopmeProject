package com.shopme.admin.setting.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StateRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testListByCountries() throws Exception {
        int countryId = 3;
        String url = "/states/list_by_country/" + countryId;

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        State[] states = objectMapper.readValue(jsonResponse, State[].class);

        Assertions.assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testCreateState() throws Exception {
        String url = "/states/save";
        int countryId = 3;
        Country country = countryRepository.findById(countryId).get();
        State state = new State("Texas", country);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Integer stateId = Integer.parseInt(response);
        Optional<State> foundState = stateRepository.findById(stateId);

        Assertions.assertThat(foundState).isPresent();
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testUpdateState() throws Exception {
        String url = "/states/save";
        int stateId = 9;
        String stateName = "Alaska";

        State state = stateRepository.findById(stateId).get();
        state.setName(stateName);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stateId)));

        Optional<State> foundState = stateRepository.findById(stateId);

        Assertions.assertThat(foundState).isPresent();

        State updatedState = foundState.get();

        Assertions.assertThat(updatedState.getName()).isEqualTo(stateName);
    }

    @Test
    @WithMockUser(username = "trungdq@gmail.com", password = "something", roles = "ADMIN")
    void testDeleteState() throws Exception {
        int stateId = 7;
        String url = "/states/delete/" + stateId;

        mockMvc.perform(get(url))
                .andExpect(status().isOk());

        Optional<State> foundState = stateRepository.findById(stateId);

        Assertions.assertThat(foundState).isNotPresent();
    }
}
