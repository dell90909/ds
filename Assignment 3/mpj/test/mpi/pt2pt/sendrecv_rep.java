package mpj.test.mpi.pt2pt;

import mpj.*;

public class sendrecv_rep {
  static public void main(String[] args) throws Exception {
    try {
      sendrecv_rep c = new sendrecv_rep(args);
    }
    catch (Exception e) {
    }
  }

  public sendrecv_rep() {
  }

  public sendrecv_rep(String[] args) throws Exception {

    int src, dest, sendtag, recvtag, tasks, me, i;
    int buf[] = new int[1000];
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if (me < 2) {
      src = dest = 1 - me;
      sendtag = me;
      recvtag = src;
      for (i = 0; i < 100; i++)
	buf[i] = me;

      status = MPI.COMM_WORLD.Sendrecv_replace(buf, 0, 100, MPI.INT, dest,
	  sendtag, src, recvtag);

      for (i = 0; i < 100; i++)
	if (buf[i] != src)
	  System.out.println("ERROR in MPI_Sendrecv: incorrect data");
      if (status.source != src)
	System.out.println("ERROR in MPI_Sendrecv: incorrect source");
      if (status.tag != recvtag)
	System.out.println("ERROR in MPI_Sendrecv: incorrect tag");
    }

    src = (me == 0) ? tasks - 1 : me - 1;
    dest = (me == tasks - 1) ? 0 : me + 1;
    sendtag = me;
    recvtag = src;
    for (i = 0; i < 100; i++)
      buf[i] = me;

    status = MPI.COMM_WORLD.Sendrecv_replace(buf, 0, 100, MPI.INT, dest,
	sendtag, src, recvtag);

    for (i = 0; i < 100; i++)
      if (buf[i] != src)
	System.out.println("ERROR in MPI_Sendrecv: incorrect data\n");
    if (status.source != src)
      System.out.println("ERROR in MPI_Sendrecv: incorrect source\n");
    if (status.tag != recvtag)
      System.out.println("ERROR in MPI_Sendrecv: incorrect tag\n");

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Sendrecv_rep TEST COMPLETE");
    MPI.Finalize();
  }
}
