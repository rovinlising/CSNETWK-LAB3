# -*- coding: utf-8 -*-
"""Multithreaded HTTP web server: one thread per request/response pair."""

from socket import *
import sys
import threading

serverPort = 6789


def handle_client(connectionSocket, addr):
  """Serve a single HTTP request on its own connection socket."""
  try:
    message = connectionSocket.recv(1024).decode()
    filename = message.split()[1]
    with open(filename[1:]) as f:
      outputdata = f.read()

    # HTTP header
    connectionSocket.send("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n".encode())

    # File content
    connectionSocket.sendall(outputdata.encode())
    connectionSocket.send("\r\n".encode())

  except (IOError, IndexError):
    # File not found (or malformed/empty request)
    try:
      connectionSocket.send("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n".encode())
      connectionSocket.send("<html><head></head><body><h1>404 Not Found</h1></body></html>\r\n".encode())
    except OSError:
      pass
  finally:
    connectionSocket.close()


serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
serverSocket.bind(('', serverPort))
serverSocket.listen(5)

try:
  while True:
    print('Ready to serve...')
    # Main thread only listens; accept() returns a new socket for this client
    connectionSocket, addr = serverSocket.accept()
    t = threading.Thread(target=handle_client, args=(connectionSocket, addr), daemon=True)
    t.start()
except KeyboardInterrupt:
  print('\nShutting down.')
finally:
  serverSocket.close()
  sys.exit()
