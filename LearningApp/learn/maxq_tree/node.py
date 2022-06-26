import abc
import math
from collections import defaultdict

import numpy as np

from api.stateDeserializer import DynamicLevelState


# A generic node from the maxQ Tree
class Node(abc.ABC):
    def __init__(self, name, children=None, primitive_action=None, alpha=0.5):
        self.name = name
        self.children = children

        self.C = None

        if self.children is not None:
            self.children = sorted(self.children, key=lambda child: child.name)
            self.C = {}

        self.V = {}

        self.alpha = alpha
        self._init_alpha = alpha

        self.primitive_action = primitive_action

    def set_children(self, children):
        self.children = list(sorted(children, key=lambda child: child.name))

    def is_primitive(self) -> bool:
        return self.primitive_action is not None

    def is_terminal(self, state: DynamicLevelState) -> bool:
        if self.is_primitive():
            return True
        return state.is_done()

    def get_action_list(self) -> []:
        return self.children

    def reset_node(self):
        self.alpha = self._init_alpha

        if not self.is_primitive():
            self.C = {}

        self.V = {}

    def get_child_by_name(self, name):
        if self.children is None:
            return None

        for child in self.children:
            if child.name == name:
                return child
        return None

    def choose_action(self, state: DynamicLevelState, expl_limit):
        state_form = str(state.basic_state_form())

        probability = np.random.rand()

        if probability < expl_limit:
            # Choose a random next node
            return self.children[np.random.randint(0, len(self.children))]

        else:
            best_V = None
            action_index = None
            # Choose the best action
            for i, node in enumerate(self.children):
                if state_form not in node.V.keys():
                    node.V[state_form] = 0

                if best_V is None:
                    best_V = node.V[state_form] + node.C[state_form][i]
                    action_index = i
                else:
                    temp = node.V[state_form] + node.C[state_form][i]
                    if best_V < temp:
                        best_V = temp
                        action_index = i

            return self.children[action_index]

    # t is the parameter of time
    # Action represents the name of the action
    def update_C(self, current_state: DynamicLevelState, new_state: DynamicLevelState, action, gamma=0.95, t=0):
        if self.is_primitive():
            raise Exception("Primitive node has no C table.")

        crr_state = str(current_state.basic_state_form())
        nw_state = str(new_state.basic_state_form())

        # The index of the action will be the first child with name
        action_list = self.children
        action_index = next(i for i, v in enumerate(action_list) if v.name == action.name)

        if crr_state not in self.C.keys():
            self.C[crr_state] = np.zeros(len(action_list))

        if nw_state not in self.V.keys():
            self.V[nw_state] = 0

        self.C.get(crr_state)[action_index] = (1 - self.alpha) * self.C.get(crr_state)[action_index] + self.alpha * (
                gamma ** t) * self.V[nw_state]

    def update_V(self, crr_state: DynamicLevelState, reward):
        if crr_state not in self.V:
            self.V[crr_state] = 0

        self.V[crr_state] = (1 - self.alpha) * self.V[crr_state] + self.alpha * reward

    def clone(self):
        return Node(self.name, self.children, self.primitive_action, self.alpha)


class TravelNode(Node):
    def __init__(self, name, dest: (int, int), children=None, alpha=0.5):
        super().__init__(name, children, None, alpha)

        self.destination = dest

    # Terminal condition occurs when the destination is achieved
    def is_terminal(self, state: DynamicLevelState) -> bool:
        if math.floor(state.player_position[0]) == self.destination[0]:
            if math.floor(state.player_position[1]) == self.destination[1]:
                return True

        # TODO: Include a max-step parameter for destinations that cannot be accessed

        return state.is_done()

    def clone(self):
        return TravelNode(self.name, self.destination, self.children, self.alpha)


def print_maxQ_tree(root_node: Node, ident=0, delim="   |"):
    for i in range(ident):
        print(delim, end="")

    print(root_node.name, end="")

    if not root_node.is_primitive():
        print(":")
        for child_node in root_node.children:
            print_maxQ_tree(child_node, ident + 1, delim)

    else:
        print("[ action =", root_node.primitive_action.name, "]")
