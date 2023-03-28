package mpj.test.mpi.comm;

import mpj.*;

public class compare {
  static public void main(String[] args) throws Exception {
    try {
      compare a = new compare(args);
    }
    catch (Exception e) {
    }
  }

  public compare() {
  }

  public compare(String[] args) throws Exception {

    Intracomm comm1, comm2;
    int me, result, color, key;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    comm1 = (Intracomm) MPI.COMM_WORLD.clone();

    result = Comm.Compare(comm1, comm1);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.IDENT) + "(MPI_IDENT)");

    result = Comm.Compare(MPI.COMM_WORLD, comm1);
    if (result != MPI.CONGRUENT)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.CONGRUENT) + "(MPI_CONGRUENT)");

    color = 1;
    key = -me;
    comm2 = comm1.Split(color, key);
    result = Comm.Compare(comm1, comm2);
    if (result != MPI.SIMILAR)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.SIMILAR) + "(MPI_SIMILAR)");

    color = me;
    comm2 = comm1.Split(color, key);
    result = Comm.Compare(comm1, comm2);
    if (result != MPI.UNEQUAL)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.UNEQUAL) + "(MPI_UNEQUAL)");

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Compare TEST COMPLETE");

    MPI.Finalize();
  }
}
