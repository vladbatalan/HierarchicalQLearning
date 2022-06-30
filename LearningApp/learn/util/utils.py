import numpy as np
from matplotlib import pyplot as plt


def plot_results(rs, alpha, gamma, nr_cum=50, path_with_starting_name=None, batch_number=None):
    number_of_episodes = len(rs)

    # Plot reward vs episodes
    # Sliding window average
    r_cumulative_sum = np.cumsum(np.insert(rs, 0, 0))
    r_cumulative_sum = (r_cumulative_sum[int(nr_cum):] - r_cumulative_sum[:-int(nr_cum)]) / nr_cum

    title = 'Points over episodes (alpha=' + str(alpha) + ', gamma=' + str(gamma) + ')'
    if batch_number is not None:
        title = 'Points over episodes (alpha=' + str(alpha) + ', gamma=' + str(gamma) + ', batch=' + str(
            batch_number) + ')'

    # Plot
    plt.title(title)
    plt.ylabel('Points')
    plt.xlabel('Episodes')
    plt.plot(r_cumulative_sum)
    if path_with_starting_name is not None:
        if batch_number is None:
            plt.savefig(path_with_starting_name + "_points.png")
        else:
            plt.savefig(path_with_starting_name + "_batch_" + str(batch_number) + "_points.png")
    plt.show()

    avg_reward = sum(rs) / len(rs)
    print('Average reward per episode:', avg_reward)

    # Print number of times the goal was reached
    n = number_of_episodes // 10
    num_gs = np.zeros(10)
    labels = []

    for i in range(10):
        num_gs[i] = np.sum(rs[i * n:(i + 1) * n] > 0)
        labels.append(str(int((i * n + (i + 1) * n) / 2)))

    x = np.arange(len(labels))
    fig, ax = plt.subplots()
    ax.set_ylabel('Win count')
    ax.set_title('Times when goal was reached (' + str(n) + ' per sample)')
    ax.set_xticks(x, labels)
    rects = ax.bar(x, num_gs)
    ax.bar_label(rects, padding=5)
    fig.tight_layout()

    if path_with_starting_name is not None:
        if batch_number is None:
            plt.savefig(path_with_starting_name + "_win.png")
        else:
            plt.savefig(path_with_starting_name + "_batch_" + str(batch_number) + "_win.png")

    plt.show()

    print("Rewards: {0}".format(num_gs))
