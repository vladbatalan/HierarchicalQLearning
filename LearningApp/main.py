import time

from apiCommands import *
from appApi import AppAPI

HOST = "127.0.0.1"
PORT = 4303


if __name__ == '__main__':

    api = AppAPI()
    api.start_main_loop(HOST, PORT)

    # configure
    config_data = "-lvl Level0 -g true -ctrl external -manual-step true"

    api.exec_command(ConfigureGameCommand(config_data))
    api.exec_command(StartGameCommand())
    api.exec_command(RestartGameCommand())

    time.sleep(5)

    # check the step
    for i in range(1000):

        if i == 30 or i == 200 or i == 400:
            api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_PRESSED))

        if i == 100 or i == 300:
            api.exec_command(PlayerActionCommand(ActionsEnum.LEFT_PRESSED))

        api.exec_command(StepFrameCommand())

        if i > 1:
            state = api.exec_command(GetStateCommand())
            if state is not None:
                print("Retreived state:", state)

    time.sleep(10)
    api.stop_main_loop()


