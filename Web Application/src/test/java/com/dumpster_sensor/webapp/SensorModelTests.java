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

@RunWith(SpringRunner.class)
@DataJpaTest
class SensorModelTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SensorRepo sRepo;

    @Test
    void whenValidConstructorAndFindByID_thenNoErrors() {
        //public Sensor(Long id, String location, String time1, String time2, String installed, String power, String lastUpdated)
        Sensor sensor = new Sensor(5L, "noneya", "9999", "10000", "true", "low", "Bedtime");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByID(sensor.getId());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getTime1(), sensor.getTime1());
        Assertions.assertEquals(found.getTime2(), sensor.getTime2());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructor_thenNoErrors() {
        Sensor sensor = new Sensor();
        sensor.setId(6L);
        sensor.setLocation("richard");
        sensor.setTime2("5");
        sensor.setTime1("2");
        sensor.setInstalled("true");
        sensor.setPower("high");
        sensor.setLastUpdated("noon");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByLocation(sensor.getLocation());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getTime1(), sensor.getTime1());
        Assertions.assertEquals(found.getTime2(), sensor.getTime2());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByLocation_thenNoErrors() {
        //public Sensor(Long id, String location, String time1, String time2, String installed, String power, String lastUpdated)
        Sensor sensor = new Sensor(7L, "noneya", "9999", "10000", "true", "low", "Bedtime");
        entityManager.merge(sensor);
        Sensor found = sRepo.findByLocation(sensor.getLocation());
        Assertions.assertEquals(found.getInstalled(), sensor.getInstalled());
        Assertions.assertEquals(found.getLocation(), sensor.getLocation());
        Assertions.assertEquals(found.getId(), sensor.getId());
        Assertions.assertEquals(found.getPower(), sensor.getPower());
        Assertions.assertEquals(found.getTime1(), sensor.getTime1());
        Assertions.assertEquals(found.getTime2(), sensor.getTime2());
        Assertions.assertEquals(found.getLastUpdated(), sensor.getLastUpdated());
        entityManager.clear();
    }

}
