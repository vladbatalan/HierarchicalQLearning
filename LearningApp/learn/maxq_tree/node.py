class Node:
    def __init__(self, name, children=None, action=None, alpha=0.5):
        # Name of the node
        self.name = name

        # The primitive action performed.
        self.action = action

        # Children of the node
        self.children = children

        # Expected cumulative reward for following the policy pi starting in state s until terminal state T(i)
        self.V = {}
        self.C = {}
        self.alpha = alpha

    def is_primitive(self):
        return self.children is None or len(self.children) == 0

    def is_state_terminal(self, state) -> bool:
        return True
