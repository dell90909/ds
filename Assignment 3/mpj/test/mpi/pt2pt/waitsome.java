package mpj.test.mpi.pt2pt;

import mpj.*;

public class waitsome {
  static public void main(String[] args) throws Exception {
    try {
      waitsome c = new waitsome(args);
    }
    catch (Exception e) {
    }
  }

  public waitsome() {
  }

  public waitsome(String[] args) throws Exception {

    int me, tasks, i, done, outcount;
    boolean flag;
    int mebuf[] = new int[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status[] = new Status[tasks];

    mebuf[0] = me;
    if (me > 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);

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
	  if (data[status[i].index] != status[i].index)
	    System.out.println("ERROR(5) in MPI_Waitsome: wrong data");
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Waitsome TEST COMPLETE");
    MPI.Finalize();
  }
}
