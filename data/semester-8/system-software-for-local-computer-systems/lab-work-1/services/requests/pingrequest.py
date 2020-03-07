import os
import socket
import struct
import sys
import time
from socket import AF_INET, IPPROTO_ICMP, SOCK_RAW

import select

from services.utils.constants import ICMP_ECHO_REQUEST, ICMP_HEADER, IP_HEADER
from services.utils.exceptions import BadHostException, TimeoutException

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])


class PingRequest:
    def __init__(self, host, packets=4, size=56, timeout=5):
        self.packets = packets
        self.packet_id = os.getpid() & 0xffff
        self.size = size
        self.sent = 0
        self.received = 0
        self.timeout = timeout
        self.host_name = host

        try:
            self.host_addr = socket.gethostbyname(host)
        except socket.error:
            raise BadHostException("unable to resolve target system name {}.".format(host))

    def ping(self):
        print("pinging {} [{}]: with {} bytes of data:".format(self.host_name, self.host_addr, self.size))

        for i in range(self.packets):
            result = None
            try:
                result = self.__single_ping(i + 1)
            except TimeoutException as t:
                print("\trequest timed out for {} ({}) within {}s".format(self.host_name, self.host_addr, t))
                continue
            except socket.gaierror as errno:
                print("\tfailed. (socket error: '{}')".format(errno))
                break
            except PermissionError:
                print("superuser needed")
                break

            delay = result[0] * 1000
            print(
                "\treply from {} ({}): bytes={} icmp_seq={} TTL={} time={:0.3f}ms ".format(self.host_name,
                                                                                           self.host_addr,
                                                                                           result[4] - len(result[2]),
                                                                                           result[3]["seq_num"],
                                                                                           result[2]["ttl"],
                                                                                           delay))
        print(self.statistic())

    def _make_payload(self):
        return struct.pack('d', time.process_time()).ljust(self.size, b'\x00')

    def _make_packet(self, seq_num):
        dummy_header = struct.pack("BBHHH", ICMP_ECHO_REQUEST, 0, 0, self.packet_id, seq_num)
        data = self._make_payload()
        checksum = calc_checksum(dummy_header + data)
        header = struct.pack("BBHHH", ICMP_ECHO_REQUEST, 0, checksum, self.packet_id, seq_num)
        packet = header + data

        return packet

    def _parse_packet(self, packet):
        icmp_header = make_dict(ICMP_HEADER, fmt="BBHHH", data=packet[20:28])
        ip_header = make_dict(IP_HEADER, fmt="BBHHHBBHII", data=packet[:20])

        return ip_header, icmp_header, packet[28:]

    def _send_ping(self, sock, seq_num):
        packet = self._make_packet(seq_num)
        send_time = time.process_time()
        sock.sendto(packet, (self.host_addr, 0))
        self.sent += 1

        return send_time

    def __recv_ping(self, sock):
        time_left = self.timeout

        while time_left > 0:
            begin_time = time.process_time()
            sockets = select.select([sock], [], [], time_left)
            wait_time = time.process_time() - begin_time

            if not sockets[0]:
                break

            packet_data, _ = sock.recvfrom(2048, socket.MSG_PEEK)
            size = len(packet_data) - 18
            ip_header, icmp_header, _ = self._parse_packet(packet_data)

            if icmp_header["packet_id"] == self.packet_id:
                sock.recvfrom(2048)
                receive_time = time.process_time()
                host_ip = socket.inet_ntoa(
                    struct.pack("I", ip_header["src_ip"]))
                self.received += 1

                return receive_time, host_ip, ip_header, icmp_header, size

            time_left -= wait_time

        raise TimeoutException(self.timeout)

    def __single_ping(self, seq_num):
        with socket.socket(AF_INET, SOCK_RAW, IPPROTO_ICMP) as sock:
            send_time = self._send_ping(sock, seq_num)
            recv_time, addr, ip, icmp, size = self.__recv_ping(sock)

        return recv_time - send_time, addr, ip, icmp, size

    def statistic(self):
        lost = self.sent - self.received
        lost_percent = (100 * lost) / self.sent
        stat = "\nping statistics for {} ({}): ".format(self.host_name, self.host_addr)
        stat += "sent = {}, received = {}, lost = {} ({:.0f}% loss)" \
            .format(self.sent, self.received, lost, lost_percent)

        return stat


def make_dict(names, fmt, data):
    unpacked_data = struct.unpack(fmt, data)

    return dict(zip(names, unpacked_data))


def carry_around_add(first_val, second_val):
    carry = first_val + second_val

    return (carry & 0xffff) + (carry >> 16)


def calc_checksum(src_string):
    answer = 0

    for i in range(0, len(src_string), 2):
        temp = src_string[i] + (src_string[i + 1] << 8)
        answer = carry_around_add(answer, temp)

    return ~answer & 0xffff
