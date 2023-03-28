package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class testanyO {
  static public void main(String[] args) throws Exception {
    try {
      testanyO c = new testanyO(args);
    }
    catch (Exception e) {
    }
  }

  public testanyO() {
  }

  public testanyO(String[] args) throws Exception {

    int me, tasks, i, index, done;
    ;
    test mebuf[] = new test[1];
    boolean flag;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    Request req[] = new Request[tasks];
    Status status;

    for (i = 0; i < tasks; i++) {
      data[i] = new test();
      data[i].a = -1;
    }

    mebuf[0] = new test();
    mebuf[0].a = me;
    if (me != 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.OBJECT, 0, 1);
    else if (me == 0) {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++) {
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);
      }

      done = 0;
      while (done < tasks - 1) {
	status = Request.Testany(req);
	if (status != null) {
	  done++;

	  if (!req[status.index].Is_null())
	    System.out.println("ERROR in MPI_Testany: reqest not set to null");
	  if (data[status.index].a != status.index)
	    System.out.println("ERROR in MPI.Testany: wrong data");
	}
      }

    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("TestanyO TEST COMPLETE");
    MPI.Finalize();
  }
}
