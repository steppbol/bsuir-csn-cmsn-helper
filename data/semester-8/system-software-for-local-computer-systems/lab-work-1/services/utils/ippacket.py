import socket
import struct

from services.utils.constants import IPV4_HEADER


class IpHeader(IPV4_HEADER):
    _format = '!BBHHHBBH4s4s'

    def pack(self):
        to_pack = (
            self[0] << 4 | self[1],
            self[2],
            self[3],
            self[4],
            self[5] << 13 | self[6],
            self[7],
            self[8],
            self[9],
            self[10],
            self[11],
        )
        return struct.pack(self._format, *to_pack)

    @classmethod
    def unpack(cls, byte_obj):
        (ver_ihl, tos, tot_len, id, flags_offset, *others) = struct.unpack(cls._format, byte_obj)
        version = ver_ihl >> 4
        ihl = ver_ihl & 0xf
        flags = flags_offset >> 13
        fragment_offset = flags_offset & 0x1fff

        return IPV4_HEADER(version, ihl, tos, tot_len, id, flags, fragment_offset, *others)

    def __len__(self):
        return struct.calcsize(self._format)


def make_ip_packet(dest_addr, ip_proto, payload, source_addr, ttl=64):
    source_addr = socket.inet_aton(source_addr)
    dest_addr = socket.inet_aton(dest_addr)

    id = 13371

    header = IpHeader(
        4, 5, 0, 0, id, 2, 0, ttl, ip_proto, 0, source_addr, dest_addr)
    data = header.pack() + payload

    return data
