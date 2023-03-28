package mpj.test.mpi.ccl;

import mpj.*;

public class alltoall {
  static public void main(String[] args) throws Exception {
    try {
      alltoall c = new alltoall(args);
    }
    catch (Exception e) {
    }
  }

  public alltoall() {
  }

  public alltoall(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int i, j, k;
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int out[] = new int[MAXLEN * tasks];
    int in[] = new int[MAXLEN * tasks];

    for (j = 1; j <= MAXLEN; j *= 10) {
      for (i = 0; i < j * tasks; i++)
	out[i] = myself;

      MPI.COMM_WORLD.Alltoall(out, 0, j, MPI.INT, in, 0, j, MPI.INT);

      for (i = 0; i < tasks; i++) {
	for (k = 0; k < j; k++) {
	  if (in[k + i * j] != i) {
	    System.out
		.println("bad answer (" + (in[k + i * j]) + ") at index "
		    + (k + i * j) + " of " + (j * tasks) + " (should be " + i
		    + ")");
	    break;
	  }
	}
      }
    }

    // MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("AllToAll TEST COMPLETE");
    MPI.Finalize();
  }
}
