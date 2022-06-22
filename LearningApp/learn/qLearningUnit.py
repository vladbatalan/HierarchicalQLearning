import matplotlib.pyplot as plt

import numpy as np

from api.env.customenv import CustomEnv


def plot_results(rs, alpha, gamma, nr_cum=50):
    number_of_episodes = len(rs)

    # Plot reward vs episodes
    # Sliding window average
    r_cumsum = np.cumsum(np.insert(rs, 0, 0))
    r_cumsum = (r_cumsum[nr_cum:] - r_cumsum[:-nr_cum]) / nr_cum

    # Plot
    plt.title('Points over episodes (alpha=' + str(alpha) + ', gamma=' + str(gamma) + ')')
    plt.ylabel('Points')
    plt.xlabel('Episodes')
    plt.plot(r_cumsum)
    plt.show()

    avg_reward = sum(rs) / len(rs)
    print('Average reward per episode:', avg_reward)

    # Print number of times the goal was reached
    n = number_of_episodes // 10
    num_gs = np.zeros(10)
    labels = []

    for i in range(10):
        num_gs[i] = np.sum(rs[i * n:(i + 1) * n] > 0)
        labels.append(str([i * n, (i + 1) * n]))

    x = np.arange(len(labels))
    fig, ax = plt.subplots()
    ax.set_ylabel('Win count')
    ax.set_title('Times when goal was reached (' + str(n) + ' per sample)')
    ax.set_xticks(x, labels)
    rects = ax.bar(x, num_gs)
    ax.bar_label(rects, padding=5)
    fig.tight_layout()
    plt.show()

    print("Rewards: {0}".format(num_gs))


class QLearningUnit:

    def __init__(self):
        self.Q = None
        self.static_state = None
        self.env = CustomEnv()

    def init_environment(self, args, host, port):
        self.env.start_env(host, port, args)
        self.static_state = self.env.static_state

    def choose_action(self, state, expl_limit):
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
              expl_limit=1, logging=True):

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
                action_index, action = self.choose_action(basic_state_form, expl_limit)

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
                print()

        plot_results(rs, alpha=alpha, gamma=gamma, nr_cum=num_episodes // 100 + 1)

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
            action_index, action = self.choose_action(basic_state_form, 0)

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
