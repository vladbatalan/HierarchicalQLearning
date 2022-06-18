from qLearningUnit import QLearningUnit

HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':

    learning_unit = QLearningUnit(HOST, PORT)

    learning_unit.init_environment('-lvl SimpleLevel -g true -ctrl external -manual-step true -reward OnlyWinLose')

    learning_unit.train(frame_per_step=5, max_steps=800)




