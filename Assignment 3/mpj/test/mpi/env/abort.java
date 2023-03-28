package mpj.test.mpi.env;

import mpj.*;

public class abort {
  static public void main(String[] args) throws Exception {
    try {
      abort c = new abort(args);
    }
    catch (Exception e) {
    }
  }

  public abort() {
  }

  public abort(String[] args) throws Exception {

    int me, tasks, i;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    System.out
	.println("This program tests the MPI_ABORT call, and will generate");
    System.out.print("error messages.");

    // if(me == 0)
    MPI.COMM_WORLD.Abort(5);

    MPI.Finalize();
  }
}
