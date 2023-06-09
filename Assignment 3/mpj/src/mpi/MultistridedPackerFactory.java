/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : MultistridedPackerFactory.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.6 $
 * Updated      : $Date: 2005/07/29 14:03:09 $
 */

package mpj.src.mpi;

public class MultistridedPackerFactory {

  public static Packer create(int rank, int[] indexes, int extent, int size,
                              int baseType) {
	  
    switch (baseType) {
      case -1: //UNDEFINED (dont know what to do here)
        break;
      
      case 0: //NULL (dont know what to do here)
        break;
      
      case 1: //BYTE
        return new MultistridedPackerByte(rank, indexes, extent, size);
      
      case 2: //CHAR
        return new MultistridedPackerChar(rank, indexes, extent, size);
	
      case 3: //SHORT
        return new MultistridedPackerShort(rank, indexes, extent, size);

      case 4: //BOOLEAN
        return new MultistridedPackerBoolean(rank, indexes, extent, size);

      case 5: //INT
        return new MultistridedPackerInt(rank, indexes, extent, size);

      case 6: //LONG
        return new MultistridedPackerLong(rank, indexes, extent, size);

      case 7: //FLOAT
        return new MultistridedPackerFloat(rank, indexes, extent, size);

      case 8: //DOUBLE
        return new MultistridedPackerDouble(rank, indexes, extent, size);

      case 9: //PACKED
        break;

      case 10: //LB
        break;

      case 11: //UB
        //i think its not dealt with here
        break;

      case 12: //OBJECT
        return new MultistridedPackerObject(rank, indexes, extent, size);

      default:
        break;
    }

    return null;
  }
}
