package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class sendrecvO {

  public sendrecvO() {
  }

  public static void main(String args[]) throws Exception {
    sendrecvO test = new sendrecvO(args);
  }

  public sendrecvO(String[] args) throws Exception {
    int src, dest, sendtag, recvtag, tasks, me, i;
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    test datatest[] = new test[1000];
    test recdata[] = new test[1000];

    for (i = 0; i < 1000; i++) {
      datatest[i] = new test();
      recdata[i] = new test();
      datatest[i].a = me;
      recdata[i].a = 0;
    }

    if (me < 2) {
      src = dest = 1 - me;
      sendtag = me;
      recvtag = src;

      status = MPI.COMM_WORLD.Sendrecv(datatest, 0, 100, MPI.OBJECT, dest,
	  sendtag, recdata, 0, 100, MPI.OBJECT, src, recvtag);

      for (i = 0; i < 2000000; i++)
	;

      for (i = 0; i < 100; i++)
	if (recdata[i].a != src) {
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

    status = MPI.COMM_WORLD.Sendrecv(datatest, 0, 100, MPI.OBJECT, dest,
	sendtag, recdata, 0, 100, MPI.OBJECT, src, recvtag);

    for (i = 0; i < 2000000; i++)
      ;

    for (i = 0; i < 100; i++)
      if (recdata[i].a != src) {
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
      System.out.println("SendRecvO TEST COMPLETE");
    MPI.Finalize();
  }
}
