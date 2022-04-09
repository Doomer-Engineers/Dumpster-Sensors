package com.dumpster_sensor.webapp;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

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
    public void whenValidLogin_thenReturnHomepage()
            throws Exception {
        User bob = new User(6L, "admin", "bob", "password", "bob@gmail.com");
        mockUser();
        when(DumpsterSensorController.getLoggedInUser()).thenReturn("bob");
        //this is because default user is user
        when(userRepo.findByUsername("user")).thenReturn(bob);
        mvc.perform(get("/homepage").flashAttr("bob", bob)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name(("homepage")));
    }

    @Test
    @WithMockUser
    public void whenValidLogin_thenReturnAddUser()
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
    public void whenInvalidLogin_thenReturnHomepage()
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
    public void whenValidUserCreation_thenReturnHomepage()
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
}
