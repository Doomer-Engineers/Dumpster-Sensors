import constants
from machine import Pin
from machine import deepsleep
from sensor import Sensor
import database_connection as dbc

relay_pin = led=Pin(??,Pin.OUT)
relay_pin.value(0)

maintenance_mode_pin = machine.Pin(??, machine.Pin.IN)

sensor_1 = Sensor(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)
sensor_2 = Sensor(trigger_pin=15, echo_pin=14, echo_timeout_us=115200)

def run():
    # If the maintenance pin is down
    if (maintenance_mode_pin.value() == 0):
        # Turns sensors on for input
        relay_pin.value(1)
        # Reads in distance in cm from each
        dist_1 = sensor_1.distance_cm(self)
        dist_2 = sensor_2.distance_cm(self)
        # If an negative value is given, the sensors are disconnected
        if (dist_1 < 0 or dist_2 < 0):
            dbc.alertQuery("Disconnected sensor")
        else:
            # Calculates what percentage of each sid eis full
            percent_1 = (dist_1 / constants.MAX_DEPTH_CM) * 100
            percent_2 = (dist_2 / constants.MAX_DEPTH_CM) * 100
            # Averages percentages
            avg = (percent_1 + percent_2) / 2
            # writes level to database
            dbc.garbageQuery(avg)
            # If the average level is high enough, send a full alert
            if (avg > 100):
                dbc.alertQuery("Full")
            elif(avg > constants.NEAR_FULL_PERCENT):
                dbc.alertQuery("Near full")
        # Turns sensors off
        relay_pin.value(0)

        """ TODO: Battery level code """

        # Puts code into deepsleep
        deepsleep(60000 * constants.DEEPSLEEP_TIME_MIN)

def