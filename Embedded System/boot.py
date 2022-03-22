# This file is executed on every boot (including wake-boot from deepsleep)
#import esp
#esp.osdebug(None)
#import webrepl
#webrepl.start()
import network
import constants
import main

station = network.WLAN(network.STA_IF)
station.active(True)
station.connect(constants.WIFI_ESSID, constants.WIFI_PASSWORD)

main.run()