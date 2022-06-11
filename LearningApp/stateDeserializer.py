import xml.etree.ElementTree as ET


class LevelStateObject:
    def __init__(self):
        self.player_pos = None
        self.obj_pos = None
        self.frame = None
        self.obj_active = None
        self.is_level_running = None
        self.is_level_complete = None
        self.is_level_lost = None

    def __str__(self):
        return "" +\
            "Player: \t" + str(self.player_pos) + "\n" +\
            "Objective:\n" +\
               "\t" + str(self.obj_pos) + \
               "\n\tactive: " + str(self.obj_active) + "\n" +\
            "Level:\n" + \
               "\tframe: " + str(self.frame) + \
               "\n\trunning: " + str(self.is_level_running) + \
               "\n\tcomplete: " + str(self.is_level_complete) + \
               "\n\tlost: " + str(self.is_level_lost) + "\n"


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
        complete = level_data.find('Complete').text
        lost = level_data.find('Lost').text
        level_state.is_level_running = (running == 'true')
        level_state.is_level_complete = (complete == 'true')
        level_state.is_level_lost = (lost == 'true')
        level_state.frame = int(level_data.find('Frame').text)

        return level_state
