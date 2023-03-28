package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class waitsomeO {
  static public void main(String[] args) throws Exception {
    try {
      waitsomeO c = new waitsomeO(args);
    }
    catch (Exception e) {
    }
  }

  public waitsomeO() {
  }

  public waitsomeO(String[] args) throws Exception {

    int me, tasks, i, done, outcount;
    boolean flag;
    test mebuf[] = new test[1];
    mebuf[0] = new test();
    mebuf[0].a = 444;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    for (int j = 0; j < tasks; j++) {
      data[j] = new test();
      data[j].a = -1;
    }
    Request req[] = new Request[tasks];
    Status status[] = new Status[tasks];

    if (me != 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.OBJECT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);

      done = 0;
      while (done < tasks - 1) {
	status = Request.Waitsome(req);
	outcount = status.length;

	if (outcount == 0)
	  System.out.println("ERROR(2) in Waitsome: outcount = 0");
	for (i = 0; i < outcount; i++) {
	  done++;

	  if (!req[status[i].index].Is_null())
	    System.out.println(i + ", " + outcount + ", " + status[i].index
		+ ", " + req[status[i].index]
		+ " ERROR(4) in MPI_Waitsome: reqest not set to NULL");
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("WaitsomeO TEST COMPLETE");
    MPI.Finalize();
  }
}
