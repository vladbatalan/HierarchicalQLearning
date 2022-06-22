from learn.maxq_tree.node import Node


class MAXQGraph:
    def __init__(self):
        self.nodes = {}

    def add_node(self, node: Node):
        self.nodes[node.name] = node

    def get_node(self, name: str) -> Node:
        if name in self.nodes:
            return self.nodes[name]
        raise Exception("There is no node named: " + name)
