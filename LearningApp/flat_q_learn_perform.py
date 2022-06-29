import getopt
import sys

from learn.qLearningUnit import QLearningUnit

if __name__ == "__main__":

    # Total arguments
    argument_list = sys.argv[1:]

    options = 'f:l:s:m:h:p:'

    long_options = ["file=", "lvl=", 'max-steps=', 'gamma=', 'host=', 'port=']

    try:
        path = None
        level_name = "Level0"
        max_steps = 200
        gamma = 0.95
        host = "127.0.0.1"
        port = 4303

        arguments, values = getopt.getopt(argument_list, options, long_options)

        for current_argument, current_value in arguments:
            if current_argument in ('-f', '--file'):
                path = current_value

            if current_argument in ('-l', '--lvl'):
                level_name = current_value

            if current_argument in ('-s', '--max-steps'):
                max_steps = int(current_value)

            if current_argument in ('-m', '--gamma'):
                gamma = float(current_value)

            if current_argument in ('-h', '--host'):
                host = current_value

            if current_argument in ('-p', '--port'):
                port = current_value

        if path is None:
            raise Exception("There must be a valid path")

        print("Configs chosen:")
        print("\thost:", host)
        print("\tport:", port)
        print("\tpath:", path)
        print("\tlevel_name:", level_name)
        print("\tmax_steps:", max_steps)
        print("\tgamma:", gamma)
        print()

        learning_unit = QLearningUnit()

        config = {
            "-lvl": level_name,
            "-g": "true",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": "OptimalReward"
        }

        learning_unit.init_environment(config, host, port)
        learning_unit.perform_with_model(model_path=path, time_delay=0.001, gamma=gamma, max_steps=max_steps)
        learning_unit.close_env()

    except getopt.error as err:
        # output error, and return with an error code
        print(str(err))
