package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateUser() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User newUser = new User("trungdqis@gmail.com", "password", "Trung", "Dang Quoc");
        newUser.addRole(roleAdmin);
        User savedUser = userRepository.save(newUser);

        Assertions.assertThat(savedUser.getId()).isPositive();
    }

    @Test
    void testCreateNewUserWithTwoRoles() {
        User userChan = new User("chan@gmail.com", "password", "Trang", "Doan Thi Ngoc");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userChan.addRole(roleEditor);
        userChan.addRole(roleAssistant);

        User savedUser = userRepository.save(userChan);

        Assertions.assertThat(savedUser.getId()).isPositive();
    }

    @Test
    void testListAllUsers() {
        Iterable<User> listUsers = userRepository.findAll();
        listUsers.forEach(System.out::println);
    }

    @Test
    void testGetUserById() {
        User foundUser = userRepository.findById(1).get();
        System.out.println(foundUser);
        Assertions.assertThat(foundUser).isNotNull();
    }

    @Test
    void testUpdateUserDetails() {
        User foundUser = userRepository.findById(1).get();
        foundUser.setEnabled(true);
        foundUser.setEmail("trungdq@gmail.com");

        userRepository.save(foundUser);
    }

    @Test
    void testUpdateUserRoles() {
        User foundUser = userRepository.findById(2).get();
        foundUser.getRoles().remove(new Role(3)); // editor
        foundUser.getRoles().add(new Role(2)); // salesperson

        userRepository.save(foundUser);
    }

    @Test
    void testDeleteUser() {
        Integer userId = 2;
        userRepository.deleteById(userId);
    }

    @Test
    void testGetUserByEmail() {
        String email = "trungdq@gmail.com";
        var userByEmail = userRepository.getUserByEmail(email);

        Assertions.assertThat(userByEmail).isNotNull();
    }

    @Test
    void testCountById() {
        Integer id = 1;
        var countById = userRepository.countById(id);

        Assertions.assertThat(countById).isNotNull().isPositive();
    }

    @Test
    void testDisableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, false);
    }

    @Test
    void testEnableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, true);
    }

    @Test
    void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var page = userRepository.findAll(pageable);

        var users = page.getContent();

        users.forEach(System.out::println);

        Assertions.assertThat(users.size()).isEqualTo(pageSize);
    }

    @Test
    void testSearchUsers() {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var page = userRepository.findAll(keyword, pageable);

        var users = page.getContent();

        users.forEach(System.out::println);

        Assertions.assertThat(users.size()).isPositive();
    }
}
