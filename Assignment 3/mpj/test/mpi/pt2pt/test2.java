package mpj.test.mpi.pt2pt;

import mpj.*;

public class test2 {
  static public void main(String[] args) throws Exception {
    try {
      test2 c = new test2(args);
    }
    catch (Exception e) {
    }
  }

  public test2() {
  }

  public test2(String[] args) throws Exception {

    int numtask, taskid, rc;
    int outmsg[] = new int[1];
    int inmsg[] = new int[1];
    int i, dest = 0, type = 1;
    int source, rtype = type, rbytes = -1, flag, dontcare = -1;
    int msgid;
    Status status;
    Request req;

    MPI.Init(args);
    taskid = MPI.COMM_WORLD.Rank();
    numtask = MPI.COMM_WORLD.Size();

    if (numtask > 2) {
      if (taskid == 0) {
	System.out.println("test2: must run with 2 tasks! ");
      }
      MPI.Finalize();
      return;
    }

    if (taskid == 1) {
      MPI.COMM_WORLD.Barrier();

      outmsg[0] = 5;
      type = 1;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.INT, dest, type);
    }

    if (taskid == 0) {
      source = MPI.ANY_SOURCE;
      rtype = MPI.ANY_TAG;
      req = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.INT, source, rtype);
      status = req.Test();
      if (status != null)
	System.out.println("ERROR(1)");
      MPI.COMM_WORLD.Barrier();

      status = req.Wait();
      if (inmsg[0] != 5 || status.source != 1 || status.tag != 1)
	System.out.println("ERROR(2)");
    }

    MPI.COMM_WORLD.Barrier();
    if (taskid == 1)
      System.out.println("Test2 TEST COMPLETE");
    MPI.Finalize();
  }
}
