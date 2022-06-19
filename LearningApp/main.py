from qLearningUnit import QLearningUnit

HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':

    learning_unit = QLearningUnit(HOST, PORT)

    learning_unit.init_environment(
        '-lvl Level0 -g false -ctrl external -manual-step true -reward PromoteAllStatesActive')

    learning_unit.train(frame_per_step=3, max_steps=400, num_episodes=6000, alpha=0.1, gamma=1)
