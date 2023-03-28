package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class waitanyO {
  static public void main(String[] args) throws Exception {
    try {
      waitanyO c = new waitanyO(args);
    }
    catch (Exception e) {
    }
  }

  public waitanyO() {
  }

  public waitanyO(String[] args) throws Exception {

    int me, tasks, i, index;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test a[] = new test[1];
    test b[] = new test[10 * tasks]; // aamir

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status;

    for (i = 0; i < 10; i++) {
      b[i] = new test();
      b[i].a = -1;
    }

    a[0] = new test();
    a[0].a = me;

    if (me != 0)
      MPI.COMM_WORLD.Send(a, 0, 1, MPI.OBJECT, 0, 1);
    else if (me == 0) {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(b, i, 1, MPI.OBJECT, i, 1);

      for (i = 1; i < tasks; i++) {
	status = Request.Waitany(req);

	if (!req[status.index].Is_null())
	  System.out.println("ERROR(3) in MPI_Waitany: reqest not set to NULL");
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("WaitanyO TEST COMPLETE");
    MPI.Finalize();
  }
}
