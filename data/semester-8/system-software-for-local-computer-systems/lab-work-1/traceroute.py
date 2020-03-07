import argparse
import os
import sys

from services.requests.tracerouterequest import TraceRouteRequest
from services.utils.exceptions import BadHostException

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])


def _parse_args(args):
    formatter = lambda prog: argparse.HelpFormatter(prog, max_help_position=46, width=100)

    parser = argparse.ArgumentParser(description="Python tracing route util", formatter_class=formatter)

    parser.add_argument("-d", "--dest", type=str, help="<required> host name", required=True)
    parser.add_argument("-c", "--count", type=int, default=30, help="count of hops")
    parser.add_argument("-t", "--timeout", type=int, default=5, help="trace reply timeout")

    if not args:
        parser.print_usage()
        sys.exit(1)

    parsed_args = parser.parse_args(args)

    return parsed_args


def main():
    args = _parse_args(sys.argv[1:])

    print('run configuration')
    print('\tdestination = {}'.format(args.dest))
    print('\thops count = {}'.format(args.count))
    print('\treply timeout = {} seconds\n'.format(args.timeout))

    try:
        trace = TraceRouteRequest(args.dest, args.count, args.timeout)
        trace.trace()
    except BadHostException as errno:
        print(errno)
    except KeyboardInterrupt:
        print("keyboard interrupt")
        sys.exit(1)
    except PermissionError:
        print("superuser needed")
        sys.exit(1)
    except:
        raise


if __name__ == '__main__':
    main()
