package mpj.test.mpi.env;

import mpj.*;

public class initialized {
  static public void main(String[] args) throws Exception {
    try {
      initialized c = new initialized(args);
    }
    catch (Exception e) {
    }
  }

  public initialized() {
  }

  public initialized(String[] args) throws Exception {

    int me;
    boolean flag;

    flag = MPI.Initialized();
    if (flag)
      System.out
	  .println("ERROR: MPI_Initialized returned TRUE before initialization");

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    flag = MPI.Initialized();
    if (!flag)
      System.out
	  .println("ERROR: MPI_Initialized returned FALSE after initialization");

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Initialized TEST COMPLETE\n");
    MPI.Finalize();
  }
}
