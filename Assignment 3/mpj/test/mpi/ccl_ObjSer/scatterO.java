package mpj.test.mpi.ccl_ObjSer;

import mpj.*;

public class scatterO {
  static public void main(String[] args) throws Exception {
    try {
      scatterO c = new scatterO(args);
    }
    catch (Exception e) {
    }
  }

  public scatterO() {
  }

  public scatterO(String[] args) throws Exception {

    final int MAXLEN = 1000;

    int root, i = 0, j, k;
    test out[] = new test[MAXLEN * 64];
    test in[] = new test[MAXLEN];
    int myself, tasks;

    for (int l = 0; l < MAXLEN; l++)
      in[l] = new test();
    for (int l = 0; l < MAXLEN * 64; l++)
      out[l] = new test();

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    for (j = 1, root = 0; j <= MAXLEN; j *= 10, root = (root + 1) % tasks) {
      if (myself == root)
	for (i = 0; i < j * tasks; i++)
	  out[i].a = i;

      MPI.COMM_WORLD.Scatter(out, 0, j, MPI.OBJECT, in, 0, j, MPI.OBJECT, root);

      for (k = 0; k < j; k++) {
	if (in[k].a != k + myself * j) {
	  System.out.println("task " + myself + ":" + "bad answer ("
	      + (in[k].a) + ") at index " + k + " of " + j + "(should be "
	      + (k + myself * j) + ")");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("ScatterO TEST COMPLETE");
    MPI.Finalize();
  }
}
