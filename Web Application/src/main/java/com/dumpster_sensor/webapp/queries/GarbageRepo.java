package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Garbage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GarbageRepo extends JpaRepository<Garbage,Long> {

    //search query for id
    @Query("SELECT g FROM Garbage g WHERE g.id = ?1")
    com.dumpster_sensor.webapp.models.Garbage findByID(Long id);

    //search query for sensor id
    @Query("SELECT g FROM Garbage g WHERE g.sensorID = ?1")
    com.dumpster_sensor.webapp.models.Garbage findBySensorID(Long id);
}
