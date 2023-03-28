package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class ssendO {
  static public void main(String[] args) throws Exception {
    try {
      ssendO c = new ssendO(args);
    }
    catch (Exception e) {
    }
  }

  public ssendO() {
  }

  public ssendO(String[] args) throws Exception {

    test a[] = new test[10];
    test b[] = new test[10];

    int len, tasks, me, i;
    Status status;
    double time, timeoffset;
    double timeBuf[] = new double[1];
    double timeoffsetBuf[] = new double[1];

    /*
     * This test makes assumptions about the global nature of MPI_WTIME that are
     * not required by MPI, and may falsely signal an error
     */

    len = a.length;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < 10; i++) {
      a[i] = new test();
      b[i] = new test();
      a[i].a = i;
      b[i].a = 0;
    }
    if (me == 0) {
      /* First, roughly synchronize the clocks */
      MPI.COMM_WORLD.Recv(timeoffsetBuf, 0, 1, MPI.DOUBLE, 1, 1);
      timeoffset = timeoffsetBuf[0];
      timeoffset = MPI.Wtime() - timeoffset;

      MPI.COMM_WORLD.Ssend(a, 0, len, MPI.OBJECT, 1, 1);

      time = MPI.Wtime() - timeoffset;
      timeBuf[0] = time;

    } else if (me == 1) {
      time = MPI.Wtime();
      timeBuf[0] = time;

      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 0, 1);

      for (i = 0; i < 3000000; i++)
	;

      MPI.COMM_WORLD.Recv(b, 0, len, MPI.OBJECT, 0, 1);

      time = timeBuf[0];
      time = time - MPI.Wtime();
      if (time < 0)
	time = -time;
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("SsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
