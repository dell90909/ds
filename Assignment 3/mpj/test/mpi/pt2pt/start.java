package mpj.test.mpi.pt2pt;

import java.nio.ByteBuffer;

import mpj.*;

public class start {

  static int me, tasks, rc, i, bytes;
  static int mebuf[] = new int[1];
  static int data[];
  // static byte buf[];
  static ByteBuffer buf = null;

  static Prequest req[];
  static Status stats[];

  static void wstart() throws MPIException {
    for (i = 0; i < tasks; i++)
      data[i] = -1;
    MPI.COMM_WORLD.Barrier();
    for (i = 0; i < 2 * tasks; i++) {
      req[i].Start();
    }

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].Get_count(MPI.INT); // fix by aamir.

      if (bytes != 1)// aamir.
	System.out.println("ERROR in Waitall: bytes = " + bytes
	    + ", should be 1");
    }

  }

  // ////////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws MPIException {
    try {
      start c = new start(args);
    }
    catch (Exception e) {
    }
  }

  public start() {
  }

  public start(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    // data = new int[tasks+MPI.BSEND_OVERHEAD];
    data = new int[tasks];
    int intsize = 4;
    // buf = new byte[tasks * (intsize+MPI.BSEND_OVERHEAD)];
    // buf = new mpi.Buffer( MPI.COMM_WORLD.Pack_size(tasks, MPI.INT) );
    buf = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(tasks, MPI.INT)
	+ tasks * MPI.BSEND_OVERHEAD);
    req = new Prequest[2 * tasks];
    stats = new Status[2 * tasks];

    MPI.Buffer_attach(buf);

    mebuf[0] = me;

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }
    if (me == 0)
      System.out.println("Testing send/recv init...");
    wstart();

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Ssend_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }
    if (me == 0)
      System.out.println("Testing ssend init...");
    wstart();

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Bsend_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }

    if (me == 0)
      System.out.println("Testing bsend init...");
    wstart();

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("Start TEST COMPLETE");
    MPI.Finalize();
  }
}
