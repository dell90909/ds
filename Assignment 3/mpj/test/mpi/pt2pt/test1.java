package mpj.test.mpi.pt2pt;

import mpj.*;

public class test1 {
  static public void main(String[] args) throws Exception {
    try {
      test1 c = new test1(args);
    }
    catch (Exception e) {
    }
  }

  public test1() {
  }

  public test1(String[] args) throws Exception {
    int outmsg[] = new int[1];
    int inmsg[] = new int[1];
    int i, me, flag = 0;
    Status status = null;
    Request msgid;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 1) {
      outmsg[0] = 5;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.INT, 0, 1);
    }
    if (me == 0) {
      msgid = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.INT, MPI.ANY_SOURCE,
	  MPI.ANY_TAG);
      while (status == null) {

	status = msgid.Test();
	// System.out.println("test1 -- status "+status);

	if (status != null)
	  break;
      }

      if (inmsg[0] != 5 || status.source != 1 || status.tag != 1)
	System.out.println("ERROR inmsg[0]=" + inmsg[0] + ", src="
	    + status.source + ", tag=" + status.tag + ", should be 5,1,1");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Test1 TEST COMPLETE");
    MPI.Finalize();
  }
}
