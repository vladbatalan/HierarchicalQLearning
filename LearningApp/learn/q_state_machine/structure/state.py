import abc
from abc import ABC

import numpy as np

from api.apiCommands import HyperActionsEnum
from api.env.customenv import CustomEnv
from api.stateDeserializer import DynamicLevelState
from learn.q_state_machine.structure.epsilon_strategy import *


class QState(abc.ABC):

    @abc.abstractmethod
    def train(self, state: DynamicLevelState, eps_strategy: EpsilonStrategy, env: CustomEnv) -> float:
        pass

    @abc.abstractmethod
    def perform(self, state: DynamicLevelState, env: CustomEnv) -> float:
        pass

    # TODO: Next state from the node


class QTableState(QState, ABC):
    def __init__(self):
        super().__init__()
        self._Q = {}
        self.alpha = 0.1
        self.gamma = 0.99
        self.action_space_n = 0

    def choose_acton(self, state, eps):
        exploration = np.random.rand()

        if exploration < eps:
            # Explore: choose a random action
            action = np.random.choice(self.get_Q_of_state(state))
        else:
            # Exploit: choose the best action
            action = np.argmax(self.get_Q_of_state(state))

        return action

    # This indicator shows that this state ended, and it is ready to move to the next one
    @abc.abstractmethod
    def terminated(self, state: DynamicLevelState) -> bool:
        pass

    # For a specific state, the node will provide a reward
    @abc.abstractmethod
    def pseudo_reward(self, state: DynamicLevelState) -> float:
        pass

    def get_Q_of_state(self, state: DynamicLevelState):
        state_str = str(state.basic_state_form())

        if state_str not in self._Q.keys():
            self._Q[state_str] = np.zeros(self.action_space_n)

        return self._Q[state_str]

    def set_Q(self, state: DynamicLevelState, action_index, value: float):
        q_values_for_state = self.get_Q_of_state(state)
        q_values_for_state[action_index] = value

    def reset(self):
        self._Q = {}


class StateQChildren(QTableState, ABC):

    def __init__(self, children):
        super().__init__()
        self.children = children
        self.action_space_n = len(children)
        self.actions = range(self.action_space_n)

    def train(self, state: DynamicLevelState, eps_strategy: EpsilonStrategy, env: CustomEnv) -> float:

        # Reset parameters
        reward_sum = 0
        done = False

        while not done:
            # Choose a child to perform
            child_index = self.choose_acton(state, eps_strategy.get_eps())
            child = self.children[child_index]

            # Execute child
            child.train(state, eps_strategy, env)
            next_state, env_reward, done = env.step(self.actions[next_action])

            # Sum of env_reward
            reward_sum += env_reward

            # Pseudo reward
            pseudo_reward = self.pseudo_reward(next_state)

            # Update the Q table
            new_q_value = (1 - self.alpha) * self.get_Q_of_state(state)[action] + self.alpha * (
                    pseudo_reward + self.gamma * np.max(self.get_Q_of_state(next_state)))
            self.set_Q(state, action, new_q_value)

            state = next_state

            done = done or self.terminated(state)

        return reward_sum

    def perform(self, state: DynamicLevelState, env: CustomEnv) -> float:
        pass


class StateQPrimitives(QTableState, ABC):

    # Primitive actions is an array of actions (Example: navTo has all move actions ass primitive)
    # This node has only one next element, so there is only one child
    def __init__(self, child, primitive_actions=None):
        super().__init__()
        self.children = child
        if primitive_actions is None:
            primitive_actions = []
        self.actions = primitive_actions
        self.action_space_n = len(self.actions)

    def perform(self, state: DynamicLevelState, env: CustomEnv) -> float:
        done = False
        res_sum = 0

        while not done:
            # Choose best action to perform
            next_action = self.choose_acton(state, 0)

            # Advance step
            next_state, env_reward, done = env.step(self.actions[next_action])

            res_sum += env_reward
            state = next_state

            done = done or self.terminated(state)

        return res_sum

    def train(self, state: DynamicLevelState, eps_strategy: EpsilonStrategy, env: CustomEnv) -> float:

        # Reset parameters
        reward_sum = 0
        done = False

        while not done:
            # Choose action to perform
            next_action = self.choose_acton(state, eps_strategy.get_eps())

            # Advance step
            next_state, env_reward, done = env.step(self.actions[next_action])

            # Sum of env_reward
            reward_sum += env_reward

            # Pseudo reward
            pseudo_reward = self.pseudo_reward(next_state)

            # Update the Q table
            new_q_value = (1 - self.alpha) * self.get_Q_of_state(state)[action] + self.alpha * (
                    pseudo_reward + self.gamma * np.max(self.get_Q_of_state(next_state)))
            self.set_Q(state, action, new_q_value)

            state = next_state

            done = done or self.terminated(state)

        return reward_sum


class NavigateToPoint(StateQPrimitives):

    def __init__(self, child, destination):
        super().__init__(child)
        self.actions = [
            HyperActionsEnum.LEFT_PRESSED,
            HyperActionsEnum.RIGHT_PRESSED,
            HyperActionsEnum.JUMP_PRESSED,
            HyperActionsEnum.JUMP_RELEASED,
        ]
        self.action_space_n = len(self.actions)
        self.destination = destination

    def terminated(self, state: DynamicLevelState) -> bool:
        player_position = (int(state.player_position[0]), int(state.player_position[1]))

        return player_position == self.destination

    def pseudo_reward(self, state: DynamicLevelState) -> float:
        # Will give a reward if the state is terminated
        if self.terminated(state):
            return 20

        # Otherwise, will evaluate proportionally to distance to target
        player_position = state.player_position
        distance = np.sqrt(
            (player_position[0] - self.destination[0]) ** 2 + (player_position[1] - self.destination[1]) ** 2)

        return -0.1 * distance


class RepetitiveAction(StateQPrimitives):
    def __init__(self, child, primitive_action, repeat_times):
        super().__init__(child)
        self.actions = [primitive_action]
        self.repeat_times = repeat_times
        self._counter = 0
        self.action_space_n = 1

    def terminated(self, state: DynamicLevelState) -> bool:
        return self._counter >= self.repeat_times

    def perform(self, action, state: DynamicLevelState, env: CustomEnv) -> float:
        done = False
        self._counter = 0
        res_sum = 0

        while not done:
            # Choose best action to perform
            next_action = self.primitive_actions[0]

            # Advance step
            next_state, env_reward, done = env.step(self.actions[next_action])

            state = next_state
            res_sum += env_reward

            self._counter += 1

            done = done or self.terminated(state)

        return res_sum

    def train(self, action, state: DynamicLevelState, eps_strategy: EpsilonStrategy, env: CustomEnv) -> float:
        # Reset parameters
        reward_sum = 0
        done = False
        self._counter = 0
        while not done:
            # Choose action to perform
            next_action = self.primitive_actions[0]

            # Advance step
            next_state, env_reward, done = env.step(self.actions[next_action])

            # Sum of env_reward
            reward_sum += env_reward

            state = next_state

            self._counter += 1

            done = done or self.terminated(state)

        return reward_sum

    def reset(self):
        super(RepetitiveAction, self).reset()
        self._counter = 0
