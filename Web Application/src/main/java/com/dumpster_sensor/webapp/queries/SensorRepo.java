package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SensorRepo extends JpaRepository<Sensor,Long> {

    //search query for id
    @Query("SELECT s FROM Sensor s WHERE s.id = ?1")
    Sensor findByID(Long id);

    //search query for location
    @Query("SELECT s FROM Sensor s WHERE s.location = ?1")
    Sensor findByLocation(String location);

    //search query descending last updates
    @Query(value = "SELECT * FROM Sensor s ORDER BY s.last_updated DESC", nativeQuery = true)
    List<Sensor> findAllOrderByLastUpdatedDesc();
}
