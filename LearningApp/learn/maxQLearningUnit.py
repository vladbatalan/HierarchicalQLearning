import numpy as np

from api.env.customenv import CustomEnv
from api.stateDeserializer import DynamicLevelState
from learn.maxq_tree.node import Node
from learn.maxq_tree.tree_builder import TimeBenderTreeBuilder
from learn.util.utils import plot_results


class MaxQLearningUnit0:

    def __init__(self, env_jar_path=None):
        self.static_state = None
        self.env_jar_path = env_jar_path
        self.env = CustomEnv()
        self.tree = None

        self._action_chain = []

    def init_environment(self, args, host, port, jar_path=None):
        self.env.start_env(host, port, args, jar_path=jar_path)
        self.static_state = self.env.static_state
        self.tree = TimeBenderTreeBuilder.build_tree(self.static_state)
        self.tree.print()

    def train(self, alpha=0.5, gamma=0.95, num_episodes=2000, max_steps=3000, time_delay=0,
              expl_limit=1, logging=True, save_fig_path=None):

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

        plot_results(rs, alpha=alpha, gamma=gamma, nr_cum=num_episodes // 100 + 1,
                     path_with_starting_name=save_fig_path)

    def _evaluate(self, action: Node, state: DynamicLevelState):
        state_desc = str(state.basic_state_form())

        if action.is_primitive():
            if state_desc not in action.V.keys():
                action.V[state_desc] = 0

            return action.V[state_desc]
        else:
            v_max = None
            for child_action in action.children:
                temp = self._evaluate(child_action, state)

                if v_max is None or temp > v_max:
                    v_max = temp

            return v_max

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
                while len(self._action_chain) > 0 and self._action_chain[len(self._action_chain) - 1] != node.name:
                    self._action_chain.pop()

                if node.is_repetitive():
                    node.increase_repetition()

                # Get to bottom of tree recursively
                new_state, no_actions, reward = self._max_q_0_update(next_node, state, gamma, max_steps, time_delay,
                                                                     expl_limit)
                cumulative_reward += reward

                # Evaluate the expected value for the node
                evaluate_res = self._evaluate(node, new_state)

                # Update the C value of the node
                node.update_C(state, next_node, evaluate_res, gamma, no_actions)

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

    def _max_q_0_perform(self, node, state, gamma, max_steps, time_delay) -> (DynamicLevelState, int, float):
        print("Perform:", node.name, state.basic_state_form())
        if node.is_primitive():

            # Take a step using the primitive action of this node
            new_state, reward, done = self.env.step(node.primitive_action.name,
                                                    time_delay=time_delay)

            self._action_chain.append(node.name)
            # print("Action chain:", self._action_chain)

            return new_state, 1, reward

        else:
            cumulative_reward = 0
            actions_done = 0

            if node.is_repetitive():
                node.reset_repetitions()

            terminated = False

            self._action_chain.append(node.name)
            while not terminated:

                next_node = node.choose_action(state, 0)

                while len(self._action_chain) > 0 and self._action_chain[len(self._action_chain) - 1] != node.name:
                    self._action_chain.pop()

                if node.is_repetitive():
                    node.increase_repetition()

                # Get to bottom of tree recursively
                new_state, no_actions, reward = self._max_q_0_perform(next_node, state, gamma, max_steps, time_delay)
                cumulative_reward += reward

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

    def perform_with_model(self, model_path, max_steps=3000, gamma=0.95, time_delay=0):

        self.load_model(model_path)

        # Reset the env
        state = self.env.reset()

        self._action_chain = []

        new_state, time_passed, reward_sum = self._max_q_0_perform(self.tree.root_node, state, gamma, max_steps,
                                                                   time_delay)

        if not state.lost and not state.complete:
            print('Level was not completed, nor lost.')
        elif state.lost:
            print('Level lost.')
        elif state.complete:
            print('Level complete.')

        print('Reward:', reward_sum)
        print('Steps:', time_passed)

    def _evaluate_max_node(self, node, state: DynamicLevelState):

        state_form = str(state.basic_state_form())

        if node.is_primitive():
            if state_form not in node._V.keys():
                node._V[state_form] = 0

            return node._V[state], node.primitive_action.name
        else:
            next_node = None
            values = (None, None)

            for action_index, child_action in enumerate(node.children):
                v_child, primitive_action = self._evaluate_max_node(child_action, state)

                if state_form not in node.C.keys():
                    node.C[state_form] = np.zeros(len(node.children))

                if next_node is None:
                    values = (v_child + node.C[state_form][action_index], primitive_action)
                    next_node = child_action
                else:
                    if v_child + node.C[state_form][action_index] > values[0]:
                        values = (v_child + node.C[state_form][action_index])
                        next_node = child_action
            return values

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
            f.write(str(len(node._V.keys())) + '\n')
            for key in node._V.keys():
                f.write(str(key) + ":" + str(node._V[key]) + '\n')

        print('The model was save at:', path)
        f.close()

    def load_model(self, path):
        self.tree.reset_tree()

        f = open(path, 'r')

        def next_line():
            value = f.readline()
            if not value:
                return value
            return value[0:-1]

        #
        # lines = [line[0:-1] for line in f.readlines() if len(line) > 0]
        # for line in lines:
        #     print(line)

        while True:

            # Read the name of the node
            node_name = next_line()
            if not node_name:
                break

            # print("Node name:", node_name)
            node = self.tree.get_node_by_name(node_name)

            # Number of keys for C
            c_len = int(next_line())

            if c_len > 0:
                node.C = {}

            for line_offset in range(c_len):
                line_split = next_line().split(':')

                key = line_split[0]
                node.C[key] = np.zeros(len(node.children))
                values_as_str = line_split[1][1:-2].split(', ')

                for i in range(len(values_as_str)):
                    node.C[key][i] = float(values_as_str[i])

            # Read number of V keys
            v_len = int(next_line())
            for line_offset in range(v_len):
                line_split = next_line().split(':')

                key = line_split[0]
                node._V[key] = float(line_split[1])

        f.close()

    def close_env(self):
        self.env.close_env()
