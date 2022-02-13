package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    @Autowired
    JdbcTemplate template;

    public List<User> findAll(){
        String sql = "select * from user";
        RowMapper<User> rm = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User users = new User(resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("role"),
                        resultSet.getString("role"));
                return users;
            }
        };
        return template.query(sql, rm);
    }
}
