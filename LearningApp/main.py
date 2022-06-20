import tests
from qLearningUnit import QLearningUnit

HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':
    tests.TestUnit.FlatQLearningShould.train_level('Level0', 'train/q_model_all_states_gamma.txt',
                                                   reward_type='PromoteAllStatesActive', max_steps=800, alpha=0.1,
                                                   gamma=1, num_episodes=10000)
    tests.TestUnit.FlatQLearningShould.perform_with_model('Level0', 'train/q_model_all_states_gamma.txt')
