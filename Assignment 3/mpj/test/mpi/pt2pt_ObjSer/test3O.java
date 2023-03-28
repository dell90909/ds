package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class test3O {
  static public void main(String[] args) throws Exception {
    try {
      test3O c = new test3O(args);
    }
    catch (Exception e) {
    }
  }

  public test3O() {
  }

  public test3O(String[] args) throws Exception {

    int i, done;
    test in[] = new test[1];
    test out[] = new test[1];
    int myself, tasks;
    Request req1, req2;
    Status status;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    in[0] = new test();
    out[0] = new test();
    in[0].a = -1;
    out[0].a = 1;

    if (myself < 2) {
      if (myself == 0) {
	req1 = MPI.COMM_WORLD.Isend(out, 0, 1, MPI.OBJECT, 1, 1);
	req2 = MPI.COMM_WORLD.Irecv(in, 0, 1, MPI.OBJECT, 1, 2);
	for (;;) {
	  status = req1.Test();
	  if (status != null)
	    break;
	}
	for (;;) {
	  status = req2.Test();
	  if (status != null)
	    break;
	}
      } else if (myself == 1) {
	MPI.COMM_WORLD.Send(out, 0, 1, MPI.OBJECT, 0, 2);
	MPI.COMM_WORLD.Recv(in, 0, 1, MPI.OBJECT, 0, 1);
      }
      if (in[0].a != 1)
	System.out.println("ERROR IN TASK " + myself + ", in[0]=" + in[0]);
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 1)
      System.out.println("Test3O TEST COMPLETE");
    MPI.Finalize();
  }
}
