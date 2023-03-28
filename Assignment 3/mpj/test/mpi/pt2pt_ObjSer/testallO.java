package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class testallO {
  static public void main(String[] args) throws Exception {
    try {
      testallO c = new testallO(args);
    }
    catch (Exception e) {
    }
  }

  public testallO() {
  }

  public testallO(String[] args) throws Exception {

    int me, tasks, i;
    test mebuf[] = new test[1];
    boolean flag;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    Request req[] = new Request[tasks];
    Status status[] = new Status[tasks];
    mebuf[0] = new test();

    for (int k = 0; k < tasks; k++) {
      data[k] = new test();
      data[k].a = -1;
    }
    mebuf[0].a = me;

    if (me != 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.OBJECT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);

      flag = false;
      while (flag == false) {
	for (i = 1; i < tasks; i++) {
	  if (req[i].Is_null())
	    System.out.println("ERROR(2) in MPI_Testall: incorrect status");
	}
	status = Request.Testall(req);
	if (status == null)
	  flag = false;
	else
	  flag = true;
      }

      for (i = 1; i < tasks; i++) {
	if (!req[i].Is_null())
	  System.out.println("ERROR(3) in Testall: request not set to NULL");
	if (status[i].source != i)
	  System.out
	      .println("ERROR(4) in Testall: request prematurely set to NULL");
	if (data[i].a != i)
	  System.out.println("ERROR(5) in MPI_Testall: incorrect data : "
	      + data[i].a);
      }

    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("TestallO TEST COMPLETE");
    MPI.Finalize();
  }
}
