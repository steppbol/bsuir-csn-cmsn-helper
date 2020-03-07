import socket
import threading
import sys
import signal
from time import sleep


exit_flag = False
ip_list = []
broadcast = ('<broadcast>', 2000)


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
                    self.s.sendto(str('##init').encode('utf-8'), broadcast)
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
    def __init__(self, s):
        self.s = s
        super().__init__()

    def run(self):
        self.s.sendto(str('#init').encode('utf-8'), broadcast)
        global ip_list
        global exit_flag

        while not exit_flag:
            msg = input('\t<you>: ')
            if msg == 'ls':
                print('<system>: users list')
                for a in ip_list:
                    print(' - %s' % (a))
            else:
                if msg == 'exit':
                    self.close_connection()
                    return
                else:
                    self.s.sendto(msg.encode('utf-8'), broadcast)

        if exit_flag:
            self.close_connection()

    def close_connection(self):
        global exit_flag
        exit_flag = True
        self.s.sendto('exit'.encode('utf-8'), broadcast)

        print('<system>: good bye')
        self.s.close()
        sleep(1)
        try:
            sys.exit(1)
        except:
            pass


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
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    s.bind(('', 2000))
    s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

    my_ip = socket.gethostbyname(socket.gethostname())
    global ip_list
    ip_list.append(my_ip)
    print('<system>: welcome to broadcast chat - %s' % my_ip)

    receiver = Receiver(s)
    receiver.start()
    sender = Sender(s)
    sender.start()


if __name__ == '__main__':
    signal.signal(signal.SIGINT, keyboard_interrupt_handler)
    main()
