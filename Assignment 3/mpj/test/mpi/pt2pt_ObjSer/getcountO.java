package mpj.test.mpi.pt2pt_ObjSer;

import java.io.*;

import mpj.*;

public class getcountO {
  static public void main(String[] args) throws Exception {
    try {
      getcountO c = new getcountO(args);
    }
    catch (Exception e) {
    }
  }

  public getcountO() {
  }

  public getcountO(String[] args) throws Exception {
    int me, count, i, j;

    int datatest[][] = new int[7][4];
    int recdata[][] = new int[7][4];
    Status status;

    for (i = 0; i < 7; i++)
      for (j = 0; j < 4; j++) {
	datatest[i][j] = j + i * 4;
	recdata[i][j] = 0;
      }
    MPI.Init(args);

    me = MPI.COMM_WORLD.Rank();

    if (me == 0)
      MPI.COMM_WORLD.Send(datatest, 0, 7, MPI.OBJECT, 1, 1);

    else if (me == 1) {
      status = MPI.COMM_WORLD.Recv(recdata, 0, 7, MPI.OBJECT, 0, 1);

      for (i = 0; i < 7; i++)
	for (j = 0; j < 4; j++) {
	  if (recdata[i][j] != datatest[i][j])
	    System.out.println("Recived data  " + recdata[i][j] + " at index ["
		+ i + "][" + j + "] should be : " + datatest[i][j]);
	}
      count = status.Get_count(MPI.OBJECT);
      if (count != 7)
	System.out.println("ERROR(4) in MPI_Get_count, count = " + count
	    + ", should be 7");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Get_countO TEST COMPLETE.");
    MPI.Finalize();
  }
}
