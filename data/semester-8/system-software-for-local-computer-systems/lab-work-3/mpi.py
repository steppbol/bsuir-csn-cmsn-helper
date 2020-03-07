import sys
import numpy as np

from mpi4py import MPI
from random import randint, seed

N = int(sys.argv[1])

seed(3)

comm = MPI.COMM_WORLD
rank = comm.Get_rank()
size = comm.Get_size()

B = np.zeros(N * N, dtype=int)

if rank == 0:
    A = np.zeros(N * N, dtype=int)
    C = np.zeros(N * N, dtype=int)

    for i in range(N * N):
        A[i] = randint(1, 5)
        B[i] = randint(1, 5)

    ave, res = divmod(N, size)
    count = [ave * N if p != (size - 1) else (ave + res) * N for p in range(size)]
    count = np.array(count)
    displ = [sum(count[:p]) for p in range(size)]
    displ = np.array(displ)
else:
    A = None
    C = None
    count = np.zeros(size, dtype=np.int)
    displ = np.zeros(size, dtype=np.int)

comm.Bcast(B, root=0)
comm.Bcast(count, root=0)
comm.Bcast(displ, root=0)

rows_data = np.zeros(count[rank], dtype=int)
buffer = np.zeros(count[rank], dtype=int)

start = MPI.Wtime()

comm.Scatterv([A, count, displ, MPI.INT32_T], rows_data, root=0)

print(f'Rank = {rank}: start calculation')

for i in range(int(count[rank] / N)):
    for j in range(N):
        for k in range(N):
            buffer[i * N + k] += rows_data[i * N + j] * B[j * N + k]

comm.Gatherv(buffer, [C, count, displ, MPI.INT32_T], root=0)

if rank == 0:
    end = MPI.Wtime()
    print(f'Result time: {end - start}')

    ordinary_multiply = np.matmul(np.reshape(A, (-1, N)), np.reshape(B, (-1, N)))
    mpi_multiply = np.reshape(C, (-1, N))
    print(f'Is result equal with ordinary multiplication? Answer: {np.allclose(ordinary_multiply, mpi_multiply)}')




