package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.Sensor;
import com.dumpster_sensor.webapp.queries.SensorRepo;
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
class SensorModelTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SensorRepo sRepo;

    @Test
    void whenValidConstructorAndFindALL_thenNoErrors() {
        //public Sensor(Long id, String location, String time1, String time2, String installed, String power, String lastUpdated)
        Sensor sensor = new Sensor(12L, "noneya", "true", "low", "Bedtime");
        Sensor sensor2 = new Sensor(13L, "boo", "false", "high", "Wakeup");

        entityManager.merge(sensor);
        entityManager.merge(sensor2);
        List<Sensor> foundAll = sRepo.findAll();
        Assertions.assertEquals(foundAll.get(0).getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(foundAll.get(0).getLocation(), sensor.getLocation());
        Assertions.assertEquals(foundAll.get(0).getId(), sensor.getId());
        Assertions.assertEquals(foundAll.get(0).getPower(), sensor.getPower());
        Assertions.assertEquals(foundAll.get(0).getLastUpdated(), sensor.getLastUpdated());

        Assertions.assertEquals(foundAll.get(1).getInstalled(), sensor2.getInstalled());
        Assertions.assertEquals(foundAll.get(1).getLocation(), sensor2.getLocation());
        Assertions.assertEquals(foundAll.get(1).getId(), sensor2.getId());
        Assertions.assertEquals(foundAll.get(1).getPower(), sensor2.getPower());
        Assertions.assertEquals(foundAll.get(1).getLastUpdated(), sensor2.getLastUpdated());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByID_thenNoErrors() {
        //public Sensor(Long id, String location, String time1, String time2, String installed, String power, String lastUpdated)
        Sensor sensor = new Sensor(14L, "noneya", "true", "low", "Bedtime");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByID(sensor.getId());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructor_thenNoErrors() {
        Sensor sensor = new Sensor();
        sensor.setId(15L);
        sensor.setLocation("richard");
        sensor.setInstalled("true");
        sensor.setPower("high");
        sensor.setLastUpdated("noon");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByLocation(sensor.getLocation());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByLocation_thenNoErrors() {
        //public Sensor(Long id, String location, String time1, String time2, String installed, String power, String lastUpdated)
        Sensor sensor = new Sensor(16L, "noneya", "true", "low", "Bedtime");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByLocation(sensor.getLocation());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

}
