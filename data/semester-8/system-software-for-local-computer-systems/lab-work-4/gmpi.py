import sys

import numpy as np
from mpi4py import MPI

MATRIX_DIM = 32


def main():
    groups_count = int(sys.argv[1])
    comm = MPI.COMM_WORLD
    world_size = comm.size

    processes_per_group = int(world_size / groups_count)
    initial_group = comm.Get_group()

    old_processes_ranks = list(range(0, world_size))
    groups_communicators = []

    for i in range(0, world_size, processes_per_group):
        new_group = MPI.Group.Incl(initial_group, old_processes_ranks[i:(i + processes_per_group)])
        groups_communicators.append(comm.Create(new_group))

    file = np.loadtxt('mpi/matrix.txt', dtype='i')
    displ = 0
    start = 0

    for group in groups_communicators:
        if group != MPI.COMM_NULL:

            if group.rank == 0:
                send_array = file[displ * int(len(file) / groups_count):int(len(file) / groups_count) * (displ + 1)]
                send_array = np.asarray(send_array).reshape(-1)

                broadcast_array = np.loadtxt('mpi/broadcast.txt', dtype='i')
                broadcast_array = np.asarray(broadcast_array).reshape(-1)

                displ += 1
            else:
                broadcast_array = np.empty(MATRIX_DIM * MATRIX_DIM, dtype='i')
                send_array = None

            start = MPI.Wtime()

            receive_array = np.empty(MATRIX_DIM, dtype='i')

            group.Bcast(broadcast_array, root=0)
            group.Scatter(send_array, receive_array, root=0)

            broadcast_array = broadcast_array.reshape(MATRIX_DIM, MATRIX_DIM)

            receive_array = receive_array.dot(broadcast_array)

            amode = MPI.MODE_WRONLY | MPI.MODE_CREATE
            handle = MPI.File.Open(group, 'mpi/group_{}_result.bin'.format(groups_communicators.index(group)), amode)

            buffer = receive_array.copy()
            offset = group.Get_rank() * buffer.nbytes

            handle.Write_at_all(offset, buffer)
            handle.Close()

    for group in groups_communicators:
        if group != MPI.COMM_NULL:
            group.Free()

    if comm.rank == 0:
        with open('out/output.txt', 'w+') as out_file:
            for i in range(groups_count):
                end = MPI.Wtime()
                data = np.fromfile('mpi/group_{}_result.bin'.format(i), dtype='i')
                data = data.reshape(processes_per_group, MATRIX_DIM)
                print(f'Result time for group {i}: {end - start}')
                nums = ''
                for data_line in data:
                    for j, num in enumerate(data_line):
                        nums += str(num) + ' '
                        if j == MATRIX_DIM - 1:
                            nums += '\n'

                out_file.writelines(nums)


if __name__ == '__main__':
    main()