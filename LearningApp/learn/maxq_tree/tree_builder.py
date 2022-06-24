from api.apiCommands import HyperActionsEnum
from api.stateDeserializer import StaticLevelState
from learn.maxq_tree.node import *
import itertools


class TimeBenderTreeBuilder:
    # Equivalent parameterized node for travel function of maxtree
    MOVE_NODES = [Node("Left", primitive_action=HyperActionsEnum.LEFT_PRESSED),
                  Node("Right", primitive_action=HyperActionsEnum.RIGHT_PRESSED),
                  Node("Stand", primitive_action=HyperActionsEnum.STAND_ACTION),
                  Node("JumpReleased", primitive_action=HyperActionsEnum.JUMP_RELEASED),
                  Node("JumpPressed", primitive_action=HyperActionsEnum.JUMP_PRESSED)]

    @staticmethod
    def _clone_move_nodes():
        move_nodes = []
        for move_node in TimeBenderTreeBuilder.MOVE_NODES:
            move_nodes.append(move_node.clone())
        return move_nodes

    @staticmethod
    def build_tree(static_state: StaticLevelState) -> Node:

        max_root = Node("MaxRoot")
        max_access_point = Node("MaxAccessPoint")
        max_goal = Node("MaxGoal")

        max_root.set_children([max_access_point, max_goal])

        # Children of max_access_point
        stand_for_access = Node("StandAccess", primitive_action=HyperActionsEnum.STAND_ACTION)
        # Create navigate for all access points added as Travel Nodes
        nav_to_access = []
        for access_point in static_state.lever_positions:
            travel_node = TravelNode("NavTo" + str(access_point), dest=access_point)
            travel_node.set_children(TimeBenderTreeBuilder._clone_move_nodes())
            nav_to_access.append(travel_node)

        max_time_travel = Node("MaxTimeTravel")
        max_access_point_children = [stand_for_access, max_time_travel]
        for node in nav_to_access:
            max_access_point_children.append(node)
        max_access_point.set_children(max_access_point_children)

        # Children of max_time_travel
        teleport_in_time = Node("SpaceTeleport", primitive_action=HyperActionsEnum.SPACE_RELEASED)
        travel_dest = static_state.time_machine_position
        nav_to_time_machine = TravelNode("NavTo" + str(travel_dest), dest=travel_dest)
        nav_to_time_machine.set_children(TimeBenderTreeBuilder._clone_move_nodes())

        max_time_travel.set_children([teleport_in_time, nav_to_time_machine])

        # Children of max_goal
        complete_game = Node("SpaceComplete", primitive_action=HyperActionsEnum.SPACE_RELEASED)
        objective_dest = static_state.game_objective_position
        nav_to_objective = TravelNode("NavTo" + str(objective_dest), dest=objective_dest)
        nav_to_objective.set_children(TimeBenderTreeBuilder._clone_move_nodes())

        max_goal.set_children([complete_game, nav_to_objective])

        return max_root
