class ActionsEnum:
    RIGHT_PRESSED = "RIGHT_PRESSED"
    RIGHT_RELEASED = "RIGHT_RELEASED"
    LEFT_PRESSED = "LEFT_PRESSED"
    LEFT_RELEASED = "LEFT_RELEASED"
    JUMP_PRESSED = "JUMP_PRESSED"
    JUMP_RELEASED = "JUMP_RELEASED"
    SPACE_RELEASED = "SPACE_RELEASED"
    NO_ACTION = ""


class SendCommand:

    def __init__(self, command=None, receives=False):
        self.command = command
        self.receives = receives

    def send_command(self) -> bytes:
        return bytes(self.command + '\r\n', 'UTF-8')

    def manage_received(self, received):
        pass


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


class PlayerActionCommand(SendCommand):
    def __init__(self, player_comm):
        super().__init__("Player command: " + player_comm, True)

    def manage_received(self, received):
        print(str(received)[2:-5])


class StepFrameCommand(SendCommand):
    def __init__(self):
        super().__init__("FrameStep")


class StopCommand(SendCommand):
    def __init__(self):
        super().__init__(".", True)

    def manage_received(self, received):
        print(str(received))

