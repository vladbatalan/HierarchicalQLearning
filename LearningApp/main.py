import time

import tests
from apiCommands import *
from appApi import AppAPI
from stateDeserializer import CustomDeserializer

HOST = "127.0.0.1"
PORT = 4303

if __name__ == '__main__':

    tests.TestUnit.ApiShould.get_initial_level_state()
