<?php
$servername = "207.191.214.21:3306";
$username = "any";
$password = "password";
$database_name = "dumpster_sensors";

if(isset($_GET["garbage_level"]) and
    isset($_GET["sensor_id"]))
{
    $garbage_level = $_GET["garbage_level"];
    $sensor_id = $_GET["sensor_id"];

    // Create MySQL connection fom PHP to MySQL server
    $connection = new mysqli($servername, $username, $password, $database_name);
    // Check connection
    if ($connection->connect_error) {
        die("MySQL connection failed: " . $connection->connect_error);
    }

    $sql = "INSERT INTO garbage (garbage_level, sensorid, time) VALUES ($garbage_level, $sensor_id, now())";

    if ($connection->query($sql) === TRUE) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . " => " . $connection->error;
    }

    $connection->close();
}
elseif(isset($_GET["error"]) and
    isset($_GET["sensor_id"]))
{
    $error = $_GET["error"];
    $sensor_id = $_GET["sensor_id"];

    // Create MySQL connection fom PHP to MySQL server
    $connection = new mysqli($servername, $username, $password, $database_name);
    // Check connection
    if ($connection->connect_error) {
        die("MySQL connection failed: " . $connection->connect_error);
    }

    $sql = "INSERT INTO alert (archived, error, sensorid) VALUES (0, $error, $sensor_id)";

    if ($connection->query($sql) === TRUE) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . " => " . $connection->error;
    }

    $connection->close();
}
elseif(isset($_GET["sensor_id"]) and
    isset($_GET["read_sensor"]))
{
    $read_sensor = $_GET["read_sensor"];
    $sensor_id = $_GET["sensor_id"];

    // Create MySQL connection fom PHP to MySQL server
    $connection = new mysqli($servername, $username, $password, $database_name);
    // Check connection
    if ($connection->connect_error) {
        die("MySQL connection failed: " . $connection->connect_error);
    }

    $sql = "SELECT * FROM sensor WHERE id=$sensor_id";
    $result = $connection->query($sql);

    if ($result->num_rows > 0) {
        // output data of each row
        while($row = $result->fetch_assoc()) {
            // Returns in order
            // id, installed, power, time1, time2
            echo $row["id"]. "," . $row["installed"]. "," . $row["power"]. "," . $row["time1"]. "," . $row["time2"];
        }
    } else {
        echo "0 results, false";
    }

}
elseif(isset($_GET["sensor_id"]) and
    isset($_GET["power"]))
{
    $power = $_GET["power"];
    $sensor_id = $_GET["sensor_id"];

    // Create MySQL connection fom PHP to MySQL server
    $connection = new mysqli($servername, $username, $password, $database_name);
    // Check connection
    if ($connection->connect_error) {
        die("MySQL connection failed: " . $connection->connect_error);
    }

    $sql = "UPDATE sensor SET power=$power WHERE id=$sensor_id";

    if ($connection->query($sql) === TRUE) {
        echo "Record updated successfully";
    } else {
        echo "Error: " . $sql . " => " . $connection->error;
    }
}
else{
    echo "Data is not set in the HTTP request";
}
?>