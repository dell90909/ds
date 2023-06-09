package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class test2O {
  static public void main(String[] args) throws Exception {
    try {
      test2O c = new test2O(args);
    }
    catch (Exception e) {
    }
  }

  public test2O() {
  }

  public test2O(String[] args) throws Exception {

    int numtask, taskid, rc;
    test outmsg[] = new test[1];
    test inmsg[] = new test[1];
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
	System.out.println("test2O must run with less than 8 tasks");
      }
      MPI.Finalize();
      return;
    }

    if (taskid == 1) {
      MPI.COMM_WORLD.Barrier();
      outmsg[0] = new test();
      outmsg[0].a = 5;
      type = 1;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.OBJECT, dest, type);
    }

    if (taskid == 0) {
      source = MPI.ANY_SOURCE;
      rtype = MPI.ANY_TAG;
      req = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.OBJECT, source, rtype);

      status = req.Test();
      if (status != null)
	System.out.println("ERROR(1)");
      MPI.COMM_WORLD.Barrier();

      status = req.Wait();
      if (inmsg[0].a != 5 || status.source != 1 || status.tag != 1) {
	System.out.println("ERROR(2)");
	System.out.println(" inmsg[0].a " + inmsg[0].a);
	System.out.println(" status.source " + status.source);
	System.out.println(" status.tag " + status.source);
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (taskid == 1)
      System.out.println("Test2O TEST COMPLETE <" + taskid + ">");
    MPI.Finalize();
  }
}
