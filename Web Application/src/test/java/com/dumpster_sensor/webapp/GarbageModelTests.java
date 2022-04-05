package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.Garbage;
import com.dumpster_sensor.webapp.queries.GarbageRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
class GarbageModelTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GarbageRepo gRepo;

    @Test
    void whenEmptyConstructorAndFindByID_thenNoErrors() {
        Garbage league = new Garbage();
        league.setId(8L);
        league.setGarbageLevel(90);
        league.setSensorID(1L);
        league.setTime("HH-MM-SS");
        entityManager.merge(league);
        Garbage found = gRepo.findByID(league.getId());
        Assertions.assertEquals(found.getTime(), league.getTime());
        Assertions.assertEquals(found.getId(), league.getId());
        Assertions.assertEquals(found.getGarbageLevel(), league.getGarbageLevel());
        Assertions.assertEquals(found.getSensorID(), league.getSensorID());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructorAndFindBySensorID_thenNoErrors() {
        Garbage league = new Garbage();
        league.setId(9L);
        league.setGarbageLevel(90);
        league.setSensorID(1L);
        league.setTime("HH-MM-SS");
        entityManager.merge(league);
        List<Garbage> found = gRepo.findAllBySensorID(league.getSensorID());
        Assertions.assertEquals(found.get(0).getTime(), league.getTime());
        Assertions.assertEquals(found.get(0).getId(), league.getId());
        Assertions.assertEquals(found.get(0).getGarbageLevel(), league.getGarbageLevel());
        Assertions.assertEquals(found.get(0).getSensorID(), league.getSensorID());
        entityManager.clear();
    }
}
