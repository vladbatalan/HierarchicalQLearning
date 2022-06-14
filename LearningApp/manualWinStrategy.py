import time

from apiCommands import ActionsEnum
from appApi import AppAPI


class CommandAtDelayTime:
    def __init__(self, api):
        self.api = api

    def command(self, secs, command):
        time.sleep(secs)
        self.api.player_action(command)


class ManualWinStrategy_Level0:
    def __init__(self):
        self._api = AppAPI()
        self._delayed = CommandAtDelayTime(self._api)

    def get_api(self):
        return self._api

    def start_game(self, HOST, PORT):
        # Start api
        self._api.start_main_loop(HOST, PORT)

        # Configure game
        self._api.configure_game("-lvl Level0 -g true -ctrl external")

        # Start game
        self._api.start_game()
        self._api.reset_game()

        # First instance: Target = open door
        to_gate_time = 1.4
        wait_gate_time = 16

        self._delayed.command(2, ActionsEnum.RIGHT_PRESSED)
        self._delayed.command(to_gate_time, ActionsEnum.RIGHT_RELEASED)
        # Target = wait enough time
        self._delayed.command(wait_gate_time, ActionsEnum.LEFT_PRESSED)
        # Target = return to gate
        self._delayed.command(to_gate_time, ActionsEnum.LEFT_RELEASED)
        # Target = Teleport
        self._delayed.command(0.3, ActionsEnum.SPACE_RELEASED)

        # Second instance: Target = get to goal lever
        to_cave_time = 3.1
        self._delayed.command(2.5, ActionsEnum.RIGHT_PRESSED)

        self._delayed.command(to_cave_time, ActionsEnum.LEFT_PRESSED)
        self._delayed.command(0.7, ActionsEnum.RIGHT_PRESSED)
        self._delayed.command(0.7, ActionsEnum.LEFT_PRESSED)
        self._delayed.command(1.2, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        # Target = Wait gate to open
        self._delayed.command(0.5, ActionsEnum.LEFT_RELEASED)

        # Target = Back to time machine
        self._delayed.command(0.3, ActionsEnum.RIGHT_PRESSED)
        self._delayed.command(0.3, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        self._delayed.command(0.8, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        self._delayed.command(0.3, ActionsEnum.LEFT_PRESSED)
        self._delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        self._delayed.command(1, ActionsEnum.RIGHT_PRESSED)
        self._delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        self._delayed.command(1, ActionsEnum.LEFT_PRESSED)
        self._delayed.command(0.0, ActionsEnum.JUMP_PRESSED)
        self._delayed.command(0.7, ActionsEnum.JUMP_RELEASED)

        self._delayed.command(1.73, ActionsEnum.LEFT_RELEASED)
        # Target = time teleport
        self._delayed.command(0.3, ActionsEnum.SPACE_RELEASED)

        # Third instance: Target = Go to goal
        self._delayed.command(2, ActionsEnum.RIGHT_PRESSED)
        self._delayed.command(0.6, ActionsEnum.RIGHT_RELEASED)
        # Target = Wait signal and win game
        self._delayed.command(7, ActionsEnum.SPACE_RELEASED)
        self._delayed.command(0.6, ActionsEnum.SPACE_RELEASED)
        self._delayed.command(0.6, ActionsEnum.SPACE_RELEASED)
        self._delayed.command(0.6, ActionsEnum.SPACE_RELEASED)

    def complete_game(self):
        time.sleep(3)
        self._api.stop_main_loop()
