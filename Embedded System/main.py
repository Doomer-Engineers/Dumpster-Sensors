import constants
from machine import Pin, ADC
from machine import deepsleep
from hcsr04 import HCSR04
import database_connection as dbc
import time
import network

#main loop of device
def run():
    #relay pin
    relay_pin = led=Pin(21,Pin.OUT)
    relay_pin.value(0)

    # Define sensors
    sensor_1 = HCSR04(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)
    sensor_2 = HCSR04(trigger_pin=19, echo_pin=18, echo_timeout_us=115200)

    time.sleep(4)
    try:
        # if connected to wifi
        if (station.active()):
            # Turns sensors on for input
            relay_pin.value(1)
            time.sleep(0.5)
            # Reads in distance in cm from each
            dist_1 = sensor_1.distance_cm()
            dist_2 = sensor_2.distance_cm()
            # If an negative value is given, the sensors are disconnected
            if (dist_1 < 0 or dist_2 < 0):
                dbc.alertQuery("SE")
            else:
                # Calculates what percentage of each sid eis full
                percent_1 = constants.DIST_PERCENT_CONVERSION(dist_1)
                percent_2 = constants.DIST_PERCENT_CONVERSION(dist_2)
                # Averages percentages
                avg = (percent_1 + percent_2) / 2
                # If the average level is high enough, send a full alert
                if (avg > 100):
                    dbc.alertQuery("F")
                    dbc.garbageQuery(100)
                elif(avg > constants.NEAR_FULL_PERCENT):
                    dbc.alertQuery("NF")
                    dbc.garbageQuery(avg)
                # If an error in reading occurs, it means it's close enough to be full
                elif(percent_1 < 0 or percent_2 < 0):
                    dbc.garbageQuery(100)
                    dbc.alertQuery("F")
                else:
                    dbc.garbageQuery(avg)
            # Turns sensors off
            relay_pin.value(0)

            # Battery level code
            # Reads in
            battery_pin = ADC(Pin(34))
            battery_pin.atten(ADC.ATTN_11DB)       #Full range: 3.3v
            volts = round((battery_pin.read()*constants.BATTERY_MULT)/1000,2)
            # update power level depending on voltage
            if(volts < 3):
                dbc.alertQuery("LP")
                dbc.powerQuery("LOW")
                #print("LOW")
            elif(volts < 3.3):
                dbc.powerQuery("MID")
                #print("MID")
            else:
                dbc.powerQuery("FULL")
    # Puts code into deepsleep
    finally:
        deepsleep(60000 * constants.DEEPSLEEP_TIME_MIN)
# Connects to wifi
station = network.WLAN(network.STA_IF)
station.active(True)
station.connect(constants.WIFI_ESSID, constants.WIFI_PASSWORD)
#If the maintenance_mode_pin pin is not on, run code
maintenance_mode_pin = Pin(35, Pin.IN)
if (not maintenance_mode_pin.value()):
    run()