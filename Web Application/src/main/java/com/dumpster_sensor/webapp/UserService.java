package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.User;
import com.dumpster_sensor.webapp.queries.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo uRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = uRepo.findByUsername(s);
        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserDetail(user);
        }
}
