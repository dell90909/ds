package mpj.test.mpi.ccl;

import mpj.*;

public class gather {
  static public void main(String[] args) throws Exception {
    try {
      gather c = new gather(args);
    }
    catch (Exception e) {
    }
  }

  public gather() {
  }

  public gather(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int root, i, j, k;
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int out[] = new int[MAXLEN];
    int in[] = new int[MAXLEN * tasks];

    for (j = 1, root = 0; j <= MAXLEN; j *= 10, root = (root + 1) % tasks) {
      for (i = 0; i < j; i++)
	out[i] = i;

      MPI.COMM_WORLD.Gather(out, 0, j, MPI.INT, in, 0, j, MPI.INT, root);

      if (myself == root) {
	for (i = 0; i < tasks; i++) {
	  for (k = 0; k < j; k++) {
	    if (in[i * j + k] != k) {
	      System.out.println("bad answer (" + (in[i * j + k])
		  + ") at index " + (i * j + k) + " of " + (j * tasks)
		  + "(should be " + k + ")");
	      break;
	    }
	  }
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Gather TEST COMPLETE");
    MPI.Finalize();
  }
}
