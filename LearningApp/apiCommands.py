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
    NO_ACTION = ""

    @staticmethod
    def get_list() -> []:
        return list(map(str, ActionsEnum))


class SendCommand:
    def __init__(self, command=None, receives=False):
        self.command = command
        self.receives = receives

    def send_command(self) -> bytes:
        return bytes(self.command + '\r\n', 'UTF-8')

    def manage_received(self, received):
        pass


class BulkOfCommands:
    def __init__(self, command_list: List[SendCommand] = []):
        self._command_list = command_list

        if len(self._command_list) == 0:
            self._command_list.append(BulkBeginCommand())
            self._command_list.append(BulkEndCommand())

        self.builder = self.BulkOfCommandsBuilder()

    def add_command(self, command):
        last_position = len(self._command_list) - 1

        if self._command_list[last_position] is BulkEndCommand:
            self._command_list.append(self._command_list[last_position])
            self._command_list[last_position] = command

        else:
            self._command_list.append(command)
            self._command_list.append(BulkEndCommand())

    def get_commands(self):
        return self._command_list

    class BulkOfCommandsBuilder:
        def __init__(self):
            self._command_list: List[SendCommand] = []
            self._command_list.append(BulkBeginCommand())

        def add_command(self, command):
            self._command_list.append(command)
            return self

        def build(self):
            self._command_list.append(BulkEndCommand())
            return BulkOfCommands(self._command_list)


class BulkBeginCommand(SendCommand):
    def __init__(self):
        super().__init__("BulkBegin")


class BulkEndCommand(SendCommand):
    def __init__(self):
        super().__init__("BulkEnd")

    def manage_received(self, received):
        print("Bulk ended")


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
        super().__init__("Player command: " + player_comm, False)

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
