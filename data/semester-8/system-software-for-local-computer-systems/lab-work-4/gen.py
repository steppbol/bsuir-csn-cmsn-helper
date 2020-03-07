import sys
import numpy as np

from time import time
from random import randint, seed


seed(3)

MATRIX_DIM = int(sys.argv[1])
file_names = ['1.txt', '2.txt']

def generate(dim1_row, dim1_col, dim2_row, dim2_col, first_file, second_file):
    with open(first_file, 'w+') as first_out, \
         open(second_file, 'w+') as second_out:
        for _ in range(0, dim1_row):
            temp = [str(randint(1, 100)) for _ in range(0, dim1_col)]
            first_out.write(" ".join(temp) + '\n')

        for _ in range(0, dim2_row):
            temp = [str(randint(1, 100)) for _ in range(0, dim2_col)]
            second_out.write(" ".join(temp) + '\n')
    
    return first_file, second_file

def multiply(first_file, second_file):
    first_mat = np.loadtxt(first_file, dtype=int)
    second_mat = np.loadtxt(second_file, dtype=int)

    start_time = time()
    result = np.matmul(first_mat, second_mat)
    end_time = time()

    res_file = first_file[:-4] + 'mul' + second_file
    np.savetxt(res_file, result, fmt='%i')

    return end_time - start_time

def main():
    # first_file, second_file = generate(MATRIX_DIM, MATRIX_DIM,
                                    #    MATRIX_DIM, MATRIX_DIM,
                                    #    file_names[0], file_names[1])

    print('start multiplication')
    time_for_mul = multiply('output1.txt', 'output2.txt')
    print('end multiplication')
    print('elapsed time: %.3f seconds' % time_for_mul)


if __name__ == '__main__':
    main()
