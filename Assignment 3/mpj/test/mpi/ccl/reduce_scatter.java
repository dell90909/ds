package mpj.test.mpi.ccl;

import mpj.*;

public class reduce_scatter {
  static public void main(String[] args) throws Exception {
    try {
      reduce_scatter c = new reduce_scatter(args);
    }
    catch (Exception e) {
    }
  }

  public reduce_scatter() {
  }

  public reduce_scatter(String[] args) throws Exception {

    final int MAXLEN = 10000;

    int out[] = new int[MAXLEN * 100];
    int in[] = new int[MAXLEN * 100];
    int i, j, k;
    int myself, tasks;
    int recvcounts[] = new int[128];

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0) {
	System.out.println("reduce_scatter must run with 8 tasks!");
      }
      MPI.Finalize();
      return;
    }
    j = 10;
    // for(j=1;j<=MAXLEN*tasks;j*=10) {
    for (i = 0; i < tasks; i++)
      recvcounts[i] = j;
    for (i = 0; i < j * tasks; i++)
      out[i] = i;

    MPI.COMM_WORLD.Reduce_scatter(out, 0, in, 0, recvcounts, MPI.INT, MPI.SUM);

    for (k = 0; k < j; k++) {
      if (in[k] != tasks * (myself * j + k)) {
	System.out.println("bad answer (" + in[k] + ") at index " + k + " of "
	    + j + "(should be " + tasks * (myself * j + k) + ")");
	break;
      }
    }
    // }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Reduce_scatter TEST COMPLETE");
    MPI.Finalize();
  }
}
