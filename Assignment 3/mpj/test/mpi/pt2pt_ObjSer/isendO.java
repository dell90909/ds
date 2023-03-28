package mpj.test.mpi.pt2pt_ObjSer;

import java.nio.ByteBuffer;

import mpj.*;

//no detach ...
public class isendO {

  static int tasks, bytes, i;
  // static byte buf[] = new byte[10000];
  static ByteBuffer buf = null;
  static Request req[];
  static Status stats[];

  static test a[] = new test[1];
  static test b[] = new test[10];

  static void wstart() throws MPIException {
    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (b[i] != null && b[i].a != i)
	System.out.println("ERROR : data is" + b[i].a + ", should be" + i);
  }

  static public void main(String[] args) throws Exception {
    try {
      isendO c = new isendO(args);
    }
    catch (Exception e) {
    }
  }

  public isendO() {
  }

  public isendO(String[] args) throws Exception {
    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (me == 0)
	System.out.println("isendO runs with less than 8 processes!");
      MPI.Finalize();
      return;
    }

    a[0] = new test();
    for (i = 0; i < 10; i++)
      b[i] = new test();

    a[0].a = MPI.COMM_WORLD.Rank();

    req = new Request[2 * tasks];
    stats = new Status[2 * tasks];
    // buf = new mpi.Buffer(10000);
    buf = ByteBuffer.allocateDirect(10000);
    MPI.Buffer_attach(buf);

    if (a[0].a == 0)
      System.out.println("> Testing Isend/Irecv...");
    for (i = 0; i < tasks; i++)
      b[i].a = -1;
    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Isend(a, 0, 1, MPI.OBJECT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(b, i, 1, MPI.OBJECT, i, 1);

    }

    wstart();
    /*
     * if(a[0].a == 0) System.out.println("> Testing Issend/Irecv...");
     * for(i=0;i<tasks;i++) b[i].a = -1;
     * 
     * for(i=0;i<tasks;i++) {
     * req[2*i]=MPI.COMM_WORLD.Issend(a,0,1,MPI.OBJECT,i,1);
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1); }
     * 
     * wstart();
     * 
     * if(a[0].a == 0) System.out.println("> Testing Irecv/Irsend...");
     * for(i=0;i<tasks;i++) b[i].a = -1; for(i=0;i<tasks;i++) {
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1); }
     * 
     * 
     * MPI.COMM_WORLD.Barrier();
     * 
     * for(i=0;i<tasks;i++) {
     * req[2*i]=MPI.COMM_WORLD.Irsend(a,0,1,MPI.OBJECT,i,1); }
     * 
     * 
     * wstart();
     * 
     * if(a[0].a == 0) System.out.println("> Testing Ibsend/Irecv...");
     * for(i=0;i<tasks;i++) b[i].a = -1; for(i=0;i<tasks;i++) {
     * 
     * req[2*i]=MPI.COMM_WORLD.Ibsend(a,0,1,MPI.OBJECT,i,1);
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1);
     * 
     * } wstart();
     */
    MPI.COMM_WORLD.Barrier();
    if (a[0].a == 1)
      System.out.println("IsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
