package mpj.test.mpi.comm;

import mpj.*;

public class intercomm {

  static int newsize, me, size, color, key, local_lead, remote_lead, newme,
      othersum;
  static int mebuf[] = new int[1];
  static int sum[] = new int[1];
  static int newsum[] = new int[1];
  static boolean flag;
  static Intracomm comm, mergecomm;
  static Intercomm intercomm;
  static Status status;
  static Group newgid;

  static void inter_tests() throws MPIException {
    flag = intercomm.Test_inter();
    if (flag != true)
      System.out.println("ERROR in MPI_Comm_test_inter: flag = " + flag
	  + ", should be 1");

    newsize = intercomm.Remote_size();
    if (newsize != size / 2)
      System.out.println("ERROR in MPI_Comm_remote_size: size = " + newsize
	  + ", should be " + (size / 2));

    newgid = intercomm.Remote_group();
    newsize = newgid.Size();
    if (newsize != size / 2)
      System.out.println("ERROR in MPI_Comm_remote_group: size = " + newsize
	  + ", should be " + (size / 2));

    newsum[0] = sum[0];
    status = intercomm.Sendrecv_replace(newsum, 0, 1, MPI.INT, newme, 7, newme,
	7);
    othersum = size / 2 * (size / 2 - 1);
    if (me % 2 == 0)
      othersum += size / 2;
    if (othersum != newsum[0])
      System.out.println("ERROR in Intercomm_create, sum = " + othersum
	  + ", should be " + newsum);

    boolean high = (color == 1) ? true : false;
    Intracomm mergecomm = intercomm.Merge(high);
    mebuf[0] = me;
    mergecomm.Allreduce(mebuf, 0, newsum, 0, 1, MPI.INT, MPI.SUM);
    if (newsum[0] != size * (size - 1) / 2)
      System.out.println("ERROR in MPI_Intercomm_merge: sum = " + newsum[0]
	  + ", should be " + size * (size - 1) / 2);

  }

  // //////////////////////////////////////////////////////////

  public static void main(String args[]) throws Exception {
    try {
      intercomm a = new intercomm(args);
    }
    catch (Exception e) {
    }
  }

  public intercomm() {
  }

  public intercomm(String[] args) throws Exception {

    Intracomm comm1, comm2;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (size % 2 == 1) {
      if (me == 0)
	System.out.println("comm->intercomm: MUST RUN WITH EVEN NUMBER"
	    + "OF TASKS");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    key = me;
    color = me % 2;
    comm = MPI.COMM_WORLD.Split(color, key);
    comm1 = comm;
    flag = comm.Test_inter();
    if (flag != false)
      System.out.println("ERROR in MPI_Comm_test_inter: flag = " + flag
	  + ", should be false");
    newme = comm.Rank();
    mebuf[0] = me;
    comm.Allreduce(mebuf, 0, sum, 0, 1, MPI.INT, MPI.SUM);
    local_lead = 0;
    // local_lead = (color==0) ? 0 : 1;
    remote_lead = (color == 1) ? 0 : 1;

    intercomm = MPI.COMM_WORLD.Create_intercomm(comm, local_lead, remote_lead,
	5);

    inter_tests();
    /*
     * Intercomm incomm = (Intercomm) intercomm.clone(); intercomm = incomm;
     * inter_tests();
     * 
     * MPI.COMM_WORLD.Barrier();
     */
    if (me == 0)
      System.out.println("Intercomm TEST COMPLETE");
    MPI.Finalize();
  }
}
