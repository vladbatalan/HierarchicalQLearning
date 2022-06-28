import getopt
import sys

from learn.qLearningUnit import QLearningUnit

if __name__ == "__main__":

    # Total arguments
    argument_list = sys.argv[1:]

    options = 'flgseamhp'

    long_options = ["file", "lvl", "graphics", 'max-steps', 'episodes', 'alpha', 'gamma', 'host', 'port']

    try:
        path = None
        level_name = "Level0"
        graphics_mode = "false"
        max_steps = 200
        num_episodes = 1000
        alpha = 0.1
        gamma = 0.95
        host = "127.0.0.1"
        port = 4303

        arguments, values = getopt.getopt(argument_list, options, long_options)

        for current_argument, current_value in arguments:
            if current_argument in ('-f', '--file'):
                path = current_value

            if current_argument in ('-l', '--lvl'):
                level_name = current_value

            if current_argument in ('-g', '--graphics'):
                graphics_mode = current_value

            if current_argument in ('-s', '--max-steps'):
                max_steps = int(current_value)

            if current_argument in ('-e', '--episodes'):
                num_episodes = int(current_value)

            if current_argument in ('-a', '--alpha'):
                alpha = float(current_value)

            if current_argument in ('-m', '--gamma'):
                gamma = float(current_value)

            if current_argument in ('-h', '--host'):
                host = current_value

            if current_argument in ('-p', '--port'):
                port = current_value

        if path is None:
            raise Exception("There must be a valid path")

        print("Configs chosen:")
        print("host:", host)
        print("port:", port)
        print("path:", path)
        print("level_name:", level_name)
        print("graphics_mode:", graphics_mode)
        print("max_steps:", max_steps)
        print("num_episodes:", num_episodes)
        print("alpha:", alpha)
        print("gamma:", gamma)

        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": graphics_mode,
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": "OptimalReward"
        }

        learning_unit.init_environment(config, host, port)
        learning_unit.train(max_steps=max_steps, num_episodes=num_episodes, alpha=alpha, gamma=gamma)
        learning_unit.save_model(path)
        learning_unit.close_env()

    except getopt.error as err:
        # output error, and return with an error code
        print(str(err))
