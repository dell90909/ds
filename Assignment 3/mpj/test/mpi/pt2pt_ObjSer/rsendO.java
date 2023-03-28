package mpj.test.mpi.pt2pt_ObjSer;

import mpj.*;

public class rsendO {
  static public void main(String[] args) throws Exception {
    try {
      rsendO c = new rsendO(args);
    }
    catch (Exception e) {
    }
  }

  public rsendO() {
  }

  public rsendO(String[] args) throws Exception {

    Class cls = mpi.pt2pt_ObjSer.test.class.getClassLoader().getClass();
    // System.out.println(" cls "+cls);
    // System.out.println(" classLoader(rsend) "+mpi.pt2pt_ObjSer.test.class.
    // getClassLoader() );

    mpi.pt2pt_ObjSer.test a[] = new mpi.pt2pt_ObjSer.test[10];
    mpi.pt2pt_ObjSer.test b[] = new mpi.pt2pt_ObjSer.test[10];

    int tasks, me, i;
    char buf[] = new char[10];
    double time;
    Status status;

    for (i = 0; i < 10; i++) {
      a[i] = new mpi.pt2pt_ObjSer.test();
      b[i] = new mpi.pt2pt_ObjSer.test();
      a[i].a = i;
      b[i].a = 0;
    }

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    // MPI.COMM_WORLD.Barrier();

    if (me == 0) {
      for (i = 0; i < 1000000; i++)
	;
      MPI.COMM_WORLD.Rsend(a, 0, 10, MPI.OBJECT, 1, 1);
    } else if (me == 1) {
      MPI.COMM_WORLD.Recv(b, 0, 10, MPI.OBJECT, 0, 1);
      for (i = 0; i < 10; i++)
	if (b[i].a != i)
	  System.out.println("Data " + b[i].a + " on index " + i + "should be "
	      + i);
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("RsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
