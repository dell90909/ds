package mpj.test.mpi.pt2pt;

import java.nio.ByteBuffer;

import mpj.*;

//no detach ...
public class isend {

  static int tasks, bytes, i;
  static int me[] = new int[1];
  static int data[] = new int[1000];
  // static mpi.Buffer buf = new mpi.Buffer(10000);
  static ByteBuffer buf = ByteBuffer.allocateDirect(10000);
  static Request req[];
  static Status stats[];

  static void wstart() throws MPIException {

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR<" + me[0] + "> : data is" + data[i]
	    + ", should be" + i);

    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */

    for (i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].Get_count(MPI.INT); // fix by aamir
      if (bytes != 1) // fix by aamir
	System.out.println("ERROR : bytes =" + bytes + ", should be 4");
    }

  }

  static public void main(String[] args) throws MPIException {
    try {
      isend c = new isend(args);
    }
    catch (Exception e) {
    }
  }

  public isend() {
  }

  public isend(String[] args) throws Exception {
    MPI.Init(args);
    me[0] = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    req = new Request[2 * tasks];
    stats = new Status[2 * tasks];

    MPI.Buffer_attach(buf);
    if (me[0] == 0)
      System.out.println("> Testing Isend/Irecv...");
    for (i = 0; i < tasks; i++)
      data[i] = -1;
    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Isend(me, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);
    }

    wstart();

    if (me[0] == 0)
      System.out.println("> Testing Issend/Irecv...");
    for (i = 0; i < tasks; i++)
      data[i] = -1;

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Issend(me, 0, 1, MPI.INT, i, i);
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, me[0]);
    }

    wstart();
    if (me[0] == 0)
      System.out.println("> Testing Irecv/Irsend...");
    for (i = 0; i < tasks; i++)
      data[i] = -1;

    for (i = 0; i < tasks; i++)
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);
    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < tasks; i++)
      req[2 * i] = MPI.COMM_WORLD.Irsend(me, 0, 1, MPI.INT, i, 1);
    wstart();

    if (me[0] == 0)
      System.out.println("> Testing Ibsend/Irecv...");
    for (i = 0; i < tasks; i++)
      data[i] = -1;
    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Ibsend(me, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);
    }

    wstart();

    MPI.COMM_WORLD.Barrier();
    if (me[0] == 1)
      System.out.println("Isend TEST COMPLETE");
    MPI.Finalize();
  }
}
