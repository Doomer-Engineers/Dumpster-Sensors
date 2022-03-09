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
else{
    echo "Data is not set in the HTTP request";
}
?>