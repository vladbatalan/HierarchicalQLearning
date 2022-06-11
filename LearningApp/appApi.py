import multiprocessing
import socket

from apiCommands import *


class AppAPI:
    def __init__(self):
        self.main_process = None
        self.is_loop_on = False
        self.request_queue = multiprocessing.Queue()
        self.recv_queue = multiprocessing.Queue()
        pass

    def start_main_loop(self, host, port):
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.connect((host, port))

        self.is_loop_on = True
        self.main_process = multiprocessing.Process(target=self.main_loop, args=(server_socket,))
        self.main_process.start()

    def main_loop(self, server_socket):
        while self.is_loop_on is True:

            # Check if the request Queue is Empty
            if not self.request_queue.empty():

                # Consume the command from top
                command = self.request_queue.get()

                if command.command == "." or command is None:
                    self.is_loop_on = False
                    print("Connection was lost due to: Disconnect")
                    break
                try:
                    # Send command message
                    server_socket.send(command.send_command())

                except Exception as e:
                    # The connection was lost
                    self.is_loop_on = False
                    print("Connection was lost due to:", str(e))
                    return

                # If there is any message expected
                if command.receives is True:
                    received = None
                    try:
                        received = server_socket.recv(4096).decode()
                        self.recv_queue.put(received)
                        command.manage_received(received)

                    except Exception as e:
                        print("Received problem!")
                        print(e)
                        return

        server_socket.close()

    def bulk_package(self, bulk_command):
        self.request_queue.put(bulk_command)

    def exec_command(self, command):
        self.request_queue.put(command)

        if command.receives is True:

            # Wait for an answer to be returned
            while self.recv_queue.empty():
                pass

            return self.recv_queue.get()

        return None

    def configure_game(self, config_data):
        command = ConfigureGameCommand(config_data)
        self.request_queue.put(command)

    def start_game(self):
        command = StartGameCommand()
        self.request_queue.put(command)

    def player_action(self, action):
        command = PlayerActionCommand(action)
        self.request_queue.put(command)

    def stop_main_loop(self):
        command = StopCommand()
        self.request_queue.put(command)

    def reset_game(self, full_reset=True):
        command = RestartGameCommand(full_reset)
        self.request_queue.put(command)

    def step_frame(self):
        command = StepFrameCommand()
        self.request_queue.put(command)
