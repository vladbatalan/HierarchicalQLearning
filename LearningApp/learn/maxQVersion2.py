import numpy as np

from api.apiCommands import HyperActionsEnum
from api.env.customenv import CustomEnv
from api.stateDeserializer import DynamicLevelState
from learn.util.utils import plot_results


class MaxQAgent:
    def __init__(self):
        self._repetitive_counter = None
        self.env = CustomEnv()
        self.graph = None
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

    def init_environment(self, args, host, port):
        self.env.start_env(host, port, args)
        self.static_state = self.env.static_state
        self._build_max_q_graph()

    def _build_max_q_graph(self):
        self.lever_positions = self.static_state.lever_positions
        self.time_machine_position = self.static_state.time_machine_position
        self.goal_position = self.static_state.game_objective_position

        # Order matters
        self.all_positions = self.lever_positions + [self.time_machine_position] + [self.goal_position]
        self.action_names = [
            "MOVE_LEFT",
            "MOVE_RIGHT",
            "STAND",
            "JUMP_PRESS",
            "JUMP_RELEASE",
            "SPACE_FOR_TELEPORT",
            "SPACE_FOR_GOAL",
            "STAND_FOR_ACCESS",
        ]

        # Primitive actions
        move_left = self.left = 0
        move_right = self.right = 1
        stand = self.stand = 2
        jump_press = self.jump_press = 3
        jump_release = self.jump_release = 4
        space_for_teleport = self.space_for_teleport = 5
        space_for_goal = self.space_for_goal = 6
        stand_for_access = self.stand_for_access = 7
        self.primitive_actions = 8
        self.env_actions = [
            HyperActionsEnum.LEFT_PRESSED.name,
            HyperActionsEnum.RIGHT_PRESSED.name,
            HyperActionsEnum.STAND_ACTION.name,
            HyperActionsEnum.JUMP_PRESSED.name,
            HyperActionsEnum.JUMP_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name,
            HyperActionsEnum.SPACE_RELEASED.name
        ]

        # The navigation actions
        self.nav_offset = 8
        nav_to_levers = self.nav_to_levers = [self.nav_offset + i for i, value in
                                              enumerate(self.lever_positions)]
        nav_to_time_machine = self.nav_to_time_machine = self.nav_offset + len(self.static_state.lever_positions)
        nav_to_goal = self.nav_to_goal = nav_to_time_machine + 1

        for i in range(self.nav_offset, nav_to_goal + 1):
            self.action_names.append("NavTo" + str(self.all_positions[i - self.nav_offset]))

        self.action_names += [
            "MaxTimeTravel",
            "MaxGoal",
            "RepetitiveStand",
            "MaxAccessPoint",
            "MaxRoot"
        ]

        # Composed actions
        max_time_travel = self.max_time_travel = nav_to_goal + 1
        max_goal = self.max_goal = max_time_travel + 1
        repetitive_stand = self.repetitive_stand = max_goal + 1
        self._repetitive_counter = 0  # Parameter that counts how many times repetitive was accessed
        self._max_repeats = 50  # Parameter that shows the maximum number of repetitions
        max_access_point = self.max_access_point = repetitive_stand + 1

        # The root
        root = self.root = max_access_point + 1

        # Number of actions
        self.action_space_size = root + 1

        # Parameters of MaxQ algorithm
        self._V = np.array([{} for i in range(self.action_space_size)])
        # If self.C[action] has no key of state, complete with self.C[action][state] = np.zeros(self.action_space_size)
        self._C = np.array([{} for i in range(self.action_space_size)])

        self.graph = []
        # For each primitive action, add set to graph
        for primitive in range(self.primitive_actions):
            self.graph.append(set())

        # Movement actions
        # For each nav_to_lever_action append move_primitive_actions
        for nav_to_lever in nav_to_levers:
            self.graph.append({move_left, move_right, stand, jump_press, jump_release})
        # Append directions for navigate_to_time_machine
        self.graph.append({move_left, move_right, stand, jump_press, jump_release})
        # Append directions for navigate_to_goal
        self.graph.append({move_left, move_right, stand, jump_press, jump_release})

        # Max time travel
        self.graph.append({nav_to_time_machine, space_for_teleport})

        # Max goal
        self.graph.append({nav_to_goal, space_for_goal})

        # Repetitive stand
        self.graph.append({stand_for_access})

        # Max access point
        all_access_moves = set()
        all_access_moves.add(repetitive_stand)
        # All actions moves to levers
        for element in nav_to_levers:
            all_access_moves.add(element)
        all_access_moves.add(max_time_travel)
        self.graph.append(all_access_moves)

        # For root
        self.graph.append({max_access_point, max_goal})

    def is_primitive(self, action):
        return action < self.primitive_actions

    def is_terminal(self, action, done, state: DynamicLevelState, last_action=-1):
        player_position = (int(state.player_position[0]), int(state.player_position[1]))

        # print("Is terminal called from:", state.basic_state_form())
        # print("\taction:", self.action_names[action])
        # print("\tdone:", done)
        # if last_action != -1:
        #     print("\tlast action:", self.action_names[last_action])

        response = True

        # If game is over, return true
        if done:
            response = True

        # If it is root, continue until game done
        elif action == self.root:
            response = done

        # It is a navigation action
        elif self.nav_offset <= action < self.nav_offset + len(self.all_positions):
            target_position = self.all_positions[action - self.nav_offset]
            response = (player_position[0] == target_position[0] and player_position[1] == target_position[1])

        # Repeat _max_repeats times
        elif action == self.repetitive_stand:
            if self._repetitive_counter >= self._max_repeats:
                self._repetitive_counter = 0
                response = True
            else:
                response = False

        # Action is complete only when it is at the right position and teleports in time
        elif action == self.max_time_travel:
            is_positioned_correctly = (player_position[0] == self.time_machine_position[0] and
                                       player_position[1] == self.time_machine_position[1])

            response = (is_positioned_correctly and last_action == self.space_for_teleport)

        # Action is completed only when goal was reached and space was pressed and goal is active
        elif action == self.max_goal:
            is_positioned_correctly = (player_position[0] == self.goal_position[0] and
                                       player_position[1] == self.goal_position[1])

            response = (is_positioned_correctly and last_action == self.space_for_goal and state.objective_active)

        # Action presumes that player went to Lever, kept it active for some moments and
        #   returned to time_machine and teleported
        elif action == self.max_access_point:
            response = (last_action == self.max_time_travel)

        # Primitive actions and immediately
        elif self.is_primitive(action):
            response = True

        # print("\tresponse:", response)
        # print()
        return response

    def get_estimate_reward(self, action, state: DynamicLevelState):
        state_str = str(state.basic_state_form())

        if self.is_primitive(action):
            return self.get_V(action, state_str)

        else:
            evaluation_vector = np.arange(0)
            for child_action in self.graph[action]:
                evaluation_vector = np.concatenate((evaluation_vector, [self.get_estimate_reward(child_action, state)]))
            max_action = np.argmax(evaluation_vector)
            return self.get_V(max_action, state_str)

    def choose_action(self, from_action, state: DynamicLevelState, eps):
        # print("Choose action for:", self.action_names[from_action])
        # A list of all accessible actions
        possible_action_arr = np.arange(0)

        # A list of each possible action reward estimator
        q_arr = np.arange(0)

        # State as string
        state_str = str(state.basic_state_form())

        # For each child action, that is primitive or not terminal, add it's cumulative value
        for child_action in self.graph[from_action]:
            # print("Is terminal from choose_action:")
            is_terminal = self.is_terminal(child_action, self.done, state, self._last_action)

            if self.is_primitive(child_action) or (not is_terminal):
                estimated_value = self.get_V(child_action, state_str) + self.get_C(from_action, state_str, child_action)
                q_arr = np.concatenate((q_arr, [estimated_value]))
                possible_action_arr = np.concatenate((possible_action_arr, [child_action]))

        max_arg = np.argmax(q_arr)

        if np.random.rand(1) < eps:
            # Choose random action
            # print("Choose random action for:", self.action_names[from_action])
            # print("\tpossible actions:", [self.action_names[act] for act in possible_action_arr])
            return np.random.choice(possible_action_arr)
        else:
            # Choose the best valued action
            return possible_action_arr[max_arg]

    def max_q_0(self, action, state: DynamicLevelState):
        if self.done:
            return

        self.done = False
        state_str = str(state.basic_state_form())

        # Action is primitive
        if self.is_primitive(action):

            # Step ahead in env
            self.new_state, reward, self.done = self.env.step(self.env_actions[action])

            # Update the reward, sum and the action number and last action
            self.r_sum += reward
            self.num_of_actions += 1

            # Bellman equation for V
            new_V = self.get_V(action, state_str) + self.alpha * (reward - self.get_V(action, state_str))
            self.set_V_value(action, state_str, new_V)

            self._action_chain.append(self.action_names[action])
            # print("Action chain:")
            # print(self._action_chain)
            # print()

            return 1

        else:
            count_actions = 0
            if action == self.repetitive_stand:
                self._repetitive_counter = 0

            self._action_chain.append(self.action_names[action])

            # print("Is terminal from max q:")
            is_terminal = self.is_terminal(action, self.done, state, self._last_action)
            while not is_terminal:
                while self._action_chain[-1] != self.action_names[action]:
                    self._action_chain.pop()

                # Choose an action accordingly to epsilon
                next_action = self.choose_action(action, state, self.eps)

                if action == self.repetitive_stand:
                    self._repetitive_counter += 1

                # Do maxQ for child and state
                n = self.max_q_0(next_action, state)
                self._last_action = next_action

                # Get the best value of next action
                estimate_reward = self.get_estimate_reward(action, self.new_state)

                # Update C of node
                c_value = self.get_C(action, state_str, next_action)
                try:
                    new_C = c_value + self.alpha * ((self.gamma ** n) * estimate_reward - c_value)
                except Exception as e:
                    print("ERROR:")
                    print("\testimated reward:", estimate_reward)
                    print("\told c value:", c_value)
                    print("\tgamma:", self.gamma)
                    print("\tn:", n)
                    print("\tsteps:", self.num_of_actions)
                    raise e
                self.set_C_value(action, state_str, next_action, new_C)

                # Update number of actions and the state
                count_actions += n

                state = self.new_state

                if self.num_of_actions >= self.max_steps:
                    self.done = True

                # print("Is terminal from max q:")
                is_terminal = self.is_terminal(action, self.done, state, self._last_action)
            return count_actions

    def reset(self):
        starting_state = self.env.reset()
        self.r_sum = 0
        self.num_of_actions = 0
        self.done = False
        self.new_state = None
        self._action_chain = []

        return starting_state

    def train(self, max_steps, num_episodes, alpha=0.5, gamma=0.99, save_plots=None):

        # Init inner params
        self.max_steps = max_steps
        self.alpha = alpha
        self.gamma = gamma
        self.eps = self._init_eps
        eps_decay = (self.eps - 0.1) / num_episodes

        # For plot usage
        rs = np.zeros(num_episodes)

        # For each episode
        for episode in range(num_episodes):
            state = self.reset()

            # Start the action from root
            self.max_q_0(self.root, state)

            rs[episode] = self.r_sum

            # Decay epsilon
            self.eps -= eps_decay

            print('Episode:', episode)
            print('Reward:', self.r_sum)
            print('Explore:', str(self.eps * 100) + '%')
            print()

        plot_results(rs, self.alpha, self.gamma, num_episodes // 100 + 1, save_plots)

    def close_env(self):
        self.env.close_env()

    # Setters and Getters
    def get_V(self, action, state_str: str):
        if state_str not in self._V[action].keys():
            self._V[action][state_str] = 0
        return self._V[action][state_str]

    def get_C(self, action, state_str: str, action2=None):
        if state_str not in self._C[action].keys():
            self._C[action][state_str] = np.zeros(self.action_space_size)
        if action2 is None:
            return self._C[action][state_str]
        return self._C[action][state_str][action2]

    def set_C_value(self, action, state_str: str, action2, new_value):
        self.get_C(action, state_str)
        self._C[action][state_str][action2] = new_value

    def set_V_value(self, action, state_str: str, new_value):
        self.get_V(action, state_str)
        self._V[action][state_str] = new_value
