package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AlertService {
    @Autowired
    JdbcTemplate template;

    public List<Alert> findAll(){
        String sql = "select * from alert";
        RowMapper<Alert> rm = new RowMapper<Alert>() {
            @Override
            public Alert mapRow(ResultSet resultSet, int i) throws SQLException {
                Alert alerts = new Alert(resultSet.getLong("id"),
                        resultSet.getString("error"),
                        resultSet.getLong("sensorid"),
                        resultSet.getBoolean("archived"));
                return alerts;
            }
        };
        return template.query(sql, rm);
    }
}


