import constants
from machine import Pin, ADC
from machine import deepsleep
from hcsr04 import HCSR04
import database_connection as dbc
import time
import network


def run():
    relay_pin = led=Pin(21,Pin.OUT)
    relay_pin.value(0)

    sensor_1 = HCSR04(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)
    sensor_2 = HCSR04(trigger_pin=19, echo_pin=18, echo_timeout_us=115200)


        # If the maintenance pin is down
    time.sleep(4)
    if (station.active() and dbc.getSensorQuery()[1] == 'true'):
        # Turns sensors on for input
        relay_pin.value(1)
        time.sleep(0.5)
        # Reads in distance in cm from each
        dist_1 = sensor_1.distance_cm()
        dist_2 = sensor_2.distance_cm()
#         print("dist_1 cm=",dist_1)
#         print("dist_2 cm=",dist_2)
    #         dist_2 = sensor_2.distance_cm()
        # If an negative value is given, the sensors are disconnected
        if (dist_1 < 0 or dist_2 < 0):
#             print("Sensor error 1")
            dbc.alertQuery("SE")
        else:
            # Calculates what percentage of each sid eis full
            percent_1 = ((constants.MAX_DEPTH_CM - dist_1) / constants.MAX_DEPTH_CM) * 100
            percent_2 = ((constants.MAX_DEPTH_CM - dist_2) / constants.MAX_DEPTH_CM) * 100
            # Averages percentages
            avg = (percent_1 + percent_2) / 2
            dbc.garbageQuery(avg)
#             print("%=",avg)
            # If the average level is high enough, send a full alert
            if (avg > 100):
                dbc.alertQuery("F")
#                 print("Full")
            elif(avg > constants.NEAR_FULL_PERCENT):
                dbc.alertQuery("NF")
#                 print("NearFull")
            elif(percent_1 < 0 or percent_2 < 0):
#                 print("SensorError2")
                dbc.alertQuery("SE")
#             else:
#                 print("Not full")
        # Turns sensors off
        relay_pin.value(0)

        """ TODO: Battery level code """
        battery_pin = ADC(Pin(34))
        battery_pin.atten(ADC.ATTN_11DB)       #Full range: 3.3v
        volts = round((battery_pin.read()*constants.BATTERY_MULT)/1000,2)
#         print("volts=",volts)
        dbc.powerQuery(volts)
        if(volts < 3):
            dbc.alertQuery("LP")

        # Puts code into deepsleep
        #deepsleep(60000 * constants.DEEPSLEEP_TIME_MIN)
        deepsleep(5000)

station = network.WLAN(network.STA_IF)
print(station.active(True))
station.connect(constants.WIFI_ESSID, constants.WIFI_PASSWORD)

maintenance_mode_pin = Pin(35, Pin.IN)

if (not maintenance_mode_pin.value()):
#     print("running main loop")
    run()
# else:
#     print("No main loop")