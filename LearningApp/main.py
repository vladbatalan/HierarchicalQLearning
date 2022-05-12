import socket
import time

HOST = "127.0.0.1"
PORT = 4303


if __name__ == '__main__':
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))

        s.send(b"Hello server!\r\n")
        data = s.recv(1024)
        print("Received:", data)

        s.send(b"StartGame\r\n")
        data = s.recv(1024)
        print("Received:", data)

        time.sleep(5)
        s.send(b"Still here\r\n")
        data = s.recv(1024)
        print("Received:", data)

        time.sleep(10)
        s.send(b".\r\n")
        data = s.recv(1024)
        print("Received:", data)

    print("Client completed job!")