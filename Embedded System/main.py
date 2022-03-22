from sensor import Sensor
import database_conection as dbc

sensor_1 = Sensor(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)
sensor_2 = Sensor(trigger_pin=15, echo_pin=14, echo_timeout_us=115200)

def run():
    dist_1 = sensor_1.distance_cm(self)
    dist_2 = sensor_2.distance_cm(self)

    avg = (dist_1 + dist_2) / 2

    dbc.garbageQuery(avg)