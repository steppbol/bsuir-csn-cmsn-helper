import socket
import threading
import sys
import signal
import struct
from time import sleep

exit_flag = False
ip_list = []
multicast_group = ('224.0.0.224', 2000)


class Receiver(threading.Thread):
    def __init__(self, s):
        self.s = s
        super().__init__()

    def run(self):
        global ip_list

        while not exit_flag:
            try:
                data, addr = self.s.recvfrom(1024)
            except:
                break

            data = data.decode('utf-8')
            if data == '#init':
                if addr[0] not in ip_list:
                    print('<system>: new user enter in chat room - %s' % (addr[0]))
                    ip_list.append(addr[0])
                    self.s.sendto(str('##init').encode('utf-8'), multicast_group)

            elif data == '##init':
                if addr[0] not in ip_list:
                    ip_list.append(addr[0])

            elif data == 'exit':
                if addr[0] in ip_list:
                    ip_list.remove(addr[0])
                    print('<system>: user %s leave the chat room' % (addr[0]))

            else:
                if addr[0] not in ip_list:
                    ip_list.append(addr[0])
                if addr[0] != ip_list[0]:
                    print('\t<%s>: %s' % (addr[0], data))


class Sender(threading.Thread):
    def __init__(self, s, mreq):
        self.s = s
        self.mreq = mreq
        super().__init__()

    def run(self):
        self.s.sendto(str('#init').encode('utf-8'), multicast_group)
        global ip_list
        global exit_flag

        while not exit_flag:
            msg = input('\t<you>: ')
            if msg == 'ls':
                print('<system>: users list')
                for a in ip_list:
                    print(' - %s' % a)
            elif msg == 'exit':
                self.close_connection()
                return
            elif 'unblock' in msg:
                self.unblock_user(self.s, msg[8:])
            elif 'block' in msg:
                self.block_user(self.s, msg[6:])
            elif msg == 'drop':
                self.drop_membership(self.s, self.mreq)
            elif msg == 'add':
                self.add_membership(self.s, self.mreq)
            else:
                self.s.sendto(msg.encode('utf-8'), multicast_group)

        if exit_flag:
            self.close_connection()

    def close_connection(self):
        global exit_flag
        exit_flag = True
        self.s.sendto('exit'.encode('utf-8'), multicast_group)

        self.s.close()
        sleep(1)
        try:
            sys.exit(1)
        except:
            pass

    def drop_membership(self, s, mreq):
        s.setsockopt(socket.IPPROTO_IP, socket.IP_DROP_MEMBERSHIP, mreq)
        self.s.sendto('exit'.encode('utf-8'), multicast_group)

    def add_membership(self, s, mreq):
        s.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
        self.s.sendto(str('#init').encode('utf-8'), multicast_group)

    def block_user(self, s, ip):
        bls = socket.inet_aton(ip)
        block = struct.pack('4si4s', socket.inet_aton(multicast_group[0]), socket.INADDR_ANY, bls)
        s.setsockopt(socket.IPPROTO_IP, socket.IP_BLOCK_SOURCE, block)

    def unblock_user(self, s, ip):
        bls = socket.inet_aton(ip)
        block = struct.pack('4si4s', socket.inet_aton(multicast_group[0]), socket.INADDR_ANY, bls)
        s.setsockopt(socket.IPPROTO_IP, socket.IP_UNBLOCK_SOURCE, block)


def keyboard_interrupt_handler(signal, frame):
    global exit_flag
    exit_flag = True
    print("\nkeyboard interrupt. exiting")
    sleep(1)
    try:
        sys.exit(1)
    except:
        pass


def main():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    ttl = struct.pack('b', 1)
    s.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, ttl)

    group = socket.inet_aton(multicast_group[0])

    mreq = struct.pack('4sL', group, socket.INADDR_ANY)
    s.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

    if not hasattr(socket, 'IP_BLOCK_SOURCE'):
        setattr(socket, 'IP_BLOCK_SOURCE', 38)
    if not hasattr(socket, 'IP_UNBLOCK_SOURCE'):
        setattr(socket, 'IP_UNBLOCK_SOURCE', 37)

    s.bind(('', 2000))

    my_ip = socket.gethostbyname(socket.gethostname())
    global ip_list
    ip_list.append(my_ip)
    print('<system>: welcome to multicast chat - %s' % my_ip)

    receiver = Receiver(s)
    receiver.start()
    sender = Sender(s, mreq)
    sender.start()


if __name__ == '__main__':
    signal.signal(signal.SIGINT, keyboard_interrupt_handler)
    main()
