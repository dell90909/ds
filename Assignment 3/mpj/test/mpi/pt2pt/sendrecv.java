package mpj.test.mpi.pt2pt;

import mpj.*;

public class sendrecv {

  static public void main(String[] args) throws Exception {
    try {
      sendrecv c = new sendrecv(args);
    }
    catch (Exception e) {
    }
  }

  public sendrecv() {
  }

  public sendrecv(String[] args) throws Exception {
    int src, dest, sendtag, recvtag, tasks, me, i;
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    int sendbuf[] = new int[1000];
    int recvbuf[] = new int[1000];
    if (me < 2) {
      src = dest = 1 - me;
      sendtag = me;
      recvtag = src;

      for (i = 0; i < 100; i++) {
	sendbuf[i] = me;
	recvbuf[i] = -1;
      }

      status = MPI.COMM_WORLD.Sendrecv(sendbuf, 0, 100, MPI.INT, dest, sendtag,
	  recvbuf, 0, 100, MPI.INT, src, recvtag);

      for (i = 0; i < 2000000; i++)
	;

      for (i = 0; i < 100; i++)
	if (recvbuf[i] != src) {
	  System.out.println("ERROR in MPI.Sendrecv: incorrect data\n");
	  break;
	}

      if (status.source != src)
	System.out.println("ERROR in MPI.Sendrecv: incorrect source\n");
      if (status.tag != recvtag)
	System.out.println("ERROR in MPI.Sendrecv: incorrect tag ("
	    + status.tag + ")");

    }

    src = (me == 0) ? tasks - 1 : me - 1;
    dest = (me == tasks - 1) ? 0 : me + 1;
    sendtag = me;
    recvtag = src;
    for (i = 0; i < 100; i++) {
      sendbuf[i] = me;
      recvbuf[i] = -1;
    }

    status = MPI.COMM_WORLD.Sendrecv(sendbuf, 0, 100, MPI.INT, dest, sendtag,
	recvbuf, 0, 100, MPI.INT, src, recvtag);

    for (i = 0; i < 2000000; i++)
      ;
    for (i = 0; i < 100; i++)
      if (recvbuf[i] != src) {
	System.out.println("ERROR in MPI.Sendrecv: incorrect data\n");
	break;
      }

    if (status.source != src)
      System.out.println("ERROR in MPI.Sendrecv: incorrect source\n");
    if (status.tag != recvtag)
      System.out.println("ERROR in MPI.Sendrecv: incorrect tag (" + status.tag
	  + ")");

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("SendRecv TEST COMPLETE");
    MPI.Finalize();
  }
}
