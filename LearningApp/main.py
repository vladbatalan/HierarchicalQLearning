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
    # FlatQLearningShould.train_level(level_name="OnlyObjective",
    #                                 train_file_path="models/only_objective/q_flat_01.txt",
    #                                 reward_type="OptimalReward",
    #                                 max_steps=100, num_episodes=10000,
    #                                 alpha=0.1, gamma=0.99999995, graphics="false")
    #
    # FlatQLearningShould.train_level(level_name="LeverWithObjective",
    #                                 train_file_path="models/lever_and_objective/q_flat_01.txt",
    #                                 reward_type="OptimalReward",
    #                                 max_steps=100, num_episodes=10000,
    #                                 alpha=0.1, gamma=0.99999995, graphics="false")
    # q_learn_perform("models/only_objective/q_flat_01.txt", 0.99999995)
    # q_learn_perform("models/lever_and_objective/q_flat_01.txt", 0.99999995)
    # MaxQTreeShould.train(model_path="models/lever_and_objective/max_q_01.txt", level_name="LeverWithObjective",
    #                      alpha=0.5, gamma=0.99999995, num_episodes=10, max_steps=150)
    # MaxQTreeShould.train(model_path="models/lever_and_objective/max_q_01.txt",
    #                                 level_name="LeverWithObjective",
    #                                 gamma=0.99999995, max_steps=150, num_episodes=20000)
