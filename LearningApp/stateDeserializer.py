import xml.etree.ElementTree as ET


class LevelStateObject:
    def __init__(self):
        self.player_pos = None
        self.obj_pos = None
        self.frame = None
        self.obj_active = None
        self.is_level_running = None

    def __str__(self):
        return "" +\
            "Player: \t" + str(self.player_pos) + "\n" +\
            "Objective: \t" + str(self.obj_pos) + ", active: " + str(self.obj_active) + "\n" +\
            "Level: \tframe: " + str(self.frame) + ", running: " + str(self.is_level_running) + "\n"


class CustomDeserializer:

    @staticmethod
    def get_level_state(level_state_str: str):
        level_state = LevelStateObject()

        my_root = ET.fromstring(level_state_str)

        player_pos = my_root.find('Player').find('Position').attrib
        level_state.player_pos = (int(player_pos['x']), int(player_pos['y']))

        obj_data = my_root.find('Objective')
        obj_pos = obj_data.find('Position').attrib
        level_state.obj_pos = (int(obj_pos['x']), int(obj_pos['y']))
        obj_active = obj_data.find('Active').text
        level_state.obj_active = (obj_active == 'true')

        level_data = my_root.find('LevelState')
        running = level_data.find('Running').text
        level_state.is_level_running = (running == 'true')
        level_state.frame = int(level_data.find('Frame').text)

        return level_state
