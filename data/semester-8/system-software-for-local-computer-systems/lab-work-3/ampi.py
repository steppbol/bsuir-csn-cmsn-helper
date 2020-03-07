import sys
import numpy as np

from mpi4py import MPI
from random import randint, seed


def calculate_rows(cycle, rows_per_cycle, matrix_size, matrix_B, calculated_rows, row_buffer):
    for i in range(rows_per_cycle):
        start_index = (cycle * rows_per_cycle + i) * matrix_size
        for j in range(matrix_size):
            for k in range(matrix_size):
                calculated_rows[start_index + k] += row_buffer[i * matrix_size + j] * matrix_B[j * matrix_size + k]


def main():
    N = int(sys.argv[1])
    rows_per_cycle = int(sys.argv[2])

    # N = 6
    # rows_per_cycle = 1

    seed(3)

    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    size = comm.Get_size()

    rows_per_process, res = divmod(N, size)
    period = N * rows_per_cycle
    period_array = np.array([period for p in range(size)])

    B = np.zeros(N * N, dtype=int)

    if rank == 0:
        A = np.zeros(N * N, dtype=int)
        C = np.zeros(N * N, dtype=int)

        for cycle in range(N * N):
            A[cycle] = randint(1, 5)
            B[cycle] = randint(1, 5)

        count = [rows_per_process * N for p in range(size)]
        count = np.array(count)
        initial_displacement = [sum(count[:p]) for p in range(size)]
        initial_displacement = np.array(initial_displacement)
    else:
        A = None
        C = None
        count = np.zeros(size, dtype=np.int)
        initial_displacement = np.zeros(size, dtype=np.int)

    comm.Bcast(B, root=0)
    comm.Bcast(count, root=0)
    comm.Bcast(initial_displacement, root=0)

    buffers = []

    buffers.append(np.zeros(period, dtype=int))
    buffers.append(np.zeros(period, dtype=int))

    calculated_rows = np.zeros(count[rank], dtype=int)

    start = MPI.Wtime()

    comm.Scatterv([A, period_array, initial_displacement, MPI.INT32_T], buffers[0], root=0)

    print(f'Rank = {rank}: start calculation')

    displacement = initial_displacement.copy()
    calculation_cycles = int(rows_per_process / rows_per_cycle)

    requests = []

    for cycle in range(calculation_cycles):
        ave, res = divmod(cycle + 1, 2)
        a = (cycle + 1) & 1
        if cycle != calculation_cycles - 1:
            displacement = np.add(displacement, period_array)
            requests.append(comm.Iscatterv([A, period_array, displacement, MPI.INT32_T], buffers[res], root=0))

        if cycle != 0:
            requests[cycle - 1].wait()

        calculate_rows(cycle, rows_per_cycle, N, B, calculated_rows, buffers[0 if res == 1 else 1])

    comm.Gatherv(calculated_rows, [C, count, initial_displacement, MPI.INT32_T], root=0)

    if rank == 0:
        end = MPI.Wtime()
        print(f'Result time: {end - start}')
        ordinary_multiply = np.matmul(np.reshape(A, (-1, N)), np.reshape(B, (-1, N)))
        mpi_multiply = np.reshape(C, (-1, N))

        print(f'Is result equal with ordinary multiplication? Answer: {np.allclose(ordinary_multiply, mpi_multiply)}')


if __name__ == "__main__":
    main()
