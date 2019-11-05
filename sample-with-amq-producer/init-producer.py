import time
import sys
import os
import stomp
import ssl

user = sys.argv[1]
password = sys.argv[2]
host = sys.argv[3]
port = sys.argv[4]
#destination =  ["/topic/event"]
#destination = destination[0]

print("Host is ",host)
# messages = 10000
data = "Hello World from Python"

conn = stomp.Connection(host_and_ports = [(host, port)])
conn.set_ssl(for_hosts=[(host, port)], ssl_version=ssl.PROTOCOL_TLS)
conn.start()
conn.connect(login=user,passcode=password)
print("Connected...")

print(data)  
#conn.subscribe(destination='/queue/testP', id=1, ack='auto')

conn.send(body='Mithun hello python', destination='/queue/testP')


conn.disconnect()