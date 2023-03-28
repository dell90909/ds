package mpj.test.mpi.topo;

import mpj.*;

public class dimscreate {
  static public void main(String[] args) throws Exception {
    try {
      dimscreate c = new dimscreate(args);
    }
    catch (Exception e) {
    }
  }

  public dimscreate() {
  }

  public dimscreate(String[] args) throws Exception {

    final int MAXDIMS = 10;
    int rc, tasks, me, ndims;
    int dims[] = new int[MAXDIMS];
    int dims2[] = new int[2];
    int dims3[] = new int[3];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    tasks = 6;

    ndims = 2;
    dims2[0] = 0;
    dims2[1] = 0;
    Cartcomm.Dims_create(tasks, dims2);
    if (dims2[0] != 3 || dims2[1] != 2)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 3, 2");

    ndims = 2;
    dims2[0] = 2;
    dims2[1] = 0;
    Cartcomm.Dims_create(tasks, dims2);
    if (dims2[0] != 2 || dims2[1] != 3)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 2, 3");

    dims3[0] = 0;
    dims3[1] = 0;
    dims3[2] = 0;
    ndims = 3;
    Cartcomm.Dims_create(tasks, dims3);
    if (dims3[0] != 3 || dims3[1] != 2 || dims3[2] != 1)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims3[0] + ","
	  + dims3[1] + "," + dims3[2] + ", should be 3,2,1");

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("DimsCreate TEST COMPLETE\n");
    MPI.Finalize();
  }
}
