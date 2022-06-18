import time
import matplotlib.pyplot as plt

from appApi import AppAPI
from apiCommands import *
import numpy as np

from stateDeserializer import CustomDeserializer


class QLearningUnit:

    def __init__(self, host, port):
        self.action_space = [value for value in ActionsEnum.get_list()]
        self.static_state = None
        self.api = AppAPI()
        self.api.start_main_loop(host, port)

    def init_environment(self, args):
        self.api.exec_command(ConfigureGameCommand(args))
        self.api.exec_command(StartGameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())

        # Receive the initial data about de environment to evaluate states
        static_state = self.api.exec_command(RequestStaticStateCommand())
        self.static_state = CustomDeserializer.get_static_level_state(static_state)

    def choose_action(self, Q, state, expl_limit):
        exploration = np.random.rand()
        if exploration < expl_limit:
            # Explore: choose a random action
            action = np.random.choice(self.action_space)
            action_index = self.action_space.index(action)
        else:
            # Exploit: choose teh best action
            action_index = np.argmax(Q[state])
            action = self.action_space[action_index]

        return action_index, action[len("ActionsEnum."):]

    def train(self, alpha=0.1, gamma=0.95, num_episodes=2000, max_steps=3000, frame_per_step=1, time_delay=0,
              expl_limit=1, expl_decay=0.05):
        Q = {}

        # Initialize reward for each episode
        rs = np.zeros(num_episodes)

        # Foreach episode
        for episode in range(num_episodes):
            reward_sum = 0
            t = 0
            done = False

            # Reset the env
            self.api.exec_command(RestartGameCommand())
            self.api.exec_command(StepFrameCommand())
            self.api.exec_command(StepFrameCommand())

            # Receive the state of the level
            state = self.api.exec_command(RequestDynamicStateCommand())
            state = CustomDeserializer.get_dynamic_level_state(state)

            while not done:

                # Choose the action to perform
                basic_state_form = str(state.basic_state_form())
                if basic_state_form not in Q.keys():
                    Q[basic_state_form] = np.full(len(self.action_space), 0.5)

                action_index, action = self.choose_action(Q, basic_state_form, expl_limit)

                # Execute action
                self.api.exec_command(PlayerActionCommand(action))

                # Get the new state of the env
                for frame in range(frame_per_step):
                    time.sleep(time_delay)
                    self.api.exec_command(StepFrameCommand())
                next_state = self.api.exec_command(RequestDynamicStateCommand())
                next_state = CustomDeserializer.get_dynamic_level_state(next_state)
                basic_next_state_form = str(next_state.basic_state_form())
                if basic_next_state_form not in Q.keys():
                    Q[basic_next_state_form] = np.full(len(self.action_space), 0.5)

                # The episode ends when level is win or lost or the frame_number exceed max_steps
                done = next_state.lost or next_state.complete or t > max_steps
                reward = next_state.reward

                # Update the QTable
                Q[basic_state_form][action_index] = (1 - alpha) * Q[basic_state_form][action_index] + alpha * (
                        reward + gamma * np.max(Q[basic_next_state_form][:]))

                # Add reward to total
                reward_sum += reward * gamma ** t

                # Update state and time
                state = next_state
                t += 1

            rs[episode] = reward_sum
            # Decay the exploration factor
            expl_limit -= expl_decay
            expl_limit = max(expl_limit, 0.02)

        self.plot_results(rs)

    def plot_results(self, rs):
        # Plot reward vs episodes
        # Sliding window average
        r_cumsum = np.cumsum(np.insert(rs, 0, 0))
        r_cumsum = (r_cumsum[50:] - r_cumsum[:-50]) / 50
        # Plot
        plt.plot(r_cumsum)
        plt.show()

        # Print number of times the goal was reached
        N = len(rs) // 10
        num_Gs = np.zeros(10)
        for i in range(10):
            num_Gs[i] = np.sum(rs[i * N:(i + 1) * N] > 0)

        print("Rewards: {0}".format(num_Gs))
