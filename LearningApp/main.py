import tests
from qLearningUnit import QLearningUnit

HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':
    # tests.TestUnit.FlatQLearningShould.train_level('Level0', 'train/new_actions.txt',
    #                                                reward_type='PromoteAllStatesActive', max_steps=800, alpha=0.1,
    #                                                gamma=1, num_episodes=1000)
    tests.TestUnit.FlatQLearningShould.perform_with_model('Level0', 'train/more_divided_states.txt', max_steps=400,
                                                          gamma=0.95)

    # tests.TestUnit.FlatQLearningShould.train_level('Level0', 'train/more_divided_states.txt',
    #                                                reward_type='PromoteAllStatesActive', max_steps=400, alpha=0.2,
    #                                                gamma=0.95, num_episodes=30, graphics="false")
