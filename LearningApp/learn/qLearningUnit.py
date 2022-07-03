import matplotlib.pyplot as plt

import numpy as np

from api.env.customenv import CustomEnv
from learn.util.utils import plot_results


class QLearningUnit:

    def __init__(self):
        self.Q = None
        self.static_state = None
        self.env = CustomEnv()

    def init_environment(self, args, host, port, jar_path=None):
        self.env.start_env(host, port, args, jar_path=jar_path)
        self.static_state = self.env.static_state

    def _choose_action(self, state, expl_limit):
        exploration = np.random.rand()
        if exploration < expl_limit:
            # Explore: choose a random action
            action = np.random.choice(self.env.action_space)
            action_index = self.env.action_space.index(action)
        else:
            # Exploit: choose the best action
            action_index = np.argmax(self.Q[state])
            action = self.env.action_space[action_index]

        return action_index, action[len("HyperActionsEnum."):]

    def train(self, alpha=0.1, gamma=0.95, num_episodes=2000, max_steps=3000, time_delay=0,
              expl_limit=1, logging=True, save_pictures_path=None):

        self.Q = {}

        # Initialize reward for each episode
        rs = np.zeros(num_episodes)

        # Initialize the decay
        expl_decay = (expl_limit - 0.1) / num_episodes

        # Foreach episode
        for episode in range(num_episodes):
            reward_sum = 0
            t = 0
            done = False

            # Reset the env
            state = self.env.reset()

            while not done:

                # Choose the action to perform
                basic_state_form = str(state.basic_state_form())
                if basic_state_form not in self.Q.keys():
                    self.Q[basic_state_form] = np.zeros(len(self.env.action_space))

                # Select the next action
                action_index, action = self._choose_action(basic_state_form, expl_limit)

                # Advance a step
                next_state, reward, done = self.env.step(action, time_delay=time_delay)

                basic_next_state_form = str(next_state.basic_state_form())
                if basic_next_state_form not in self.Q.keys():
                    self.Q[basic_next_state_form] = np.zeros(len(self.env.action_space))

                # The episode ends when level is win or lost or the frame_number exceed max_steps
                done = done or t > max_steps

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
                print('Steps:', str(t))
                print()

        plot_results(rs, alpha=alpha, gamma=gamma, nr_cum=num_episodes // 100 + 1,
                     path_with_starting_name=save_pictures_path)

    def perform_with_model(self, model_path, max_steps=3000, time_delay=0, gamma=0.95):

        self.load_model(model_path)

        done = False
        reward_sum = 0
        t = 0

        # Reset the env
        state = self.env.reset()

        while not done:

            # Choose the action to perform
            basic_state_form = str(state.basic_state_form())
            if basic_state_form not in self.Q.keys():
                raise Exception('An unexpected state occured! State:', basic_state_form)

            # Take the best action
            action_index, action = self._choose_action(basic_state_form, 0)

            next_state, reward, done = self.env.step(action, time_delay=time_delay)
            basic_next_state_form = str(next_state.basic_state_form())

            # This action should not happen
            if basic_next_state_form not in self.Q.keys():
                raise Exception('An unexpected state occured! State:', basic_state_form)

            # The episode ends when level is win or lost or the frame_number exceed max_steps
            done = done or t > max_steps

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
            values = np.zeros(len(self.env.action_space))
            str_of_values = elements[1][1:-2].split(', ')

            for i in range(len(str_of_values)):
                values[i] = float(str_of_values[i])

            self.Q[key] = values

    def close_env(self):
        self.env.close_env()
