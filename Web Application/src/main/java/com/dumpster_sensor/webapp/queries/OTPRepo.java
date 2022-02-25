package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OTPRepo extends JpaRepository<OTP,Long> {

    //search query for id
    @Query("SELECT o FROM OTP o WHERE o.id = ?1")
    OTP findByID(Long id);

    //search query for user id
    @Query("SELECT o FROM OTP o WHERE o.userID = ?1")
    OTP findByUserID(Long id);

    //search query for otp
    @Query("SELECT o FROM OTP o WHERE o.otp = ?1")
    OTP findByOTP(int otp);

}
