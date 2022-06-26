class MaxQTree:
    def __init__(self, root_node, is_terminal: (lambda node, state, done: bool),
                 is_composed_terminal: (lambda parent, action: bool)):
        self.root_node = root_node
        self.is_terminal = is_terminal
        self.is_composed_terminal = is_composed_terminal

    def reset_tree(self, alpha=None):
        self._reset_rec(self.root_node, alpha)

    def _reset_rec(self, node, alpha=None):
        node.reset_node()
        if alpha is not None:
            node.alpha = alpha

        if not node.is_primitive():
            for child in node.children:
                self._reset_rec(child)

    def print(self, ident=0, delim="   |"):
        self._print_rec(self.root_node, ident, delim)

    def _print_rec(self, root_node, ident, delim):
        for i in range(ident):
            print(delim, end="")

        print(root_node.name, end="")

        if not root_node.is_primitive():
            print(":")
            for child_node in root_node.children:
                self._print_rec(child_node, ident + 1, delim)

        else:
            print("[ action =", root_node.primitive_action.name, "]")
