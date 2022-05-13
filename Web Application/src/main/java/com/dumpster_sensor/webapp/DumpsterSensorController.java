package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.*;
import com.dumpster_sensor.webapp.queries.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public User userDTO() { return new User(); }

    @ModelAttribute("sensor")
    public Sensor sensorDTO() { return new Sensor(); }

    @ModelAttribute("uvp")
    public PasswordValidator pwDto() { return new PasswordValidator(); }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String HOME = "homepage";
    private static final String REDIRECT_HOME ="redirect:/homepage";

    public static String getLoggedInUser(){
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    //to send email
    public void sendEmail(String email, String subject, String body){
        //Sender's email ID needs to be mentioned.
        String from = "doomerengineers@outlook.com";

        //Assuming you are sending email from through gmail smtp.
        String host = "smtp-mail.outlook.com";

        //Get system properties.
        Properties properties = System.getProperties();

        //Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.auth", "true");

        //Get the Session object.// and pass username and password.
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("doomerengineers@outlook.com", "Tacomaster123");

            }

        });

        //Used to debug SMTP issues.
        //session.setDebug(true);

        try {
            //Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            //Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            //Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            //Set Subject: header field.
            message.setSubject(subject);

            //Now set the actual message.
            message.setText(body);

            //Send message.
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private Pair<Integer, Model> passwordErrorCounterAndModel(@ModelAttribute("user") User user, @ModelAttribute("uvp") PasswordValidator uvp, Model model, int errorCounter) {
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
        return Pair.of(errorCounter, model);
    }

    private  Pair<Integer, Model> sensorErrorCounterAndModel(@ModelAttribute("sensor") Sensor sensor, Model model, Long id) {
        int errors = 0;

        List<Sensor> sensors = sRepo.findAll();
        for (Sensor value : sensors) {
            if (!value.getId().equals(id) && (value.getLocation().equalsIgnoreCase(sensor.getLocation()))) {

                model.addAttribute("locationError", "Location is already in use");
                errors++;
            }
        }
        return Pair.of(errors, model);
    }

    private void updatePassword(User user, String encoded){
        user.setPassword(encoded);
        uRepo.save(user);
        String subject = "Your Dumpster Sensor Password has been updated";
        String message = "Your password has been updated.\nPlease contact an admin if this was not you.\n\nThank You,\nDoomer Engineers";
        sendEmail(user.getEmail(),subject, message);
    }

    private void serviceErrorCheck(Sensor sensor){

        LocalDateTime now = LocalDateTime.now().minusDays(1);
        LocalDateTime checkDate = LocalDateTime.parse(sensor.getLastUpdated(),DATE_TIME_FORMATTER);
        if (!now.isBefore(checkDate)){
            Alert alert = new Alert();
            alert.setError("Sensor has not been serviced in the last hour");
            alert.setSensorID(sensor.getId());
            aRepo.save(alert);
        }

    }

    //Processes GET and POST requests
    //Does UPDATE and PUT inside GETs and POSTs

    @GetMapping("/")
    public String defaultRequest(){
        return "redirect:/login";
    }

    // Returns homepage
    // Puts most recent updated sensor to model, up to 5.
    @GetMapping("/homepage")
    public String getHomepage(Model model) {
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);
        List<Sensor> sensors = sRepo.findAllOrderByLastUpdatedDesc();
        List<Sensor> checkSensors = sRepo.findAll();

        for (Sensor checkSensor : checkSensors) {
            if(checkSensor.getLastUpdated() != null && checkSensor.getInstalled().equals("true")) {
                serviceErrorCheck(checkSensor);
            }
        }

        if(sensors.size() < 5){
            model.addAttribute("sensors", sensors.subList(0, sensors.size()));
        }
        else{
            model.addAttribute("sensors", sensors.subList(0,5));
        }
        model.addAttribute("user", currentUser);
        return HOME;
    }

    // returns new user page
    @GetMapping("/addUser")
    public String getAddUser() {
        //restricts role access
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);
        if (currentUser.getRole().equals("general")){
            return HOME;
        }
        return "addUser";
    }

    //validates new user
    //redirect to homepage
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
        Pair<Integer, Model> pair = passwordErrorCounterAndModel(user, uvp, model, errorCounter);
        if (pair.getFirst() > 0){
            model.addAttribute(pair.getSecond());
            return "addUser";
        }

        //encodes password as a fixed hash function
        String tmpPass = user.getPassword();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        uRepo.save(user);
        String subject = "Your Dumpster Sensor for UI account has been created";
        String message = "An admin for this system has created your new account.\n\nYour username is: " + user.getUsername() + "\nYour password is: " + tmpPass + "\nPlease change your password as soon as you login.\nPlease also whitelist this email.\n\nThank You,\nDoomer Engineers";
        sendEmail(user.getEmail(),subject, message);
        return REDIRECT_HOME;
    }

    //get new otp page to change password
    //generates a random value not already in the otp table
    @GetMapping("/user/change_password")
    public String getOTP(@ModelAttribute("otp") OTP otp, Model model) {
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);

        Random rand = new Random();
        int upperbound = 10000000;
        int otpNum = 0;

        boolean check = false;
        while(!check){
            otpNum = rand.nextInt(upperbound);
            if (oRepo.findByOTP(otpNum) == null){
                check = true;
            }
        }

        LocalDateTime now = LocalDateTime.now().plusMinutes(5);

        OTP temp = oRepo.findByUserID(currentUser.getId());

        if (temp == null){
            OTP temp2 = new OTP(otpNum, now.format(DATE_TIME_FORMATTER), currentUser.getId());
            oRepo.save(temp2);
            String subject = "Here is your one time password";
            String message = "Your one time password expires in less then five minutes.\n\nIt is: " + otpNum;
            sendEmail(currentUser.getEmail(),subject,message);
        }
        else{
            temp.setOtp(otpNum);
            temp.setExpired(now.format(DATE_TIME_FORMATTER));
            oRepo.save(temp);
            String subject = "Here is your one time password";
            String message = "Your one time password expires in less then five minutes.\n\nIt is: " + otpNum;
            sendEmail(currentUser.getEmail(),subject,message);
        }

        model.addAttribute("otp",otp);
        return "otpPage";
    }

    //validates opt from user
    //returns change password page
    @PostMapping("/user/change_password/otp")
    public String checkOTP(@ModelAttribute("otp") OTP otp, Model model) {
        String username = getLoggedInUser();

        User currentUser = uRepo.findByUsername(username);
        LocalDateTime now = LocalDateTime.now();

        OTP currentUserOTP = oRepo.findByUserID(currentUser.getId());
        if (currentUserOTP ==  null || (currentUserOTP.getOtp() != otp.getOtp())){
            model.addAttribute("wrongOTP", "Your OTP was incorrect.");
            return "otpPage";
        }

        LocalDateTime checkDate = LocalDateTime.parse(currentUserOTP.getExpired(),DATE_TIME_FORMATTER);

        if(now.isAfter(checkDate)){
            model.addAttribute("expiredOTP", "Your OTP was expired.");
            return "otpPage";
        }

        return "redirect:/user/change_password/authenticated";
    }

    //returns change password page
    @GetMapping("/user/change_password/authenticated")
    public String getPasswordChange(Model model){
        String username = getLoggedInUser();
        User user = uRepo.findByUsername(username);
        user.setPassword("");
        model.addAttribute("user", user);
        return "updatePassword";
    }

    //validates the changed password
    //redirects to homepage once successful if not admin
    //if admin, returns user admin page
    @PostMapping("/user/change_password/authenticated/confirm")
    public String validatePasswordChange(@ModelAttribute("user") User user, @ModelAttribute("uvp") PasswordValidator uvp, Model model){
        Pair<Integer, Model> pair = passwordErrorCounterAndModel(user, uvp, model, 0);
        if (pair.getFirst() > 0){
            model.addAttribute(pair.getSecond());
            return "updatePassword";
        }

        //encodes password as a fixed hash function
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);
        if(user.getId().equals(currentUser.getId())){
            updatePassword(currentUser, encodedPassword);
            model.addAttribute("currentUser", currentUser);
            List<User> users = uRepo.findAll();
            model.addAttribute("users",users);
            return "updateUsersAdmin";
        }
        else{
            updatePassword(user, encodedPassword);
        }
        return REDIRECT_HOME;
    }


    //returns update users from admin role
    @GetMapping("/users/update")
    public String getUpdateUsers(Model model) {
        //restricts role access
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);
        if (currentUser.getRole().equals("general")){
            return HOME;
        }
        List<User> users = uRepo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "updateUsersAdmin";
    }

    //deletes a user
    //returns update user admin page
    @PostMapping(("/user/{id}/delete"))
    public String deleteUser(Model model, @PathVariable(value = "id") Long id){
        uRepo.deleteById(id);
        List<User> users = uRepo.findAll();
        model.addAttribute("users", users);
        String username = getLoggedInUser();
        User currentUser = uRepo.findByUsername(username);
        model.addAttribute("currentUser", currentUser);
        return "redirect:/users/update";
    }

    //allows an admin to reset a password
    //returns update password page
    @GetMapping(("/user/{id}/recover"))
    public String recoverPassword(Model model, @PathVariable(value = "id") Long id){
        User user = uRepo.findByID(id);
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("currentUser", getLoggedInUser());
        return "updatePassword";
    }

    //allows and admin to change pole and update email
    //returns update user page
    @GetMapping(("/user/{id}/update"))
    public String updateUser(Model model, @PathVariable(value = "id") Long id){
        User user = uRepo.findByID(id);
        model.addAttribute("user", user);
        return "updateUser";
    }

    //validates user changes
    //returns update user admin page
    @PostMapping(("/user/{id}/update"))
    public String validateUpdateUser(Model model, @PathVariable(value = "id") Long id, @ModelAttribute("user") User user){
        User userToUpdate = uRepo.findByID(id);
        if(!user.getEmail().endsWith("uiowa.edu")){
            //checks if email is an uiowa email address
            model.addAttribute("wrongEmail", user.getEmail());
            return "updateUser";
        }
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setRole(user.getRole());
        uRepo.save(userToUpdate);
        List<User> users = uRepo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", getLoggedInUser());
        return "redirect:/users/update";
    }

    //gets add sensor page
    @GetMapping("/addSensor")
    public String getAddSensor(){
        return "addSensor";
    }

    //validates new sensor
    @PostMapping("/addSensor")
    public String addSensor(@ModelAttribute("sensor") Sensor sensor, Model model){
        Pair<Integer,Model> pair = sensorErrorCounterAndModel(sensor, model,0L);
        if (pair.getFirst() > 0){
            model.addAttribute(pair.getSecond());
            return "addSensor";
        }
        String location = sensor.getLocation().toLowerCase();
        sensor.setLocation(location);
        LocalDateTime now = LocalDateTime.now();
        sensor.setLastUpdated(now.format(DATE_TIME_FORMATTER));
        sRepo.save(sensor);
        return REDIRECT_HOME;
    }

    //allows a user to view all sensors
    @GetMapping("/view/sensors")
    public String getAllSensor(Model model){
        List<Sensor> sensors = sRepo.findAll();
        model.addAttribute("sensors", sensors);
        return "viewSensors";
    }

    //allows a user to search for a sensor
    @GetMapping("/find/sensor")
    public String findSensor(@ModelAttribute("sensor") Sensor sensor, Model model){
        model.addAttribute("sensor", sensor);
        return "findSensor";
    }

    //validates sensor search
    @PostMapping("/find/sensor/submit")
    public String findSensorByLocation(@ModelAttribute("sensor") Sensor sensor, Model model){
        final String message = "Requested sensor is not found";
        Sensor temp;
        if(sensor.getLocation() != null){
            temp = sRepo.findByLocation(sensor.getLocation());
        }
        else{
            temp = sRepo.findByID(sensor.getId());
        }
        if (temp == null) {
            model.addAttribute("error", message);
            return "findSensor";
        }
        else{
            return "redirect:/sensor/update/" + temp.getId();
        }
    }

    @GetMapping("/sensor/update/{id}")
    public String getUpdateSensor(@PathVariable(value="id") Long id, Model model){
        Sensor sensor = sRepo.findByID(id);
        if (sensor.getLastUpdated() != null && sensor.getInstalled().equals("true")){
            serviceErrorCheck(sensor);
        }
        List<Garbage> garbageList = garbageRepo.findAllBySensorID(id);
        Map<String, Integer> graphData = new TreeMap<>();
        for (Garbage garbage : garbageList) {
            graphData.put(garbage.getTime(), garbage.getGarbageLevel());
        }
        model.addAttribute("chartData", graphData);
        model.addAttribute("sensor", sensor);
        return "getSensor";
    }

    @PostMapping("/sensor/update/{id}/uninstall")
    public String getUpdateSensorUninstall(@PathVariable(value="id") Long id){
        Sensor sensor = sRepo.findByID(id);
        sensor.setInstalled("false");
        LocalDateTime now = LocalDateTime.now();
        sensor.setLastUpdated(now.format(DATE_TIME_FORMATTER));
        sRepo.save(sensor);
        return "redirect:/sensor/update/" + id;
    }

    @PostMapping("/sensor/update/{id}/install")
    public String getUpdateSensorInstall(@PathVariable(value="id") Long id){
        Sensor sensor = sRepo.findByID(id);
        sensor.setInstalled("true");
        LocalDateTime now = LocalDateTime.now();
        sensor.setLastUpdated(now.format(DATE_TIME_FORMATTER));
        sRepo.save(sensor);
        return "redirect:/sensor/update/" + id;
    }

    @GetMapping("/sensor/update/{id}/edit")
    public String getUpdateSensorEdit(@PathVariable(value="id") Long id, Model model){
        Sensor sensor = sRepo.findByID(id);
        model.addAttribute("sensor", sensor);
        return "updateSensor";
    }

    //validates new sensor
    @PostMapping("/sensor/update/{id}/edit/confirm")
    public String editSensor(@PathVariable(value="id") Long id, @ModelAttribute("sensor") Sensor sensor, Model model){
        Sensor currentSensor = sRepo.findByID(id);
        Pair<Integer, Model> pair = sensorErrorCounterAndModel(sensor, model, id);
        if (pair.getFirst() > 0){
            model.addAttribute(pair.getSecond());
            return "updateSensor";
        }
        currentSensor.setLocation(sensor.getLocation().toLowerCase());
        currentSensor.setLastUpdated(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        sRepo.save(currentSensor);
        model.addAttribute("sensor", currentSensor);
        return "redirect:/sensor/update/" + id;
    }

    @PostMapping("/sensor/update/{id}/delete")
    public String deleteSensor(@PathVariable(value="id") Long id){
        sRepo.deleteById(id);
        return REDIRECT_HOME;
    }

    @GetMapping("/logs")
    public String getLogs(Model model){
        List<Alert> alerts = aRepo.findAll();
        String username = getLoggedInUser();
        User user = uRepo.findByUsername(username);
        model.addAttribute("alerts",alerts);
        model.addAttribute("user", user);
        return "logs";
    }

    @PostMapping("/log/{id}/archive")
    public String archiveLog(@PathVariable(value = "id") Long id, Model model) {
        Alert alert = aRepo.findByID(id);
        alert.setArchived(true);
        aRepo.save(alert);
        List<Alert> alerts = aRepo.findAll();
        model.addAttribute("alerts", alerts);
        return "redirect:/logs";
    }
}
