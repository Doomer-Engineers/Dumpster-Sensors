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
    void whenValidConstructorAndFindByName_thenNoErrors(){
        User bob = new User(1L,"admin","bob","password","bob@gmail.com");
        entityManager.merge(bob);
        User found = uRepo.findByUsername(bob.getUsername());
        Assertions.assertEquals(found.getUsername(), bob.getUsername());
    }
}
