package mpj.test.mpi.comm;

import mpj.*;

public class commdup {
  static public void main(String[] args) throws Exception {
    try {
      commdup a = new commdup(args);
    }
    catch (Exception e) {
    }
  }

  public commdup() {
  }

  public commdup(String[] args) throws Exception {
    final int ITER = 20;
    int i, myself;

    MPI.Init(args);

    Comm comm, newcomm;

    myself = MPI.COMM_WORLD.Rank();

    for (i = 0; i < ITER; i++)
      comm = (Comm) MPI.COMM_WORLD.clone();

    comm = MPI.COMM_WORLD;
    for (i = 0; i < ITER; i++) {
      newcomm = (Comm) comm.clone();
      comm = newcomm;
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("CommDup TEST COMPLETE");
    MPI.Finalize();
  }
}
