package mpj.test.mpi.dtyp_ObjSer;

import mpj.*;

public class hvecO {
  static public void main(String[] args) throws Exception {
    try {
      hvecO c = new hvecO(args);
    }
    catch (Exception e) {
    }
  }

  public hvecO() {
  }

  public hvecO(String[] args) throws Exception {
    int my_rank;

    MPI.Init(args);
    my_rank = MPI.COMM_WORLD.Rank();

    Datatype messtyp, messtyp2;
    int root = 0;
    int i, istat, big_offset;
    int intlen;
    final int DL = 32;

    test dar[] = new test[DL];

    for (i = 0; i < DL; i++) {
      dar[i] = new test();
      dar[i].a = my_rank;
    }

    int count = 2;
    int bllen = 3;
    int gap = 1;
    int str = bllen + gap;

    messtyp = Datatype.Vector(count, bllen, str, MPI.OBJECT);
    messtyp.Commit();
    MPI.COMM_WORLD.Bcast(dar, 0, 1, messtyp, root);
    if (my_rank == 1) {
      System.out.println("  0 = Sent, 1 = Not Sent");
      System.out.println("  Vector Type with Gap : ");
    }

    if (my_rank == 1) {
      for (i = 0; i < DL; i++)
	System.out.print(dar[i].a + " ");
      System.out.println();
      System.out.println();
    }

    intlen = 1;
    for (big_offset = -intlen; big_offset <= 2 * intlen; big_offset += intlen) {
      if (my_rank == 1)
	System.out
	    .println(" Three of above vector types combined, with offset = "
		+ (big_offset / intlen) + " ints");

      for (i = 0; i < DL; i++)
	dar[i].a = my_rank;

      count = 3;
      int ext = messtyp.Extent();
      messtyp2 = Datatype.Hvector(count, 1, ext + big_offset, messtyp);
      messtyp2.Commit();
      MPI.COMM_WORLD.Bcast(dar, 0, 1, messtyp2, root);

      MPI.COMM_WORLD.Barrier();

      if (my_rank == 1) {
	for (i = 0; i < DL; i++)
	  System.out.print(dar[i].a + " ");
	System.out.println();
	System.out.println();
      }
    }

    MPI.Finalize();
  }
}
