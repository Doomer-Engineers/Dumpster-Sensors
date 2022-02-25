package com.dumpster_sensor.webapp.queries;

import com.dumpster_sensor.webapp.models.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SensorService {
    @Autowired
    JdbcTemplate template;

    public List<Sensor> findAll(){
        String sql = "select * from sensor";
        RowMapper<Sensor> rm = new RowMapper<Sensor>() {
            @Override
            public Sensor mapRow(ResultSet resultSet, int i) throws SQLException {
                Sensor sensors = new Sensor(resultSet.getLong("id"),
                        resultSet.getString("installed"),
                        resultSet.getString("time1"),
                        resultSet.getString("time2"),
                        resultSet.getString("location"),
                        resultSet.getString("power"),
                        resultSet.getString("last_updated"));
                return sensors;
            }
        };
        return template.query(sql, rm);
    }

}

