import time

from apiCommands import ActionsEnum
from appApi import AppAPI

HOST = "127.0.0.1"
PORT = 4303


if __name__ == '__main__':

    api = AppAPI()
    api.start_main_loop(HOST, PORT)

    # configure
    api.configure_game("-lvl Level0 -g true -ctrl external -manual-step true")

    # start game
    api.start_game()
    api.reset_game()

    # check the step
    for i in range(1000):
        time.sleep(0.5)
        api.step_frame()

        if i == 500:
            api.player_action(ActionsEnum.RIGHT_PRESSED)

        if i == 800:
            api.player_action(ActionsEnum.LEFT_PRESSED)




