package com.dumpster_sensor.webapp.models;

import javax.persistence.*;

@Entity
@Table(name = "otp")
public class OTP {
    //PK. Autogenerated in DB
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private int otp;

    @Column(nullable = false)
    private String expired;

    @Column(nullable = false)
    private Long userID;

    public OTP(int otp, String expired, Long userID) {
        this.otp = otp;
        this.expired = expired;
        this.userID = userID;
    }

    public OTP(int otp, String expired) {
        this.otp = otp;
        this.expired = expired;
    }

    public OTP() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
