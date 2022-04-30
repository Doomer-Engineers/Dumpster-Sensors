package com.dumpster_sensor.webapp;


import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.models.OTP;
import com.dumpster_sensor.webapp.models.Sensor;
import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DumpsterSensorController.class)
public class DumpsterSensorControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private AlertRepo alertRepo;

    @MockBean
    private OTPRepo otpRepo;

    @MockBean
    private GarbageRepo garbageRepo;

    @MockBean
    private SensorRepo sensorRepo;

    public void mockUser() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("bob");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser
    public void whenValidLoginAndExistingOTP_thenReturnOTPPage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "Afasdfasf",bob.getId());
        mockUser();

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);

        when(otpRepo.findByOTP(password.getOtp())).thenReturn(null);
        when(otpRepo.findByUserID(password.getUserID())).thenReturn(password);

        mvc.perform(get("/user/change_password").flashAttr("user", bob).flashAttr("otp", password)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("otpPage"));
    }

    @Test
    @WithMockUser
    public void whenValidLoginAndNonExistingOTP_thenReturnOTPPage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "Afasdfasf",bob.getId());
        mockUser();

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);

        when(otpRepo.findByOTP(password.getOtp())).thenReturn(null);
        when(otpRepo.findByUserID(password.getUserID())).thenReturn(null);

        mvc.perform(get("/user/change_password").flashAttr("user", bob).flashAttr("otp", password)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("otpPage"));
    }

    //from here out on tests are good and test model
    @Test
    @WithMockUser
    public void whenValidLoginAndUnexpiredOTP_thenRedirect()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "2023-12-12 01:01:01", bob.getId());
        mockUser();

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);

        when(otpRepo.findByUserID(password.getUserID())).thenReturn(password);

        mvc.perform(post("/user/change_password/otp").flashAttr("user", bob).flashAttr("otp", password).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/user/change_password/authenticated")));
    }

    @Test
    @WithMockUser
    public void whenValidLoginAndWrongOTP_thenRedirect()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "2023-12-12 01:01:01",5L);
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);
        when(otpRepo.findByUserID(password.getUserID())).thenReturn(null);
        mvc.perform(post("/user/change_password/otp").flashAttr("user", bob).flashAttr("otp", password).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("otpPage")))
                .andExpect(model().attribute("wrongOTP","Your OTP was incorrect."));
    }

    @Test
    @WithMockUser
    public void whenValidLoginAndExpiredOTP_thenRedirect()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "1999-12-12 01:01:01",6L);
        mockUser();

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);
        when(otpRepo.findByUserID(password.getUserID())).thenReturn(password);

        mvc.perform(post("/user/change_password/otp").flashAttr("user", bob).flashAttr("otp", password).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("otpPage")))
                .andExpect(model().attribute("expiredOTP", "Your OTP was expired."));
    }

    @Test
    @WithMockUser
    public void whenValidAdminRole_thenReturnAddUser()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(get("/addUser").flashAttr("bob", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addUser")));
    }

    @Test
    @WithMockUser
    public void whenValidGeneralRole_thenReturnHomepage()
            throws Exception {
        User bob = new User(6L, "general", "bob", "password", "bob@gmail.com");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(get("/addUser").flashAttr("bob", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("homepage")));
    }

    @Test
    @WithMockUser
    public void whenValidUserCreation_thenRedirectHomepage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        mockUser();
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(post("/addUser").flashAttr("user", bob).flashAttr("uvp", uvp)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/homepage")));
    }

    @Test
    @WithMockUser
    public void whenUserCreationAndInvalidUsername_thenReturnAddUser()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        User adam = new User(7L, "general", "adam", "Valid123&", "adam@uiowa.edu");

        mockUser();
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername(bob.getUsername())).thenReturn(adam);
        mvc.perform(post("/addUser").flashAttr("user", bob).flashAttr("uvp", uvp)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addUser")))
                .andExpect(model().attribute("user", bob))
                .andExpect(model().attribute("inUse", bob.getUsername()));
    }

    @Test
    @WithMockUser
    public void whenUserCreationAndInvalidUVP_thenReturnAddUser()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        mockUser();
        PasswordValidator uvp = new PasswordValidator();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(post("/addUser").flashAttr("user", bob).flashAttr("uvp", uvp)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addUser")))
                .andExpect(model().attribute("user", bob))
                .andExpect(model().attribute("pwError", "Password fields do not match."));
    }

    @Test
    @WithMockUser
    public void whenUserCreationAndInvalidPassword_thenRedirectHomepage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "valid", "bob@uiowa.edu");
        mockUser();
        PasswordValidator uvp = new PasswordValidator();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(post("/addUser").flashAttr("user", bob).flashAttr("uvp", uvp)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addUser")))
                .andExpect(model().attribute("user", bob))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    @WithMockUser
    public void whenUserCreationAndInvalidEmailUserCreationAndInvalidEmail_thenReturnAddUser()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@gmail.com");
        mockUser();
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(post("/addUser").flashAttr("user", bob).flashAttr("uvp", uvp)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addUser")))
                .andExpect(model().attribute("user", bob))
                .andExpect(model().attribute("wrongEmail",bob.getEmail()));
    }

    @Test
    @WithMockUser
    public void whenValidLogin_thenReturnHomepage() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        mvc.perform(get("/homepage").flashAttr("user", bob).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("homepage")))
                .andExpect(model().attribute("user", bob));


    }

    @Test
    public void whenDefaultURL_thenRedirectLogin() throws Exception {
        mvc.perform(get("/").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/login")));
    }

    @Test
    @WithMockUser
    public void whenChangePassword_ThenReturnUpdatePassword() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        mvc.perform(get("/user/change_password/authenticated").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updatePassword")))
                .andExpect(model().attribute("user", bob));
    }

    @Test
    @WithMockUser
    public void whenValidChangePassword_ThenRedirectHomepage() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid123&", "bob@uiowa.edu");
        User adam = new User(7L, "general", "adam", "Valid123&", "adam@uiowa.edu");
        when(userRepo.findByUsername("user")).thenReturn(adam);
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());

        mvc.perform(post("/user/change_password/authenticated/confirm").with(csrf()).flashAttr("user", bob).flashAttr("uvp", uvp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/homepage")));
    }

    @Test
    @WithMockUser
    public void whenInvalidChangePassword_ThenReturnUpdatePassword() throws Exception {
        User bob = new User(6L, "admin", "bob", "valid", "bob@uiowa.edu");
        User adam = new User(7L, "general", "adam", "Valid123&", "adam@uiowa.edu");
        when(userRepo.findByUsername("user")).thenReturn(adam);
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());

        mvc.perform(post("/user/change_password/authenticated/confirm").with(csrf()).flashAttr("user", bob).flashAttr("uvp", uvp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updatePassword")))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    @WithMockUser
    public void whenValidChangePasswordAndAdmin_ThenReturnUsersAdmin() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        when(userRepo.findByUsername("user")).thenReturn(bob);
        PasswordValidator uvp = new PasswordValidator();
        uvp.setCheckPW(bob.getPassword());

        mvc.perform(post("/user/change_password/authenticated/confirm").with(csrf()).flashAttr("user", bob).flashAttr("uvp", uvp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updateUsersAdmin")));
    }

    @Test
    @WithMockUser
    public void whenValidUserUpdateAndAdmin_ThenReturnUsersAdmin() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        mvc.perform(get("/users/update").with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updateUsersAdmin")));
    }

    @Test
    @WithMockUser
    public void whenValidUserUpdateAndGeneral_ThenReturnHomepage() throws Exception {
        User bob = new User(6L, "general", "bob", "Valid$123", "bob@uiowa.edu");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        mvc.perform(get("/users/update").with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("homepage")));
    }

    @Test
    @WithMockUser
    public void whenDeleteUser_ThenRedirectsUsersAdmin() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        mvc.perform(post("/user/{id}/delete", bob.getId()).with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/users/update")));
    }

    @Test
    @WithMockUser
    public void whenResetPassword_ThenReturnUpdatePassword() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        when(userRepo.findByID(bob.getId())).thenReturn(bob);
        mvc.perform(get("/user/{id}/recover", bob.getId()).with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updatePassword")));
    }

    @Test
    @WithMockUser
    public void whenUpdateUser_ThenReturnUpdateUser() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        when(userRepo.findByID(bob.getId())).thenReturn(bob);
        mvc.perform(get("/user/{id}/update", bob.getId()).with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updateUser")));
    }

    @Test
    @WithMockUser
    public void whenValidUpdatedUser_ThenRedirectUpdateUsersAdmin() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        when(userRepo.findByID(bob.getId())).thenReturn(bob);

        mvc.perform(post("/user/{id}/update", bob.getId()).with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/users/update")));
    }

    @Test
    @WithMockUser
    public void whenInvalidEmailAndUpdateUser_ThenReturnUpdateUser() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@gmail.com");
        when(userRepo.findByID(bob.getId())).thenReturn(bob);

        mvc.perform(post("/user/{id}/update", bob.getId()).with(csrf()).flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("updateUser")))
                .andExpect(model().attribute("wrongEmail", bob.getEmail()));
    }

    @Test
    @WithMockUser
    public void whenAddSensor_ThenReturnAddSensor() throws Exception {
        mvc.perform(get("/addSensor").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addSensor")));
    }

    @Test
    @WithMockUser
    public void whenValidSensor_ThenRedirectHomepage() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1", "time2", "false", "low", "will be overridden");
        mvc.perform(post("/addSensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/homepage")));
    }

    @Test
    @WithMockUser
    public void whenAddSensorAndTimesAreEqual_ThenReturnAddSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1", "time1", "false", "low", "will be overridden");
        mvc.perform(post("/addSensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addSensor")))
                .andExpect(model().attribute("timeErrorE", "The two selected times cannot be equal"));
    }

    @Test
    @WithMockUser
    public void whenAddSensorAndTime1IsAlreadyTaken_ThenReturnAddSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        Sensor sensor2 = new Sensor(14L, "moon", "time1-1", "time2a", "false", "low", "will be overridden");
        List<Sensor> allSensors = new ArrayList<>();
        allSensors.add(sensor2);
        when(sensorRepo.findAll()).thenReturn(allSensors);
        mvc.perform(post("/addSensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addSensor")))
                .andExpect(model().attribute("timeError1", "Selected time is currently in use"));
    }

    @Test
    @WithMockUser
    public void whenAddSensorAndTime2IsAlreadyTaken_ThenReturnAddSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        Sensor sensor2 = new Sensor(14L, "moon", "time1", "time2", "false", "low", "will be overridden");
        List<Sensor> allSensors = new ArrayList<>();
        allSensors.add(sensor2);
        when(sensorRepo.findAll()).thenReturn(allSensors);
        mvc.perform(post("/addSensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addSensor")))
                .andExpect(model().attribute("timeError2", "Selected time is currently in use"));
    }

    @Test
    @WithMockUser
    public void whenAddSensorAndLocationIsAlreadyTaken_ThenReturnAddSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        Sensor sensor2 = new Sensor(14L, "dumpster", "time1", "time2a", "false", "low", "will be overridden");
        List<Sensor> allSensors = new ArrayList<>();
        allSensors.add(sensor2);
        when(sensorRepo.findAll()).thenReturn(allSensors);
        mvc.perform(post("/addSensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("addSensor")))
                .andExpect(model().attribute("locationError", "Location is already in use"));
    }

    @Test
    @WithMockUser
    public void whenViewSensors_ThenReturnViewSensors() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        Sensor sensor2 = new Sensor(14L, "moon", "time1", "time2a", "false", "low", "will be overridden");
        List<Sensor> allSensors = new ArrayList<>();
        allSensors.add(sensor2);
        allSensors.add(sensor);
        when(sensorRepo.findAll()).thenReturn(allSensors);
        mvc.perform(get("/view/sensors").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("viewSensors")))
                .andExpect(model().attribute("sensors", allSensors));
    }

    @Test
    @WithMockUser
    public void whenFindSensor_ThenReturnFindSensor() throws Exception {
        Sensor sensor = new Sensor();
        mvc.perform(get("/find/sensor").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("findSensor")))
                .andExpect(model().attribute("sensor", sensor));
    }

    @Test
    @WithMockUser
    public void whenFindSensorByLocationAndValid_ThenRedirectToSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        when(sensorRepo.findByLocation(sensor.getLocation())).thenReturn(sensor);
        mvc.perform(post("/find/sensor/submit").with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/sensor/update/" + sensor.getId())));
    }

    @Test
    @WithMockUser
    public void whenFindSensorByIDAndValid_ThenRedirectToSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        Sensor sensor2 = new Sensor();
        sensor2.setId(16L);
        when(sensorRepo.findByID(sensor2.getId())).thenReturn(sensor);
        mvc.perform(post("/find/sensor/submit").with(csrf()).flashAttr("sensor", sensor2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(("redirect:/sensor/update/" + sensor.getId())));
    }

    @Test
    @WithMockUser
    public void whenFindSensorByIDAndInvalid_ThenReturnFindSensor() throws Exception {
        Sensor sensor2 = new Sensor();
        sensor2.setId(13L);
        when(sensorRepo.findByID(sensor2.getId())).thenReturn(null);
        mvc.perform(post("/find/sensor/submit").with(csrf()).flashAttr("sensor", sensor2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("findSensor"))
                .andExpect(model().attribute("error", "Requested sensor is not found"));
    }

    @Test
    @WithMockUser
    public void whenUpdateSensor_ThenReturnGetSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");
        //not mocking garbage data for this sensor.
        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);
        mvc.perform(get("/sensor/update/{id}", sensor.getId()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("getSensor"))
                .andExpect(model().attribute("sensor", sensor))
                .andExpect(model().attributeExists("chartData"));
    }

    @Test
    @WithMockUser
    public void whenInstallSensor_ThenRedirectToUpdateSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "false", "low", "will be overridden");

        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);
        mvc.perform(post("/sensor/update/{id}/install", sensor.getId()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sensor/update/" + sensor.getId()));
    }

    @Test
    @WithMockUser
    public void whenUnstallSensor_ThenRedirectToUpdateSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "true", "low", "will be overridden");

        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);
        mvc.perform(post("/sensor/update/{id}/uninstall", sensor.getId()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sensor/update/" + sensor.getId()));
    }

    @Test
    @WithMockUser
    public void whenEditSensor_ThenReturnUpdateSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "true", "low", "will be overridden");

        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);
        mvc.perform(get("/sensor/update/{id}/edit", sensor.getId()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("updateSensor"))
                .andExpect(model().attribute("sensor", sensor));
    }

    @Test
    @WithMockUser
    public void whenEditSensorAndValid_ThenRedirectToUpdateSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time1-1", "time2", "true", "low", "will be overridden");

        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);

        mvc.perform(post("/sensor/update/{id}/edit/confirm", sensor.getId()).with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sensor/update/"+ sensor.getId()));
    }

    @Test
    @WithMockUser
    public void whenEditSensorAndInvalidTimes_ThenReturnUpdateSensor() throws Exception {
        Sensor sensor = new Sensor(16L, "dumpster", "time", "time", "true", "low", "will be overridden");

        when(sensorRepo.findByID(sensor.getId())).thenReturn(sensor);

        mvc.perform(post("/sensor/update/{id}/edit/confirm", sensor.getId()).with(csrf()).flashAttr("sensor", sensor)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("updateSensor"));
    }

    @Test
    @WithMockUser
    public void whenDeleteSensor_ThenRedirectHomepage() throws Exception {
        mvc.perform(post("/sensor/update/{id}/delete", 16).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/homepage"));
    }

    @Test
    @WithMockUser
    public void whenViewAlerts_ThenReturnLogs() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        Alert alert = new Alert(55L, "Low power", 16L, false);
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        List<Alert> alerts = new ArrayList<>();
        alerts.add(alert);

        when(alertRepo.findAll()).thenReturn(alerts);

        mvc.perform(get("/logs").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("logs"))
                .andExpect(model().attribute("user", bob))
                .andExpect(model().attribute("alerts", alerts));
    }

    @Test
    @WithMockUser
    public void whenArchiveAlert_ThenReturnLogs() throws Exception {
        User bob = new User(6L, "admin", "bob", "Valid$123", "bob@uiowa.edu");
        Alert alert = new Alert(55L, "Low power", 16L, false);
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        when(userRepo.findByUsername("user")).thenReturn(bob);

        List<Alert> alerts = new ArrayList<>();
        alerts.add(alert);

        when(alertRepo.findByID(alert.getId())).thenReturn(alert);
        when(alertRepo.findAll()).thenReturn(alerts);

        mvc.perform(post("/log/{id}/archive",alert.getId() ).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logs"));
    }

}
