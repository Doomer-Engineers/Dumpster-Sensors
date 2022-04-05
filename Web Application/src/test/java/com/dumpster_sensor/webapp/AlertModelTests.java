package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.queries.AlertRepo;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//bootstrap bd on startup
@RunWith(SpringRunner.class)
@DataJpaTest
class AlertModelTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepo aRepo;

    @Test
    void whenValidConstructorAndFindBySensorID_thenNoErrors() {
        Alert kek = new Alert(7L, "admin", 1L, false);
        entityManager.merge(kek);
        Alert found = aRepo.findBySensorID(kek.getSensorID());
        Assertions.assertEquals(found.getError(), kek.getError());
        Assertions.assertEquals(found.getSensorID(), kek.getSensorID());
        Assertions.assertEquals(found.getId(), kek.getId());
        Assertions.assertEquals(found.isArchived(), kek.isArchived());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByID_thenNoErrors() {
        Alert kek = new Alert(5L, "admin", 2L, false);
        entityManager.merge(kek);
        Alert found = aRepo.findByID(kek.getId());
        System.out.println("HEY" + kek.getId());
        Assertions.assertEquals(found.getError(), kek.getError());
        Assertions.assertEquals(found.getSensorID(), kek.getSensorID());
        Assertions.assertEquals(found.getId(), kek.getId());
        Assertions.assertEquals(found.isArchived(), kek.isArchived());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructor_thenNoErrors() {
        Alert teemo = new Alert();
        teemo.setId(6L);
        teemo.setArchived(false);
        teemo.setSensorID(1L);
        teemo.setError("teemo");
        entityManager.merge(teemo);
        Alert found = aRepo.findBySensorID(teemo.getSensorID());
        Assertions.assertEquals(found.getError(), teemo.getError());
        Assertions.assertEquals(found.getSensorID(), teemo.getSensorID());
        Assertions.assertEquals(found.getId(), teemo.getId());
        Assertions.assertEquals(found.isArchived(), teemo.isArchived());
        entityManager.clear();
    }
}