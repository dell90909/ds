package mpj.test.mpi.pt2pt;

import mpj.*;

public class waitnull {
  static public void main(String[] args) throws Exception {
    try {
      waitnull c = new waitnull(args);
    }
    catch (Exception e) {
    }
  }

  public waitnull() {
  }

  public waitnull(String[] args) throws Exception {

    int me, tasks, i;
    Request request;
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    request = MPI.REQUEST_NULL;
    status = request.Wait();

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Waitnull TEST COMPLETE");
    MPI.Finalize();
  }
}
