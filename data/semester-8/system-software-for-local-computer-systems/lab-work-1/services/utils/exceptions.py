class BadHostException(Exception):
    def __init__(self, message):
        super().__init__(message)


class TimeoutException(RuntimeError):
    def __init__(self, timeout):
        super().__init__(timeout)
