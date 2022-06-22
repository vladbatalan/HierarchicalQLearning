import time
import matplotlib.pyplot as plt

from appApi import AppAPI
from apiCommands import *
import numpy as np

from stateDeserializer import CustomDeserializer


class QLearningUnit:

    def __init__(self, host, port):
        self.Q = None
        self.action_space = [value for value in HyperActionsEnum.get_list()]
        self.static_state = None
        self.api = AppAPI()
        self.api.start_main_loop(host, port)

    def init_environment(self, args):
        self.api.exec_command(ConfigureGameCommand(args))
        self.api.exec_command(StartGameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())

        # Receive the initial data about de environment to evaluate states
        static_state = self.api.exec_command(RequestStaticStateCommand())
        self.static_state = CustomDeserializer.get_static_level_state(static_state)

    def choose_action(self, state, expl_limit):
        exploration = np.random.rand()
        if exploration < expl_limit:
            # Explore: choose a random action
            action = np.random.choice(self.action_space)
            action_index = self.action_space.index(action)
        else:
            # Exploit: choose the best action
            action_index = np.argmax(self.Q[state])
            action = self.action_space[action_index]

        return action_index, action[len("HyperActionsEnum."):]

    def train(self, alpha=0.1, gamma=0.95, num_episodes=2000, max_steps=3000, frame_per_step=1, time_delay=0,
              expl_limit=1, logging=True):

        self.Q = {}

        # Initialize reward for each episode
        rs = np.zeros(num_episodes)

        # Initialize the decay
        expl_decay = 1 / num_episodes

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
                if basic_state_form not in self.Q.keys():
                    self.Q[basic_state_form] = np.full(len(self.action_space), 0.5)

                action_index, action = self.choose_action(basic_state_form, expl_limit)

                # Execute action
                self.api.exec_command(PlayerSmartCommand(action))

                # Get the new state of the env
                for frame in range(frame_per_step):
                    time.sleep(time_delay)
                    self.api.exec_command(StepFrameCommand())

                next_state = self.api.exec_command(RequestDynamicStateCommand())
                next_state = CustomDeserializer.get_dynamic_level_state(next_state)
                basic_next_state_form = str(next_state.basic_state_form())
                if basic_next_state_form not in self.Q.keys():
                    self.Q[basic_next_state_form] = np.full(len(self.action_space), 0.5)

                # The episode ends when level is win or lost or the frame_number exceed max_steps
                done = next_state.lost or next_state.complete or t > max_steps
                reward = next_state.reward

                # Update the QTable
                self.Q[basic_state_form][action_index] = (1 - alpha) * self.Q[basic_state_form][
                    action_index] + alpha * (reward + gamma * np.max(self.Q[basic_next_state_form][:]))

                # Add reward to total
                reward_sum += reward * gamma ** t

                # Update state and time
                state = next_state
                t += 1

            rs[episode] = reward_sum

            # Decay the exploration factor
            expl_limit -= expl_decay

            if logging is True:
                print('Episode:', episode)
                print('Reward:', reward_sum)
                print('Explore:', str(expl_limit * 100) + '%')
                print()

        self.plot_results(rs)

    def perform_with_model(self, model_path, max_steps=3000, frame_per_step=1, time_delay=0, gamma=0.95):

        self.load_model(model_path)

        # Reset the env
        self.api.exec_command(RestartGameCommand())
        self.api.exec_command(StepFrameCommand())
        self.api.exec_command(StepFrameCommand())

        # Receive the state of the level
        state = self.api.exec_command(RequestDynamicStateCommand())
        state = CustomDeserializer.get_dynamic_level_state(state)
        done = False

        reward_sum = 0
        t = 0

        while not done:

            # Choose the action to perform
            basic_state_form = str(state.basic_state_form())
            if basic_state_form not in self.Q.keys():
                raise Exception('An unexpected state occured! State:', basic_state_form)

            # Take the best action
            action_index, action = self.choose_action(basic_state_form, 0)

            # Execute action
            self.api.exec_command(PlayerSmartCommand(action))

            # Get the new state of the env
            for frame in range(frame_per_step):
                time.sleep(time_delay)
                self.api.exec_command(StepFrameCommand())

            next_state = self.api.exec_command(RequestDynamicStateCommand())
            next_state = CustomDeserializer.get_dynamic_level_state(next_state)
            basic_next_state_form = str(next_state.basic_state_form())

            # This action should not happen
            if basic_next_state_form not in self.Q.keys():
                raise Exception('An unexpected state occured! State:', basic_state_form)

            # The episode ends when level is win or lost or the frame_number exceed max_steps
            done = next_state.lost or next_state.complete or t > max_steps
            reward = next_state.reward

            # Add reward to total
            reward_sum += reward * gamma ** t

            # Update state and time
            state = next_state
            t += 1

        if not state.lost and not state.complete:
            print('Level was not completed, nor lost.')
        elif state.lost:
            print('Level lost.')
        elif state.complete:
            print('Level complete.')

        print('Reward:', reward_sum)

    def save_model(self, path):
        if len(self.Q) == 0:
            raise Exception('Model has not been build yet!')

        f = open(path, 'w')

        for key in self.Q:
            f.write(key + ":" + str(self.Q[key].tolist()) + '\n')

        print('The model was save at:', path)
        f.close()

    def load_model(self, path):
        self.Q = {}
        f = open(path, 'r')

        for line in f.readlines():
            if len(line) == 0:
                continue

            elements = line.split(':')
            key = elements[0]
            values = np.zeros(len(self.action_space))
            str_of_values = elements[1][1:-2].split(', ')

            for i in range(len(str_of_values)):
                values[i] = float(str_of_values[i])

            self.Q[key] = values

    def plot_results(self, rs):
        # Plot reward vs episodes
        # Sliding window average
        r_cumsum = np.cumsum(np.insert(rs, 0, 0))
        r_cumsum = (r_cumsum[50:] - r_cumsum[:-50]) / 50

        # Plot
        plt.title('Reward over episodes')
        plt.ylabel('Reward')
        plt.xlabel('Episodes')
        plt.plot(r_cumsum)
        plt.show()

        avg_reward = sum(rs) / len(rs)
        print('Average reward per episode:', avg_reward)

        # Print number of times the goal was reached
        N = len(rs) // 10
        num_Gs = np.zeros(10)
        for i in range(10):
            num_Gs[i] = np.sum(rs[i * N:(i + 1) * N] > 0)

        print("Rewards: {0}".format(num_Gs))

    def close_env(self):
        self.api.stop_main_loop()
