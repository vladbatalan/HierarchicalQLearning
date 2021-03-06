import getopt
import sys

from learn.maxQLearningUnit import MaxQLearningUnit0
from learn.maxQVersion2 import MaxQAgent

if __name__ == "__main__":

    # Total arguments
    argument_list = sys.argv[1:]

    options = "f:l:g:s:e:a:m:h:p:r:t"

    long_options = ["file=", "lvl=", "graphics=", "max-steps=", "episodes=", "alpha=", "gamma=", "host=", "port=", "reward=", "perform-test"]

    try:
        path = None
        level_name = "Level0"
        graphics_mode = "false"
        max_steps = 200
        num_episodes = 1000
        alpha = 0.2
        gamma = 0.99
        host = "127.0.0.1"
        port = 4303
        reward_type = "OptimalReward"
        with_perform_test = False

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

            if current_argument in ('-r', '--reward'):
                reward_type = current_value

            if current_argument in ('-t', '--perform-test'):
                with_perform_test = True

        if path is None:
            raise Exception("There must be a valid path")

        print("Configs chosen:")
        print("\thost:", host)
        print("\tport:", port)
        print("\tpath:", path)
        print("\tlevel_name:", level_name)
        print("\tgraphics_mode:", graphics_mode)
        print("\tmax_steps:", max_steps)
        print("\tnum_episodes:", num_episodes)
        print("\talpha:", alpha)
        print("\tgamma:", gamma)
        print("\twith perform:", with_perform_test)
        print()

        max_q_agent = MaxQAgent()

        config_data = {
            "-lvl": level_name,
            "-g": graphics_mode,
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": reward_type
        }

        max_q_agent.init_environment(config_data, host, port, tree_type="Determined")
        max_q_agent.train(alpha=alpha, gamma=gamma, num_episodes=num_episodes, max_steps=max_steps,
                          save_plots=path.split(".")[0], batches=False, with_perform_test=with_perform_test)
        max_q_agent.save_model(path)
        max_q_agent.close_env()

    except getopt.error as err:
        # output error, and return with an error code
        print(str(err))
