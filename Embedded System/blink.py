# Code that tests the esp32 by blinking a led
import time
from machine import Pin
led=Pin(22,Pin.OUT)

while True:
led.value(1)
time.sleep(1)
led.value(0)
time.sleep(1)