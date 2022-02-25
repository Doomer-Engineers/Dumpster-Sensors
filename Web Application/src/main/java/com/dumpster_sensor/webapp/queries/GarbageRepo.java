package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Garbage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GarbageRepo extends JpaRepository<Garbage,Long> {

    //search query for id
    @Query("SELECT g FROM Garbage g WHERE g.id = ?1")
    Garbage findByID(Long id);

    //search query for all with sensor id
    @Query(value = "SELECT g FROM Garbage g WHERE g.sensorID = ?1")
    List<Garbage> findAllBySensorID(Long uid);
}
