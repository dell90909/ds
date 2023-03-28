package mpj.test.mpi.ccl;

import mpj.*;

public class scatter {
  static public void main(String[] args) throws Exception {
    try {
      scatter c = new scatter(args);
    }
    catch (Exception e) {
    }
  }

  public scatter() {
  }

  public scatter(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int root, i, j, k;
    int out[] = new int[MAXLEN * 64];
    int in[] = new int[MAXLEN];
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    for (j = 1, root = 0; j <= MAXLEN; j *= 10, root = (root + 1) % tasks) {
      if (myself == root)
	for (i = 0; i < j * tasks; i++)
	  out[i] = i;

      MPI.COMM_WORLD.Scatter(out, 0, j, MPI.INT, in, 0, j, MPI.INT, root);

      for (k = 0; k < j; k++) {
	if (in[k] != k + myself * j) {
	  System.out.println("task " + myself + ":" + "bad answer (" + (in[k])
	      + ") at index " + k + " of " + j + "(should be "
	      + (k + myself * j) + ")");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Scatter TEST COMPLETE");
    MPI.Finalize();
  }
}
