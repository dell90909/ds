package mpj.test.mpi.env;

import mpj.*;

public class wtime {
  static public void main(String[] args) throws Exception {
    try {
      wtime c = new wtime(args);
    }
    catch (Exception e) {
    }
  }

  public wtime() {
  }

  public wtime(String[] args) throws Exception {

    double time[] = new double[20];
    int i, me;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 0) {
      System.out.println("resolution = " + MPI.Wtick());
      for (i = 0; i < 20; i++)
	time[i] = MPI.Wtime();
      for (i = 0; i < 20; i++)
	System.out.println("time = " + time[i]);
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Wtime and Wtick TEST COMPLETE\n");
    MPI.Finalize();
  }
}
