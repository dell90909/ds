package mpj.test.mpi.pt2pt_ObjSer;

import java.nio.ByteBuffer;

import mpj.*;

public class startO {

  static int me, tasks, rc, i, bytes;
  static test mebuf[] = new test[1];
  static test data[];
  // static byte buf[];
  static ByteBuffer buf = null;

  static Prequest req[];
  static Status stats[];

  static void wstart() throws MPIException {
    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < 2 * tasks; i++) {
      if (req[i] != null) {
	req[i].Start();
      }
    }

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != null && data[i].a != i)
	System.out.println("ERROR in Startall: data is " + data[i].a
	    + ", should be " + i);
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (i = 1; i < 2 * tasks; i += 2) {
      if (stats[i] != null) {
	bytes = stats[i].Get_count(MPI.OBJECT);

	if (bytes != 1)
	  System.out.println("ERROR in Waitall: bytes = " + bytes
	      + ", should be 1");
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws Exception {
    try {
      startO c = new startO(args);
    }
    catch (Exception e) {
    }
  }

  public startO() {
  }

  public startO(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    data = new test[tasks];

    for (i = 0; i < tasks; i++) {
      data[i] = new test();
      data[i].a = -1;
    }

    int intsize = 4;
    // buf = new byte[100000];
    // Sizing of buffer is basically hit-and-miss for OBJECT types,
    // because `Comm.Pack_size()' doesn't work in this case.

    req = new Prequest[2 * tasks];
    stats = new Status[2 * tasks];
    // buf = new mpi.Buffer(100000);
    buf = ByteBuffer.allocateDirect(100000);
    MPI.Buffer_attach(buf);
    mebuf[0] = new test();
    mebuf[0].a = me;

    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.OBJECT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.OBJECT, i, 1);

    }
    if (me == 0)
      System.out.println("Testing send/recv init...");
    wstart();

    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Bsend_init(mebuf, 0, 1, MPI.OBJECT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.OBJECT, i, 1);

    }
    if (me == 0)
      System.out.println("Testing bsend init...");
    wstart();

    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Ssend_init(mebuf, 0, 1, MPI.OBJECT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.OBJECT, i, 1);

    }
    if (me == 0)
      System.out.println("Testing ssend init...");
    wstart();

    MPI.COMM_WORLD.Barrier();
    MPI.Buffer_detach();
    if (me == 1)
      System.out.println("StartO TEST COMPLETE");
    MPI.Finalize();
  }
}
