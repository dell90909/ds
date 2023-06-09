package mpj.test.mpi.ccl_ObjSer;

import mpj.*;

public class allgathervO {
  static public void main(String[] args) throws Exception {
    try {
      allgathervO c = new allgathervO(args);
    }
    catch (Exception e) {
    }
  }

  public allgathervO() {
  }

  public allgathervO(String[] args) throws Exception {

    final int MAXLEN = 10;

    int root, i, j, k;
    int myself, tasks, stride = 15;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0)
	System.out.println("allgathervO must run with fewer than 8 tasks!");
      MPI.Finalize();
      return;
    }

    int out[][] = new int[MAXLEN][MAXLEN];
    int in[][] = new int[MAXLEN * stride * tasks][MAXLEN];
    int dis[] = new int[MAXLEN];
    int rcount[] = new int[MAXLEN];
    int ans[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0 };

    for (i = 0; i < MAXLEN; i++)
      for (j = 0; j < MAXLEN; j++) {
	dis[i] = i * stride;
	rcount[i] = 5;
	out[j][i] = j + 1;
      }
    rcount[0] = 10;

    for (i = 0; i < MAXLEN * tasks * stride; i++)
      for (j = 0; j < MAXLEN; j++)
	in[i][j] = 0;

    if (myself == 0)
      MPI.COMM_WORLD.Allgatherv(out, 0, 10, MPI.OBJECT, in, 0, rcount, dis,
	  MPI.OBJECT);
    else
      MPI.COMM_WORLD.Allgatherv(out, 0, 5, MPI.OBJECT, in, 0, rcount, dis,
	  MPI.OBJECT);
    /*
     * for(j = 0; j < tasks; j++){ if(myself==j){ for(k=0; k<MAXLEN;k++)
     * for(i=0; i<tasks*stride; i++) if (ans[i]!=in[i][k])
     * System.out.println("recived data : "
     * +in[i][k]+"at ["+i+"]["+k+"] should be : "+ans[i]+" on proc. : "+j); }
     * MPI.COMM_WORLD.Barrier(); }
     */
    if (myself == 0)
      System.out.println("AllgathervO TEST COMPLETE");
    MPI.Finalize();
  }
}
