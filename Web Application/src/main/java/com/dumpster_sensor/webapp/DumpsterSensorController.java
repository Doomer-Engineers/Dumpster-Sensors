package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.queries.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DumpsterSensorController {

    @Autowired
    private UserRepo uRepo;

}
