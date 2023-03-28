package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class waitallO {
  static public void main(String[] args) throws Exception {
    try {
      waitallO c = new waitallO(args);
    }
    catch (Exception e) {
    }
  }

  public waitallO() {
  }

  public waitallO(String[] args) throws Exception {

    int me, tasks, bytes, i;
    test mebuf[] = new test[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    mebuf[0] = new test();
    mebuf[0].a = me;

    test data[] = new test[tasks];
    for (int j = 0; j < tasks; j++) {
      data[j] = new test();
      data[j].a = -1;
    }
    Request req[] = new Request[2 * tasks];
    Status stats[] = new Status[2 * tasks];

    // mebuf[0] = me;
    for (i = 0; i < tasks; i++) {
      if (i != me) {
	req[2 * i] = MPI.COMM_WORLD.Isend(mebuf, 0, 1, MPI.OBJECT, i, 1);
	// Original IBM code used `Irsend' here. Clearly wrong? dbc.

	req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);
      }
    }
    stats = Request.Waitall(req);

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("WaitallO TEST COMPLETE");
    MPI.Finalize();
  }
}
