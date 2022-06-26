from enum import Enum
from typing import List


class ActionsEnum(Enum):
    RIGHT_PRESSED = "RIGHT_PRESSED"
    RIGHT_RELEASED = "RIGHT_RELEASED"
    LEFT_PRESSED = "LEFT_PRESSED"
    LEFT_RELEASED = "LEFT_RELEASED"
    JUMP_PRESSED = "JUMP_PRESSED"
    JUMP_RELEASED = "JUMP_RELEASED"
    SPACE_RELEASED = "SPACE_RELEASED"
    NO_ACTION = "NO_ACTION"

    @staticmethod
    def get_list() -> []:
        return list(map(str, ActionsEnum))


class HyperActionsEnum(Enum):
    SPACE_RELEASED = "SPACE_RELEASED"
    STAND_ACTION = "STAND"  # Left_released + Right_released
    LEFT_PRESSED = "LEFT_PRESSED"
    RIGHT_PRESSED = "RIGHT_PRESSED"
    JUMP_PRESSED = "JUMP_PRESSED"
    JUMP_RELEASED = "JUMP_RELEASED"
    NO_ACTION = "NO_ACTION"

    @staticmethod
    def get_list() -> []:
        return list(map(str, HyperActionsEnum))


class SendCommand:
    def __init__(self, command=None, receives=False):
        self.command = command
        self.receives = receives

    def send_command(self) -> []:
        return [bytes(self.command + '\r\n', 'UTF-8')]

    def manage_received(self, received):
        pass


class PlayerSmartCommand(SendCommand):
    def __init__(self, command=None):
        super().__init__(command)

    def send_command(self) -> []:
        addition = "Player command: "
        if self.command == "STAND_ACTION":
            return [bytes(addition + str(ActionsEnum.RIGHT_RELEASED.value) + '\r\n', 'UTF-8'),
                    bytes(addition + str(ActionsEnum.LEFT_RELEASED.value) + '\r\n', 'UTF-8')]
        else:
            return [bytes(addition + str(self.command) + '\r\n', 'UTF-8')]


class ConfigureGameCommand(SendCommand):
    def __init__(self, config_data):
        super().__init__("Configure " + config_data, False)


class StartGameCommand(SendCommand):
    def __init__(self):
        super().__init__("StartGame", False)


class RestartGameCommand(SendCommand):
    def __init__(self, full_restart=True):
        super().__init__(receives=False)
        if full_restart is True:
            self.command = "RestartLevel"
        else:
            self.command = "SubRestartLevel"


class RequestDynamicStateCommand(SendCommand):
    def __init__(self):
        super().__init__("RequestLevelState", True)

    def manage_received(self, received):
        pass
        # print('From RequestLevelState:', received)


class RequestStaticStateCommand(SendCommand):
    def __init__(self):
        super().__init__("RequestInitialState", True)

    def manage_received(self, received):
        pass
        # print('From RequestLevelState:', received)


class PlayerActionCommand(SendCommand):
    def __init__(self, player_comm):
        super().__init__("Player command: " + str(player_comm.name), False)

    # def manage_received(self, received):
    #     print('From Player command:', received)


class StepFrameCommand(SendCommand):
    def __init__(self):
        super().__init__("FrameStep")

    def manage_received(self, received):
        print('From FrameStep:', received)


class StopCommand(SendCommand):
    def __init__(self):
        super().__init__(".", True)

    def manage_received(self, received):
        print('From StopCommand:', received)
