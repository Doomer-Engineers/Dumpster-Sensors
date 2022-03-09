<?php
if(isset($_GET["garbage_level"])) {
    $garbage_level = $_GET["garbage_level"];

    $servername = "207.191.214.21:3306";
    $username = "any";
    $password = "root";
    $database_name = "dumpster_sensors";

    // Create MySQL connection fom PHP to MySQL server
    $connection = new mysqli($servername, $username, $password, $database_name);
    // Check connection
    if ($connection->connect_error) {
        die("MySQL connection failed: " . $connection->connect_error);
    }

    $sql = "INSERT INTO garbage (garbage_level, sensorid, time) VALUES ($garbage_level, 69, now())";

    if ($connection->query($sql) === TRUE) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . " => " . $connection->error;
    }

    $connection->close();
}
else{
    echo "garbage_level is not set in the HTTP request";
}
?>