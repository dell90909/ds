package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class rsend2O {
  static public void main(String[] args) throws Exception {
    try {
      rsend2O c = new rsend2O(args);
    }
    catch (Exception e) {
    }
  }

  public rsend2O() {
  }

  public rsend2O(String[] args) throws Exception {

    int me, tasks, bytes, i;
    test mebuf[] = new test[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    mebuf[0] = new test();
    mebuf[0].a = me;
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    Request req[] = new Request[2 * tasks];
    Status stats[] = new Status[2 * tasks];

    for (i = 0; i < tasks; i++)
      data[i] = new test();

    for (i = 0; i < tasks; i++) {
      if (i != me)
	req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);
    }

    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < tasks; i++) {
      if (i != me)
	req[2 * i] = MPI.COMM_WORLD.Irsend(mebuf, 0, 1, MPI.OBJECT, i, 1);
    }

    stats = Request.Waitall(req);

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Rsend2O TEST COMPLETE");
    MPI.Finalize();
  }
}
