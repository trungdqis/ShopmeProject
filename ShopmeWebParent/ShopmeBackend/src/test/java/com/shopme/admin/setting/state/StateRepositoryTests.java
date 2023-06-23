package com.shopme.admin.setting.state;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class StateRepositoryTests {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testCreateStatesInIndia() {
        Integer countryId = 1;
        Country country = testEntityManager.find(Country.class, countryId);

//        State state = stateRepository.save(new State("Karnataka", country));
//        State state = stateRepository.save(new State("Pubjab", country));
//        State state = stateRepository.save(new State("Uttar Pradesh", country));
        State state = stateRepository.save(new State("West Bengal", country));

        Assertions.assertThat(state).isNotNull();
        Assertions.assertThat(state.getId()).isPositive();
    }

    @Test
    void testCreateStatesInUS() {
        Integer countryId = 3;
        Country country = testEntityManager.find(Country.class, countryId);

//        State state = stateRepository.save(new State("California", country));
//        State state = stateRepository.save(new State("Texas", country));
//        State state = stateRepository.save(new State("New York", country));
        State state = stateRepository.save(new State("Washington", country));

        Assertions.assertThat(state).isNotNull();
        Assertions.assertThat(state.getId()).isPositive();
    }

    @Test
    void testListStates() {
        Integer countryId = 3;
        Country country = testEntityManager.find(Country.class, countryId);

        List<State> states = stateRepository.findByCountryOrderByNameAsc(country);
        states.forEach(System.out::println);

        Assertions.assertThat(states).isNotEmpty();
    }

    @Test
    void testUpdateState() {
        Integer stateId = 3;
        String stateName = "Tamil Nadu";

        State state = stateRepository.findById(stateId).get();
        state.setName(stateName);

        State updatedState = stateRepository.save(state);

        Assertions.assertThat(updatedState.getName()).isEqualTo(stateName);
    }

    @Test
    void testGetState() {
        Integer stateId = 1;
        Optional<State> state = stateRepository.findById(stateId);

        Assertions.assertThat(state).isPresent();
    }

    @Test
    void testDeleteState() {
        Integer stateId = 8;
        stateRepository.deleteById(stateId);

        Optional<State> foundState = stateRepository.findById(stateId);

        Assertions.assertThat(foundState).isEmpty();
    }
}
