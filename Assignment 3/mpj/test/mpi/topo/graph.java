package mpj.test.mpi.topo;

import mpj.*;

public class graph {
  static public void main(String[] args) throws Exception {
    try {
      graph c = new graph(args);
    }
    catch (Exception e) {
    }
  }

  public graph() {
  }

  public graph(String[] args) throws Exception {

    int index[] = { 2, 3, 4, 6 };
    int edges[] = { 1, 3, 0, 3, 0, 2 };
    int me, tasks;
    boolean reorder;
    int i, num, start, type, nnodes, nedges;
    int neighbors[] = new int[4];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks != 4) {
      if (me == 0)
	System.out.println("topo->graph runs with 4 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    reorder = false;
    Graphcomm comm = MPI.COMM_WORLD.Create_graph(index, edges, reorder);

    type = comm.Topo_test();
    if (type != MPI.GRAPH)
      System.out.println("ERROR in MPI_Topo_test, type = " + type
	  + ", should be " + MPI.GRAPH + " (MPI.GRAPH)");

    index = comm.Get().index;
    nnodes = comm.Get().index.length;
    nedges = comm.Get().edges.length;
    if (nnodes != 4 || nedges != 6)
      System.out.println("ERROR in MPI_Graphdims_get, nnodes= " + nnodes
	  + ", nedges = " + nedges + ", should be 4, 6");

    index = comm.Get().index;
    edges = comm.Get().edges;
    if (index[0] != 2 || index[1] != 3 || index[2] != 4 || index[3] != 6)
      System.out.println("ERROR in MPI_Graph_get: index = " + index[0] + ", "
	  + index[1] + ", " + index[2] + ", " + index[3]
	  + ", should be 2,3,4,6");
    if (edges[0] != 1 || edges[1] != 3 || edges[2] != 0 || edges[3] != 3
	|| edges[4] != 0 || edges[5] != 2)
      System.out.println("ERROR in MPI_Graph_get: edges = " + edges[0] + ", "
	  + edges[1] + ", " + edges[2] + ", " + edges[3] + ", " + edges[4]
	  + ", " + edges[5] + ", should be 1,3,0,3,0,2");

    nnodes = comm.Neighbours(me).length;
    int k = (me == 0 || me == 3) ? 1 : 0;
    if (nnodes != 1 + k)
      System.out.println("ERROR in MPI_Graph_neighbors_count: count = "
	  + nnodes + ", should be 1+(me==0|me==3)");

    neighbors = comm.Neighbours(me);
    start = (me == 0) ? 0 : index[me - 1];
    num = (me == 0) ? index[0] : index[me] - start;
    for (i = 0; i < num; i++) {
      if (neighbors[i] != edges[start + i])
	System.out
	    .println("ERROR in MPI_Graph_neighbors: wrong neighbor on task "
		+ me + " index " + i + ", neighbor = " + neighbors[i]
		+ ", should be " + edges[start + i]);
    }

    Graphcomm comm2 = MPI.COMM_WORLD.Create_graph(index, edges, reorder);

    comm.Barrier();
    if (me == 0)
      System.out.println("Graph TEST COMPLETE\n");
    MPI.Finalize();

  }
}
