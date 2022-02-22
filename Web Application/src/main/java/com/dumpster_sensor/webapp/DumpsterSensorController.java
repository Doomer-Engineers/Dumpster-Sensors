package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.Alert;
import com.dumpster_sensor.webapp.models.OTP;
import com.dumpster_sensor.webapp.models.Sensor;
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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

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

    //to send email
    public void sendEmail(String email, String subject, String body){
        //Sender's email ID needs to be mentioned.
        String from = "doomerengineers@gmail.com";

        //Assuming you are sending email from through gmails smtp.
        String host = "smtp.gmail.com";

        //Get system properties.
        Properties properties = System.getProperties();

        //Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //Get the Session object.// and pass username and password.
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("doomerengineers@gmail.com", "Taco9876");

            }

        });

        //Used to debug SMTP issues.
        session.setDebug(true);

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

            System.out.println("sending...");
            //Send message.
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
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
        String tmpPass = user.getPassword();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        uRepo.save(user);
        String subject = "Your Dumpster Sensor for UI account has been created";
        String message = "An admin for this system has created your new account.\n\nYour username is: " + user.getUsername() + "\nYour password is: " + tmpPass + "\nPlease change your password as soon as you login.\n\nThank You,\nDoomer Engineers";
        sendEmail(user.getEmail(),subject, message);
        return "redirect:/homepage";
    }

    @GetMapping("/user/change_password")
    public String getOTP(@ModelAttribute("otp") OTP otp, Model model) {
        User currentUser = getLoggedInUser();

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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        OTP temp = oRepo.findByUserID(currentUser.getId());
        if (temp == null){
            OTP temp2 = new OTP(otpNum, now.format(dtf), currentUser.getId());
            oRepo.save(temp2);
            String subject = "Here is your one time password";
            String message = "Your one time password expires in less then five minutes.\n\nIt is: " + otpNum;
            sendEmail(currentUser.getEmail(),subject,message);
        }
        else{
            temp.setOtp(otpNum);
            temp.setExpired(now.format(dtf));
            oRepo.save(temp);
            String subject = "Here is your one time password";
            String message = "Your one time password expires in less then five minutes.\n\nIt is: " + otpNum;
            sendEmail(currentUser.getEmail(),subject,message);
        }

        model.addAttribute("otp",otp);
        return "otpPage";
    }

    @PostMapping("/user/change_password/otp")
    public String checkOTP(@ModelAttribute("otp") OTP otp, Model model) {
        User currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();

        OTP currentUserOTP = oRepo.findByUserID(currentUser.getId());
        if (currentUserOTP.getOtp() != otp.getOtp()){
            model.addAttribute("wrongOTP", "Your OTP was incorrect.");
            return "otpPage";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime checkDate = LocalDateTime.parse(currentUserOTP.getExpired(),dtf);

        if(now.isAfter(checkDate)){
            model.addAttribute("expiredOTP", "Your OTP was expired.");
            return "otpPage";
        }

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

    @PostMapping("/addSensor")
    public String addSensor(@ModelAttribute("sensor") Sensor sensor, Model model){
        int errors = 0;
        if (sensor.getTime1().equals(sensor.getTime2())){
            model.addAttribute("timeErrorE", "The two selected times cannot be equal");
            errors++;
        }

        List<Sensor> sensors =  sRepo.findAll();
        for (Sensor value : sensors) {
            if (value.getTime1().equals(sensor.getTime1()) || value.getTime2().equals(sensor.getTime1())) {
                model.addAttribute("timeError1", "Selected time is currently in use");
                errors++;
            }
            if (value.getTime1().equals(sensor.getTime2()) || value.getTime2().equals(sensor.getTime2())) {
                model.addAttribute("timeError2", "Selected time is currently in use");
                errors++;
            }
            if (value.getLocation().equals(sensor.getLocation())) {
                model.addAttribute("locationError", "Location is already in use");
                errors++;
            }
        }
        if (errors > 0){
            return "addSensor";
        }
        String location = sensor.getLocation().toLowerCase();
        sensor.setLocation(location);
        sRepo.save(sensor);
        return "redirect:/homepage";
    }


    @GetMapping("/logs")
    public String getLogs(Model model){
        List<Alert> alerts = aRepo.findAll();
        User user = getLoggedInUser();
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
        model.addAttribute("alerts",alerts);
        return "logs";
    }

    @GetMapping("/find/sensor")
    public String findSensor(@ModelAttribute("sensor") Sensor sensor, Model model){
        model.addAttribute("sensor", sensor);
        return "findSensor";
    }

    @PostMapping("/find/sensor/submit")
    public String findSensorByLocation(@ModelAttribute("sensor") Sensor sensor, Model model){
        final String message = "Requested poll is not found";
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
        model.addAttribute("sensor", sensor);
        return "getSensor";
    }




}
