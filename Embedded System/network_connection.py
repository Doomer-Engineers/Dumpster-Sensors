import network
station = network.WLAN(network.STA_IF)
station.active(True)
station.connect("CupO'Joe!", "Wh053Ba1ls?!")
print(station.isconnected())
print(station.ifconfig())