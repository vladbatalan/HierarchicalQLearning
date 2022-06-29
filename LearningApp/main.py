import subprocess
from subprocess import Popen, PIPE
from config import jar_path

from tests.flat_q_learning_should import FlatQLearningShould


def q_learn_train(train_path):
    FlatQLearningShould.train_level(level_name="OnlyObjective", train_file_path=train_path,
                                    reward_type="OptimalReward", max_steps=100, alpha=0.5,
                                    gamma=1, num_episodes=100, graphics="false")


def q_learn_perform(model_path, gamma=1.0):
    FlatQLearningShould.perform_with_model(level_name="LeverWithObjective", max_steps=100,
                                           model_file_path=model_path, gamma=gamma)


HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':
    subprocess.call(['java', '-jar', jar_path, '-extern'])
