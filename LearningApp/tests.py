import time

from appApi import AppAPI
from qLearningUnit import QLearningUnit
from stateDeserializer import CustomDeserializer
from apiCommands import *
from manualWinStrategy import ManualWinStrategy_Level0

HOST = "127.0.0.1"
PORT = 4303


class TestUnit:
    class ApiShould:

        @staticmethod
        def complete_level_by_manual_commands():
            manual_win_strategy = ManualWinStrategy_Level0()

            manual_win_strategy.start_game(HOST, PORT)

            # Request dynamic state and check if level is complete
            api = manual_win_strategy.get_api()

            state = api.exec_command(RequestDynamicStateCommand())

            state_obj = CustomDeserializer.get_dynamic_level_state(state).complete

            manual_win_strategy.complete_game()
            assert state_obj is True

        @staticmethod
        def handle_graphic_level_and_recv_state():
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
                if i == 30:
                    api.exec_command(PlayerActionCommand(ActionsEnum.RIGHT_PRESSED))
                if level_state is not None and level_state.player_position == (14, 11) and released_time == -1:
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

                if level_state is not None and level_state.lost:
                    api.exec_command(RestartGameCommand(False))
                    released_time = 0

                api.exec_command(StepFrameCommand())

                if i > 1 and (i % 5) == 4:
                    state_string = api.exec_command(RequestDynamicStateCommand())
                    if state_string is not None:
                        level_state = CustomDeserializer.get_dynamic_level_state(state_string)
                        print("State:")
                        print(level_state)

            api.stop_main_loop()

        @staticmethod
        def get_initial_level_state(level_name='Level0'):
            api = AppAPI()
            api.start_main_loop(HOST, PORT)

            # configure
            config_data = "-lvl " + level_name + " -g true -ctrl external -manual-step true"

            api.exec_command(ConfigureGameCommand(config_data))
            api.exec_command(StartGameCommand())
            api.exec_command(RestartGameCommand())

            time.sleep(2)

            level_initial_state = api.exec_command(RequestStaticStateCommand())
            level_initial_state = CustomDeserializer.get_static_level_state(level_initial_state)
            print(level_initial_state)

            api.exec_command(StepFrameCommand())
            api.exec_command(StepFrameCommand())
            api.exec_command(StepFrameCommand())

            api.stop_main_loop()

    class FlatQLearningShould:

        @staticmethod
        def train_level(level_name, train_file_path, reward_type, frame_per_step=5, max_steps=200, num_episodes=1000,
                        alpha=0.1, gamma=0.95, graphics="false"):
            learning_unit = QLearningUnit(HOST, PORT)

            learning_unit.init_environment(
                '-lvl ' + level_name + ' -g ' + graphics + ' -ctrl external -manual-step true -reward ' + reward_type)

            learning_unit.train(frame_per_step=frame_per_step, max_steps=max_steps, num_episodes=num_episodes,
                                alpha=alpha, gamma=gamma)

            learning_unit.save_model(train_file_path)

            learning_unit.close_env()

        @staticmethod
        def perform_with_model(level_name, model_file_path, reward_type='PromoteAllStatesActive', frame_per_step=3,
                               max_steps=200, gamma=0.95):
            learning_unit = QLearningUnit(HOST, PORT)

            learning_unit.init_environment(
                '-lvl ' + level_name + ' -g true -ctrl external -manual-step true -reward ' + reward_type)

            learning_unit.perform_with_model(model_file_path, frame_per_step=frame_per_step, time_delay=0.001,
                                             gamma=gamma, max_steps=max_steps)

            learning_unit.close_env()
