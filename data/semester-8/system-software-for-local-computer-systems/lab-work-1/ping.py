import argparse
import multiprocessing as mp
import os
import sys

from services.requests.pingrequest import PingRequest
from services.utils.exceptions import BadHostException

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])


def _parse_args(args):
    formatter = lambda prog: argparse.HelpFormatter(prog, max_help_position=46, width=100)

    parser = argparse.ArgumentParser(description="python parallel ping util", formatter_class=formatter)

    parser.add_argument("-d", "--dest", nargs="+", help="<required> list of hosts", required=True)
    parser.add_argument("-c", "--count", type=int, default=4, help="count of packages will be sent")
    parser.add_argument("-t", "--timeout", type=int, default=2, help="ping reply wait timeout")
    parser.add_argument("-s", "--size", type=int, default=32, help="size of echo package payload")

    if not args:
        parser.print_usage()
        sys.exit(1)

    parsed_args = parser.parse_args(args)

    return parsed_args


def ping_wrapper(request):
    try:
        request.ping()
    except KeyboardInterrupt:
        print("\nkeyboard interrupt")
        sys.exit(1)
    except:
        raise


def main():
    args = _parse_args(sys.argv[1:])

    print('run configuration:')
    print('\thosts = {}'.format(args.dest))
    print('\tpackets count = {}'.format(args.count))
    print('\tpacket size = {} bytes'.format(args.size))
    print('\treply timeout = {} seconds\n'.format(args.timeout))

    settings = [args.count, args.size, args.timeout]
    try:
        requests = [PingRequest(dest, *settings) for dest in args.dest]
    except BadHostException as e:
        print(e)
        sys.exit(1)

    pool = mp.Pool(len(args.dest))
    pool.map(ping_wrapper, requests)


if __name__ == '__main__':
    main()
