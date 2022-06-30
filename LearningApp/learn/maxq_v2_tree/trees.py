import abc

import numpy as np

from api.apiCommands import HyperActionsEnum
from api.stateDeserializer import DynamicLevelState, StaticLevelState


class MaxQTree(abc.ABC):
    def __init__(self):
        self.root = None
        self.graph = []
        self.action_names = []
        self.primitive_actions = None
        self.nav_offset = None
        self.env_actions = None
        self.action_space_size = None
        self.repetitive_counter = {}
        self.max_count = {}

        self.lever_positions = None
        self.time_machine_position = None
        self.goal_position = None
        self.all_positions = None

        self.static_state = None

    @abc.abstractmethod
    def build_tree(self, static_state: StaticLevelState):
        pass

    @abc.abstractmethod
    def is_terminal(self, action, done, state: DynamicLevelState, last_action=-1):
        pass

    # There are 3 types of composed actions:
    # 1) Simple actions - subtasks will run in any order, until the termination is complete
    # 2) Ordered actions - each subtask will execute only once in order
    # 3) Repetitive actions - subtask will be repeated self.repetitive_counter[action] times
    @abc.abstractmethod
    def action_type(self, action) -> int:
        pass

    @abc.abstractmethod
    def pseudo_reward(self, action, state: DynamicLevelState) -> float:
        pass

    def reset(self):
        for action in range(self.action_space_size):
            if self.action_type(action) == 3:
                self.repetitive_counter[action] = 0

    def is_primitive(self, action):
        return action < self.primitive_actions


class NoLeverTree(MaxQTree):
    def __init__(self):
        super(NoLeverTree, self).__init__()
        self.max_goal = None
        self.nav_to_goal = None
        self.nav_to_time_machine = None
        self.space_for_goal = None
        self.jump_release = None
        self.jump_press = None
        self.stand = None
        self.right = None
        self.left = None

    def _create_nodes(self):
        # Primitive actions
        self.left = 0
        self.right = 1
        self.jump_press = 2
        self.jump_release = 3
        self.space_for_goal = 4

        self.env_actions = [
            HyperActionsEnum.LEFT_PRESSED.name,
            HyperActionsEnum.RIGHT_PRESSED.name,
            HyperActionsEnum.JUMP_PRESSED.name,
            HyperActionsEnum.JUMP_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name
        ]

        self.primitive_actions = self.nav_offset = 5

        # The navigation actions
        self.nav_offset = self.primitive_actions
        self.nav_to_goal = self.nav_offset

        self.max_goal = self.nav_to_goal + 1
        self.root = self.max_goal + 1

        # Number of actions
        self.action_space_size = self.root + 1

    def _create_action_names(self):
        self.action_names = [
            "MOVE_LEFT",
            "MOVE_RIGHT",
            "JUMP_PRESS",
            "JUMP_RELEASE",
            "SPACE_FOR_GOAL",
            "NavTo" + str(self.goal_position),
            "MaxGoal",
            "MaxRoot"
        ]

    def _create_graph(self):
        self.graph = []

        # For each primitive action, add set to graph
        for primitive in range(self.primitive_actions):
            self.graph.append(set())

        # Append directions for navigate_to_goal
        self.graph.append({self.left, self.right, self.jump_press, self.jump_release})

        # Max goal
        self.graph.append([self.nav_to_goal, self.space_for_goal])

        # For root
        self.graph.append({self.max_goal})

        # Print tree
        print("Created tree:")
        for action in range(self.action_space_size):
            print(self.action_names[action], ":", [self.action_names[child] for child in self.graph[action]])
        print()

    def build_tree(self, static_state: StaticLevelState):
        self.goal_position = static_state.game_objective_position
        self.all_positions = [self.goal_position]
        self.static_state = static_state

        self._create_nodes()
        self._create_action_names()
        self._create_graph()

    def is_terminal(self, action, done, state: DynamicLevelState, last_action=-1):
        player_position = (int(state.player_position[0]), int(state.player_position[1]))

        # If game is over, return true
        if done:
            return True

        # If it is root, continue until game done
        elif action == self.root:
            return done

        # It is a navigation action
        elif self.nav_offset <= action < self.nav_offset + len(self.all_positions):
            target_position = self.all_positions[action - self.nav_offset]
            succeed = (player_position[0] == target_position[0] and player_position[1] == target_position[1])
            if succeed:
                print("Reached destination for:", self.action_names[action])
                print()
            return succeed

        # Action is completed only when goal was reached and space was pressed and goal is active
        elif action == self.max_goal:
            is_positioned_correctly = (player_position[0] == self.goal_position[0] and
                                       player_position[1] == self.goal_position[1])
            return is_positioned_correctly and last_action == self.space_for_goal and state.objective_active

        # Primitive actions and immediately
        elif self.is_primitive(action):
            return True

    def action_type(self, action) -> int:

        # # Ordered actions
        if action == self.max_goal:
            return 2

        # Others are simple actions here
        return 1

    def pseudo_reward(self, action, state: DynamicLevelState) -> float:
        player_position = (int(state.player_position[0]), int(state.player_position[1]))

        # It is a navigation action
        if self.nav_offset <= action < self.nav_offset + len(self.all_positions):
            target_position = self.all_positions[action - self.nav_offset]
            if player_position[0] == target_position[0] and player_position[1] == target_position[1]:
                return 10
            return -0.5

        # If action is a space action
        if action == self.space_for_goal:
            # Check if the goal is active
            if state.objective_active and player_position[0] == self.goal_position[0] and player_position[1] == \
                    self.goal_position[1]:
                return 10
            return -10

        # Don t need pseudo reward for others
        return 0


class AdvancedTree(MaxQTree):

    def __init__(self):
        super(AdvancedTree, self).__init__()
        self.max_access_point = None
        self._max_repeats = None
        self._repetitive_counter = None
        self.repetitive_stand = None
        self.max_goal = None
        self.max_time_travel = None
        self.nav_to_goal = None
        self.nav_to_time_machine = None
        self.nav_to_levers = None
        self.stand_for_access = None
        self.space_for_goal = None
        self.space_for_teleport = None
        self.jump_release = None
        self.jump_press = None
        self.stand = None
        self.right = None
        self.left = None

    def _create_nodes(self):

        # Primitives
        self.left = 0
        self.right = 1
        self.jump_press = 2
        self.jump_release = 3
        self.space_for_teleport = 4
        self.space_for_goal = 5
        self.stand_for_access = 6

        self.env_actions = [
            HyperActionsEnum.LEFT_PRESSED.name,
            HyperActionsEnum.RIGHT_PRESSED.name,
            HyperActionsEnum.JUMP_PRESSED.name,
            HyperActionsEnum.JUMP_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.STAND_ACTION.name
        ]

        self.primitive_actions = self.nav_offset = 7

        # Navigation
        self.nav_to_levers = [self.nav_offset + i for i, value in enumerate(self.lever_positions)]
        self.nav_to_time_machine = self.nav_offset + len(self.static_state.lever_positions)
        self.nav_to_goal = self.nav_to_time_machine + 1

        # Composed actions
        self.max_time_travel = self.nav_to_goal + 1
        self.max_goal = self.max_time_travel + 1
        self.repetitive_stand = self.max_goal + 1
        self.choose_one_dest = self.repetitive_stand + 1

        # Repetitive parameters
        #  that count how many times it was accessed
        self.repetitive_counter[self.repetitive_stand] = 0
        self.max_count[self.repetitive_stand] = 50

        self.repetitive_counter[self.choose_one_dest] = 0
        self.max_count[self.choose_one_dest] = 1

        self.max_access_point = self.choose_one_dest + 1
        self.root = self.max_access_point + 1

        # Root action
        self.action_space_size = self.root + 1

    def _create_action_names(self):

        self.action_names = [
            "MOVE_LEFT",
            "MOVE_RIGHT",
            "STAND",
            "JUMP_PRESS",
            "JUMP_RELEASE",
            "SPACE_FOR_TELEPORT",
            "SPACE_FOR_GOAL",
            "STAND_FOR_ACCESS",
        ]

        for i in range(self.nav_offset, self.nav_to_goal + 1):
            self.action_names.append("NavTo" + str(self.all_positions[i - self.nav_offset]))

        self.action_names += [
            "MaxTimeTravel",
            "MaxGoal",
            "RepetitiveStand",
            "ChooseOneDest",
            "MaxAccessPoint",
            "MaxRoot"
        ]

    def _create_graph(self):

        self.graph = []

        # Primitive actions
        for primitive in range(self.primitive_actions):
            self.graph.append(set())

        # For each nav action add movement
        for nav_to_lever in self.nav_to_levers:
            self.graph.append({self.left, self.right, self.stand, self.jump_press, self.jump_release})

        # To time machine
        self.graph.append({self.left, self.right, self.stand, self.jump_press, self.jump_release})

        # To goal
        self.graph.append({self.left, self.right, self.stand, self.jump_press, self.jump_release})

        # Max time travel - Ordered
        self.graph.append([self.nav_to_time_machine, self.space_for_teleport])

        # Max goal - Ordered
        self.graph.append([self.nav_to_goal, self.space_for_goal])

        # Repetitive stand
        self.graph.append({self.stand_for_access})

        # Choose one destination out of all lever points
        all_access_moves = set()
        for element in self.nav_to_levers:
            all_access_moves.add(element)
        self.graph.append(all_access_moves)

        # Max access point - Ordered
        self.graph.append([self.choose_one_dest, self.repetitive_stand, self.max_time_travel])

        # For root
        self.graph.append({self.max_access_point, self.max_goal})

        # Print tree
        print("Created tree:")
        for action in range(self.action_space_size):
            print(self.action_names[action], ":", [self.action_names[child] for child in self.graph[action]])
        print()

    def build_tree(self, static_state: StaticLevelState):

        self.lever_positions = static_state.lever_positions
        self.time_machine_position = static_state.time_machine_position
        self.goal_position = static_state.game_objective_position
        self.all_positions = self.lever_positions + [self.time_machine_position, self.goal_position]

        self._create_nodes()
        self._create_action_names()
        self._create_graph()

    def is_terminal(self, action, done, state: DynamicLevelState, last_action=-1):
        player_position = (int(state.player_position[0]), int(state.player_position[1]))
        action_type = self.action_type(action)

        # If game is over, return true
        if done:
            return True

        # If it is root, continue until game done
        elif action == self.root:
            return done

        # It is a navigation action
        elif self.nav_offset <= action < self.nav_offset + len(self.all_positions):
            target_position = self.all_positions[action - self.nav_offset]

            succeed = (player_position[0] == target_position[0] and player_position[1] == target_position[1])
            if succeed:
                print("Reached destination for:", self.action_names[action])
                print()
            return succeed

        # Repetitive actions
        elif action_type == 3:
            if self.repetitive_counter[action] >= self.max_count[action]:
                self.repetitive_counter[action] = 0
                return True
            return False

        # Ordered actions end after the last action
        elif action_type == 2:
            return last_action == self.graph[action][-1]

        # Primitive actions
        elif self.is_primitive(action):
            return True

    def action_type(self, action) -> int:
        if action in [self.repetitive_stand, self.choose_one_dest]:
            return 3

        if action in [self.max_access_point, self.max_goal, self.max_time_travel]:
            return 2

        return 1

    def pseudo_reward(self, action, state: DynamicLevelState) -> float:
        player_position = (int(state.player_position[0]), int(state.player_position[1]))

        # It is a navigation action
        if self.nav_offset <= action < self.nav_offset + len(self.all_positions):
            target_position = self.all_positions[action - self.nav_offset]
            if player_position[0] == target_position[0] and player_position[1] == target_position[1]:
                return 10
            return -0.1 * np.sqrt((state.player_position[0] - target_position[0]) ** 2 + (
                        state.player_position[1] - target_position[1]) ** 2)

        # If action is a space action
        if action == self.space_for_goal:
            # Check if the goal is active
            if state.objective_active and player_position[0] == self.goal_position[0] and player_position[1] == \
                    self.goal_position[1]:
                return 10
            return -10

        # TODO: Add for choose one dest

        # Don t need pseudo reward for others
        return 0
