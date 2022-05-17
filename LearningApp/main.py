import socket
import time

from appApi import AppAPI

HOST = "127.0.0.1"
PORT = 4303


if __name__ == '__main__':

    api = AppAPI()

    # Start api
    api.start_main_loop(HOST, PORT)

    # Configure game
    api.configure_game("-lvl Level0 -g true")

    # Start game
    api.start_game()

    # Command reset in 10 seconds
    time.sleep(10)
    api.reset_game()

    # Command reset in 10 seconds
    time.sleep(10)
    api.reset_game()

    # Wait 20 seconds and disconnect
    time.sleep(20)
    api.stop_main_loop()