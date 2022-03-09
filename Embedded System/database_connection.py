try:
    import usocket as socket
except:
    import socket
import constants

def yeet(use_stream=False):
    s = socket.socket()

    ai = socket.getaddrinfo(constants.HOST_NAME, 80)
    print("Address infos:", ai)
    addr = ai[0][-1]

    print("Connect address:", addr)
    s.connect(addr)

    if use_stream:
        # MicroPython socket objects support stream (aka file) interface
        # directly, but the line below is needed for CPython.
        s = s.makefile("rwb", 0)
        s.write(b"GET"+constants.PATH_NAME+"HTTP/1.0\r\n\r\n")
        print(s.read())
    else:
        s.send(b"GET "+constants.PATH_NAME+" HTTP/1.0\r\n\r\n")
        print(s.recv(4096))

    s.close()
    
yeet()
