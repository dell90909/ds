package mpj.test.mpi.pt2pt; 

import java.nio.ByteBuffer ;

import mpj.*;

public class bsend_test1 {
   static public void main(String[] args) throws MPIException {
	  try{
		bsend_test1 c = new bsend_test1(args);
	  }catch(Exception e){
	  }
  }

  public bsend_test1() {
  }
  
  public bsend_test1(String[] args) throws Exception {	  
    /* Note that the buffer sizes must include the BSEND_OVERHEAD;
       these values are probably sizeof(int) too large */
    
    int len,tsks,me,i,size,rc;	
    final int A1   = 1000;
    final int A100 = 100000;
    Status status;

    MPI.Init(args);
    me=MPI.COMM_WORLD.Rank();  
    size = MPI.COMM_WORLD.Size();

    if(size > 2) {
      if(me == 0)	    
        System.out.println("bsend_test1: Must run with 2 tasks!");
      MPI.Finalize();	    
      return;
    }

    //System.out.println(MPI.Get_processor_name());
    
    int data1[] = new int[A1];
    int data100[] = new int[A100];
    
    int intsize = 4;

//mpi.Buffer buf1 = new mpi.Buffer(MPI.COMM_WORLD.Pack_size( A1, MPI.INT) );  
ByteBuffer buf1 = ByteBuffer.allocateDirect ( 
		MPI.COMM_WORLD.Pack_size(A1, MPI.INT) 
		+ MPI.BSEND_OVERHEAD );
//mpi.Buffer buf100 = new mpi.Buffer(MPI.COMM_WORLD.Pack_size(A100, MPI.INT));
ByteBuffer buf100 = ByteBuffer.allocateDirect ( 
		MPI.COMM_WORLD.Pack_size(A100, MPI.INT) + MPI.BSEND_OVERHEAD );
    
    if ( me == 0 ) {      
      MPI.Buffer_attach(buf1);  
      MPI.COMM_WORLD.Bsend(data1,0,A1,MPI.INT,1,1);
      MPI.Buffer_detach();
      MPI.Buffer_attach(buf100);
      MPI.COMM_WORLD.Barrier();
      
      /* test to see if large array is REALLY being buffered */
      for(i=0;i<A100;i++)  data100[i] = 1;
      
      
      MPI.COMM_WORLD.Bsend(data100,0,A100,MPI.INT,1,2);//1
      MPI.COMM_WORLD.Recv(data100,0,A100,MPI.INT,1,3);//2
      
      for(i=0;i<A100;i++)
	if(data100[i] != 2)  
	  System.out.println
	    ("ERROR, incorrect data["+i+"]="+data100[i]+", task 0");
      
    } else if ( me == 1 ) {
      MPI.COMM_WORLD.Recv(data1,0,A1,MPI.INT,0,1);
      MPI.COMM_WORLD.Barrier();

      MPI.Buffer_attach(buf100);
      
      /* test to see if large array is REALLY being buffered */
      for(i=0;i<A100;i++)  data100[i] = 2;
      
      MPI.COMM_WORLD.Bsend(data100,0,A100,MPI.INT,0,3);//2
      MPI.COMM_WORLD.Recv(data100,0,A100,MPI.INT,0,2);//1
      
      for(i=0;i<A100;i++) { 
	if(data100[i] != 1)  
	  System.out.println
	    ("ERROR , incorrect data["+i+"]="+data100[i]+", task 1");
      }
      
    }
    
    MPI.COMM_WORLD.Barrier();

    if(me == 1) System.out.println("Bsend TEST COMPLETE"+me);
    MPI.Finalize();
  }
  
}
