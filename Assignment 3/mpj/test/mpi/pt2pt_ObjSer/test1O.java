package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class test1O {
  static public void main(String[] args) throws Exception {
    try {
      test1O c = new test1O(args);
    }
    catch (Exception e) {
    }
  }

  public test1O() {
  }

  public test1O(String[] args) throws Exception {
    test outmsg[] = new test[1];
    test inmsg[] = new test[1];
    int i, me, flag = 0;
    Status status = null;
    Request msgid;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 1) {
      outmsg[0] = new test();
      outmsg[0].a = 5;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.OBJECT, 0, 1);
    }

    if (me == 0) {
      inmsg[0] = new test();
      inmsg[0].a = 0;
      msgid = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE,
	  MPI.ANY_TAG);

      while (status == null)
	status = msgid.Test();

      if (inmsg[0].a != 5 || status.source != 1 || status.tag != 1)
	System.out.println("ERROR inmsg[0]=" + inmsg[0].a + ", src="
	    + status.source + ", tag=" + status.tag + ", should be 5,1,1");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Test1O TEST COMPLETE");
    MPI.Finalize();
  }
}
