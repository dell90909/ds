package mpj.test.mpi.pt2pt;

import mpj.*;

public class iprobe {
  static public void main(String[] args) throws Exception {
    try {
      iprobe c = new iprobe(args);
    }
    catch (Exception e) {
    }
  }

  public iprobe() {
  }

  public iprobe(String[] args) throws Exception {
    int me, cnt = 1, src = -1, tag;
    int data[] = new int[1];
    boolean flag;
    Intracomm comm;
    Status status = null;

    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.Rank();

    if (me == 0) {
      data[0] = 7;
      comm.Send(data, 0, 1, MPI.INT, 1, 1);
    } else if (me == 1) {
      try {
	Thread.currentThread().sleep(1000);
      }
      catch (Exception e) {
      }
      // for(int k=0 ; k<2 ; k ++) {
      for (;;) {
	// System.out.println(" (b) status "+status);
	status = comm.Iprobe(0, 1);
	// System.out.println(" (a) status "+status);
	if (status != null)
	  break;
      }

      src = status.source;
      if (src != 0)
	System.out
	    .println("ERROR in MPI_Probe: src = " + src + ", should be 0");

      tag = status.tag;
      if (tag != 1)
	System.out
	    .println("ERROR in MPI_Probe: tag = " + tag + ", should be 1");

      cnt = status.Get_count(MPI.INT);
      System.out.println(" MPI_Probe1: cnt = " + cnt);
      if (cnt != 1)
	System.out
	    .println("ERROR in MPI_Probe: cnt = " + cnt + ", should be 1");

      status = comm.Recv(data, 0, cnt, MPI.INT, src, tag);
      cnt = status.Get_count(MPI.INT);
      System.out.println(" MPI_Probe2: cnt = " + cnt);

      if (data[0] != 7)
	System.out.println("ERROR inMPI_Recv,data[0]=" + data[0]
	    + "should be 7");

    }

    comm.Barrier();
    if (me == 1)
      System.out.println("Iprobe TEST COMPLETE ");
    MPI.Finalize();
  }
}
