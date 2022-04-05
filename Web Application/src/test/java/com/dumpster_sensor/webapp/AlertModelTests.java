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

    //testi
    @Test
    void whenValidConstructorsAndFindAll_thenNoErrors() {
        Alert a1 = new Alert(7L, "power", 1L, false);
        Alert a2 = new Alert(8L, "missed service", 1L, false);
        entityManager.merge(a1);
        entityManager.merge(a2);
        List<Alert> foundAll = aRepo.findAll();
        Assertions.assertEquals(foundAll.get(0).getError(), a1.getError());
        Assertions.assertEquals(foundAll.get(0).getSensorID(), a1.getSensorID());
        Assertions.assertEquals(foundAll.get(0).getId(), a1.getId());
        Assertions.assertEquals(foundAll.get(0).isArchived(), a1.isArchived());

        Assertions.assertEquals(foundAll.get(1).getError(), a2.getError());
        Assertions.assertEquals(foundAll.get(1).getSensorID(), a2.getSensorID());
        Assertions.assertEquals(foundAll.get(1).getId(), a2.getId());
        Assertions.assertEquals(foundAll.get(1).isArchived(), a2.isArchived());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByID_thenNoErrors() {
        Alert kek = new Alert(9L, "admin", 2L, false);
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
        teemo.setId(10L);
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

    @Test
    void whenValidConstructorAndFindBySensorID_thenNoErrors() {
        Alert kek = new Alert(11L, "admin", 1L, false);
        entityManager.merge(kek);
        Alert found = aRepo.findBySensorID(kek.getSensorID());
        Assertions.assertEquals(found.getError(), kek.getError());
        Assertions.assertEquals(found.getSensorID(), kek.getSensorID());
        Assertions.assertEquals(found.getId(), kek.getId());
        Assertions.assertEquals(found.isArchived(), kek.isArchived());
        entityManager.clear();
    }
}