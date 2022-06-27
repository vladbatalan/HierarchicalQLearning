from api.apiCommands import *
from api.env.customenv import CustomEnv
from learn.maxQLearningUnit import MaxQLearningUnit0
from learn.maxq_tree.node import *
from learn.maxq_tree.tree_builder import TimeBenderTreeBuilder

HOST = "127.0.0.1"
PORT = 4303


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

    @staticmethod
    def train(model_path, level_name="Level0", alpha=0.5, gamma=0.95, num_episodes=2000, max_steps=3000, time_delay=0,
              expl_limit=1, logging=True):
        max_q_alg = MaxQLearningUnit0()

        config_data = {
            "-lvl": level_name,
            "-g": "false",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": "OptimalReward"
        }

        max_q_alg.init_environment(config_data, HOST, PORT)

        max_q_alg.train(alpha, gamma, num_episodes, max_steps, time_delay,
                        expl_limit, logging)

        max_q_alg.save_model(model_path)

        max_q_alg.close_env()
