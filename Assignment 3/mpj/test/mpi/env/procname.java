package mpj.test.mpi.env;

import mpj.*;

public class procname {
  static public void main(String[] args) throws Exception {
    try {
      procname c = new procname(args);
    }
    catch (Exception e) {
    }
  }

  public procname() {
  }

  public procname(String[] args) throws Exception {

    int me, len;
    char tmp[] = new char[256];
    String procname;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    procname = MPI.Get_processor_name();

    System.out.println("Processor name is " + procname);

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Procname TEST COMPLETE\n");
    MPI.Finalize();
  }
}
