package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class testsomeO {
  static public void main(String[] args) throws Exception {
    try {
      testsomeO c = new testsomeO(args);
    }
    catch (Exception e) {
    }
  }

  public testsomeO() {
  }

  public testsomeO(String[] args) throws Exception {

    int me, tasks, i, index, done, outcount;
    test mebuf[] = new test[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    Request req[] = new Request[tasks];
    Status status[];

    mebuf[0] = new test();
    for (i = 0; i < tasks; i++) {
      data[i] = new test();
      data[i].a = -1;
    }

    mebuf[0].a = me;
    if (me != 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.OBJECT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);

      done = 0;
      while (done < tasks - 1) {
	status = Request.Testsome(req);

	outcount = status.length;
	for (i = 0; i < outcount; i++) {
	  done++;

	  if (!req[status[i].index].Is_null())
	    System.out
		.println("ERROR(2) in MPI_Testsome: reqest not set to NULL");
	  if (data[status[i].index].a != status[i].index)
	    System.out.println("ERROR(3) in MPI_Testsome: wrong data");
	}
      }

      status = Request.Testsome(req);
      if (status.length != 0)
	// if(status != null) //Aamir changed it from
	// null to checking length zero
	System.out.println("ERROR in MPI_Testsome: status is NOT null");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("TestsomeO TEST COMPLETE");
    MPI.Finalize();
  }
}
