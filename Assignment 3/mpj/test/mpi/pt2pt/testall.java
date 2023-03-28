package mpj.test.mpi.pt2pt;

import mpj.*;

public class testall {
  static public void main(String[] args) throws Exception {
    try {
      testall c = new testall(args);
    }
    catch (Exception e) {
    }
  }

  public testall() {
  }

  public testall(String[] args) throws Exception {

    int me, tasks, i;
    int mebuf[] = new int[1];
    boolean flag;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status[] = new Status[tasks];

    mebuf[0] = me;
    if (me > 0) {
      // try {
      // Thread.currentThread().sleep(500);
      // }catch(Exception e){
      // }
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.INT, 0, 1);
    } else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);

      flag = false;
      while (flag == false) {

	for (i = 1; i < tasks; i++) {
	  if (req[i].Is_null())
	    System.out.println("ERROR(2) in MPI_Testall: incorrect status");
	}

	status = Request.Testall(req);
	if (status == null) {
	  flag = false;
	  // System.out.println("null");
	} else
	  flag = true;
      }

      for (i = 1; i < tasks; i++) {
	if (!req[i].Is_null())
	  System.out.println("ERROR(3) in Testall: request not set to NULL");

	if (status[i].source != i)
	  System.out
	      .println("ERROR(4) in Testall: request prematurely set to NULL");

	if (data[i] != i)
	  System.out.println("ERROR(5) in MPI_Testall: incorrect data");

      }

      status = Request.Testall(req);
      if (status == null)
	System.out.println("ERROR(6) in MPI_Testall: status(flag) is not set");

    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Testall TEST COMPLETE");
    MPI.Finalize();
  }
}
