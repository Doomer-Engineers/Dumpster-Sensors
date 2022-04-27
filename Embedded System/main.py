import constants
from machine import Pin
from machine import deepsleep
from hcsr04 import HCSR04
import database_connection as dbc
import time
import network

station = network.WLAN(network.STA_IF)
print(station.active(True))
station.connect(constants.WIFI_ESSID, constants.WIFI_PASSWORD)

def run():


    relay_pin = led=Pin(21,Pin.OUT)
    relay_pin.value(0)

    sensor_1 = HCSR04(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)
    # sensor_2 = HCSR04(trigger_pin=19, echo_pin=18, echo_timeout_us=115200)


        # If the maintenance pin is down

    if (dbc.getSensorQuery()[1] == 'true'):
        # Turns sensors on for input
        relay_pin.value(1)
        time.sleep(0.5)
        # Reads in distance in cm from each
        dist_1 = sensor_1.distance_cm()
        print("cm=",dist_1)
    #         dist_2 = sensor_2.distance_cm()
        # If an negative value is given, the sensors are disconnected
    #         if (dist_1 < 0 or dist_2 < 0):
        if (dist_1 < 0 or dist_1):
            print("Sensor error")
            dbc.alertQuery("Sensor error")
        else:
            # Calculates what percentage of each sid eis full
            percent_1 = ((constants.MAX_DEPTH_CM - dist_1) / constants.MAX_DEPTH_CM) * 100
    #             percent_2 = (dist_2 / constants.MAX_DEPTH_CM) * 100
    #             # Averages percentages
    #             avg = (percent_1 + percent_2) / 2
    #             # writes level to database
    #             dbc.garbageQuery(avg)
            dbc.garbageQuery(percent_1)
            print("%=",percent_1)
            # If the average level is high enough, send a full alert
            if (percent_1 > 100):
                dbc.alertQuery("Full")
                print("Full")
            elif(percent_1 > constants.NEAR_FULL_PERCENT):
                dbc.alertQuery("Near full")
                print("Near full")
            elif(percent_1 < 0):
                print("Sensor error")
                dbc.alertQuery("Sensor error")
            else:
                print("Not full")
        # Turns sensors off
        relay_pin.value(0)

        """ TODO: Battery level code """

        # Puts code into deepsleep
        deepsleep(60000 * constants.DEEPSLEEP_TIME_MIN)

maintenance_mode_pin = Pin(34, Pin.IN)

print(maintenance_mode_pin.value())

if (not maintenance_mode_pin.value()):
    print("running main loop")
    run()
else:
    print("No main loop")

