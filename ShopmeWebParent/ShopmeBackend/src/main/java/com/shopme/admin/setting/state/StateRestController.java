package com.shopme.admin.setting.state;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/states/list_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
        List<StateDTO> result = new ArrayList<>();

        listStates.forEach(s -> result.add(new StateDTO(s.getId(), s.getName())));

        return result;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state) {
        State savedState = stateRepository.save(state);

        return String.valueOf(savedState.getId());
    }

    @GetMapping("/states/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        stateRepository.deleteById(id);
    }
}
