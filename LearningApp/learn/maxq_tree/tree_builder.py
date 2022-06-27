from api.apiCommands import HyperActionsEnum
from api.stateDeserializer import StaticLevelState
from learn.maxq_tree.maxq_tree import MaxQTree
from learn.maxq_tree.node import *


class TimeBenderTreeBuilder:
    # Equivalent parameterized node for travel function of maxtree
    MOVE_NODES = [Node("Left", primitive_action=HyperActionsEnum.LEFT_PRESSED),
                  Node("Right", primitive_action=HyperActionsEnum.RIGHT_PRESSED),
                  Node("Stand", primitive_action=HyperActionsEnum.STAND_ACTION),
                  Node("JumpReleased", primitive_action=HyperActionsEnum.JUMP_RELEASED),
                  Node("JumpPressed", primitive_action=HyperActionsEnum.JUMP_PRESSED)]

    @staticmethod
    def _clone_move_nodes(alpha=0.5, name_offset=""):
        move_nodes = []
        for move_node in TimeBenderTreeBuilder.MOVE_NODES:
            cloned = move_node.clone()
            cloned.alpha = alpha
            cloned.name += name_offset
            move_nodes.append(cloned)
        return move_nodes

    @staticmethod
    def build_tree(static_state: StaticLevelState, alpha=0.5) -> MaxQTree:

        # Create the root node
        max_root = Node("MaxRoot", alpha=alpha)

        # Children of root
        max_access_point = Node("MaxAccessPoint", alpha=alpha)
        max_goal = Node("MaxGoal", alpha=alpha)
        max_root.set_children([max_access_point, max_goal])

        # Children of max_access_point
        repetitive_stand = RepetitiveNode("RepetitiveStand", 50, alpha=alpha)
        stand_for_access = Node("StandAccess", primitive_action=HyperActionsEnum.STAND_ACTION, alpha=alpha)
        repetitive_stand.set_children([stand_for_access])
        # Create navigate for all access points added as Travel Nodes
        nav_to_access = []
        for access_point in static_state.lever_positions:
            travel_node = TravelNode("NavTo" + str(access_point), dest=access_point, alpha=alpha)
            children = TimeBenderTreeBuilder._clone_move_nodes(alpha, str(access_point))
            for child in children:
                child.name = child.name + str(access_point)
            travel_node.set_children(children)
            nav_to_access.append(travel_node)

        max_time_travel = Node("MaxTimeTravel", alpha=alpha)
        max_access_point_children = [repetitive_stand, max_time_travel]
        for node in nav_to_access:
            max_access_point_children.append(node)
        max_access_point.set_children(max_access_point_children)

        # Children of max_time_travel
        teleport_in_time = Node("SpaceTeleport", primitive_action=HyperActionsEnum.SPACE_RELEASED, alpha=alpha)
        travel_dest = static_state.time_machine_position
        nav_to_time_machine = TravelNode("NavTo" + str(travel_dest), dest=travel_dest, alpha=alpha)
        nav_to_time_machine.set_children(TimeBenderTreeBuilder._clone_move_nodes(alpha, str(travel_dest)))

        max_time_travel.set_children([teleport_in_time, nav_to_time_machine])

        # Children of max_goal
        complete_game = Node("SpaceComplete", primitive_action=HyperActionsEnum.SPACE_RELEASED, alpha=alpha)
        objective_dest = static_state.game_objective_position
        nav_to_objective = TravelNode("NavTo" + str(objective_dest), dest=objective_dest, alpha=alpha)
        nav_to_objective.set_children(TimeBenderTreeBuilder._clone_move_nodes(alpha, str(objective_dest)))

        max_goal.set_children([complete_game, nav_to_objective])

        # Establish the terminal states for each action
        def terminal_node_function(action, state, done):
            # Get the name of the action
            a = action.name

            if done:
                return True

            # If it is the root, return if the problem was completed
            if a == "MaxRoot":
                return done

            if a == "RepetitiveStand":
                return action.is_terminal()

            if a[0:len("NavTo")] == "NavTo":
                return action.reached_destination(state)

            if action.is_primitive():
                return True

            return False

        def composed_terminal_function(parent, finished_action):
            a = parent.name
            f = finished_action.name

            if a == "MaxAccessPoint" and f == "MaxTimeTravel":
                return True

            if a == "MaxTimeTravel" and f == "SpaceTeleport":
                return True

            if a == "MaxGoal" and f == "SpaceComplete":
                return True

            return False

        # Create the tree framework for the nodes
        tree = MaxQTree(max_root, is_terminal=terminal_node_function, is_composed_terminal=composed_terminal_function)

        return tree
