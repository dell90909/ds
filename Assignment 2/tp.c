#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

#define N 1000

int main(int argc, char *argv[]) {
    int rank, size;
    int nums[N];
    int local_sum, global_sum;
    int i;
    int n_per_proc;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // Generate N random numbers on root process
    if (rank == 0) {
        for (i = 0; i < N; i++) {
            nums[i] = rand() % 10;
        }
    }

    // Broadcast the array to all processes
    MPI_Bcast(nums, N, MPI_INT, 0, MPI_COMM_WORLD);

    // Determine the number of elements each process should handle
    n_per_proc = N / size;

    // Calculate local sum
    local_sum = 0;
    for (i = rank * n_per_proc; i < (rank + 1) * n_per_proc; i++) {
        local_sum += nums[i];
    }

    // Display local sum
    printf("Rank %d local sum: %d\n", rank, local_sum);

    // Reduce local sums to get global sum
    MPI_Reduce(&local_sum, &global_sum, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    // Display global sum on root process
    if (rank == 0) {
        printf("Global sum: %d\n", global_sum);
    }

    MPI_Finalize();
    return 0;
}