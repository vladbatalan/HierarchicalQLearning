import xml.etree.ElementTree as ET


class DynamicLevelState:
    def __init__(self):
        self.player_position = None
        self.level_states = {}
        self.still_states = {}

    @property
    def running(self):
        return self.level_states['Running']

    @property
    def frame(self):
        return self.level_states['Frame']

    @property
    def complete(self):
        return self.level_states['Complete']

    @property
    def lost(self):
        return self.level_states['Lost']

    def __str__(self):
        return "" + \
               "Player: " + str(self.player_position) + "\n" + \
               "Level: \n\t" + str(self.level_states) + "\n" + \
               "Active: \n\t" + str(self.still_states) + "\n"


class StaticLevelState:
    def __init__(self):
        self.map = []
        self.positions = {}

    def __str__(self):
        ret_str = "Map:\n"
        for row in self.map:
            ret_str += "\t" + str(row) + "\n"

        ret_str += "Initial positions:\n"

        for key in self.positions.keys():
            ret_str += key + ":\n"
            for position in self.positions[key]:
                ret_str += "\t" + str(position) + "\n"

        return ret_str


class CustomDeserializer:

    @staticmethod
    def get_dynamic_level_state(level_state_str: str) -> DynamicLevelState:
        level_state = DynamicLevelState()

        my_root = ET.fromstring(level_state_str)

        # Get player position
        player_pos = my_root.find('Player').find('Position').attrib
        level_state.player_position = (int(player_pos['x']), int(player_pos['y']))

        # Get active states
        states = my_root.find('States')

        # Foreach objectType
        for object_type in list(states):
            level_state.still_states[object_type.tag] = []

            for active_state in list(object_type):
                active_bool = (active_state.text == "true")
                level_state.still_states[object_type.tag].append(active_bool)

        # Get level state flags
        level_flags = my_root.find('LevelState')
        for flag in list(level_flags):
            text = flag.text
            flag_value = None

            if text in ["true", "false"]:
                flag_value = (text == "true")
            else:
                flag_value = int(text)

            level_state.level_states[flag.tag] = flag_value

        return level_state

    @staticmethod
    def get_static_level_state(level_state_str: str) -> StaticLevelState:
        static_level_state = StaticLevelState()

        my_root = ET.fromstring(level_state_str)

        # Collect the map.
        map_object = my_root.find("Map")
        for row in list(map_object):
            row_array = [int(element) for element in row.text.split(" ")]
            static_level_state.map.append(row_array)

        # Collect the object positions
        positions_object = my_root.find("Positions")
        for object_type in list(positions_object):
            static_level_state.positions[object_type.tag] = []

            for position in list(object_type):
                value = (int(position.attrib['x']), int(position.attrib['y']))
                static_level_state.positions[object_type.tag].append(value)

        return static_level_state
