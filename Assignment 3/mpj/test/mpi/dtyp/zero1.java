package mpj.test.mpi.dtyp;

import mpj.*;

public class zero1 {
  public static void main(String args[]) throws Exception {
    try {
      zero1 c = new zero1(args);
    }
    catch (Exception e) {
    }
  }

  public zero1() {
  }

  public zero1(String[] args) throws Exception {

    int myself, tasks;

    int ii[] = new int[1];
    int numtasks, me;
    int count1, count2, count3;
    int len[] = new int[0];
    int disp[] = new int[0];
    Datatype type[] = new Datatype[0];

    MPI.Init(args);
    Datatype newtype;
    Status status;

    myself = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if ((numtasks > 2) && (me > 1)) {
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    } else {
      newtype = Datatype.Struct(len, disp, type);
      newtype.Commit();

      if (myself == 0) {
	ii[0] = 2;
	MPI.COMM_WORLD.Send(ii, 0, 100, newtype, 1, 0);
      } else if (myself == 1) {
	ii[0] = 0;
	status = MPI.COMM_WORLD.Recv(ii, 0, 100, newtype, 0, 0);
	if (ii[0] != 0)
	  System.out.println("ERROR!");

	count1 = status.Get_count(newtype);
	count2 = status.Get_elements(newtype);

	if ((count1 == 100) && (count2 == MPI.UNDEFINED))
	  System.out.println("Success\n");
	else
	  System.out.println("Should be 100, MPI.UNDEFINED but is " + count1
	      + ", " + count2);
      }

      if (myself == 1)
	System.out.println("zero1 TEST COMPLETE");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
    }
  }
}
