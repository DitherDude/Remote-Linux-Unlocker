#!/usr/bin/env python
# -*- coding: utf-8 -*-
import socket, sys, json, subprocess, os

def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except (ValueError, e):
        return False
    return True

def get_user_from_key(key):
    with open(os.path.dirname(os.path.realpath(__file__)) + '/keys') as file:
        for line in file:
            line = line.strip().split(' ')
            if line[1] == key:
                return line[0]
                break
    return ""

def authenticate_key(key):
    # with open(os.path.dirname(os.path.realpath(__file__)) + '/keys') as file:
    #     for line in file:
    #         if line.strip().split(' ')[1] == key:
    #             return True
    #             break
    # return False
    return get_user_from_key(key) != ""

def is_locked(key):
    # users = [i.split(':') for i in open('/etc/shadow').readlines()]
    # user = [i[0] for i in users if i[1] not in ('!', '*')][0]
    user = get_user_from_key(key)
    if user == "":
        return False
    #commands = 'su ' + user + ' -c -- "gdbus call -e -d com.canonical.Unity -o /com/canonical/Unity/Session -m com.canonical.Unity.Session.IsLocked"'
    commands = 'su ' + user + ' -c -- "dbus-send --session --dest=org.freedesktop.ScreenSaver --type=method_call --print-reply /org/freedesktop/ScreenSaver org.freedesktop.ScreenSaver.GetActive"'
    p = subprocess.Popen(commands,stdout=subprocess.PIPE, shell=True)
    res = str(p.communicate())
    if "true" in res:
        return True
    else:
        # print(users)
        print(user + "> " + res)
        return False
    return False

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

# Bind the socket to the port
server_address = ('', 61599)
eprint('starting up on %s port %s' % server_address)
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    eprint('waiting for a connection')
    connection, client_address = sock.accept()

    try:
        eprint('connection from', client_address)

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(256).strip()
            eprint('received "%s"' % data)
            if is_json(data):
                data = json.loads(data)
                if data["command"] == "lock" and data["key"] and authenticate_key(data["key"]):
                    eprint('client requesting lock')
                    subprocess.call(["loginctl", "lock-sessions"])
                    connection.sendall(b'{"status":"success"')
                    break
                elif data["command"] == "unlock" and data["key"] and authenticate_key(data["key"]):
                    eprint('client requesting unlock')
                    subprocess.call(["loginctl", "unlock-sessions"])
                    connection.sendall(b'{"status":"success"')
                    break
                elif data["command"] == "status" and data["key"] and authenticate_key(data["key"]):
                    eprint('client requesting echo')
                    response = '{"status":"success","hostname":"' + socket.gethostname() +  '","isLocked":"' + str(is_locked(data["key"])) + '"}'
                    eprint( response)
                    connection.sendall(response.encode("utf-8"))
                    break

            else:
                eprint('no more data from', client_address)
                break

    finally:
        # Clean up the connection
        connection.close()
