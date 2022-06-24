from tests import tests


def q_learn_train(train_path):
    tests.FlatQLearningShould.train_level(level_name="Level0", train_file_path=train_path,
                                          reward_type="PromoteAllStatesActive", max_steps=400, alpha=0.5,
                                          gamma=1, num_episodes=100, graphics="false")


def q_learn_perform(model_path):
    tests.FlatQLearningShould.perform_with_model(level_name="Level0", max_steps=400,
                                                 model_file_path=model_path, gamma=1)


if __name__ == '__main__':
    # q_learn_train("models/train_03.txt")
    q_learn_perform("models/train_03.txt")
