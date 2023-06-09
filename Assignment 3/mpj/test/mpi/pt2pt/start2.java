package mpj.test.mpi.pt2pt;

import java.nio.ByteBuffer;

import mpj.*;

public class start2 {

  static int me, tasks, rc, i, bytes;
  static int mebuf[] = new int[1];
  static int data[];
  static ByteBuffer buf;

  static Prequest req[] = new Prequest[4];
  static Status stats[];

  static void wstart() throws MPIException {
    for (i = 0; i < tasks; i++)
      data[i] = -1;

    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < 2 * tasks; i++)
      req[i].Start();

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].Get_count(MPI.BYTE);

      if (bytes != 4)
	System.out.println("ERROR in Waitall: bytes = " + bytes
	    + ", should be 4");
    }
  }

  //
  // ////////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws MPIException {
  }

  public start2() {
  }

  public start2(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();
    int X = 100;
    int[] data1 = new int[X];
    int[] data2 = new int[X];
    int[] data3 = new int[X];
    int[] data4 = new int[X];

    int tag1 = 1;
    int tag2 = 2;
    int tag3 = 3;
    int tag4 = 4;

    // buf = new mpi.Buffer( MPI.COMM_WORLD.Pack_size( X, MPI.INT) );
    buf = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(X, MPI.INT));
    MPI.Buffer_attach(buf);

    if (me == 0) {
      System.out.println("Testing send/recv init...");
      for (int i = 0; i < X; i++)
	data1[i] = -1;
      req[0] = MPI.COMM_WORLD.Send_init(data1, 0, X, MPI.INT, 1, tag1);
    } else if (me == 1) {
      req[0] = MPI.COMM_WORLD.Recv_init(data1, 0, X, MPI.INT, 0, tag1);
    }

    if (me == 0) {
      System.out.println("Testing ssend init...");
      for (int i = 0; i < X; i++)
	data2[i] = -1;
      req[1] = MPI.COMM_WORLD.Ssend_init(data2, 0, X, MPI.INT, 1, tag2);
    } else if (me == 1) {
      req[1] = MPI.COMM_WORLD.Recv_init(data2, 0, X, MPI.INT, 0, tag2);
    }

    if (me == 0) {
      System.out.println("Testing bsend init...");
      for (int i = 0; i < X; i++)
	data3[i] = -1;
      req[2] = MPI.COMM_WORLD.Bsend_init(data3, 0, X, MPI.INT, 1, tag3);
    } else if (me == 1) {
      req[2] = MPI.COMM_WORLD.Recv_init(data3, 0, X, MPI.INT, 0, tag3);
    }

    if (me == 0) {
      System.out.println("Testing rsend init...");
      for (int i = 0; i < X; i++)
	data4[i] = -1;
      req[3] = MPI.COMM_WORLD.Rsend_init(data4, 0, X, MPI.INT, 1, tag4);
    } else if (me == 1) {
      req[3] = MPI.COMM_WORLD.Recv_init(data4, 0, X, MPI.INT, 0, tag4);
    }

    /* Starting the communications */
    for (int k = 0; k < 4; k++) {
      if (req[k] != null)
	req[k].Start();
    }

    if (me == 1) {
      for (int i = 0; i < X; i++) {
	if (data1[i] != -1) {
	  System.out.println("ERROR(1): incorrect data");
	}

	if (data2[i] != -1) {
	  System.out.println("ERROR(2): incorrect data");
	}

	if (data3[i] != -1) {
	  System.out.println("ERROR(3): incorrect data");
	}

	if (data4[i] != -1) {
	  System.out.println("ERROR(4): incorrect data");
	}
      }
    }// checking the results ..

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Start2 TEST COMPLETE\n");
    MPI.Finalize();
  }
}
