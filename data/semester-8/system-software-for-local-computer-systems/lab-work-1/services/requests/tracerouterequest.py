import os
import select
import socket
import struct
import sys
import time

from socket import AF_INET, SOCK_RAW, IPPROTO_ICMP

path = os.path.dirname(os.path.abspath(__file__))
sys.path.append(path[:-9])

from services.utils.exceptions import TimeoutException
from services.utils.constants import ICMP_TTL_EXCEEDED, ICMP_ECHO_REPLY
from services.requests.pingrequest import PingRequest


class TraceRouteRequest(PingRequest):
    def __init__(self, dest, hops=30, req_timeout=3):
        self.hops = hops
        self.ttl = 1
        super().__init__(dest, timeout=req_timeout)

    def __send_tracert(self, sock, seq_num):
        packet = self._make_packet(seq_num)
        sock.sendto(packet, (self.host_addr, 0))
        send_time = time.process_time()

        return send_time

    def __recv_tracert(self, sock):
        time_left = self.timeout

        while time_left > 0:
            begin_time = time.process_time()
            readable, *_ = select.select([sock], [], [], time_left)
            wait_time = time.process_time() - begin_time
            time_left -= wait_time

            if not readable:
                break

            receive_time = time.process_time()
            packet_data, addr = sock.recvfrom(1024)
            _, icmp_header, payload = self._parse_packet(packet_data)
            time_sent = struct.unpack('d', payload[:struct.calcsize('d')])[0]

            icmp_result = (icmp_header["type"], icmp_header["code"])

            if icmp_result == ICMP_TTL_EXCEEDED:
                return receive_time - time_sent, socket.getfqdn(addr[0]), addr[0], False

            if icmp_result == ICMP_ECHO_REPLY and self.packet_id == icmp_header["packet_id"]:
                return receive_time - time_sent, socket.getfqdn(addr[0]), addr[0], True

        raise TimeoutException(self.timeout)

    def __single_trace(self, sock):
        host_name = self.host_name
        host_addr = None
        complete = False
        latency = []

        for seq_num in range(0, 3):
            self.__send_tracert(sock, seq_num)

            try:
                delay, host_name, host_addr, complete = self.__recv_tracert(sock)
            except TimeoutException:
                latency.append("*")
                continue

            delay *= 1000
            data = "{:.0f}".format(delay)
            latency.append(data)

        return latency, host_name, host_addr, complete

    def trace(self):
        print("tracing route to {} [{}] over a maximum of {} hops:\n".format(self.host_name, self.host_addr, self.hops))

        for ttl in range(self.ttl, self.hops + 1):
            with socket.socket(AF_INET, SOCK_RAW, IPPROTO_ICMP) as sock:
                sock.setsockopt(socket.IPPROTO_IP, socket.IP_TTL, ttl)
                latency, host_name, host_addr, complete = self.__single_trace(sock)

            if host_addr is not None:
                print("{:3d} {:>6} ms {:>7} ms {:>7} ms    {} [{}]"
                      .format(ttl, latency[0], latency[1], latency[2],
                              host_name, host_addr))
            else:
                print("{:3d} {:>6}  {:>9}  {:>9}       {}"
                      .format(ttl, "*", "*", "*", "request timed out"))

            if complete:
                break