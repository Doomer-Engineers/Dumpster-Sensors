from sensor import Sensor
from time import sleep

# ESP32
sensor = Sensor(trigger_pin=17, echo_pin=16, echo_timeout_us=115200)

# ESP8266
#sensor = HCSR04(trigger_pin=12, echo_pin=14, echo_timeout_us=10000)

while True:
    distance = sensor.distance_cm()
    print('Distance:', distance, 'cm')
    sleep(1)
