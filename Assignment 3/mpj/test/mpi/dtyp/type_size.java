package mpj.test.mpi.dtyp;

import mpj.*;

public class type_size {
  public static void main(String args[]) throws Exception {
    try {
      type_size c = new type_size(args);
    }
    catch (Exception e) {
    }
  }

  public type_size() {
  }

  public type_size(String[] args) throws Exception {
    int my_rank;

    MPI.Init(args);
    my_rank = MPI.COMM_WORLD.Rank();
    if (my_rank == 0) {
      System.out.println();
      System.out.println("MPI.BYTE.Size()    = " + MPI.BYTE.Size());
      System.out.println("MPI.CHAR.Size()    = " + MPI.CHAR.Size());
      System.out.println("MPI.SHORT.Size()   = " + MPI.SHORT.Size());
      System.out.println("MPI.BOOLEAN.Size() = " + MPI.BOOLEAN.Size());
      System.out.println("MPI.INT.Size()     = " + MPI.INT.Size());
      System.out.println("MPI.LONG.Size()    = " + MPI.LONG.Size());
      System.out.println("MPI.FLOAT.Size()   = " + MPI.FLOAT.Size());
      System.out.println("MPI.DOUBLE.Size()  = " + MPI.DOUBLE.Size());
      System.out.println();
      System.out.println("type_size TEST COMPLETED");
    }

    MPI.Finalize();

  }
}
