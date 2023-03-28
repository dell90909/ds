package mpj.test.mpi.pt2pt;

import mpj.*;

public class rsend {
  static public void main(String[] args) throws Exception {
    try {
      rsend c = new rsend(args);
    }
    catch (Exception e) {
    }
  }

  public rsend() {
  }

  public rsend(String[] args) throws Exception {
    int tasks, me, i;
    char buf[] = new char[10];
    double time;
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    MPI.COMM_WORLD.Barrier();

    if (me == 0) {
      // for(i=0;i<1000000;i++) ;
      // Thread.currentThread().sleep(5000);
      MPI.COMM_WORLD.Rsend(buf, 0, 10, MPI.CHAR, 1, 1);
    } else if (me == 1) {
      MPI.COMM_WORLD.Recv(buf, 0, 10, MPI.CHAR, 0, 1);
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Rsend TEST COMPLETE");
    MPI.Finalize();
  }
}
