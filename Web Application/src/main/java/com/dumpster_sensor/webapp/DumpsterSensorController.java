package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.util.List;

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

    //model attributes to be placed on page..
    @ModelAttribute("user")
    public User userDto() {
        return new User();
    }

    @ModelAttribute("uvp")
    public PasswordValidator pwDto() { return new PasswordValidator(); }

    public User getLoggedInUser(){
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return uRepo.findByUsername(username);
    }

    //Processes GET and POST requests
    //Does UPDATE and PUT inside GETs and POSTs

    @GetMapping("/")
    public String defaultRequest(){
        return "redirect:/login";
    }

    // used during developmemt
    // need to delete later
    @GetMapping("/index")
    public String getIndex() {return "index";}

    @GetMapping("/addUser")
    public String getAddUser() {
        //restricts role access
        User currentUser = getLoggedInUser();
        if (currentUser.getRole().equals("general")){
            return "homepage";
        }
        return "addUser";
    }

    @PostMapping("/addUser")
    public String processRegistration(@ModelAttribute("user") User user, Model model, @ModelAttribute("uvp") PasswordValidator uvp){

        //checks to see if entered username is in the system.
        User checkUserValid = uRepo.findByUsername(user.getUsername());
        // counter to check for server errors that is not caught client side.
        int errorCounter = 0;
        if(checkUserValid != null){
            //error checks for if username is in use.
            model.addAttribute("inUse", user.getUsername());
            errorCounter++;
        }
        if(!user.getEmail().endsWith("uiowa.edu")){
            //checks if email is an uiowa email address
            model.addAttribute("wrongEmail", user.getEmail());
            errorCounter++;
        }
        if(uvp.hasErrors(user.getPassword()) && user.getPassword() != null){
            //error checks if password has errors.
            model.addAttribute("errors", uvp.getErrors());
            errorCounter++;
        }
        if(!user.getPassword().equals(uvp.getCheckPW())){
            //error checks if password verification has errors.
            model.addAttribute("pwError", "Password fields do not match.");
            errorCounter++;
        }

        if (errorCounter > 0){
            return "addUser";
        }

        //encodes password as a fixed hash function
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        uRepo.save(user);
        return "redirect:/homepage";
    }

    @GetMapping("/deleteUsers")
    public String getDeleteUsers(Model model) {
        //restricts role access
        User currentUser = getLoggedInUser();
        if (currentUser.getRole().equals("general")){
            return "homepage";
        }
        List<User> users = uRepo.findAll();
        model.addAttribute("users", users);
        return "deleteUsers";
    }

    @PostMapping(("/user/{id}/delete"))
    public String deleteUser(Model model, @PathVariable(value = "id") Long id){
        uRepo.deleteById(id);
        List<User> users = uRepo.findAll();
        model.addAttribute("users", users);
        return "deleteUsers";
    }

    @GetMapping("/homepage")
    public String getHomepage(Model model) {
        User currentUser = getLoggedInUser();
        model.addAttribute("user", currentUser);
        return "homepage";
    }

    @GetMapping("/addSensor")
    public String getAddSensor(){
        return "addSensor";
    }

}
