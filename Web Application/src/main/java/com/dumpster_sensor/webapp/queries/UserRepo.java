package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User,Long> {

    //search query for username
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    //search query for id
    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User findByID(Long username);

    //search query for email
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);


}
