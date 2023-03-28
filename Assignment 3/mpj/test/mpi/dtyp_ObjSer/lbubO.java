package mpj.test.mpi.dtyp_ObjSer;

import mpj.*;

public class lbubO {
  static public void main(String[] args) throws Exception {
    try {
      lbubO c = new lbubO(args);
    }
    catch (Exception e) {
    }
  }

  public lbubO() {
  }

  public lbubO(String[] args) throws Exception {

    Datatype newtype, newtype2, newtype3, newtype4, newtype5;
    Datatype newtype6, newtype7, newtype8, newtype9;
    int aob2[] = new int[2];
    int aob3[] = new int[3];
    int error = 0;
    int numtasks, me;
    int extent, lb, ub;
    int aod2[] = new int[2];
    int aod3[] = new int[3];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();

    Datatype aot2[] = new Datatype[2];
    Datatype aot3[] = new Datatype[3];

    if ((numtasks > 1) && (me != 0)) {
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    newtype = Datatype.Contiguous(4, MPI.OBJECT);
    newtype.Commit();

    aot2[0] = newtype;
    aod2[0] = 0;
    aob2[0] = 1;
    aot2[1] = MPI.UB;
    aod2[1] = 97;
    aob2[1] = 1;
    newtype2 = Datatype.Struct(aob2, aod2, aot2);
    newtype2.Commit();

    extent = newtype2.Extent();
    lb = newtype2.Lb();
    ub = newtype2.Ub();
    if ((extent != 97) | (lb != 0) | (ub != 97)) {
      error++;
      System.out.println("Should be: Extent = 97, lb = 0, ub = 97.");
      System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	  + ", ub = " + ub);
    }

    aot3[0] = newtype;
    aod3[0] = 0;
    aob3[0] = 1;
    aot3[1] = MPI.LB;
    aod3[1] = 3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 94;
    aob3[2] = 1;
    newtype4 = Datatype.Struct(aob3, aod3, aot3);
    newtype4.Commit();

    extent = newtype4.Extent();
    lb = newtype4.Lb();
    ub = newtype4.Ub();
    if ((extent != 91) | (lb != 3) | (ub != 94)) {
      error++;
      System.out.println("Should be: Extent = 91, lb = 3, ub = 94.");
      System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	  + ", ub = " + ub);
    }

    aot3[0] = newtype;
    aod3[0] = 0;
    aob3[0] = 2;
    aot3[1] = MPI.LB;
    aod3[1] = -3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 96;
    aob3[2] = 1;
    newtype5 = Datatype.Struct(aob3, aod3, aot3);
    newtype5.Commit();

    extent = newtype5.Extent();
    lb = newtype5.Lb();
    ub = newtype5.Ub();
    if ((extent != 99) | (lb != -3) | (ub != 96)) {
      error++;
      System.out.println("Should be: Extent = 99, lb = -3, ub = 96.");
      System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	  + ", ub = " + ub);
    }

    aot3[0] = newtype;
    aod3[0] = 2;
    aob3[0] = 2;
    aot3[1] = MPI.LB;
    aod3[1] = -3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 86;
    aob3[2] = 1;
    newtype5 = Datatype.Struct(aob3, aod3, aot3);
    newtype5.Commit();

    extent = newtype5.Extent();
    lb = newtype5.Lb();
    ub = newtype5.Ub();
    if ((extent != 89) | (lb != -3) | (ub != 86)) {
      error++;
      System.out.println("Should be: Extent = 89, lb = -3, ub = 86.");
      System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	  + ", ub = " + ub);
    }

    if (error == 0)
      System.out.println("Upper bound/lower bound/extent test passed.\n");
    else
      System.out.println("ERRORS in bounds/extent test.\n");

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
