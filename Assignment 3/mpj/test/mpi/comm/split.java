package mpj.test.mpi.comm;

import mpj.*;

public class split {
  static public void main(String[] args) throws Exception {
    try {
      split a = new split(args);
    }
    catch (Exception e) {
    }
  }

  public split() {
  }

  public split(String[] args) throws Exception {

    int i, me, tasks, color, key;
    int mebuf[] = new int[1];
    int ranks[] = new int[128];

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if (tasks % 2 == 1) { // || tasks < 8) {
      if (me == 0)
	System.out.println("comm->split: MUST RUN WITH EVEN NUMBER OF TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    color = me % 2;
    key = me;
    Intracomm newcomm = MPI.COMM_WORLD.Split(color, key);

    mebuf[0] = me;
    newcomm.Allgather(mebuf, 0, 1, MPI.INT, ranks, 0, 1, MPI.INT);

    if (me % 2 == 0) {
      for (i = 0; i < tasks / 2; i++)
	if (ranks[i] != 2 * i)
	  System.out.println("ERROR in MPI.Comm_split: wrong tasks");
    }
    if (me % 2 != 0) {
      for (i = 0; i < tasks / 2; i++)
	if (ranks[i] != 2 * i + 1)
	  System.out.println("ERROR in MPI.Comm_split: wrong tasks");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Split TEST COMPLETE");
    MPI.Finalize();
  }
}
