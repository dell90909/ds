package mpj.test.mpi.pt2pt;

import mpj.*;

public class rsend2 {
  static public void main(String[] args) throws Exception {
    try {
      rsend2 c = new rsend2(args);
    }
    catch (Exception e) {
    }
  }

  public rsend2() {
  }

  public rsend2(String[] args) throws Exception {

    int me, tasks, bytes, i;
    int mebuf[] = new int[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    mebuf[0] = me;
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[2 * tasks];
    Status stats[] = new Status[2 * tasks];

    for (i = 0; i < tasks; i++) {
      if (i != me)
	req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);
    }

    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < tasks; i++) {
      if (i != me)
	req[2 * i] = MPI.COMM_WORLD.Irsend(mebuf, 0, 1, MPI.INT, i, 1);
    }

    stats = Request.Waitall(req);

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Rsend2 TEST COMPLETE");
    MPI.Finalize();
  }
}
