import time

from api.appApi import AppAPI
from api.env.customenv import CustomEnv
from learn.maxq_tree.node import print_maxQ_tree, Node, TravelNode
from learn.maxq_tree.tree_builder import TimeBenderTreeBuilder
from learn.qLearningUnit import QLearningUnit
from api.stateDeserializer import CustomDeserializer
from api.apiCommands import *
from tests.manualWinStrategy import ManualWinStrategy_Level0

HOST = "127.0.0.1"
PORT = 4303


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
    def train_level(level_name, train_file_path, reward_type, max_steps=200, num_episodes=1000,
                    alpha=0.1, gamma=0.95, graphics="false"):
        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": graphics,
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": reward_type
        }

        learning_unit.init_environment(config, HOST, PORT)

        learning_unit.train(max_steps=max_steps, num_episodes=num_episodes,
                            alpha=alpha, gamma=gamma)

        learning_unit.save_model(train_file_path)

        learning_unit.close_env()

    @staticmethod
    def perform_with_model(level_name, model_file_path, reward_type='PromoteAllStatesActive',
                           max_steps=200, gamma=0.95):
        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": "true",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": reward_type
        }

        learning_unit.init_environment(config, HOST, PORT)

        learning_unit.perform_with_model(model_file_path, time_delay=0.001, gamma=gamma, max_steps=max_steps)

        learning_unit.close_env()


class MaxQTreeShould:
    @staticmethod
    def _create_manual_tree() -> Node:
        max_root = Node("MaxRoot")
        max_access_point = Node("MaxAccessPoint")
        max_goal = Node("MaxGoal")

        max_root.set_children([max_access_point, max_goal])

        # Children of max_access_point
        stand_for_access = Node("StandAccess", primitive_action=HyperActionsEnum.STAND_ACTION)
        # Create navigate for all access points added as Travel Nodes
        nav_to_access_01 = TravelNode("NavTo(14, 13)", dest=(14, 13))
        nav_to_access_02 = TravelNode("NavTo(3, 21)", dest=(3, 21))
        max_time_travel = Node("MaxTimeTravel")

        max_access_point.set_children([stand_for_access, nav_to_access_01, nav_to_access_02, max_time_travel])

        # Children of max_time_travel
        teleport_in_time = Node("SpaceTeleport", primitive_action=HyperActionsEnum.SPACE_RELEASED)
        nav_to_time_machine_01 = TravelNode("NavTo(2, 13)", dest=(2, 13))

        max_time_travel.set_children([teleport_in_time, nav_to_time_machine_01])

        # Children of max_goal
        complete_game = Node("SpaceComplete", primitive_action=HyperActionsEnum.SPACE_RELEASED)
        nav_to_objective = TravelNode("NavTo(7, 13)", dest=(7, 13))

        max_goal.set_children([complete_game, nav_to_objective])

        # Equivalent parameterized node for travel function of maxtree
        move_nodes = [Node("Left", primitive_action=HyperActionsEnum.LEFT_PRESSED),
                      Node("Right", primitive_action=HyperActionsEnum.RIGHT_PRESSED),
                      Node("Stand", primitive_action=HyperActionsEnum.RIGHT_PRESSED),
                      Node("JumpReleased", primitive_action=HyperActionsEnum.JUMP_RELEASED),
                      Node("JumpPressed", primitive_action=HyperActionsEnum.JUMP_PRESSED)]

        for nav_nodes in [nav_to_access_01, nav_to_access_02, nav_to_objective, nav_to_time_machine_01]:
            children = []
            for move_node in move_nodes:
                children.append(move_node.clone())
            nav_nodes.set_children(children)

        return max_root

    @staticmethod
    def extract_tree_from_level():
        created_tree = MaxQTreeShould._create_manual_tree()
        print("Manually created tree: ")
        print_maxQ_tree(created_tree)

        # Create environment
        game_env = CustomEnv()
        config_data = {
            "-lvl": "Level0",
            "-g": "false",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": "PromoteAllStatesActive"
        }

        # Extract the static state of level
        game_env.start_env(HOST, PORT, config_data)
        game_static_state = game_env.static_state

        # Build tree based on state
        game_tree = TimeBenderTreeBuilder.build_tree(game_static_state)
        print()
        print("Generated tree from level: ")
        game_tree.print()

        game_env.close_env()
