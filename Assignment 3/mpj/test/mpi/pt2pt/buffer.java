package mpj.test.mpi.pt2pt; 

import java.nio.ByteBuffer ;

import mpj.*;

public class buffer {
   static public void main(String[] args) throws MPIException {
	  try{
		buffer c = new buffer(args);
	  }catch(Exception e){
	  }
  }

  public buffer() {
  }

  public buffer(String[] args) throws Exception {

    int len,tasks,me,i,size,flag;
    int data[] = new int[100001];
    int sizeofint = 4;
  
    Status status;
    Request request;
    Errhandler warn;
 
    MPI.Init(args);    

                         
    me=MPI.COMM_WORLD.Rank();   
  int over = MPI.BSEND_OVERHEAD ;  // Checks constant correctly
                                     // initialized before MPI.Init().

    if(me==0) {
      size = MPI.COMM_WORLD.Pack_size( 100001, MPI.INT ) + over;      
      //System.out.println("over <"+over+">");
      //System.out.println(" size <"+size+">");
      //mpi.Buffer buf = new mpi.Buffer( size );
      ByteBuffer buf = ByteBuffer.allocateDirect ( size );
      MPI.Buffer_attach(buf);        
      
      for(i=0;i<100001;i++)  data[i] = i;

      request = MPI.COMM_WORLD.Ibsend(data,0,100001,MPI.INT,1,1); 
      status = request.Test();

    } else if(me == 1) {
      for(i=0;i<4000004;i++);

      MPI.COMM_WORLD.Recv(data,0,100001,MPI.INT,0,1);
      for(i=0;i<100001;i++)
	if(data[i] != i) { System.out.println("ERROR i = " + i); break; }
    }

    MPI.COMM_WORLD.Barrier();
    if(me == 1)  System.out.println("Buffer TESTS COMPLETE");
    MPI.Finalize();
  }
}

