import urequests
import constants
station = 0


def sendQuery(query):
    url = constants.HOST_NAME + constants.PATH_NAME + query
    response = urequests.get(url)
    print(response.text)

def garbageQuery(garbage_lvl):
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&garbage_level="+str(garbage_lvl)
    sendQuery(query)

def alertQuery(error):
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&error='"+error+"'"
    sendQuery(query)
