package mpj.test.mpi.pt2pt;

import mpj.*;

public class waitall {
  static public void main(String[] args) throws Exception {
    try {
      waitall c = new waitall(args);
    }
    catch (Exception e) {
    }
  }

  public waitall() {
  }

  public waitall(String[] args) throws Exception {

    int me, tasks, bytes, i;
    int mebuf[] = new int[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[2 * tasks];
    Status stats[] = new Status[2 * tasks];

    mebuf[0] = me;
    for (i = 0; i < tasks; i++) {
      if (i != me)
	req[2 * i] = MPI.COMM_WORLD.Isend(mebuf, 0, 1, MPI.INT, i, 1);
      // Original IBM code used `Irsend' here. Clearly wrong? dbc.

      if (i != me)
	req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);
    }
    stats = Request.Waitall(req);

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Waitall TEST COMPLETE");
    MPI.Finalize();
  }
}
