import time

from apiCommands import *
from appApi import AppAPI
from stateDeserializer import CustomDeserializer

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

    time.sleep(2)

    level_state = None
    released_time = -1
    fixed_time = 100
    delta_time = 60

    # check the step
    for i in range(1000):
        time.sleep(0.001)
        if i == 30:
            api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_PRESSED))
        if level_state is not None and level_state.player_pos == (14, 11) and released_time == -1:
            api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_RELEASED))
            released_time = 0
        if released_time != -1:
            released_time += 1
        if released_time == fixed_time:
            api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_PRESSED))
        if released_time == fixed_time + 1 * delta_time:
            api.exec_command(PlayerActionCommand(ActionsEnum.LEFT_PRESSED))
        if released_time == fixed_time + 2 * delta_time:
            api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_PRESSED))
        if released_time == fixed_time + 3 * delta_time:
            api.exec_command(PlayerActionCommand(ActionsEnum.LEFT_PRESSED))

        if level_state is not None and level_state.is_level_lost:
            api.exec_command(RestartGameCommand(False))
            released_time = 0

        api.exec_command(StepFrameCommand())

        if i > 1 and (i % 5) == 4:
            state_string = api.exec_command(GetStateCommand())
            if state_string is not None:
                level_state = CustomDeserializer.get_level_state(state_string)
                print("State:")
                print(level_state)

    api.stop_main_loop()
