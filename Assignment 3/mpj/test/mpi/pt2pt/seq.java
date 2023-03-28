package mpj.test.mpi.pt2pt;

import mpj.*;

public class seq {
  static public void main(String[] args) throws Exception {
    try {
      seq c = new seq(args);
    }
    catch (Exception e) {
    }
  }

  public seq() {
  }

  public seq(String[] args) throws Exception {

    int i, me, tasks;
    int data[] = new int[1];
    Status status;

    final int ITER = 1;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (me == 0) {
      for (i = 0; i < (tasks - 1) * ITER; i++)
	MPI.COMM_WORLD.Recv(data, 0, 1, MPI.INT, 1, 1);
    } else if (me == 1) {
      for (i = 0; i < (tasks - 1) * ITER; i++)
	MPI.COMM_WORLD.Send(data, 0, 1, MPI.INT, 0, 1);
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Seq TEST COMPLETE");
    MPI.Finalize();
  }
}
