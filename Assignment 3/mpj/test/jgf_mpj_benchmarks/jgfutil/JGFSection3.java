/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - MPJ Version 1.0             *
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


package mpj.test.jgf_mpj_benchmarks.jgfutil; 

import mpj.*;

public interface JGFSection3 {
  public void JGFsetsize(int size);
  public void JGFinitialise() throws MPIException;
  public void JGFapplication() throws MPIException;
  public void JGFvalidate();
  public void JGFtidyup();  
  public void JGFrun(int size) throws MPIException; 
}
 
