package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.queries.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DumpsterSensorController {

    @Autowired
    private UserRepo uRepo;

    @Autowired
    private OTPRepo oRepo;

    @Autowired
    private AlertRepo aRepo;

    @Autowired
    private SensorRepo sRepo;

    @Autowired
    private GarbageRepo garbageRepo;

    @GetMapping("")
    public String defaultRequest(){
        return "redirect:/login";
    }

}
