package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.OTP;
import com.dumpster_sensor.webapp.queries.OTPRepo;

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
class OTPModelTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OTPRepo oRepo;

    @Test
    void whenValidConstructorAndFindByOTP_thenNoError() {
        OTP sett = new OTP(1, "true", 1L);
        sett.setId(17L);
        entityManager.merge(sett);
        OTP found = oRepo.findByOTP(sett.getOtp());
        Assertions.assertEquals(found.getOtp(), sett.getOtp());
        Assertions.assertEquals(found.getExpired(), sett.getExpired());
        Assertions.assertEquals(found.getId(), sett.getId());
        Assertions.assertEquals(found.getUserID(), sett.getUserID());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByUserID_thenNoError() {
        OTP sett = new OTP(3, "true", 3L);
        sett.setId(18L);
        entityManager.merge(sett);
        OTP found = oRepo.findByUserID(sett.getUserID());
        System.out.println("HEY" + sett.getId());
        Assertions.assertEquals(found.getOtp(), sett.getOtp());
        Assertions.assertEquals(found.getExpired(), sett.getExpired());
        Assertions.assertEquals(found.getId(), sett.getId());
        Assertions.assertEquals(found.getUserID(), sett.getUserID());
        entityManager.clear();
    }

    @Test
    void whenValidConstructorAndFindByID_thenNoError() {
        OTP sett = new OTP(2, "true", 2L);
        sett.setId(19L);
        entityManager.merge(sett);
        OTP found = oRepo.findByID(sett.getId());
        System.out.println("HEY" + sett.getId());
        Assertions.assertEquals(found.getOtp(), sett.getOtp());
        Assertions.assertEquals(found.getExpired(), sett.getExpired());
        Assertions.assertEquals(found.getId(), sett.getId());
        Assertions.assertEquals(found.getUserID(), sett.getUserID());
        entityManager.clear();
    }

    @Test
    void whenEmptyConstructor_thenNoError() {
        OTP morde = new OTP();
        morde.setId(20L);
        morde.setExpired("true");
        morde.setOtp(3);
        morde.setUserID(4L);
        entityManager.merge(morde);
        OTP found = oRepo.findByID(morde.getId());
        Assertions.assertEquals(found.getOtp(), morde.getOtp());
        Assertions.assertEquals(found.getId(), morde.getId());
        Assertions.assertEquals(found.getUserID(), morde.getUserID());
        Assertions.assertEquals(found.getExpired(), morde.getExpired());
        entityManager.clear();
    }
}