package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertRepo extends JpaRepository<Alert,Long> {

    //search query for id
    @Query("SELECT a FROM Alert a WHERE a.id = ?1")
    com.dumpster_sensor.webapp.models.Alert findByID(Long id);

    //search query for sensor id
    @Query("SELECT a FROM Alert a WHERE a.sensorID = ?1")
    com.dumpster_sensor.webapp.models.Alert findBySensorID(Long id);

}
