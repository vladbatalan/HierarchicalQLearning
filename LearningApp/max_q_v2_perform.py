import getopt
import sys

from learn.maxQLearningUnit import MaxQLearningUnit0
from learn.maxQVersion2 import MaxQAgent

if __name__ == "__main__":

    # Total arguments
    argument_list = sys.argv[1:]

    options = "f:l:h:p:"

    long_options = ["file=", "lvl=", "host=", "port="]

    try:
        path = None
        level_name = "Level0"
        graphics_mode = "false"
        host = "127.0.0.1"
        port = 4303

        arguments, values = getopt.getopt(argument_list, options, long_options)

        for current_argument, current_value in arguments:
            if current_argument in ('-f', '--file'):
                path = current_value

            if current_argument in ('-l', '--lvl'):
                level_name = current_value

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
        print("\tgraphics_mode:", graphics_mode)
        print()

        max_q_agent = MaxQAgent()

        config_data = {
            "-lvl": level_name,
            "-g": "true",
            "-ctrl": "external",
            "-manual-step": "true",
            "-reward": "OptimalReward"
        }

        max_q_agent.init_environment(config_data, host, port, tree_type="Determined")
        max_q_agent.load_model(path)
        max_q_agent.perform(0.001)
        max_q_agent.close_env()

    except getopt.error as err:
        # output error, and return with an error code
        print(str(err))
