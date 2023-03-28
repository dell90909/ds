package mpj.test.mpi.pt2pt;

import java.nio.ByteBuffer;

import mpj.*;

//no detach ...
public class startall {

  static int me, tasks, i, bytes;
  static int mebuf[] = new int[1];
  static int data[];
  static ByteBuffer buf;

  static Prequest req[];
  static Status stats[];

  static void wstart() throws MPIException {

    for (i = 0; i < tasks; i++)
      data[i] = -1;

    Prequest.Startall(req);

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);
    for (i = 1; i < 2 * tasks; i += 2) {
      if (stats[i] != null) {
	bytes = stats[i].Get_count(MPI.INT);// aamir
	if (bytes != 1)// aamir
	  System.out.println("ERROR in Waitall: bytes = " + bytes
	      + ", should be 1");// aamir
      }
    }
  }

  // //////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws MPIException {
    try {
      startall c = new startall(args);
    }
    catch (Exception e) {
    }
  }

  public startall() {
  }

  public startall(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    data = new int[tasks];
    int intsize = MPI.COMM_WORLD.Pack_size(1, MPI.INT);
    // buf = new mpi.Buffer( tasks * (intsize+MPI.BSEND_OVERHEAD) );
    buf = ByteBuffer.allocateDirect(tasks * (intsize + MPI.BSEND_OVERHEAD));
    req = new Prequest[2 * tasks];
    stats = new Status[2 * tasks];

    MPI.Buffer_attach(buf);

    mebuf[0] = me;
    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);

    }
    wstart();

    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);

    }
    wstart();
    /*
     * 
     * for(i=0;i<tasks;i++) {
     * 
     * req[2*i] = MPI.COMM_WORLD.Bsend_init(mebuf,0,1,MPI.INT,i,1); req[2*i+1] =
     * MPI.COMM_WORLD.Recv_init(data,i,1,MPI.INT,i,1);
     * 
     * } wstart();
     */

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("StartAll TEST COMPLETE");
    MPI.Finalize();
  }
}
