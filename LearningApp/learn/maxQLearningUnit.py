import numpy as np

from api.env.customenv import CustomEnv
from api.stateDeserializer import DynamicLevelState
from learn.maxq_tree.tree_builder import TimeBenderTreeBuilder
from learn.util.utils import plot_results


class MaxQLearningUnit0:

    def __init__(self):
        self.static_state = None
        self.env = CustomEnv()
        self.tree = None

        self._action_chain = []

    def init_environment(self, args, host, port):
        self.env.start_env(host, port, args)
        self.static_state = self.env.static_state
        self.tree = TimeBenderTreeBuilder.build_tree(self.static_state)
        self.tree.print()

    def train(self, alpha=0.5, gamma=0.95, num_episodes=2000, max_steps=3000, time_delay=0,
              expl_limit=1, logging=True):

        # Reset the tree
        self.tree.reset_tree(alpha)

        # Initialize reward for each episode
        rs = np.zeros(num_episodes)

        # Initialize the decay
        expl_decay = (expl_limit - 0.1) / num_episodes

        # Foreach episode apply the MaxQ-0 algorithm for training
        for episode in range(num_episodes):

            # Reset the env
            state = self.env.reset()

            self._action_chain = []

            new_state, time_passed, reward_sum = self._max_q_0_update(self.tree.root_node, state, gamma, max_steps,
                                                                      time_delay, expl_limit)
            rs[episode] = reward_sum
            expl_limit -= expl_decay

            if logging:
                print('Episode:', episode)
                print('Reward:', reward_sum)
                print('Explore:', str(expl_limit * 100) + '%')
                print()

        plot_results(rs, alpha=alpha, gamma=gamma, nr_cum=num_episodes // 100 + 1)

    def _max_q_0_update(self, node, state, gamma, max_steps, time_delay, expl_limit) -> (DynamicLevelState, int, float):
        if node.is_primitive():

            # Take a step using the primitive action of this node
            new_state, reward, done = self.env.step(node.primitive_action.name,
                                                    time_delay=time_delay)

            # Update V of the node
            node.update_V(state, reward)

            self._action_chain.append(node.name)

            # print("Action chain:", self._action_chain)

            return new_state, 1, reward

        else:
            cumulative_reward = 0
            actions_done = 0

            if node.is_repetitive():
                node.reset_repetitions()

            terminated = False

            # TODO: Remove this
            self._action_chain.append(node.name)
            # print("Action chain:", self._action_chain)

            while not terminated:

                next_node = node.choose_action(state, expl_limit)

                # TODO: Remove this
                # while len(self._action_chain) > 0 and self._action_chain[len(self._action_chain) - 1] != node.name:
                #     self._action_chain.pop()

                if node.is_repetitive():
                    node.increase_repetition()

                # Get to bottom of tree recursively
                new_state, no_actions, reward = self._max_q_0_update(next_node, state, gamma, max_steps, time_delay,
                                                                     expl_limit)
                cumulative_reward += reward

                # Update the V value of the node
                node.update_V(state, reward)

                # Update the C value of the node
                node.update_C(state, new_state, next_node, gamma, no_actions)

                # Update the number of total actions and state
                actions_done += no_actions
                state = new_state

                # print("\tSteps:", actions_done)

                # Terminate conditions
                terminated = self.tree.is_terminal(node, new_state, new_state.is_done())
                terminated = terminated or self.tree.is_composed_terminal(node, next_node)
                terminated = terminated or actions_done > max_steps

            if node.name[len("NavTo"):] == "NavTo":
                if np.floor(state.player_position[0]) == node.destination[0] and \
                        np.floor(state.player_position[1]) == node.destination[1]:
                    print("Successful NavTo", str(node.destination), "Player is at:", state.player_position)

            return state, actions_done, cumulative_reward

    def save_model(self, path):

        f = open(path, 'w')

        for node in self.tree.all_nodes:
            f.write(node.name + '\n')

            # Write C
            if node.is_primitive():
                f.write("0\n")
            else:
                f.write(str(len(node.C.keys())) + '\n')
                for key in node.C.keys():
                    f.write(key + ":" + str(node.C[key].tolist()) + '\n')

            # Write V
            f.write(str(len(node.V.keys())) + '\n')
            for key in node.V.keys():
                f.write(str(key) + ":" + str(node.V[key]) + '\n')

        print('The model was save at:', path)
        f.close()

    def load_model(self, path):
        self.tree.reset_tree()

        f = open(path, 'r')

        lines = [line for line in f.readlines() if len(line) > 0]

        i = 0
        while i < len(lines):

            # Read the name of the node
            node_name = lines[i]
            node = self.tree.get_node_by_name(node_name)
            i += 1

            # Number of keys for C
            c_len = int(lines[i])
            i += 1

            if c_len > 0:
                node.C = {}

            for line_offset in range(c_len):
                line_split = lines[i + line_offset].split(':')

                key = line_split[0]
                node.C[key] = np.zeros(len(node.children))
                values_as_str = line_split[1][1:-2].split(', ')

                for i in range(len(values_as_str)):
                    node.C[key][i] = float(values_as_str[i])
            i += c_len

            # Read number of V keys
            v_len = int(lines[i])
            i += 1
            for line_offset in range(v_len):
                line_split = lines[i + line_offset].split(':')

                key = line_split[0]
                node.V[key] = float(line_split[1])
            i += v_len


    def close_env(self):
        self.env.close_env()
