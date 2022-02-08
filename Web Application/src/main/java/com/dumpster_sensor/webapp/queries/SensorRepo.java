package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SensorRepo extends JpaRepository<Sensor,Long> {

    //search query for username
    @Query("SELECT s FROM Sensor s WHERE s.id = ?1")
    com.dumpster_sensor.webapp.models.Sensor findByID(Long id);
}
