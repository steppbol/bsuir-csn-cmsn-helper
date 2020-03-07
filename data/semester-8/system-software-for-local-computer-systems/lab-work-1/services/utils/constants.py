from collections import namedtuple

ICMP_ECHO_REQUEST = 8
ICMP_ECHO_REPLY = (0, 0)
ICMP_TTL_EXCEEDED = (11, 0)

ICMP_HEADER = [
    "type",
    "code",
    "checksum",
    "packet_id",
    "seq_num"
]

IP_HEADER = [
    "version",
    "type",
    "length",
    "id",
    "flags",
    "ttl",
    "protocol",
    "checksum",
    "src_ip",
    "dest_ip"
]

IPV4_HEADER = namedtuple('IPv4Header', [
    'version',
    'ihl',
    'tos',
    'total_length',
    'id',
    'flags',
    'fragment_offset',
    'ttl',
    'proto',
    'checksum',
    'src',
    'dest',
])
