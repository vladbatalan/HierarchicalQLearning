import numpy as np

from api.env.customenv import CustomEnv
from api.stateDeserializer import DynamicLevelState
from learn.maxq_v2_tree.trees import *
from learn.util.utils import plot_results


class MaxQAgent:
    def __init__(self):
        self._use_determined_tree = None
        self._C = None
        self._V = None
        self._repetitive_counter = None
        self.env = CustomEnv()
        self.tree = None
        self.new_state = None

        # Used to follow actions
        self._action_chain = []
        self._last_action = -1

        # The initial shape of the environment
        self.static_state = None

        # Training parameters
        self.alpha = 0.5
        self.gamma = 0.99
        self.r_sum = 0
        self.done = False
        self.num_of_actions = 0
        self._init_eps = 1
        self.eps = self._init_eps
        self.max_steps = 150
        self.current_reward = 0

    def init_environment(self, args, host, port, tree_type: str = "Advanced"):
        self.env.start_env(host, port, args)
        self.static_state = self.env.static_state
        self._tree_type = tree_type
        self._build_max_q_graph()

    def _build_max_q_graph(self):
        self.lever_positions = self.static_state.lever_positions
        self.time_machine_position = self.static_state.time_machine_position
        self.goal_position = self.static_state.game_objective_position

        # Order matters
        self.all_positions = self.lever_positions + [self.time_machine_position] + [self.goal_position]

        if len(self.lever_positions) == 0:
            self.tree = NoLeverTree()
        else:
            if self._tree_type == "Advanced":
                self.tree = AdvancedTree()
            elif self._tree_type == "Determined":
                self.tree = AdvancedTreeDeterminedHighActions()
            else:
                self.tree = DifferentPrimitivesForNavTree()

        self.tree.build_tree(self.static_state)

        # Parameters of MaxQ algorithm
        self._V = np.array([{} for i in range(self.tree.action_space_size)])
        # If self.C[action] has no key of state, complete with
        #       self.C[action][state] = np.zeros(self.tree.action_space_size)
        self._C = np.array([{} for i in range(self.tree.action_space_size)])

        # # Create C_hat for MaxQQ alg
        # self._C_hat = np.array([{} for i in range(self.tree.action_space_size)])

    def is_primitive(self, action):
        return self.tree.is_primitive(action)

    def is_terminal(self, action, done, state: DynamicLevelState, last_action=-1):
        return self.tree.is_terminal(action, done, state, last_action)

    def get_estimate_reward(self, action, state: DynamicLevelState):
        state_str = str(state.basic_state_form())

        if self.is_primitive(action):
            return self.get_V(action, state_str, is_copy=True)

        else:
            for j in self.tree.graph[action]:
                self.set_V_value(j, state_str, self.get_estimate_reward(j, state), is_copy=True)
            evaluation_vector = np.arange(0)
            for child_action in self.tree.graph[action]:
                evaluation_vector = np.concatenate(
                    (evaluation_vector, [self.get_V(child_action, state_str, is_copy=True)]))
            max_action = np.argmax(evaluation_vector)
            return self.get_V(max_action, state_str, is_copy=True)

    def choose_action(self, from_action, state: DynamicLevelState, eps):
        # A list of all accessible actions
        possible_action_arr = np.arange(0)

        # A list of each possible action reward estimator
        q_arr = np.arange(0)

        # State as string
        state_str = str(state.basic_state_form())

        if from_action == 14:
            print("Choosing action for One Dest:")
            print("\tChildren:", [self.tree.action_names[act] for act in self.tree.graph[from_action]])

        # For each child action, that is primitive or not terminal, add it's cumulative value
        for child_action in self.tree.graph[from_action]:

            # print("\tIs terminal (" + self.tree.action_names[child_action] + "):", end=" ")
            is_terminal = self.is_terminal(child_action, self.done, state, self._last_action)
            # print(is_terminal)
            # print("\tIs primitive (" + self.tree.action_names[child_action] + "):", self.is_primitive(child_action))

            if self.is_primitive(child_action) or not is_terminal:
                estimated_value = self.get_V(child_action, state_str) + self.get_C(from_action, state_str, child_action)
                q_arr = np.concatenate((q_arr, [estimated_value]))
                possible_action_arr = np.concatenate((possible_action_arr, [child_action]))
        # print("\tQ Arr:", q_arr)
        # print("\tPosible actions:", [self.tree.action_names[act] for act in possible_action_arr])
        max_arg = np.argmax(q_arr)

        if from_action == 14:
            print("\tPossible actions:", [self.tree.action_names[act] for act in possible_action_arr])
            print("\tBest action:", self.tree.action_names[possible_action_arr[max_arg]])
            print()

        if np.random.rand(1) < eps:
            # Choose random action
            # print("Choose random action for:", self.action_names[from_action])
            # print("\tpossible actions:", [self.action_names[act] for act in possible_action_arr])
            act = np.random.choice(possible_action_arr)
            # print("\t\tChosen action:", self.tree.action_names[act])
            # print()
            return act
        else:
            # if self.tree.action_names[from_action][0:len("NavTo")] == "NavTo":
            # print("Choose action for:", self.tree.action_names[from_action])
            # print("Player position:", state.player_position)
            # print("\tPossible pairs:")
            # for pair in [(self.tree.action_names[elem], q_arr[elem]) for elem in possible_action_arr]:
            #     print("\t\t", pair)
            # print("\tWe choose:", (self.tree.action_names[possible_action_arr[max_arg]], q_arr[max_arg]))
            # Choose the best valued action
            # print("\t\tChosen action:", self.tree.action_names[possible_action_arr[max_arg]])
            return possible_action_arr[max_arg]

    def max_q_0(self, action, state: DynamicLevelState):
        if self.done:
            return

        self.done = False
        state_str = str(state.basic_state_form())

        # Action is primitive
        if self.is_primitive(action):

            # Step ahead in env
            self.new_state, reward, self.done = self.env.step(self.tree.env_actions[action])

            # Update the reward, sum and the action number and last action
            self.r_sum += reward
            self.num_of_actions += 1

            # Bellman equation for V
            new_V = self.get_V(action, state_str) + self.alpha * (reward - self.get_V(action, state_str))
            self.set_V_value(action, state_str, new_V)

            self._action_chain.append(self.tree.action_names[action])
            # print("Move", self.num_of_actions, ":", self._action_chain)
            return 1

        else:
            count_actions = 0
            # print("Curent action:", self.tree.action_names[action])
            self._action_chain.append(self.tree.action_names[action])

            # print("Is terminal from max q:")
            is_terminal = self.is_terminal(action, self.done, state, self._last_action)
            while not is_terminal:

                # There are 3 types of composed actions:
                # 1) Simple actions - subtasks will run in any order, until the termination is complete
                # 2) Ordered actions - each subtask will execute only once in order
                # 3) Repetitive actions - subtask will be repeated self.repetitive_counter[action] times
                action_type = self.tree.action_type(action)
                if action_type == 1:

                    self._clear_action_list_until(action)

                    # It is a simple action
                    # Choose an action accordingly to epsilon
                    next_action = self.choose_action(action, state, self.eps)

                    # Do maxQ for chosen child
                    n = self._update_node(action, next_action, state)

                    state = self.new_state

                    # Update number of actions and the state
                    count_actions += n

                    # print("Is terminal from max q:")
                    is_terminal = self.is_terminal(action, self.done, state, self._last_action)

                elif action_type == 2:
                    # It is an ordered action
                    # Every node must execute in order

                    for next_action in self.tree.graph[action]:

                        self._clear_action_list_until(action)
                        if not self.tree.is_primitive(next_action):
                            self._action_chain.append(self.tree.action_names[next_action])

                        # If the action ended prematurely
                        if is_terminal:
                            return count_actions

                        # Execute node only once
                        # Do maxQ for child and state
                        n = self._update_node(action, next_action, state)

                        state = self.new_state

                        # Update number of actions and the state
                        count_actions += n

                        # print("Is terminal from max q:")
                        is_terminal = self.is_terminal(action, self.done, state, self._last_action)

                elif action_type == 3:
                    # This is a repetitive action. It executes of self.tree.max_counter[action] times and finishes
                    # Repetitive actions can have more children
                    next_action = self.choose_action(action, state, self.eps)
                    self.tree.repetitive_counter[action] = 0

                    for times in range(self.tree.max_count[action]):

                        self._clear_action_list_until(action)

                        # If the action ended prematurely
                        if is_terminal:
                            return count_actions

                        # Do maxQ for child
                        n = self._update_node(action, next_action, state)

                        # Update current state
                        state = self.new_state

                        # Update number of actions and the state
                        count_actions += n

                        # Repeat 1
                        self.tree.repetitive_counter[action] += 1

                        # print("Is terminal from max q:")
                        is_terminal = self.is_terminal(action, self.done, state, self._last_action)

            return count_actions

    def _update_node(self, action, next_action, state):

        state_str = str(state.basic_state_form())
        n = self.max_q_0(next_action, state)
        self._last_action = next_action

        # Get the best value of next action

        self.V_copy = self._V.copy()
        estimate_reward = self.get_estimate_reward(action, self.new_state)

        # if estimate_reward > 1:
        #     print("Node update:", self.tree.action_names[action])
        #     print("Estimated reward:", estimate_reward)
        #     print()

        # if self.done and self.num_of_actions < self.max_steps:
        #     print("In node:", self.tree.action_names[action])
        #     print("Estimated reward for terminated due to exceed of episodes:")
        #     print("\t", estimate_reward)

        # Update C of node
        c_value = self.get_C(action, state_str, next_action)
        # print("\tOld C:", c_value)
        # print("\tEstimated reward:", estimate_reward)
        new_C = c_value + self.alpha * ((self.gamma ** n) * estimate_reward - c_value)
        self.set_C_value(action, state_str, next_action, new_C)

        # if self.done and self.num_of_actions < self.max_steps:
        #     print("\tc before:", c_value)
        #     print("\tc after:", new_C)
        #     print()
        #
        if self.num_of_actions >= self.max_steps:
            self.done = True

        return n

    def _clear_action_list_until(self, action):
        while self._action_chain[-1] != self.tree.action_names[action]:
            self._action_chain.pop()

    def reset(self):
        starting_state = self.env.reset()
        self.r_sum = 0
        self.num_of_actions = 0
        self.done = False
        self.new_state = None
        self.tree.reset()
        self._action_chain = []
        self._last_action = -1

        return starting_state

    def train(self, max_steps, num_episodes, alpha=0.5, gamma=0.99, save_plots=None, batches=False,
              with_perform_test=True):

        save_plots_root = save_plots.split(".")[0]

        # Init inner params
        self.max_steps = max_steps
        self.alpha = alpha
        self.gamma = gamma
        self.eps = 0.001
        eps_decay = 0
        # eps_decay = (self.eps - 0.1) / num_episodes

        # For batch strategy
        batch_size = num_episodes // 20
        batch_wins = 0

        # For perform test
        perform_at = 50

        # For plot usage
        rs = np.zeros(num_episodes)

        # For each episode
        for episode in range(num_episodes):
            state = self.reset()

            # Start the action from root
            self.max_q_0(self.tree.root, state)

            rs[episode] = self.r_sum

            # Decay epsilon
            self.eps -= eps_decay

            batch_number = episode // batch_size
            if (episode - 1) // batch_size != batch_number:
                # We entered into next batch
                # Check the number of wins in order to stop the model
                if batch_wins > batch_size / 2 and batches is True:
                    # Make a save of the batch
                    plot_file = save_plots_root
                    model_path = save_plots_root + "_batch_" + str(batch_number) + ".txt"
                    plot_results(rs[0:episode], alpha, gamma, 50, plot_file, batch_number=batch_number)
                    self.save_model(model_path)
                batch_wins = 0

            if self.r_sum > 0:
                batch_wins += 1

            print('Episode:', episode)
            print('Reward:', self.r_sum)
            print('Explore:', str(self.eps * 100) + '%')
            print('Number of steps:', self.num_of_actions)
            print()

            # Once every perform_at episodes, try to perform
            if with_perform_test is True and (episode + 1) % perform_at == 0:

                print("Perform at episode", episode)
                reward = self.perform(0, max_steps=max_steps)

                # If level is won, keep the model, stop training
                if reward > 0:
                    num_episodes = episode
                    break

        plot_results(rs[0:num_episodes], self.alpha, self.gamma, 50, save_plots)

    def max_q_0_perform(self, action, state: DynamicLevelState, time_delay=0, max_steps=None):
        if self.done:
            return

        self.done = False

        # Action is primitive
        if self.is_primitive(action):

            # Step ahead in env
            self.new_state, reward, self.done = self.env.step(self.tree.env_actions[action], time_delay=time_delay)

            # Update the reward, sum and the action number and last action
            self.r_sum += reward
            self.num_of_actions += 1

            self._action_chain.append(self.tree.action_names[action])
            if max_steps is None:
                print("Move", self.num_of_actions, ":", self._action_chain)
            return 1

        else:
            count_actions = 0
            # print("Curent action:", self.tree.action_names[action])
            self._action_chain.append(self.tree.action_names[action])

            # print("Is terminal from max q:")
            is_terminal = self.is_terminal(action, self.done, state, self._last_action)
            while not is_terminal:

                # There are 3 types of composed actions:
                # 1) Simple actions - subtasks will run in any order, until the termination is complete
                # 2) Ordered actions - each subtask will execute only once in order
                # 3) Repetitive actions - subtask will be repeated self.repetitive_counter[action] times
                action_type = self.tree.action_type(action)
                if action_type == 1:

                    self._clear_action_list_until(action)

                    # It is a simple action
                    # Choose an action accordingly to epsilon = 0
                    next_action = self.choose_action(action, state, 0)

                    n = self.max_q_0_perform(next_action, state, time_delay, max_steps)
                    self._last_action = next_action

                    state = self.new_state

                    # Update number of actions and the state
                    count_actions += n

                    if max_steps is not None and self.num_of_actions >= max_steps:
                        self.done = True

                    # print("Is terminal from max q:")
                    is_terminal = self.is_terminal(action, self.done, state, self._last_action)

                elif action_type == 2:
                    # It is an ordered action
                    # Every node must execute in order

                    for next_action in self.tree.graph[action]:

                        self._clear_action_list_until(action)

                        # If the action ended prematurely
                        if is_terminal:
                            return count_actions

                        # Execute node only once
                        # Do maxQ for child and state
                        n = self.max_q_0_perform(next_action, state, time_delay=time_delay, max_steps=max_steps)
                        self._last_action = next_action

                        state = self.new_state

                        # Update number of actions and the state
                        count_actions += n

                        if max_steps is not None and self.num_of_actions >= max_steps:
                            self.done = True

                        # print("Is terminal from max q:")
                        is_terminal = self.is_terminal(action, self.done, state, self._last_action)

                elif action_type == 3:

                    # This is a repetitive action. It executes of self.tree.max_counter[action] times and finishes
                    # Repetitive actions can have more children
                    next_action = self.choose_action(action, state, 0)
                    self.tree.repetitive_counter[action] = 0

                    for times in range(self.tree.max_count[action]):

                        self._clear_action_list_until(action)

                        # If the action ended prematurely
                        if is_terminal:
                            return count_actions

                        # Do maxQ for child
                        n = self.max_q_0_perform(next_action, state, time_delay=time_delay, max_steps=max_steps)
                        self._last_action = next_action

                        # Update current state
                        state = self.new_state

                        # Update number of actions and the state
                        count_actions += n

                        if max_steps is not None and self.num_of_actions >= max_steps:
                            self.done = True

                        # Repeat 1
                        self.tree.repetitive_counter[action] += 1

                        # print("Is terminal from max q:")
                        is_terminal = self.is_terminal(action, self.done, state, self._last_action)

            return count_actions

    def perform(self, time_delay=0, max_steps=None):

        print("Prepare env for performing:")

        # Reset the env
        state = self.reset()

        self.max_q_0_perform(self.tree.root, state, time_delay, max_steps)
        state = self.new_state
        reward_sum = self.r_sum
        time_passed = self.num_of_actions

        if not state.lost and not state.complete:
            print('\tLevel was not completed, nor lost.')
        elif state.lost:
            print('\tLevel lost.')
        elif state.complete:
            print('\tLevel complete.')

        print('\tReward:', reward_sum)
        print('\tSteps:', time_passed)
        print()

        return reward_sum

    def save_model(self, path):

        f = open(path, 'w')

        for action in range(self.tree.action_space_size):
            f.write(str(action) + '\n')

            # Write C
            f.write(str(len(self._C[action].keys())) + "\n")
            for key in self._C[action].keys():
                f.write(key + ":" + str(self.get_C(action, key).tolist()) + '\n')

            # Write V
            f.write(str(len(self._V[action].keys())) + "\n")
            for key in self._V[action].keys():
                f.write(str(key) + ":" + str(self.get_V(action, key)) + '\n')

        print('The model was save at:', path)
        f.close()

    def load_model(self, path):
        f = open(path, 'r')

        def next_line():
            value = f.readline()
            if not value:
                return value
            return value[0:-1]

        self._V = np.array([{} for i in range(self.tree.action_space_size)])
        self._C = np.array([{} for i in range(self.tree.action_space_size)])

        while True:

            # Read the code of the action
            action = next_line()
            if not action:
                break

            action = int(action)

            # Number of keys for C
            c_len = int(next_line())

            for line_offset in range(c_len):
                line_split = next_line().split(':')

                key = line_split[0]
                values_as_str = line_split[1][1:-2].split(', ')

                for child_action in range(len(values_as_str)):
                    self.set_C_value(action, key, child_action, float(values_as_str[child_action]))

            # Read number of V keys
            v_len = int(next_line())
            for line_offset in range(v_len):
                line_split = next_line().split(':')

                key = line_split[0]
                self.set_V_value(action, key, float(line_split[1]))

        f.close()

    def close_env(self):
        self.env.close_env()

    # Setters and Getters
    def get_V(self, action, state_str: str, is_copy=False):
        if not is_copy:
            if state_str not in self._V[action].keys():
                self._V[action][state_str] = 0
            return self._V[action][state_str]
        else:
            if state_str not in self.V_copy[action].keys():
                self.V_copy[action][state_str] = 0
            return self.V_copy[action][state_str]

    def get_C(self, action, state_str: str, action2=None):
        if state_str not in self._C[action].keys():
            self._C[action][state_str] = np.zeros(self.tree.action_space_size)
        if action2 is None:
            return self._C[action][state_str]
        return self._C[action][state_str][action2]

    # def get_C_hat(self, action, state_str: str, action2=None):
    #     if state_str not in self._C_hat[action].keys():
    #         self._C_hat[action][state_str] = np.zeros(self.tree.action_space_size)
    #     if action2 is None:
    #         return self._C_hat[action][state_str]
    #     return self._C_hat[action][state_str][action2]

    def set_C_value(self, action, state_str: str, action2, new_value):
        self.get_C(action, state_str)
        self._C[action][state_str][action2] = new_value

    # def set_C_hat_value(self, action, state_str: str, action2, new_value):
    #     self.get_C_hat(action, state_str)
    #     self._C_hat[action][state_str][action2] = new_value

    def set_V_value(self, action, state_str: str, new_value, is_copy=False):
        self.get_V(action, state_str, is_copy=is_copy)

        if not is_copy:
            self._V[action][state_str] = new_value
        else:
            self.V_copy[action][state_str] = new_value
