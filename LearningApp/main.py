import socket
import time

from apiCommands import ActionsEnum
from appApi import AppAPI

HOST = "127.0.0.1"
PORT = 4303


class CommandAtDelayTime:
    def __init__(self, api):
        self.api = api

    def command(self, secs, command):
        time.sleep(secs)
        self.api.player_action(command)


if __name__ == '__main__':
    api = AppAPI()
    delayed = CommandAtDelayTime(api)

    # Start api
    api.start_main_loop(HOST, PORT)

    # Configure game
    api.configure_game("-lvl Level0 -g true -ctrl external")

    # Start game
    api.start_game()
    api.reset_game()

    # First instance: Target = open door
    to_gate_time = 1.4
    wait_gate_time = 16

    delayed.command(2, ActionsEnum.RIGHT_PRESSED)
    delayed.command(to_gate_time, ActionsEnum.RIGHT_RELEASED)
    # Target = wait enough time
    delayed.command(wait_gate_time, ActionsEnum.LEFT_PRESSED)
    # Target = return to gate
    delayed.command(to_gate_time, ActionsEnum.LEFT_RELEASED)
    # Target = Teleport
    delayed.command(0.3, ActionsEnum.SPACE_RELEASED)

    # Second instance: Target = get to goal lever
    to_cave_time = 3.1
    delayed.command(2.5, ActionsEnum.RIGHT_PRESSED)

    delayed.command(to_cave_time, ActionsEnum.LEFT_PRESSED)
    delayed.command(0.7, ActionsEnum.RIGHT_PRESSED)
    delayed.command(0.7, ActionsEnum.LEFT_PRESSED)
    delayed.command(1.2, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    # Target = Wait gate to open
    delayed.command(0.5, ActionsEnum.LEFT_RELEASED)

    # Target = Back to time machine
    delayed.command(0.3, ActionsEnum.RIGHT_PRESSED)
    delayed.command(0.3, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    delayed.command(0.8, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    delayed.command(0.3, ActionsEnum.LEFT_PRESSED)
    delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    delayed.command(1, ActionsEnum.RIGHT_PRESSED)
    delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    delayed.command(1, ActionsEnum.LEFT_PRESSED)
    delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
    delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

    delayed.command(1.73, ActionsEnum.LEFT_RELEASED)
    # Target = time teleport
    delayed.command(0.3, ActionsEnum.SPACE_RELEASED)

    # Third instance: Target = Go to goal
    delayed.command(2, ActionsEnum.RIGHT_PRESSED)
    delayed.command(0.6, ActionsEnum.RIGHT_RELEASED)
    # Target = Wait signal and win game
    delayed.command(7, ActionsEnum.SPACE_RELEASED)
    delayed.command(0.6, ActionsEnum.SPACE_RELEASED)
    delayed.command(0.6, ActionsEnum.SPACE_RELEASED)
    delayed.command(0.6, ActionsEnum.SPACE_RELEASED)


    time.sleep(3)
    api.stop_main_loop()
