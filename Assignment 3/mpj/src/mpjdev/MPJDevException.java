/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 
 *
 * File         : MPJException.java
 * Author       : Bryan Carpenter
 * Created      : Fri Nov 15 23:28:05 EST 2002
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2005/06/14 09:23:47 $
 */



package mpj.src.mpjdev;

import xdev.XDevException;

public class MPJDevException extends XDevException {

  //XDevException not to be thrown by natmpjdev
  // at the moment we leave this as is
  public MPJDevException() {
    super();
  }

  public MPJDevException(Throwable cause) {
    super(cause);
  }

  public MPJDevException(String s) {
    super(s);
  }

}
