package mpj.test.mpi.pt2pt;

import mpj.*;

public class testsome {
  static public void main(String[] args) throws Exception {
    try {
      testsome c = new testsome(args);
    }
    catch (Exception e) {
    }
  }

  public testsome() {
  }

  public testsome(String[] args) throws Exception {

    int me, tasks, i, index, done, outcount;
    int mebuf[] = new int[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status[];

    mebuf[0] = me;
    if (me > 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.INT, 0, 1);
    else if (me == 0) {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);

      done = 0;
      while (done < tasks - 1) {

	status = Request.Testsome(req);
	outcount = status.length;

	for (i = 0; i < outcount; i++) {
	  done++;
	  if (!req[status[i].index].Is_null())
	    System.out
		.println("ERROR(2) in MPI_Testsome: reqest not set to NULL");

	  if (data[status[i].index] != status[i].index)
	    System.out.println("ERROR(3) in MPI_Testsome: wrong data");
	}

      }
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Testsome TEST COMPLETE");
    MPI.Finalize();
  }
}
