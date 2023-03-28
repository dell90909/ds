package mpj.test.mpi.ccl;

import mpj.*;

public class reduce {
  static public void main(String[] args) throws Exception {
    try {
      reduce c = new reduce(args);
    }
    catch (Exception e) {
    }
  }

  public reduce() {
  }

  public reduce(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int root, i, j, k;
    int out[] = new int[MAXLEN];
    int in[] = new int[MAXLEN];
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    root = tasks / 2;

    for (j = 1; j <= MAXLEN; j *= 10) {
      for (i = 0; i < j; i++)
	out[i] = i;

      MPI.COMM_WORLD.Reduce(out, 0, in, 0, j, MPI.INT, MPI.SUM, root);

      if (myself == root) {
	for (k = 0; k < j; k++) {
	  if (in[k] != k * tasks) {
	    System.out.println("bad answer (" + (in[k]) + ") at index " + k
		+ " of " + j + "(should be " + (k * tasks) + ")");
	    break;
	  }
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Reduce TEST COMPLETE");
    MPI.Finalize();
  }
}
