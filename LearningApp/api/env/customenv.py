import time

from api.appApi import AppAPI
from api.apiCommands import *
from api.stateDeserializer import CustomDeserializer, DynamicLevelState


class CustomEnv:
    def __init__(self):
        self.static_state = None
        self.action_space = [value for value in HyperActionsEnum.get_list()]

        self.api = AppAPI()

    def start_env(self, host, port, config_options=None):

        self.api.start_main_loop(host, port)

        # Config the game, create config options
        config_string = ""
        if type(config_options) is dict:
            count = 0
            for option in config_options:
                value = config_options[option]
                config_string += option + " " + value

                if count < len(config_options) - 1:
                    config_string += " "

                count += 1

        elif type(config_options) is str:
            config_string += config_options

        self.api.exec_command(ConfigureGameCommand(config_string))
        self.api.exec_command(StartGameCommand())

        # Some frames must be skipped
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())

        # Receive the initial data about de environment to evaluate states
        static_state = self.api.exec_command(RequestStaticStateCommand())
        self.static_state = CustomDeserializer.get_static_level_state(static_state)

    def _take_action(self, action):
        # Perform command
        self.api.exec_command(PlayerSmartCommand(action))

    def _next_observation(self) -> DynamicLevelState:
        return CustomDeserializer.get_dynamic_level_state(self.api.exec_command(RequestDynamicStateCommand()))

    def step(self, action, frames_per_step=4, time_delay=0) -> (DynamicLevelState, float, bool):

        self._take_action(action)

        # Skip frames
        for i in range(frames_per_step):
            time.sleep(time_delay)
            self.api.exec_command(StepFrameCommand())

        # Gather current state
        observation = self._next_observation()

        # Extract reward
        reward = observation.reward

        # Extract done condition
        done = observation.lost or observation.complete

        return observation, reward, done

    def reset(self):
        self.api.exec_command(RestartGameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())

        return self._next_observation()

    def close_env(self):
        self.api.stop_main_loop()

