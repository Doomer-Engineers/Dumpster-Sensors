WIFI_ESSID = "UI-DeviceNet"
WIFI_PASSWORD = "UI-DeviceNet"

HOST_NAME = "https://sheltered-citadel-31729.herokuapp.com"
PATH_NAME = "/database_connection.php"

SENSOR_ID = 1

MAX_DEPTH_CM = 78
DIST_PERCENT_CONVERSION_RATE = 100/(MAX_DEPTH_CM-20)
def DIST_PERCENT_CONVERSION(distance):
    percent = 100 + DIST_PERCENT_CONVERSION_RATE*(20-distance)
    return percent
NEAR_FULL_PERCENT = 80

DEEPSLEEP_TIME_MIN = 60*12

BATTERY_MULT = 1.64
