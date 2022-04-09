package com.dumpster_sensor.webapp;


import com.dumpster_sensor.webapp.models.OTP;
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

import static org.mockito.Mockito.when;
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

    @Test
    @WithMockUser
    public void whenValidLogin_thenReturnHomepage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(bob.getUsername());
        SecurityContextHolder.setContext(securityContext);
        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        //this is because default user is user
        when(userRepo.findByUsername(Mockito.anyString())).thenReturn(bob);
        mvc.perform(get("/homepage").flashAttr("user", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"));
    }

    @Test
    @WithMockUser
    public void whenValidLoginAndExistingOTP_thenReturnOTPPage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        OTP password = new OTP(5, "Afasdfasf",bob.getId());
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(bob.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        //this is because default user is user
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
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(bob.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        //this is because default user is user
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
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(bob.getUsername());
        SecurityContextHolder.setContext(securityContext);

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
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("bob");
        SecurityContextHolder.setContext(securityContext);

        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
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
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(bob.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(DumpsterSensorController.getLoggedInUser()).thenReturn(bob.getUsername());
        when(userRepo.findByUsername("user")).thenReturn(bob);

        when(otpRepo.findByUserID(password.getUserID())).thenReturn(password);

        mvc.perform(post("/user/change_password/otp").flashAttr("user", bob).flashAttr("otp", password).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("otpPage")))
                .andExpect(model().attribute("expiredOTP", "Your OTP was expired."));
    }
}
