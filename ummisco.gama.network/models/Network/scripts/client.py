import socket

HOST = "localhost"  # The server's hostname or IP address
PORT = 3001  # The port used by the server

if __name__ == '__main__':
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))
        while True:
            s.sendall(b"Hey, I'm the client\n")
            data = s.recv(1024)
            print(f"Received {data!r}")
