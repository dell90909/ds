package mpj.test.mpi.pt2pt;

import mpj.*;

public class wildcard {
  static public void main(String[] args) throws Exception {
    try {
      wildcard c = new wildcard(args);
    }
    catch (Exception e) {
    }
  }

  public wildcard() {
  }

  public wildcard(String[] args) throws Exception {

    int me, tasks, i, tag, expected;
    int val[] = new int[1];
    Status status;
    final int ITER = 1;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (me == 0) {
      for (i = 0; i < (tasks - 1) * ITER; i++) {
	status = MPI.COMM_WORLD.Recv(val, 0, 1, MPI.INT, MPI.ANY_SOURCE, i
	    / (tasks - 1));
	expected = status.source * 1000 + status.tag;
	if (val[0] != expected)
	  System.out.println("ERROR, val[0] = " + (val[0]) + ", should be "
	      + expected);
      }
    } else {
      for (i = 0; i < ITER; i++) {
	tag = i;
	val[0] = me * 1000 + tag;
	MPI.COMM_WORLD.Send(val, 0, 1, MPI.INT, 0, tag);
      }
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 0) {
      System.out.println("Wildcard TEST COMPLETE");
    }

    MPI.Finalize();
  }
}
