package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


//bootstrap bd on startup
@RunWith(SpringRunner.class)
@DataJpaTest
class UserModelTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo uRepo;

    @Test
    void whenValidConstructorAndFindByName_thenNoErrors() {
        User bob = new User(1L, "admin", "bob", "password", "bob@gmail.com");
        entityManager.merge(bob);
        User found = uRepo.findByUsername(bob.getUsername());
        Assertions.assertEquals(found.getUsername(), bob.getUsername());
        Assertions.assertEquals(found.getRole(), bob.getRole());
        Assertions.assertEquals(found.getId(), bob.getId());
        Assertions.assertEquals(found.getPassword(), bob.getPassword());
        Assertions.assertEquals(found.getEmail(), bob.getEmail());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByID_thenNoErrors() {
        User bob = new User(2L, "admin", "bob", "password", "bob@gmail.com");
        entityManager.merge(bob);
        User found = uRepo.findByID(bob.getId());
        Assertions.assertEquals(found.getUsername(), bob.getUsername());
        Assertions.assertEquals(found.getRole(), bob.getRole());
        Assertions.assertEquals(found.getId(), bob.getId());
        Assertions.assertEquals(found.getPassword(), bob.getPassword());
        Assertions.assertEquals(found.getEmail(), bob.getEmail());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructor_thenNoErrors() {
        User adam = new User();
        adam.setId(3L);
        adam.setPassword("password");
        adam.setRole("general");
        adam.setUsername("adam");
        adam.setEmail("adam@gmail.com");
        entityManager.merge(adam);
        User found = uRepo.findByEmail(adam.getEmail());
        Assertions.assertEquals(found.getUsername(), adam.getUsername());
        Assertions.assertEquals(found.getRole(), adam.getRole());
        Assertions.assertEquals(found.getId(), adam.getId());
        Assertions.assertEquals(found.getPassword(), adam.getPassword());
        Assertions.assertEquals(found.getEmail(), adam.getEmail());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByEmail_thenNoErrors() {
        User bob = new User(4L, "admin", "bob", "password", "bob@gmail.com");
        entityManager.merge(bob);
        User found = uRepo.findByEmail(bob.getEmail());
        Assertions.assertEquals(found.getUsername(), bob.getUsername());
        Assertions.assertEquals(found.getRole(), bob.getRole());
        Assertions.assertEquals(found.getId(), bob.getId());
        Assertions.assertEquals(found.getPassword(), bob.getPassword());
        Assertions.assertEquals(found.getEmail(), bob.getEmail());
        entityManager.clear();
    }
}