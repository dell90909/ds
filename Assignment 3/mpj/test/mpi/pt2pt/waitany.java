package mpj.test.mpi.pt2pt;

import mpj.*;

public class waitany {

  static public void main(String[] args) throws Exception {
    try {
      waitany c = new waitany(args);
    }
    catch (Exception e) {
    }
  }

  public waitany() {
  }

  public waitany(String[] args) throws Exception {

    int me, tasks, i, index;
    int mebuf[] = new int[1];

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

      for (i = 1; i < tasks; i++) {
	status = Request.Waitany(req);
	if (!req[status.index].Is_null())
	  System.out.println("ERROR(3) in MPI_Waitany: reqest not set to NULL");
	if (data[status.index] != status.index)
	  System.out.println("ERROR(4) in MPI_Waitany: wrong data");
      }

      /***
       * ??? mpich1.1.1 status = Request.Waitany(req); if(status.index !=
       * MPI.UNDEFINED) System.out.println
       * ("ERROR(5) in MPI_Waitany: index not = MPI.UNDEFINED "+status.index);
       ******/
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Waitany TEST COMPLETE");
    MPI.Finalize();
  }
}
