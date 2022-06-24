import abc
import math

import numpy as np

from api.stateDeserializer import DynamicLevelState


# A generic node from the maxQ Tree
class Node(abc.ABC):
    def __init__(self, name, children=None, primitive_action=None, alpha=0.5):
        self.name = name
        self.children = children

        if self.children is not None:
            self.children = sorted(self.children, key=lambda child: child.name)

        self.C = {}
        self.V = {}

        self.alpha = alpha
        self._init_alpha = alpha

        self.primitive_action = primitive_action

    def is_primitive(self) -> bool:
        return self.primitive_action is None

    def is_terminal(self, state: DynamicLevelState) -> bool:
        if self.is_primitive():
            return True
        return state.lost or state.complete

    def get_action_list(self) -> []:
        return self.children

    def reset_node(self):
        self.alpha = self._init_alpha
        self.C = {}
        self.V = {}

    def get_child_by_name(self, name):
        if self.children is None:
            return None

        for child in self.children:
            if child.name == name:
                return child
        return None

    # t is the parameter of time
    # Action represents the name of the action
    def update_C(self, current_state: DynamicLevelState, new_state: DynamicLevelState, action, gamma=0.95, t=0):
        crr_state = current_state.basic_state_form()
        nw_state = new_state.basic_state_form()

        # The index of the action will be the first child with name
        action_list = self.get_action_list()
        action_index = next(i for i, v in enumerate(action_list) if v.name == action)

        if crr_state not in self.C:
            self.C[crr_state] = np.zeros(len(action_list))

        if nw_state not in self.V:
            self.V[nw_state] = 0

        self.C[crr_state][action_index] = (1 - self.alpha) * self.C[crr_state][action_index] + self.alpha * (
                    gamma ** t) * self.V[nw_state]

    def update_V(self, crr_state: DynamicLevelState, reward):
        if crr_state not in self.V:
            self.V[crr_state] = 0

        self.V[crr_state] = (1 - self.alpha) * self.V[crr_state] + self.alpha * reward
        

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

        return state.lost or state.complete
