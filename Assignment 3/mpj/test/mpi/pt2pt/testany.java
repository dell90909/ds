package mpj.test.mpi.pt2pt;

import mpj.*;

public class testany {
  static public void main(String[] args) throws Exception {
    try {
      testany c = new testany(args);
    }
    catch (Exception e) {
    }
  }

  public testany() {
  }

  public testany(String[] args) throws Exception {

    int me, tasks, i, index, done;
    ;
    int mebuf[] = new int[1];
    boolean flag;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status;

    mebuf[0] = me;
    if (me > 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.INT, 0, 1);
    else if (me == 0) {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);

      done = 0;
      while (done < tasks - 1) {
	status = Request.Testany(req);
	if (status != null) {
	  done++;
	  if (!req[status.index].Is_null())
	    System.out.println("ERROR in MPI_Testany: reqest not set to null");
	  if (data[status.index] != status.index)
	    System.out.println("ERROR in MPI.Testany: wrong data");
	}
      }
    }

    // MPI.COMM_WORLD.Barrier();
    // if(me == 1)
    System.out.println("Testany TEST COMPLETE <" + me + ">");
    MPI.Finalize();
  }
}
