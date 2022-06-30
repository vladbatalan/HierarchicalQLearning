import abc


class EpsilonStrategy(abc.ABC):
    @abc.abstractmethod
    def get_eps(self) -> float:
        pass

    @abc.abstractmethod
    def decay_eps(self):
        pass

    @abc.abstractmethod
    def reset(self):
        pass


class ConstantEpsStrategy(EpsilonStrategy):

    def __init__(self, eps: float):
        self._init_eps = eps
        self._eps = eps

    def get_eps(self) -> float:
        return self._eps

    def decay_eps(self):
        # Do nothing
        pass

    def reset(self):
        self._eps = self._init_eps


class LinearEpsStrategy(EpsilonStrategy):
    def __init__(self, max_eps, min_eps, steps_to_complete_decay):
        self._init_eps = max_eps
        self._eps = max_eps
        self._min_eps = min_eps
        self._eps_decay = (self._init_eps - self._min_eps) / steps_to_complete_decay

    def get_eps(self) -> float:
        return self._eps

    def decay_eps(self):
        next_eps = self._eps - self._eps_decay
        self._eps = max(next_eps, self._min_eps)

    def reset(self):
        self._eps = self._init_eps


class ExponentialEpsStrategy(EpsilonStrategy):
    def __init__(self, max_eps, min_eps, beta):
        self._init_eps = max_eps
        self._eps = max_eps
        self._min_eps = min_eps
        self._eps_decay = beta

    def get_eps(self) -> float:
        return self._eps

    def decay_eps(self):
        next_eps = self._eps * self._eps_decay
        self._eps = max(next_eps, self._min_eps)

    def reset(self):
        self._eps = self._init_eps
