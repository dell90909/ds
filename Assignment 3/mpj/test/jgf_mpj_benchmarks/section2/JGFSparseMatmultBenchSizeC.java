/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package mpj.test.jgf_mpj_benchmarks.section2;

import jgf_mpj_benchmarks.section2.crypt.*; 
import jgf_mpj_benchmarks.section2.series.*; 
import jgf_mpj_benchmarks.section2.lufact.*; 
import jgf_mpj_benchmarks.section2.sor.*; 
import jgf_mpj_benchmarks.section2.sparsematmult.*;
import mpj.*;
import jgf_mpj_benchmarks.jgfutil.*;

public class JGFSparseMatmultBenchSizeC{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws MPIException{

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();
     nprocess = MPI.COMM_WORLD.Size();

    if(rank==0) {
      JGFInstrumentor.printHeader(2,2,nprocess);
    }

    JGFSparseMatmultBench smm = new JGFSparseMatmultBench(nprocess,rank); 
    smm.JGFrun(2);

/* Finalise MPI */
     MPI.Finalize();
 
  }
}


