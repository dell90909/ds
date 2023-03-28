package mpj.test.mpi.pt2pt_ObjSer;

import java.nio.ByteBuffer;

import mpj.*;

//only one detach ..need two i guess at rank0 and one at rank1
public class bsendO {
  static public void main(String[] args) throws Exception {
    try {
      bsendO c = new bsendO(args);
    }
    catch (Exception e) {
    }
  }

  public bsendO() {
  }

  public bsendO(String[] args) throws Exception {
    /*
     * Note that the buffer sizes must include the BSEND_OVERHEAD; these values
     * are probably sizeof(int) too large
     */

    int len, tasks, me, i, size, rc;
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    test datatest[] = new test[10];
    test recdata[] = new test[10];
    test a[] = new test[1000];
    test b[] = new test[1000];

    int intsize = 4;
    // byte buf1[] = new byte[1000*intsize+MPI.BSEND_OVERHEAD];
    // byte buf100[] = new byte[100000*intsize+MPI.BSEND_OVERHEAD];
    // No obvious rationale to this.
    // Probably bsend is always unsafe for objects. dbc.
    // mpi.Buffer buf1 = new mpi.Buffer(
    // MPI.COMM_WORLD.Pack_size(1000,MPI.INT) );
    ByteBuffer buf1 = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(1000,
	MPI.INT));
    // mpi.Buffer buf100 = new mpi.Buffer(
    // MPI.COMM_WORLD.Pack_size(100000,MPI.INT));
    ByteBuffer buf100 = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(
	100000, MPI.INT));

    for (i = 0; i < 10; i++) {
      datatest[i] = new test();
      recdata[i] = new test();
      datatest[i].a = 1;
      recdata[i].a = 0;
    }

    if (me == 0) {

      MPI.Buffer_attach(buf1);
      MPI.COMM_WORLD.Bsend(datatest, 0, 10, MPI.OBJECT, 1, 1);

      MPI.Buffer_detach();

      MPI.Buffer_attach(buf100);

      // MPI.COMM_WORLD.Barrier();

      /* test to see if large array is REALLY being buffered */
      for (i = 0; i < 1000; i++) {
	a[i] = new test();
	a[i].a = 1;
	b[i] = new test();
	b[i].a = 0;
      }

      MPI.COMM_WORLD.Bsend(a, 0, 1000, MPI.OBJECT, 1, 1);

      MPI.COMM_WORLD.Recv(b, 0, 1000, MPI.OBJECT, 1, 2);

      for (i = 0; i < 1000; i++)
	if (b[i].a != 2)
	  System.out.println("ERROR, incorrect data[" + i + "]=" + b[i].a
	      + ", task 0");

    } else if (me == 1) {
      MPI.COMM_WORLD.Recv(recdata, 0, 10, MPI.OBJECT, 0, 1);
      // MPI.COMM_WORLD.Barrier();

      MPI.Buffer_attach(buf100);

      /* test to see if large array is REALLY being buffered */
      for (i = 0; i < 1000; i++) {
	a[i] = new test();
	a[i].a = 2;
	b[i] = new test();
	b[i].a = 0;
      }
      MPI.COMM_WORLD.Bsend(a, 0, 1000, MPI.OBJECT, 0, 2);

      MPI.COMM_WORLD.Recv(b, 0, 1000, MPI.OBJECT, 0, 1);

      for (i = 0; i < 1000; i++)
	if (b[i].a != 1)
	  System.out.println("ERROR , incorrect data[" + i + "]=" + b[i].a
	      + ", task 1");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("BsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
