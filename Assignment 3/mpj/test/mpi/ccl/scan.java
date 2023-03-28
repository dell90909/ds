package mpj.test.mpi.ccl;

import mpj.*;

public class scan {
  static public void main(String[] args) throws Exception {
    try {
      scan c = new scan(args);
    }
    catch (Exception e) {
    }
  }

  public scan() {
  }

  public scan(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int i, j, k;
    int out[] = new int[MAXLEN];
    int in[] = new int[MAXLEN];
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    for (j = 1; j <= MAXLEN; j *= 10) {
      for (i = 0; i < j; i++)
	out[i] = i;

      MPI.COMM_WORLD.Scan(out, 0, in, 0, j, MPI.INT, MPI.SUM);

      for (k = 0; k < j; k++) {
	if (in[k] != k * (myself + 1)) {
	  System.out.println("bad answer (" + (in[k]) + ") at index " + k
	      + " of " + j + "(should be " + (k * (myself + 1)) + ")");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Scan TEST COMPLETE");
    MPI.Finalize();

  }
}
