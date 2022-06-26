from learn.qLearningUnit import QLearningUnit

HOST = "127.0.0.1"
PORT = 4303


class FlatQLearningShould:

    @staticmethod
    def train_level(level_name, train_file_path, reward_type, max_steps=200, num_episodes=1000,
                    alpha=0.1, gamma=0.95, graphics="false"):
        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": graphics,
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": reward_type
        }

        learning_unit.init_environment(config, HOST, PORT)

        learning_unit.train(max_steps=max_steps, num_episodes=num_episodes,
                            alpha=alpha, gamma=gamma)

        learning_unit.save_model(train_file_path)

        learning_unit.close_env()

    @staticmethod
    def perform_with_model(level_name, model_file_path, reward_type='PromoteAllStatesActive',
                           max_steps=200, gamma=0.95):
        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": "true",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": reward_type
        }

        learning_unit.init_environment(config, HOST, PORT)

        learning_unit.perform_with_model(model_file_path, time_delay=0.001, gamma=gamma, max_steps=max_steps)

        learning_unit.close_env()
