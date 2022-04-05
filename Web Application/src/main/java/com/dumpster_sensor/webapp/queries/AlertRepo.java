package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertRepo extends JpaRepository<Alert,Long> {

    //search query for id
    @Query("SELECT a FROM Alert a WHERE a.id = ?1")
    Alert findByID(Long id);

    //search query for sensor id
    @Query("SELECT a FROM Alert a WHERE a.sensorID = ?1")
    Alert findBySensorID(Long id);

    //search for all alerts
    @Query(value = "SELECT * FROM Alert a", nativeQuery = true)
    List<Alert> findAll();

}
