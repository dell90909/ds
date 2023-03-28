package mpj.test.mpi.topo;

import mpj.*;

public class sub {
  static public void main(String[] args) throws Exception {
    try {
      sub c = new sub(args);
    }
    catch (Exception e) {
    }
  }

  public sub() {
  }

  public sub(String[] args) throws Exception {

    int dims[] = new int[2];
    boolean periods[] = new boolean[2];
    int me, tasks, i;
    int size, rank;
    int cnt = 0;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks != 6) {
      if (me == 0)
	System.out.println("MUST RUN WITH 6 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }
    Comm comms[] = new Comm[20];

    dims[0] = 2;
    dims[1] = 3;
    Cartcomm comm = MPI.COMM_WORLD.Create_cart(dims, periods, false);
    comms[cnt++] = comm;

    int[] dims2 = comm.Get().dims;

    boolean remain[] = new boolean[2];
    remain[0] = false;
    remain[1] = true;
    Cartcomm subcomm = comm.Sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.Size();
    if (size != 3)
      System.out.println("ERROR in MPI_Cart_sub (1): size = " + size
	  + ", should be 3");

    rank = subcomm.Rank();
    if (rank != me % 3)
      System.out.println("ERROR in MPI_Cart_sub (2): rank =" + rank
	  + ", should be " + me);

    remain[0] = false;
    remain[1] = false;
    subcomm = comm.Sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.Size();
    if (size != 1)
      System.out.println("ERROR in MPI_Cart_sub (3): size = " + size
	  + ", should be 1");

    rank = subcomm.Rank();
    if (rank != 0)
      System.out.println("ERROR in MPI_Cart_sub (4): rank =" + rank
	  + ", should be 0");

    remain[0] = true;
    remain[1] = true;
    subcomm = comm.Sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.Size();
    if (size != tasks)
      System.out.println("ERROR in MPI_Cart_sub (5): size = " + size
	  + ", should be " + tasks);

    rank = subcomm.Rank();
    if (rank != me)
      System.out.println("ERROR in MPI_Cart_sub (6): rank =" + rank
	  + ", should be " + me);

    remain[0] = true;
    remain[1] = false;
    subcomm = comm.Sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.Size();
    if (size != 2)
      System.out.println("ERROR in MPI_Cart_sub (7): size = " + size
	  + ", should be 2");

    rank = subcomm.Rank();
    if (rank != me / 3)
      System.out.println("ERROR in MPI_Cart_sub (8): rank =" + rank
	  + ", should be " + (me / 3));

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Sub TEST COMPLETE\n");
    MPI.Finalize();
  }
}
