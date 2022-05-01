import urequests
import constants


def sendQuery(query):
    url = constants.HOST_NAME + constants.PATH_NAME + query
    response = urequests.get(url)
    return response.text

def garbageQuery(garbage_lvl):
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&garbage_level="+str(garbage_lvl)
    sendQuery(query)

def alertQuery(error):
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&error='"+error+"'"
    sendQuery(query)

def getSensorQuery():
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&read_sensor=1"
    return sendQuery(query).split(',')

def powerQuery(power):
    query = "?sensor_id="+str(constants.SENSOR_ID)+"&power='"+power+"'"
    sendQuery(query)