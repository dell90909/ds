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
package mpj.test.jgf_mpj_benchmarks.section3;

import jgf_mpj_benchmarks.section3.moldyn.*;
import jgf_mpj_benchmarks.section3.montecarlo.*;
import jgf_mpj_benchmarks.section3.raytracer.*;
import mpj.*;
import jgf_mpj_benchmarks.jgfutil.*;

public class JGFRayTracerBenchSizeA{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws MPIException{

/* Initialise MPI */
    MPI.Init(argv);
    rank = MPI.COMM_WORLD.Rank();
    nprocess = MPI.COMM_WORLD.Size();

    if(rank==0) {
      JGFInstrumentor.printHeader(3,0,nprocess);
    }

    JGFRayTracerBench rtb = new JGFRayTracerBench(nprocess,rank); 
    rtb.JGFrun(0);
 
/* Finalise MPI */
    MPI.Finalize();

  }
}

 
