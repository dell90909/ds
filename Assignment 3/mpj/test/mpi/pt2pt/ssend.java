package mpj.test.mpi.pt2pt;

import mpj.*;

public class ssend {
  static public void main(String[] args) throws Exception {
    try {
      ssend c = new ssend(args);
    }
    catch (Exception e) {
    }
  }

  public ssend() {
  }

  public ssend(String[] args) throws Exception {

    char buf[] = new char[10];
    int len, tasks, me, i;
    Status status;
    double time, timeoffset;
    double timeBuf[] = new double[1];
    double timeoffsetBuf[] = new double[1];

    /*
     * This test makes assumptions about the global nature of MPI_WTIME that are
     * not required by MPI, and may falsely signal an error
     */

    len = buf.length;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 0) {
      /* First, roughly synchronize the clocks */
      MPI.COMM_WORLD.Recv(timeoffsetBuf, 0, 1, MPI.DOUBLE, 1, 1);
      timeoffset = timeoffsetBuf[0];
      timeoffset = MPI.Wtime() - timeoffset;

      MPI.COMM_WORLD.Ssend(buf, 0, len, MPI.CHAR, 1, 1);

      time = MPI.Wtime() - timeoffset;
      timeBuf[0] = time;
      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 1, 2);
    } else if (me == 1) {
      time = MPI.Wtime();
      timeBuf[0] = time;

      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 0, 1);

      for (i = 0; i < 3000000; i++)
	;

      MPI.COMM_WORLD.Recv(buf, 0, len, MPI.CHAR, 0, 1);
      MPI.COMM_WORLD.Recv(timeBuf, 0, 1, MPI.DOUBLE, 0, 2);
      time = timeBuf[0];
      time = time - MPI.Wtime();
      if (time < 0)
	time = -time;
      if (time > .1)
	System.out.println("ERROR (Not important): MPI_Ssend did"
	    + "not synchronize");

      // Don't understand exactly what this is *meant* to do, but on
      // general principles it seems dubious one could make an effective
      // test of the MPI spec this way... DBC.
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("Ssend TEST COMPLETE");
    MPI.Finalize();
  }
}
