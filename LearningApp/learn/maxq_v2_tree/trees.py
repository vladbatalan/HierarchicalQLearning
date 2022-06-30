import abc

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

    def build_tree(self, static_state: StaticLevelState):
        self.time_machine_position = static_state.time_machine_position
        self.goal_position = static_state.game_objective_position
        self.all_positions = [self.goal_position]
        self.static_state = static_state

        # Primitive actions
        move_left = self.left = 0
        move_right = self.right = 1
        # stand = self.stand = 2
        jump_press = self.jump_press = 2
        jump_release = self.jump_release = 3
        space_for_goal = self.space_for_goal = 4

        self.primitive_actions = 5

        self.graph = []
        # For each primitive action, add set to graph
        for primitive in range(self.primitive_actions):
            self.graph.append(set())

        self.env_actions = [
            HyperActionsEnum.LEFT_PRESSED.name,
            HyperActionsEnum.RIGHT_PRESSED.name,
            # HyperActionsEnum.STAND_ACTION.name,
            HyperActionsEnum.JUMP_PRESSED.name,
            HyperActionsEnum.JUMP_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name
        ]
        self.action_names = [
            "MOVE_LEFT",
            "MOVE_RIGHT",
            # "STAND",
            "JUMP_PRESS",
            "JUMP_RELEASE",
            "SPACE_FOR_GOAL",
            "NavTo" + str(self.goal_position),
            "MaxGoal",
            "MaxRoot"
        ]

        # The navigation actions
        self.nav_offset = self.primitive_actions
        nav_to_goal = self.nav_to_goal = self.nav_offset

        max_goal = self.max_goal = nav_to_goal + 1
        root = self.root = max_goal + 1

        # Number of actions
        self.action_space_size = root + 1

        # Append directions for navigate_to_goal
        # self.graph.append({move_left, move_right, stand, jump_press, jump_release})
        self.graph.append({move_left, move_right, jump_press, jump_release})

        # Max goal
        self.graph.append([nav_to_goal, space_for_goal])

        # For root
        self.graph.append({max_goal})

        # Print tree
        print("Created tree:")
        for action in range(self.action_space_size):
            print(self.action_names[action], ":", [self.action_names[child] for child in self.graph[action]])
        print()

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
            # print("Check destination for:", self.action_names[action])
            # print("\tPlayer position:", player_position)
            # print("\tTarget position:", target_position)
            # print()
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
        self.stand_for_access = None
        self.space_for_goal = None
        self.space_for_teleport = None
        self.jump_release = None
        self.jump_press = None
        self.stand = None
        self.right = None
        self.left = None

    def build_tree(self, static_state: StaticLevelState):
        self.lever_positions = static_state.lever_positions
        self.time_machine_position = static_state.time_machine_position
        self.goal_position = static_state.game_objective_position
        self.all_positions = self.lever_positions + [self.time_machine_position, self.goal_position]

        self.action_names = []
        self.env_actions = []
        # Primitive actions
        # for destination in self.all_positions:

        move_left = self.left = 0
        move_right = self.right = 1
        stand = self.stand = 2
        jump_press = self.jump_press = 3
        jump_release = self.jump_release = 4
        space_for_teleport = self.space_for_teleport = 5
        space_for_goal = self.space_for_goal = 6
        stand_for_access = self.stand_for_access = 7

        self.primitive_actions = 8
        self.env_actions = [
            HyperActionsEnum.LEFT_PRESSED.name,
            HyperActionsEnum.RIGHT_PRESSED.name,
            HyperActionsEnum.STAND_ACTION.name,
            HyperActionsEnum.JUMP_PRESSED.name,
            HyperActionsEnum.JUMP_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name
        ]

        self.graph = []
        # For each primitive action, add set to graph
        for primitive in range(self.primitive_actions):
            self.graph.append(set())

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

        # The navigation actions
        self.nav_offset = 8
        nav_to_levers = self.nav_to_levers = [self.nav_offset + i for i, value in
                                              enumerate(self.lever_positions)]
        nav_to_time_machine = self.nav_to_time_machine = self.nav_offset + len(self.static_state.lever_positions)
        nav_to_goal = self.nav_to_goal = nav_to_time_machine + 1

        for i in range(self.nav_offset, nav_to_goal + 1):
            self.action_names.append("NavTo" + str(self.all_positions[i - self.nav_offset]))

        self.action_names += [
            "MaxTimeTravel",
            "MaxGoal",
            "RepetitiveStand",
            "MaxAccessPoint",
            "MaxRoot"
        ]

        # Composed actions
        max_time_travel = self.max_time_travel = nav_to_goal + 1
        max_goal = self.max_goal = max_time_travel + 1
        repetitive_stand = self.repetitive_stand = max_goal + 1
        self._repetitive_counter = 0  # Parameter that counts how many times repetitive was accessed
        self._max_repeats = 50  # Parameter that shows the maximum number of repetitions
        max_access_point = self.max_access_point = repetitive_stand + 1

        # The root
        root = self.root = max_access_point + 1

        # Number of actions
        self.action_space_size = root + 1

        # Movement actions
        # For each nav_to_lever_action append move_primitive_actions
        for nav_to_lever in nav_to_levers:
            self.graph.append({move_left, move_right, stand, jump_press, jump_release})
        # Append directions for navigate_to_time_machine
        self.graph.append({move_left, move_right, stand, jump_press, jump_release})
        # Append directions for navigate_to_goal
        self.graph.append({move_left, move_right, stand, jump_press, jump_release})

        # Max time travel
        self.graph.append({nav_to_time_machine, space_for_teleport})

        # Max goal
        self.graph.append({nav_to_goal, space_for_goal})

        # Repetitive stand
        self.graph.append({stand_for_access})

        # Max access point
        all_access_moves = set()
        # All actions moves to levers
        for element in nav_to_levers:
            all_access_moves.add(element)

        all_access_moves.add(repetitive_stand)
        all_access_moves.add(max_time_travel)
        self.graph.append(all_access_moves)

        # For root
        self.graph.append({max_access_point, max_goal})
