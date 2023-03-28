package mpj.test.mpi.ccl;

import mpj.*;

public class allgather {
  static public void main(String[] args) throws Exception {
    try {
      new allgather(args);
    }
    catch (Exception e) {
    }
  }

  public allgather() {
  }

  public allgather(String[] args) throws Exception {
    final int MAXLEN = 10000;

    int root, i, j, k;
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int out[] = new int[MAXLEN];
    int in[] = new int[MAXLEN * tasks];

    for (j = 1; j <= MAXLEN; j *= 10) {
      for (i = 0; i < j; i++)
	out[i] = myself;

      MPI.COMM_WORLD.Allgather(out, 0, j, MPI.INT, in, 0, j, MPI.INT);
      for (i = 0; i < tasks; i++) {
	for (k = 0; k < j; k++) {
	  if (in[k + i * j] != i) {
	    System.out
		.println("bad answer (" + (in[k]) + ") at index " + (k + i * j)
		    + " of " + (j * tasks) + " (should be " + i + ")");
	    break;
	  }
	}
      }

    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Allgather TEST COMPLETE");
    MPI.Finalize();
  }
}
