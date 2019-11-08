import time
import sys
import os
import stomp
import ssl
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("user")
parser.add_argument("password")
parser.add_argument("host")
parser.add_argument("port")
parser.add_argument("file",help="Path to trigger file with name")
parser.add_argument("type",help='queue/topic')
parser.add_argument("name",help='destination name')

args = parser.parse_args()

print(args.host)

user = args.user
password = args.password
host = args.host
port = args.port
triggerFile = args.file
destination = args.name
destype = args.type

f = open(triggerFile,'r')

data = f.read()

print(data)

conn = stomp.Connection(host_and_ports = [(host, port)])
conn.set_ssl(for_hosts=[(host, port)], ssl_version=ssl.PROTOCOL_TLS)
conn.start()
conn.connect(login=user,passcode=password)
print("Connected...")

print(data)  
#conn.subscribe(destination='/queue/testP', id=1, ack='auto')

conn.send(body=data, destination='/'+destype+'/'+destination)


conn.disconnect()