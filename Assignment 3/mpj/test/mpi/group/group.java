package mpj.test.mpi.group;

import mpj.*;

public class group {
  static public void main(String[] args) throws Exception {
    try {
      group a = new group(args);
    }
    catch (Exception e) {
    }
  }

  public group() {
  }

  public group(String[] args) throws Exception {

    int tasks, me, size, rank, i, result, rc;
    int cnt = 0;

    MPI.Init(args);

    mpi.Group group1, group2, group3, newgroup;
    mpi.Group groups[] = new mpi.Group[20];
    Comm newcomm;

    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();
    int ranks1[] = new int[tasks / 2];
    int ranks2[] = new int[tasks / 2];
    int ranks3[] = new int[tasks];

    if (tasks != 8) {
      if (me == 0)
	System.out.println("group->group: MUST HAVE 8 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    group1 = MPI.COMM_WORLD.Group();
    groups[cnt++] = group1;

    size = group1.Size();

    if (size != tasks)
      System.out.println("ERROR in MPI_Group_size, size = " + size
	  + ", should be " + tasks);

    rank = group1.Rank();

    if (rank != me)
      System.out.println("ERROR in MPI_Group_rank, rank = " + rank
	  + ", should be " + me);

    for (i = 0; i < tasks / 2; i++)
      ranks1[i] = i;

    newgroup = group1.Incl(ranks1);

    /* newgroup freed below */
    size = newgroup.Size();
    if (size != (tasks / 2))
      System.out.println("ERROR in MPI_Group_size, size = " + size
	  + ", should be " + (tasks / 2));

    result = mpi.Group.Compare(newgroup, newgroup);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Group_compare (1), result = " + result
	  + ", should be " + MPI.IDENT);

    result = mpi.Group.Compare(newgroup, group1);
    if (result != MPI.UNEQUAL)
      System.out.println("ERROR in MPI_Group_compare (2), result = " + result
	  + ", should be " + MPI.UNEQUAL);

    group2 = mpi.Group.Union(group1, newgroup);
    groups[cnt++] = group2;
    result = mpi.Group.Compare(group1, group2);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Group_compare (3), result = " + result
	  + ", should be " + MPI.IDENT);

    group2 = mpi.Group.Intersection(newgroup, group1);
    groups[cnt++] = group2;
    result = mpi.Group.Compare(group2, newgroup);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Group_compare (4), result = " + result
	  + ", should be " + MPI.IDENT);

    group2 = mpi.Group.Difference(group1, newgroup);
    groups[cnt++] = group2;
    size = group2.Size();
    if (size != (tasks / 2))
      System.out.println("ERROR in MPI_Group_size, size = " + size
	  + ", should be " + (tasks / 2));

    for (i = 0; i < size; i++)
      ranks1[i] = i;
    ranks2 = mpi.Group.Translate_ranks(group2, ranks1, group1);
    for (i = 0; i < size; i++) {
      if (ranks2[i] != (tasks / 2 + i))
	System.out.println("ERROR in MPI_Group_translate_ranks.");
    }

    newcomm = MPI.COMM_WORLD.Create(newgroup);
    if (newcomm != null) {
      group3 = newcomm.Group();
      groups[cnt++] = group3;
      result = mpi.Group.Compare(group3, newgroup);
      if (result != MPI.IDENT)
	System.out.println("ERROR in MPI_Group_compare (4.5) , result = "
	    + result + ", should be " + MPI.IDENT);
    }

    group3 = group1.Excl(ranks1);
    groups[cnt++] = group3;
    result = mpi.Group.Compare(group2, group3);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Group_compare (5) , result = " + result
	  + ", should be " + MPI.IDENT);

    for (i = 0; i < tasks; i++)
      ranks3[tasks - 1 - i] = i;
    group3 = group1.Incl(ranks3);
    groups[cnt++] = group3;
    result = mpi.Group.Compare(group1, group3);
    if (result != MPI.SIMILAR)
      System.out.println("ERROR in MPI_Group_compare (6), result = " + result
	  + ", should be " + MPI.SIMILAR);

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Group TEST COMPLETE");
    MPI.Finalize();

  }
}
