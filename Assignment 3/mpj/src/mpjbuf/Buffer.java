/* This file generated automatically from template Buffer.java.in. */

/*
The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package mpj.src.mpjbuf;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.nio.ByteOrder ;
import java.nio.ByteBuffer ;

/**
 * A buffer object, for holding an mpjdev message.
 * <p>
 * Through appropriate use of <i>sections</i>, described below,
 * a single buffer object can contain a mixture of elements of any Java type.
 * A single buffer object can be reused both for sending and receiving
 * messages, although at any given time a buffer is either in
 * a <i>readable</i> state or a <i>writeable</i> state.
 * A buffer is created with a fixed <i>static capacity</i> which does
 * not change during the lifetime of the buffer.  However a
 * buffer can also (under user control) store data in a dynamically
 * allocated region---this is necessary in particular to support
 * serialized objects.  A characteristic feature of the
 * {@link mpjbuf.Buffer mpjbuf.Buffer}
 * is that is supports transfer of data from and to non-contiguous
 * sections of a Java array through various gather and scatter methods.
 *
 * <h3>Sections</h3>
 * A message is considered to consist of zero or more "sections".
 * Each section holds a sequence of elements which
 * are either all of the same primitive type, or all have Object type.
 * When a buffer is in writeable mode, 
 * a new section is started by invoking
 * {@link #putSectionHeader(Type) putSectionHeader()}
 * to mark the start of the section.  This is followed by a series
 * {@link #write(byte[],int,int) write()},
 * {@link #gather(byte[],int,int,int[]) gather()}, and/or
 * {@link #strGather(byte[],int,int,int,int,int[]) strGather()}
 * operations to write the data to the section.  When a buffer is
 * in readable mode, one starts reading a section by invoking
 * {@link #getSectionHeader() getSectionHeader()}
 * to determine the type of elements in the next section,
 * and to make it the current section.
 * This is followed by a series
 * {@link #read(byte[],int,int) read()},
 * {@link #scatter(byte[],int,int,int[]) scatter()}, and/or
 * {@link #strScatter(byte[],int,int,int,int,int[]) strScatter()}
 * operations to read the data from the section.  Buffers may
 * only be written and read sequentially, from beginning to end.
 * <p>
 * In general a buffer object comprises two regions: a <i>primary</i> or
 * <i>static</i> buffer, and a <i>secondary</i> or <i>dynamic</i> buffer.
 * The static buffer has a fixed capacity, specified when the buffer is
 * created.  The size of the dynamic buffer grows automatically as new
 * data is written to it.  Any section can be created as a <i>static
 * section</i> or a <i>dynamic section</i>, and this controls which region
 * of the buffer the section data is stored in.
 * It is important to note that in general writing to or reading
 * from a static section is <i>much</i> faster than the corresponding
 * operation on a dynamic section.  Dynamic sections are required to
 * support storing serialized objects in the buffer.  As a convenience
 * to the programmer, dynamic sections holding primitive types
 * are also supported.  But in general use of the dynamic buffer is
 * is only recommended for short or infrequent message exchanges.
 *
 * <h3>Physical layout of buffer</h3>
 * Numeric data in the static buffer can be represented in big-endian
 * or little-endian format.  This is determined by the <tt>encoding</tt>
 * property of the buffer, which takes on of the values
 * <tt>java.nio.ByteOrder.BIG_ENDIAN</tt> or
 * <tt>java.nio.ByteOrder.LITTLE_ENDIAN</tt>.  The <tt>encoding</tt> property
 * of a newly created buffer is determined by the return value of
 * <tt>java.nio.ByteOrder.nativeOrder()</tt>.
 * <p>
 * The overall layout of the static buffer is illustrated in the following
 * figure:
 * <p>
 * <img src="../../images/mpj-static.png"/>
 * <p>
 * In practise many messages will contain only a single section.
 * There are up to 7 bytes of padding after each section
 * to ensure that the next section header begins on a boundary that is a
 * multiple of {@link #ALIGNMENT_UNIT ALIGNMENT_UNIT}, which has value 8.
 * <p>
 * The general layout of an individual section in the static buffer
 * is illustrated in the following figure:
 * <p>
 * <img src="../../images/mpj-section.png"/>
 * <p>
 * The first byte of the section header defines the type of the section,
 * encoding one of the possible values
 * {@link Type#BYTE BYTE},
 * {@link Type#CHAR CHAR},
 * {@link Type#SHORT SHORT},
 * {@link Type#BOOLEAN BOOLEAN},
 * {@link Type#INT INT},
 * {@link Type#LONG LONG},
 * {@link Type#FLOAT FLOAT}, or
 * {@link Type#DOUBLE DOUBLE}, if the section is static, or one of
 * {@link Type#OBJECT OBJECT},
 * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC},
 * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC},
 * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC},
 * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC},
 * {@link Type#INT_DYNAMIC INT_DYNAMIC},
 * {@link Type#LONG_DYNAMIC LONG_DYNAMIC},
 * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}, or
 * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC} if the section is dynamic.
 * The second 4 bytes of the header holds the number of elements
 * in the section.  This numeric value is represented according to the
 * <tt>encoding</tt> property of the buffer.  The size of the header
 * in bytes is {@link #SECTION_OVERHEAD SECTION_OVERHEAD}, which has value 8.
 * If the section is static, the header is followed by the values of
 * the elements, again represented according to the
 * <tt>encoding</tt> property of the buffer.  If the section is dynamic,
 * the "Section data" is absent from the diagram above (because the
 * data is in the dynamic buffer).  
 * <p>
 * The format of the <i>dynamic buffer</i>
 * is entirely determined by the standard Java serialization classes
 * <tt>java.io.ObjectOutputStream</tt> and <tt>java.io.ObjectInputStream</tt>.
 * <p>
 * <h3>Read and write operations</h3>
 * There are 3 basic kinds of operation for writing data to a buffer
 * section, and 3 basic kinds of operation for reading data from a buffer
 * section.  The simplest data transfer operations are
 * {@link #write(byte[],int,int) write()} and
 * {@link #read(byte[],int,int) read()},
 * which transfer contiguous sections of a Java
 * array.  The generic forms are respectively:
   <pre><code>
    <b>write</b>(<i>type</i>[]&nbsp;source,
          int&nbsp;srcOff,
          int&nbsp;numEls)
   </code></pre>
 * and
   <pre><code>
    <b>read</b>(<i>type</i>[]&nbsp;dest,
         int&nbsp;dstOff,
         int&nbsp;numEls)
   </code></pre>
 * The {@link #write(byte[],int,int) write()}
 * operation copies the values of elements:
   <pre><code>
    source [srcOff], source [srcOff + 1], ..., source [srcOff + numEls - 1]
   </code></pre>
 * to the buffer, in the order shown.
 * Conversely the {@link #read(byte[],int,int) read()},
 * operation reads <tt>numEls</tt> values from the buffer and writes
 * them to the elements:
   <pre><code>
    dest [dstOff], dest [dstOff + 1], ..., dest [dstOff + numEls - 1]
   </code></pre>
 * <p>
 * The operations {@link #gather(byte[],int,int,int[]) gather()} and
 * {@link #scatter(byte[],int,int,int[]) scatter()} transfer
 * data from or to a subset of elements of a Java array, selected by
 * giving an index vector.  The generic forms are respectively:
   <pre><code>
    <b>gather</b>(<i>type</i>[]&nbsp;source,
           int&nbsp;numEls,
           int&nbsp;idxOff,
           int[]&nbsp;indexes)
   </code></pre>
 * and
   <pre><code>
    <b>scatter</b>(<i>type</i>[]&nbsp;dest,
            int&nbsp;numEls,
            int&nbsp;idxOff,
            int[]&nbsp;indexes)
   </code></pre>
 * The {@link #gather(byte[],int,int,int[]) gather()}
 * operation copies the values of elements:
   <pre><code>
    source [indexes [idxOff]], source [indexes [idxOff + 1]], ..., source [indexes [idxOff + numEls - 1]]
   </code></pre>
 * to the buffer, in the order shown.
 * Conversely the {@link #scatter(byte[],int,int,int[]) scatter()} 
 * operation reads <tt>numEls</tt> values from the buffer and writes
 * them to the elements:
   <pre><code>
    dest [indexes [idxOff]], dest [indexes [idxOff + 1]], ..., dest [indexes [idxOff + numEls - 1]]
   </code></pre>
 * <p>
 * The operations
 * {@link #strGather(byte[],int,int,int,int,int[]) strGather()} and
 * {@link #strScatter(byte[],int,int,int,int,int[]) strScatter()} likewise
 * transfer data from or to a subset of elements of a Java array, but
 * in these cases the selected subset is a "multi-strided region"
 * of the Java array.  The specification is fairly complex, but these
 * are useful operations for dealing with multidimensional data
 * structures, which occur often in scientific programming.
 * The generic forms are respectively:
   <pre><code>
    <b>strGather</b>(<i>type</i>[]&nbsp;source,
              int&nbsp;srcOff,
              int&nbsp;rank,
              int&nbsp;exts,
              int&nbsp;srs,
              int[]&nbsp;shape)
   </code></pre>
 * and
   <pre><code>
    <b>strScatter</b>(<i>type</i>[]&nbsp;dest,
               int&nbsp;dstOff,
               int&nbsp;rank,
               int&nbsp;exts,
               int&nbsp;srs,
               int[]&nbsp;shape)
   </code></pre>
 * A multi-strided region is characterized a <i>rank</i>, or
 * <i>dimensionality</i>, which we will call <i>r</i>.  The detailed shape of
 * the region is represented by a vector of <i>r</i> <i>extents</i> or
 * <i>sizes</i>, which we will call
 * <i>n</i><sub>1</sub>,
 * <i>n</i><sub>2</sub>, ...,
 * <i>n</i><sub><i>r</i></sub>,
 * and a vector of <i>r</i> <i>strides</i>, which we will call 
 * <i>s</i><sub>1</sub>,
 * <i>s</i><sub>2</sub>, ...,
 * <i>s</i><sub><i>r</i></sub>.
 * <p>
 * In the one dimensional case (<i>r</i> = 1), for example, a
 * {@link #strGather(byte[],int,int,int,int,int[]) strGather()}
 * operation copies values of the elements:
   <pre><code>
    source[srcOff], source[srcOff + s<sub>1</sub>], ..., source[srcOff + (n<sub>1</sub> - 1)s<sub>1</sub>],
   </code></pre>
 * to the buffer, in the order shown.
 * Conversely a rank-1 
 * {@link #strScatter(byte[],int,int,int,int,int[]) strScatter()}
 * operation reads <i>n</i><sub>1</sub>
 * values from the buffer and writes them to the elements:
   <pre><code>
    dest[dstOff], dest[dstOff + s<sub>1</sub>], ..., dest[dstOff + (n<sub>1</sub> - 1)s<sub>1</sub>],
   </code></pre>
 * In the two dimensional case, a
 * {@link #strGather(byte[],int,int,int,int,int[]) strGather()}
 * operation copies values of the elements:
   <pre><code>
    source[srcOff], source[srcOff + s<sub>2</sub>], ..., source[srcOff + (n<sub>2</sub> - 1)s<sub>2</sub>],
    source[srcOff + s<sub>1</sub>], source[srcOff + s<sub>1</sub> + s<sub>2</sub>], ..., source[srcOff + s<sub>1</sub> + (n<sub>2</sub> - 1)s<sub>2</sub>],
    ...,
    source[srcOff + (n<sub>1</sub> - 1)s<sub>1</sub>], source[srcOff + (n<sub>1</sub> - 1)s<sub>1</sub> + s<sub>2</sub>], ..., source[srcOff + (n<sub>1</sub> - 1)s<sub>1</sub> + (n<sub>2</sub> - 1)s<sub>2</sub>]
   </code></pre>
 * to the buffer, in the order shown.
 * A rank-2 
 * {@link #strScatter(byte[],int,int,int,int,int[]) strScatter()}
 * operation reads
 * <i>n</i><sub>1</sub> <i>n</i><sub>2</sub>
 * values from the buffer and writes them to the elements:
   <pre><code>
    dest[dstOff], dest[dstOff + s<sub>2</sub>], ..., dest[dstOff + (n<sub>2</sub> - 1)s<sub>2</sub>],
    dest[dstOff + s<sub>1</sub>], dest[dstOff + s<sub>1</sub> + s<sub>2</sub>], ..., dest[dstOff + s<sub>1</sub> + (n<sub>2</sub> - 1)s<sub>2</sub>],
    ...,
    dest[dstOff + (n<sub>1</sub> - 1)s<sub>1</sub>], dest[dstOff + (n<sub>1</sub> - 1)s<sub>1</sub> + s<sub>2</sub>], ..., dest[dstOff + (n<sub>1</sub> - 1)s<sub>1</sub> + (n<sub>2</sub> - 1)s<sub>2</sub>]
   </code></pre>
 * This pattern generalizes to any rank.
 * In the concrete parameters lists, the sizes
 * <i>n</i><sub>1</sub>,
 * <i>n</i><sub>2</sub>, ...,
 * <i>n</i><sub><i>r</i></sub> are given by the <tt>shape</tt>
 * elements:
   <pre><code>
    shape[exts], shape[exts + 1], ..., shape[exts + rank - 1]
   </code></pre>
 * and the strides
 * <i>s</i><sub>1</sub>,
 * <i>s</i><sub>2</sub>, ...,
 * <i>s</i><sub><i>r</i></sub> are given by the <tt>shape</tt>
 * elements:
   <pre><code>
    shape[strs], shape[strs + 1], ..., shape[strs + rank - 1]
   </code></pre>
 * <p>
 *
 * <h3>Read-write modes</h3>
 * When a buffer object is initially created, the buffer is in a
 * <i>writable</i> state.  In this state, new sections and data can
 * be added to the buffer.  However read operations are <i>not</i>
 * allowed while a buffer is in the writable state.  When writing
 * is completed, the {@link #commit() commit()} method "freezes" the buffer,
 * putting it in a <i>readable</i> state, and moves the read pointer to
 * the start of the buffer.  Now read operations can begin; but
 * write operations are no longer allowed.
 * <p>
 * After the data has been read, you may want to put the buffer back
 * into a writable state, ready for more data.  To do this use the
 * {@link #clear() clear()} method.  This erases the original data, and moves
 * the write pointer to the start of the buffer.
 * If on the other hand you need to re-read the same buffer contents from
 * the beginning, the {@link #commit() commit()} method can be called
 * again to rewind the read pointer back to the start of the buffer,
 * without changing contents.
 * 
 * <h3>Miscellany</h3>
 * Where consistent with efficiency, operations reading from or writing
 * to the buffer attempt to check whether the operation will succeed
 * <i>before</i> transfering any data.  In some circumstances
 * this is difficult to guarantee; if an operation resulting
 * in an exception <i>does</i> result in a partial read or write,
 * subsequent calls to {@link #getRemaining() getRemaining()} or
 * {@link #getSectionSize() getSectionSize()}
 * will generally reflect how much data was transferred before
 * the exception occurred.  But even this behavior cannot be guaranteed
 * in the case of {@link Type#OBJECT OBJECT} sections,
 * where an individual object may be incompletely transferred.
 */
public class Buffer {
  
    /*
     * Every section has a header that occupies
     * <tt>SECTION_OVERHEAD</tt> units of capacity.
     * Note this increases the effective
     * space occupied by a section.
     */
    public static final int SECTION_OVERHEAD = 8 ;

    /*
     * Padding is added after each section in the buffer
     * to ensure every section starts on a boundary that is a multiple
     * of <tt>ALIGNMENT_UNIT</tt> units of buffer capacity.
     * Note this increases the effective space occupied by a section.
     */
    public static final int ALIGNMENT_UNIT   = 8 ;
            // For beginning of sections.


    private static final ByteOrder localEncoding = ByteOrder.nativeOrder() ;

    private int capacity ;

    private ByteOrder encoding ;
    
    private boolean freed ;

    private boolean writeable ;

    private Type currentSectionType ;

    private int size ;
    private int bufoffset ;  // delete it later
    private int sectionHeader ;
    private int sectionSize ;

    private int readPtr ;
    private int elementsRemaining ;
            // Elements remaining to be read in current section.

    /*
     * Streams used for dynamic part of buffer ("secondary payload") 
     */

    private ByteArrayOutputStream byteOut;
    private ObjectOutputStream out;

    private ObjectInputStream in;

    private byte [] dynamicBuffer ;
    private RawBuffer staticBuffer ;

    private CustomObjectInputStream inn;       

    /**
     * Default constructor 
     */
    protected Buffer( ) {
    }

    /**
     * Creates a buffer with specified static capacity, and
     * static buffer encoding given by
     * <tt>java.nio.ByteOrder.nativeEncoding()</tt>.
     * When created the buffer is empty, and it is in a writeable state.
     * @param capacity the capacity of the static buffer, in bytes.
     */
    public Buffer(int capacity) {
        //why needed: 	    
	//            src/mpjdev/Comm.java is using this constr. 
	//            no factory at that level ...
        encoding = localEncoding ;

        currentSectionType = null ;
        writeable = true ;

        //staticBuffer = new NativeBuffer(capacity) ;
                // In principle, can slot in other implementations
                // of `RawBuffer' here.
	staticBuffer = new NIOBuffer(capacity);
        freed = false ;
    }

    public int offset() {
      return bufoffset ;	    
    }

    /**
     * Creates a buffer with specified rawbuffer, and staring
     * at the offset specified. Encoding given by
     * <tt>java.nio.ByteOrder.nativeEncoding()</tt>.
     * When created the buffer is not necessarily empty, but
     * is in a writeable state.
     * This constructor supports the Buffered mode of send at the higher
     * level.
     *  buffer Rawbuffer
     *  offset the starting offset of this buffer
     * need level argument ...
     * public access required for bsend object is made in BasicType.java 
     * in 'createWriteBuffer' method.
     */
    public Buffer(RawBuffer buffer, int bufoffset, int capacity) {

        encoding = localEncoding;

        currentSectionType = null;
        writeable = true;

        staticBuffer = buffer;
        freed = false;
        this.bufoffset = bufoffset;
        //this.offset = offset ;
        this.capacity = capacity;
    }

    /* not well thought ..instead of sending bits n pieces of Buffer, 
     * why not just send the reference of buffer itself and then 
     * figure out things within this method like geting the reference
     * of static buffer and dynamic buffer 
     */
    public void copy(ByteBuffer srcStaticBuffer, int srcOffset, 
		    int staticBufferLength, int dstOffset, 
		    byte[] srcDynamicBuffer, int dynamicBufferLength) {
	    
      staticBuffer.copy(srcStaticBuffer, srcOffset, 
		      staticBufferLength, 
		      dstOffset) ;
      
      setSize( staticBufferLength ) ;
      
      if(dynamicBufferLength > 0) {
        setDynamicBuffer( srcDynamicBuffer) ; 	      
      }

    }

    /**
     * Free resources associated with this buffer.  The only
     * method invocations allowed on the buffer instance after this
     * method has been called are further calls to <tt>free()</tt>
     * or {@link #finalize() finalize()}, none of which have any effect
     * after the first invocation.
     */
    public void free() {

        if(!freed) {

            closeObjectStreams() ;

            dynamicBuffer = null ;

            staticBuffer.free() ;

            freed = true ;
        }
    }

    /**
     * Freeze buffer, putting it into a readable state, and setting the
     * read pointer to the start of the buffer.  This method can
     * also be used to "rewind" a buffer that is already in readable
     * state.
     * @throws DynamicBufferException if there are unforseen problems
     * saving the state of the dynamic buffer.
     * @throws WrongStateException if buffer has already been freed.
     */
    public void commit() throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;
        // Save size of last section to buffer.
        if(currentSectionType != null) {
            staticBuffer.putInt(sectionSize, sectionHeader+4+bufoffset ) ;
        }

        if (out != null) {
            try {
                out.flush() ;
                dynamicBuffer = byteOut.toByteArray();
		//System.out.println(" length ="+dynamicBuffer.length) ; 

            } catch(IOException e) {
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }

        closeObjectStreams() ;

        currentSectionType = null ;
        readPtr = 0 ;
        elementsRemaining = 0 ;

        writeable = false ;
    }

    /**
     * Empty the buffer and put it in a writeable state.
     * @throws WrongStateException if buffer has already been freed.
     */
    public void clear() throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        closeObjectStreams() ;
        
        encoding = localEncoding ;

        currentSectionType = null ;
        size = 0 ;
        sectionHeader = 0 ;

        dynamicBuffer = null ;

        writeable = true ;
    }

    private void closeObjectStreams() {

        if (in != null) {
            try {
                in.close();
                in = null;
            } catch(IOException e){
                // IO error should not occur when we close
                // byte array inputstream.
            }
        }
                                                                                
        if (out != null) {
            try {
                out.close() ;
                out = null ;
                byteOut = null ;
            } catch (IOException e){
                // IO error should not occur when we close
                // byte array inputstream.
            }
        }
    }
 
    /**
     * Start a new section in the buffer: write a section header to
     * the buffer and make the new section the current section.
     * The header requires
     * {@link #SECTION_OVERHEAD SECTION_OVERHEAD}
     * bytes of static buffer capacity.
     * Any preceding section will be padded with the minimum
     * number of bytes necessary to ensure that the new section starts on
     * a boundary that is a multiple of
     * {@link #ALIGNMENT_UNIT ALIGNMENT_UNIT} bytes.
     * This may require up to <tt>ALIGNMENT_UNIT - 1</tt> bytes of
     * additional buffer capacity.
     * @param type the type of this section.  One of
     * {@link Type#BYTE BYTE},
     * {@link Type#CHAR CHAR},
     * {@link Type#SHORT SHORT},
     * {@link Type#BOOLEAN BOOLEAN},
     * {@link Type#INT INT},
     * {@link Type#LONG LONG},
     * {@link Type#FLOAT FLOAT}, or
     * {@link Type#DOUBLE DOUBLE}, if this is a static section, or one of
     * {@link Type#OBJECT OBJECT},
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC},
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC},
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC},
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC},
     * {@link Type#INT_DYNAMIC INT_DYNAMIC},
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC},
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}, or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}
     * if this is a dynamic section.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or buffer has already been freed.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     */
    public void putSectionHeader(Type type) throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(!writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a writeable state.") ;

        // Save size of previous section to buffer.
        if(currentSectionType != null) {
            staticBuffer.putInt(sectionSize, bufoffset+sectionHeader + 4) ;
        }

        int newSectionHeader = ALIGNMENT_UNIT *
                ((size + ALIGNMENT_UNIT - 1) / ALIGNMENT_UNIT) ;
                        // Round up to multiple of ALIGNMENT_UNIT.

        int newSize = newSectionHeader + SECTION_OVERHEAD ;
        if(newSize > staticBuffer.getCapacity())
            throw new BufferOverflowException("Buffer capacity " +
                    "too small for new section header.") ;

        staticBuffer.putByte(type.getCode() , newSectionHeader + bufoffset) ;

        size = newSize ;
        sectionHeader = newSectionHeader ;
        currentSectionType = type ;
        sectionSize = 0 ;
    }

    /**
     * Read a section header from the buffer,
     * and make this the current section.
     * @return the type of the expected section.  One of
     * {@link Type#BYTE BYTE},
     * {@link Type#CHAR CHAR},
     * {@link Type#SHORT SHORT},
     * {@link Type#BOOLEAN BOOLEAN},
     * {@link Type#INT INT},
     * {@link Type#LONG LONG},
     * {@link Type#FLOAT FLOAT},
     * {@link Type#DOUBLE DOUBLE},
     * {@link Type#OBJECT OBJECT},
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC},
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC},
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC},
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC},
     * {@link Type#INT_DYNAMIC INT_DYNAMIC},
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC},
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}, or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws WrongStateException if buffer is not in a readable state,
     * or buffer has already been freed.
     * @throws TypeMismatchException if the value of the type
     * parameter does not precisely match the value specified when the
     * section was originally created by {@link #putSectionHeader(Type)
     * putSectionHeader()}.
     * @throws SectionSizeMismatchException if there are elements remaining
     * to be read in the previous section.
     * @throws BufferUnderflowException if there there are no more
     * sections remaining to be read in the buffer.
     */
    public Type getSectionHeader() throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a readable state.") ;

        if(elementsRemaining != 0)
            throw new SectionSizeMismatchException("Previous " +
                    "section was not completely read.") ;

        int headerStart = 8 * ((readPtr + 7) / 8) ;
                // Round up to multiple of 8.
			//	System.out.println("headerStart = "+headerStart+" bufoffset = "+bufoffset);
        int newReadPtr = headerStart + SECTION_OVERHEAD ;

        if(newReadPtr > size) {
             throw new BufferUnderflowException("Trying to read " +
                                               "past end of buffer. < newReadPtr > size > "+newReadPtr+" > "+size) ;
        }

        int typeCode = staticBuffer.getByte(headerStart+bufoffset) ;
        sectionSize = staticBuffer.getInt(headerStart + 4 +bufoffset,
                                          encoding != localEncoding) ;
		//		System.out.println("typeCode = "+typeCode+" sectionSize = "+sectionSize);         
				readPtr = newReadPtr ;
        currentSectionType = Type.getType(typeCode) ;
        elementsRemaining  = sectionSize ;
 
        return currentSectionType ;
    }

    /**
     * Size of current section.
     * If buffer is currently readable, return value is fixed for this section.
     * If buffer is currently writable, return value is number of elements
     * written to this section so far.
     * @return number of elements in this section.
     * @throws WrongStateException if current section is undefined,
     * or buffer has already been freed.
     */
    public int getSectionSize() throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;


        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'getSectionHeader' or 'putSectionHeader'") ;

        return sectionSize ;
    }

    /**
     * Returns number of elements remaining to be read in the current
     * section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or buffer has already been freed.
     */
    public int getRemaining() throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a readable state.") ;

        return elementsRemaining ;
    }


    /**
     * Append <tt>numEls</tt> consecutive byte elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 1 * numEls
     * units of static buffer capacity.
     * @param source the byte array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(byte [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.BYTE) {

            int newSize = size + 1 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeByte(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive short elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 2 * numEls
     * units of static buffer capacity.
     * @param source the short array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(short [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.SHORT) {

            int newSize = size + 2 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeShort(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive int elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 4 * numEls
     * units of static buffer capacity.
     * @param source the int array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(int [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.INT) {

            int newSize = size + 4 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeInt(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive long elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 8 * numEls
     * units of static buffer capacity.
     * @param source the long array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(long [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.LONG) {

            int newSize = size + 8 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeLong(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive char elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 2 * numEls
     * units of static buffer capacity.
     * @param source the char array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(char [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.CHAR) {

            int newSize = size + 2 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeChar(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive float elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 4 * numEls
     * units of static buffer capacity.
     * @param source the float array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(float [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.FLOAT) {

            int newSize = size + 4 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeFloat(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive double elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 8 * numEls
     * units of static buffer capacity.
     * @param source the double array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(double [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.DOUBLE) {

            int newSize = size + 8 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeDouble(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive boolean elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.  If the current section
     * is a static section this requires 1 * numEls
     * units of static buffer capacity.
     * @param source the boolean array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(boolean [] source, int srcOff, int numEls)
            throws BufferException {
        //System.out.println("Reaching in the write method");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType == Type.BOOLEAN) {

            int newSize = size + 1 * numEls ;
            if(newSize > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            staticBuffer.write(source, srcOff, numEls, size+bufoffset) ;

            sectionSize += numEls ;
            size = newSize ; 
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeBoolean(source[srcOff + i]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> consecutive Object elements from array
     * <tt>source</tt>, starting at index <tt>srcOff</tt>,
     * to the current section of buffer.
     * @param source the Object array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>srcOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void write(Object [] source, int srcOff, int numEls)
            throws BufferException {

        //System.out.println("Reaching in the write method2");
        writeCheckArgs(source.length, srcOff, numEls) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section, or missing call to 'putSectionHeader'") ;

        try {
            if (out == null){
                byteOut = new ByteArrayOutputStream();
                out = new ObjectOutputStream(byteOut);
            }
        
            for (int i = 0; i < numEls; i++) {
                out.writeObject(source[srcOff + i]);     
                sectionSize++ ;
            }

        } catch(IOException e){
            throw new DynamicBufferException("Problem writing " +
                    "dynamic buffer", e) ;
        }
    }

    /*
     * Common argument sanity checks for `write()' methods.
     */
    private void writeCheckArgs(int sourceLen, int srcOff, int numEls) 
            throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(!writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a writeable state.") ;

        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'putSectionHeader'") ;

        if(srcOff < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in source array: " + srcOff) ;

        if(srcOff + numEls > sourceLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in source array: " + (srcOff + numEls - 1)) ;
    }


    /**
     * Append <tt>numEls</tt> selected byte elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 1 * numEls
     * units of static buffer capacity.
     * @param source the byte array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(byte [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.BYTE) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 1 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 1 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeByte(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected short elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 2 * numEls
     * units of static buffer capacity.
     * @param source the short array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(short [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.SHORT) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 2 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 2 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeShort(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected int elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 4 * numEls
     * units of static buffer capacity.
     * @param source the int array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(int [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.INT) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 4 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 4 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeInt(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected long elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 8 * numEls
     * units of static buffer capacity.
     * @param source the long array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(long [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.LONG) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 8 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 8 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeLong(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected char elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 2 * numEls
     * units of static buffer capacity.
     * @param source the char array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(char [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.CHAR) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 2 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 2 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeChar(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected float elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 4 * numEls
     * units of static buffer capacity.
     * @param source the float array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(float [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.FLOAT) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 4 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 4 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeFloat(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected double elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 8 * numEls
     * units of static buffer capacity.
     * @param source the double array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(double [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.DOUBLE) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 8 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 8 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeDouble(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected boolean elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * If the current section of the buffer
     * is a static section this requires 1 * numEls
     * units of static buffer capacity.
     * @param source the boolean array from which data is taken.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(boolean [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.BOOLEAN) {

            // Convenient to do explicit check before we go into native code.
            if(source == null)
                throw new NullPointerException("null") ;

            if(size + 1 * numEls > staticBuffer.getCapacity())
                throw new BufferOverflowException("Buffer capacity " +
                        "too small for attempted write.") ;

            int transfered =
                     staticBuffer.gather(source, numEls, idxOff, indexes,
                                         size+bufoffset) ;

            size += 1 * transfered ;
            sectionSize += transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in source array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                for (int i = 0; i < numEls; i++) {
                    out.writeBoolean(source[indexes[idxOff + i]]);     
                    sectionSize++ ;
                }

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }



    /**
     * Append <tt>numEls</tt> selected Object elements from array
     * <tt>source</tt> to the current section of buffer.  
     * The elements copied from <tt>source</tt> are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param source the Object array from which data is taken.
     * @param numEls the total number of elements to be copied from
     * <tt>source</tt>
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void gather(Object [] source, 
                       int numEls, int idxOff, int [] indexes)
            throws BufferException {

        gatherCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;

        try {
            if (out == null){
                byteOut = new ByteArrayOutputStream();
                out = new ObjectOutputStream(byteOut);
            }
        
            for (int i = 0 ; i < numEls; i++) {
                out.writeObject(source[indexes[idxOff + i]]);     
                sectionSize++ ;
            }

        } catch(IOException e){
            throw new DynamicBufferException("Problem writing " +
                    "dynamic buffer", e) ;
        }
    }

    /*
     * Common argument sanity checks for `gather()' methods.
     */
    private void gatherCheckArgs(int numEls, int idxOff, int indexesLen)
            throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(!writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a writeable state.") ;

        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'putSectionHeader'") ;

        if(idxOff < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in indexes array: " + idxOff) ;

        if(idxOff + numEls > indexesLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in indexes array: " + (idxOff + numEls - 1)) ;
    }



    /**
     * Append selected byte elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 1 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the byte array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(byte [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.BYTE) {
            if(volume != 0) {
                int newSize = size + 1 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(byte [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeByte(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected short elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 2 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the short array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(short [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.SHORT) {
            if(volume != 0) {
                int newSize = size + 2 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(short [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeShort(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected int elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 4 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the int array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(int [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.INT) {
            if(volume != 0) {
                int newSize = size + 4 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(int [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeInt(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected long elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 8 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the long array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(long [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.LONG) {
            if(volume != 0) {
                int newSize = size + 8 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(long [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeLong(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected char elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 2 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the char array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(char [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.CHAR) {
            if(volume != 0) {
                int newSize = size + 2 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(char [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeChar(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected float elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 4 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the float array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(float [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.FLOAT) {
            if(volume != 0) {
                int newSize = size + 4 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(float [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeFloat(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected double elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 8 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the double array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(double [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.DOUBLE) {
            if(volume != 0) {
                int newSize = size + 8 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(double [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeDouble(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected boolean elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * If the current section of the buffer
     * is a static section this requires 1 * <i>volume</i>
     * units of static buffer capacity, where <i>volume</i>
     * is the product of the extents of the specified region.
     * @param source the boolean array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws BufferOverflowException if there is insufficient space
     * left in the static buffer.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(boolean [] source, int srcOff, 
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType == Type.BOOLEAN) {
            if(volume != 0) {
                int newSize = size + 1 * volume ;
                if(newSize > staticBuffer.getCapacity())
                    throw new BufferOverflowException("Buffer capacity " +
                            "too small for attempted write.") ;

                staticBuffer.strGather(source, srcOff,
                                       rank, exts, strs, shape, 
				       size+bufoffset) ;

                sectionSize += volume ;
                size = newSize ; 
            }
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (out == null){
                    byteOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(byteOut);
                }
        
                doStrGather(source, srcOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem writing " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }


    private void doStrGather(boolean [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeBoolean(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }


    /**
     * Append selected boolean elements from a multistrided region of array
     * <tt>source</tt> to the current section of buffer.  
     * See {@link mpjbuf.Buffer class description} for details.
     * @param source the boolean array from which data is taken.
     * @param srcOff the subscript of the first element in the
     * <tt>source</tt> array that is to be copied.
     * @param rank the rank or dimensionality of the region to
     * be copied from <tt>source</tt>
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region to be copied from <tt>source</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws WrongStateException if buffer is not in a writeable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>source</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * writing to the dynamic buffer.
     */
    public void strGather(Object [] source, int srcOff,
                          int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strGatherCheckArgs(source.length, srcOff,
                                        rank, exts, strs, shape) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;

        try {
            if (out == null){
                byteOut = new ByteArrayOutputStream();
                out = new ObjectOutputStream(byteOut);
            }
    
            doStrGather(source, srcOff, rank, exts, strs, shape);

        } catch(IOException e){
            throw new DynamicBufferException("Problem writing " +
                    "dynamic buffer", e) ;
        }
    }

    private void doStrGather(Object [] source, int srcOff,
                             int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            out.writeObject(source[srcOff]);    
            sectionSize++ ;
        }
        else{
            int str = shape[strs];
            for (int i = 0; i < shape[exts]; i++)
                doStrGather(source, srcOff + str * i, 
                            rank - 1, exts + 1, strs + 1, shape); 
        }
    }

    /*
     * Common argument sanity checks for `strGather()' methods.
     */
    private int strGatherCheckArgs(int sourceLen, int srcOff, int rank,
                                   int exts, int strs, int [] shape)
            throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(!writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a writeable state.") ;

        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'putSectionHeader'") ;

        if(rank < 0)
            throw new mpjbuf.IllegalArgumentException("Negative rank " +
                    "specified") ;

        int minIndex = srcOff, maxIndex = srcOff ;
        int volume = 1;

        for (int i = 0; i < rank; i++) {

            int ext = shape [exts + i] ;
            int str = shape [strs + i] ;

            if (ext < 0) 
                throw new ArrayIndexOutOfBoundsException("Patch has " +
                        "negative extent.") ;
            
            if (ext == 0)
                return 0 ;

            if (str < 0) 
                minIndex += str * (ext - 1);
            else
                maxIndex += str * (ext - 1);

            volume *= ext ;
        }

        if(minIndex < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in source array: " + minIndex) ;

        if(maxIndex >= sourceLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in source array: " + maxIndex) ;

        return volume ;
    }


    /**
     * Read the next <tt>numEls</tt> byte items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the byte array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(byte [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.BYTE) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 1 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readByte();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> short items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the short array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(short [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.SHORT) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 2 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readShort();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> int items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the int array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(int [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.INT) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 4 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readInt();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> long items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the long array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(long [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.LONG) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 8 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readLong();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> char items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the char array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(char [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.CHAR) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 2 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readChar();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> float items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the float array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(float [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.FLOAT) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 4 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readFloat();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> double items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the double array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(double [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.DOUBLE) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 8 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readDouble();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> boolean items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the boolean array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */

    public void read(boolean [] dest, int dstOff, int numEls)
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType == Type.BOOLEAN) {

            staticBuffer.read(dest, dstOff, numEls, readPtr+bufoffset,
                              encoding != localEncoding) ;

            readPtr += 1 * numEls ;
            elementsRemaining -= numEls ;
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (in == null) {
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }

                for (int i = 0; i < numEls; i++) {
                    dest[dstOff + i] = in.readBoolean();
                    elementsRemaining-- ;
                }

            } catch(IOException e) {
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section.") ;
        }
    }


    /**
     * Read the next <tt>numEls</tt> Object items from the current section
     * of the buffer, and write them to consecutive elements of the array
     * <tt>dest</tt>, starting at index <tt>dstOff</tt>.
     * @param dest the Object array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>dstOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are problems
     * reading objects from the dynamic buffer.
     */
    public void read(Object [] dest, int dstOff, int numEls) 
            throws BufferException {

        readCheckArgs(dest.length, dstOff, numEls) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section, or missing call to 'getSectionHeader'") ;

        try {
            if (inn == null){
                ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                in = new ObjectInputStream(o);
            }
            Object obj;
            for (int i = 0; i < numEls; i++) {
                obj = in.readObject();
                dest[dstOff + i] = obj;     
                elementsRemaining-- ;
            }
        }
        catch(ClassNotFoundException ce){

            //throw new DynamicBufferException("Problem reading " +
              //      "dynamic buffer", e) ;

             try {
               ByteArrayInputStream o =   new ByteArrayInputStream(dynamicBuffer);
               inn = new CustomObjectInputStream(o);
               for (int i = 0; i < numEls; i++) {
                 Object obj =inn.readObject();
                 dest[dstOff + i] = obj;
                 elementsRemaining-- ;
                }
                }
              catch(Exception ioe){
                 throw new DynamicBufferException("Problem reading " +
                  "dynamic buffer", ioe) ;
              }

        }
        catch(Exception e){
            throw new DynamicBufferException("Problem reading " +
                    "dynamic buffer", e) ;
        }
    }

    /*
     * Common argument sanity checks for `read()' methods.
     */
    private void readCheckArgs(int destLen, int dstOff, int numEls)
            throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a readable state.") ;

        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'getSectionHeader'") ;

        if(dstOff < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in dest array: " + dstOff) ;

        if(dstOff + numEls > destLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in dest array: " + (dstOff + numEls - 1)) ;

        if(numEls > elementsRemaining)
            throw new SectionSizeMismatchException("Trying to read past " +
                    "end of current section.") ;
    }


    /**
     * Read the next <tt>numEls</tt> byte items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the byte array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(byte [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.BYTE) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 1 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readByte()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readByte();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> short items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the short array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(short [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.SHORT) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 2 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readShort()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readShort();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> int items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the int array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(int [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.INT) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 4 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readInt()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readInt();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> long items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the long array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(long [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.LONG) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 8 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readLong()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readLong();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> char items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the char array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(char [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.CHAR) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 2 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readChar()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readChar();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> float items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the float array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(float [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.FLOAT) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 4 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readFloat()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readFloat();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> double items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the double array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(double [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.DOUBLE) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 8 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readDouble()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readDouble();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> boolean items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the boolean array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void scatter(boolean [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType == Type.BOOLEAN) {

            // Convenient to do explicit check before we go into native code.
            if(dest == null)
                throw new NullPointerException("null") ;

            int transfered =
                    staticBuffer.scatter(dest, numEls, idxOff, indexes,
                                         readPtr+bufoffset, 
					 encoding != localEncoding) ;

            readPtr += 1 * transfered ;
            elementsRemaining -= transfered ;

            if(transfered < numEls) {
                throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                        "in dest array: " + indexes [idxOff + transfered]) ;
                                // Only reason this can happen.
            }
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                            new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                for (int i = 0 ; i < numEls; i++) {

                    // `readBoolean()' must not occur if LHS raises exception.

                    int idx = indexes[idxOff + i] ;
                    if(idx >= 0 && idx < dest.length) {
                        dest[idx] = in.readBoolean();
                        elementsRemaining-- ;
                    }
                    else
                        throw new ArrayIndexOutOfBoundsException("Out of " +
                                "bounds in dest array: " + idx) ;
                }
            }
            catch(IOException e){

                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
    }



    /**
     * Read the next <tt>numEls</tt> Object items from the current section
     * of the buffer, and write them to selected elements of the array
     * <tt>dest</tt>.
     * The elements of <tt>dest</tt> to which data is copied are selected
     * by subscripts taken from <tt>numEls</tt> consecutive
     * elements of the array <tt>indexes</tt>, starting
     * at index <tt>idxOff</tt> in the latter array.
     * See {@link mpjbuf.Buffer class description} for further details.
     * @param dest the Object array to which data will be written.
     * @param numEls the total number of items to be copied from
     * the buffer to elements of <tt>dest</tt>.
     * @param idxOff the subscript of the first element in the array
     * <tt>indexes</tt> to be used as an index into <tt>source</tt>.
     * @param indexes an int array containing subscripts into
     * <tt>dest</tt>.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws ArrayIndexOutOfBoundsException if the specified
     * combination of <tt>idxOff</tt> and <tt>numEls</tt> would imply
     * access to elements outside the bounds of <tt>indexes</tt>,
     * or if a selected element of <tt>indexes</tt> requires access
     * to an element outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are problems
     * reading objects from the dynamic buffer.
     */
    public void scatter(Object [] dest,
                        int numEls, int idxOff, int [] indexes)
            throws BufferException {

        scatterCheckArgs(numEls, idxOff, indexes.length) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;

        try {
            if (in == null){
                ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                in = new ObjectInputStream(o);
            }
    
            for (int i = 0 ; i < numEls ; i++) {

                // `readObject()' must not occur if LHS raises exception.

                int idx = indexes[idxOff + i] ;
                if(idx >= 0 && idx < dest.length) {
                    dest[idx] = in.readObject();
                    elementsRemaining-- ;
                }
                else
                    throw new ArrayIndexOutOfBoundsException("Out of " +
                            "bounds in dest array: " + idx) ;
            }

        }
        catch(IOException e){

            throw new DynamicBufferException("Problem reading " +
                    "dynamic buffer", e) ;
        }
        catch(ClassNotFoundException e){

            throw new DynamicBufferException("Problem reading " +
                    "dynamic buffer", e) ;
        }
    }

    /*
     * Common argument sanity checks for `scatter()' methods.
     */
    private void scatterCheckArgs(int numEls, int idxOff, int indexesLen)
            throws BufferException {

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a readable state.") ;

        if(currentSectionType == null)
            throw new WrongStateException("Missing call to " +
                    "'getSectionHeader'") ;

        if(numEls > elementsRemaining)
            throw new SectionSizeMismatchException("Trying to read past " +
                    "end of current section.") ;

        if(idxOff < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in indexes array: " + idxOff) ;

        if(idxOff + numEls > indexesLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in indexes array: " + (idxOff + numEls - 1)) ;
    }



    /**
     * Read byte items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the byte array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BYTE BYTE} or
     * {@link Type#BYTE_DYNAMIC BYTE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(byte [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.BYTE) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 1 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.BYTE_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(byte [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readByte();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read short items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the short array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#SHORT SHORT} or
     * {@link Type#SHORT_DYNAMIC SHORT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(short [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.SHORT) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 2 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.SHORT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(short [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readShort();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read int items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the int array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#INT INT} or
     * {@link Type#INT_DYNAMIC INT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(int [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.INT) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 4 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.INT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(int [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readInt();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read long items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the long array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#LONG LONG} or
     * {@link Type#LONG_DYNAMIC LONG_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(long [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.LONG) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 8 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.LONG_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(long [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readLong();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read char items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the char array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#CHAR CHAR} or
     * {@link Type#CHAR_DYNAMIC CHAR_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(char [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.CHAR) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 2 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.CHAR_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(char [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readChar();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read float items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the float array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#FLOAT FLOAT} or
     * {@link Type#FLOAT_DYNAMIC FLOAT_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(float [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.FLOAT) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 4 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.FLOAT_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(float [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readFloat();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read double items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the double array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#DOUBLE DOUBLE} or
     * {@link Type#DOUBLE_DYNAMIC DOUBLE_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(double [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.DOUBLE) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 8 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.DOUBLE_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(double [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readDouble();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read boolean items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the boolean array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#BOOLEAN BOOLEAN} or
     * {@link Type#BOOLEAN_DYNAMIC BOOLEAN_DYNAMIC}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are unforseen problems
     * reading the dynamic buffer.
     */
    public void strScatter(boolean [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        int volume = strScatterCheckArgs(dest.length, dstOff,
                                         rank, exts, strs, shape) ;

        if(currentSectionType == Type.BOOLEAN) {
            if(volume != 0) {
       
                staticBuffer.strScatter(dest, dstOff, rank, exts, strs, shape,
                                        readPtr+bufoffset, 
					encoding != localEncoding) ;

                readPtr += 1 * volume ;
                elementsRemaining -= volume ;
            }
        }
        else if(currentSectionType == Type.BOOLEAN_DYNAMIC) {

            try {
                if (in == null){
                    ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                    in = new ObjectInputStream(o);
                }
        
                doStrScatter(dest, dstOff, rank, exts, strs, shape);

            } catch(IOException e){
                throw new DynamicBufferException("Problem reading " +
                        "dynamic buffer", e) ;
            }
        }
        else {
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;
        }
    }

    private void doStrScatter(boolean [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException {

        if (rank == 0) {
            dest[dstOff] = in.readBoolean();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }



    /**
     * Read Object items from the current section of the buffer,
     * and write them to a multistrided region of the array <tt>dest</tt>.
     * See {@link mpjbuf.Buffer class description} for details.
     * @param dest the Object array to which data will be written.
     * @param dstOff the subscript of the first element in the
     * <tt>dest</tt> array that will be overwritten.
     * @param rank the rank or dimensionality of the region in
     * <tt>dest</tt> to which data is to be copied.
     * @param exts the offset in the <tt>shape</tt> array of
     * the first of rank extents.
     * @param strs the offset in the <tt>shape</tt> array of
     * the first of rank strides.
     * @param shape an int array holding extent and stride information
     * for the region in <tt>dest</tt> to which data is to be copied.
     * @throws TypeMismatchException if the current section was not
     * created with type {@link Type#OBJECT OBJECT}.
     * @throws SectionSizeMismatchException if this operation
     * would imply reading past the end of the current section.
     * @throws WrongStateException if buffer is not in a readable state,
     * or the current section is undefined, or the buffer has already been
     * freed.
     * @throws IllegalArgumentException if the specified rank is negative.
     * @throws ArrayIndexOutOfBoundsException if the values of <tt>rank</tt>
     * and <tt>exts</tt> and <tt>strs</tt> would imply
     * access to elements outside the bounds of <tt>shape</tt>, or
     * if the region defined by the resulting extents and strides would
     * imply access to elements outside the bounds of <tt>dest</tt>.
     * @throws DynamicBufferException if there are problems
     * reading objects from the dynamic buffer.
     */
    public void strScatter(Object [] dest, int dstOff,
                           int rank, int exts, int strs, int [] shape)
            throws BufferException {

        strScatterCheckArgs(dest.length, dstOff, rank, exts, strs, shape) ;

        if(currentSectionType != Type.OBJECT)
            throw new TypeMismatchException("Wrong type for current " +
                    "section") ;

        try {
            if (inn == null){
                ByteArrayInputStream o =
                        new ByteArrayInputStream(dynamicBuffer);
                in = new ObjectInputStream(o);
            }
    
            doStrScatter(dest, dstOff, rank, exts, strs, shape);
        }
        catch(ClassNotFoundException e){

            //throw new DynamicBufferException("Problem reading " +
              //      "dynamic buffer", e) ;
            try{
              ByteArrayInputStream o =   new ByteArrayInputStream(dynamicBuffer);
              inn = new CustomObjectInputStream(o);
              for (int i = 0; i < strs; i++) {
                Object obj =inn.readObject();
                dest[dstOff + i] = obj;
                elementsRemaining-- ;
              }
              }
              catch(Exception ioe){
                    throw new DynamicBufferException("Problem reading " +
                    "dynamic buffer", ioe) ;
              }
        }
        catch(Exception e){
            throw new DynamicBufferException("Problem reading " +
                    "dynamic buffer", e) ;
        }
    }

    private void doStrScatter(Object [] dest, int dstOff,
                              int rank, int exts, int strs, int [] shape)
            throws IOException, ClassNotFoundException {

        if (rank == 0) {
            dest[dstOff] = in.readObject();
            elementsRemaining-- ;
        }
        else {
            for (int i = 0; i < shape[exts]; i++)
                doStrScatter(dest, dstOff + shape[strs] * i, 
                             rank - 1, exts + 1, strs + 1, shape); 
        }
    }

    /*
     * Common argument sanity checks for `strScatter()' methods.
     */
    private int strScatterCheckArgs(int destLen, int dstOff, int rank,
                                    int exts, int strs, int [] shape)
            throws BufferException {

        // Also implicitly checks that `shape' != null,
        // and `shape' subscripts in range.

        if(freed)
            throw new WrongStateException("Buffer has already been freed.") ;

        if(writeable)
            throw new WrongStateException("Buffer is not in " +
                                          "a readable state.") ;

        if(rank < 0)
            throw new mpjbuf.IllegalArgumentException("Negative rank" +
                    " specified") ;

        if(currentSectionType == null)
            throw new TypeMismatchException("Missing call to " +
                    "'getSectionHeader'") ;

        int minIndex = dstOff, maxIndex = dstOff ;
        int volume = 1;

        for (int i = 0; i < rank; i++) {

            int ext = shape [exts + i] ;
            int str = shape [strs + i] ;

            if (ext < 0) 
                throw new ArrayIndexOutOfBoundsException("Patch has " +
                        "negative extent.") ;
            
            if (ext == 0)
                return 0 ;

            if (str < 0) 
                minIndex += str * (ext - 1);
            else
                maxIndex += str * (ext - 1);

            volume *= ext ;
        }

        if(minIndex < 0)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in dest array: " + minIndex) ;

        if(maxIndex >= destLen)
            throw new ArrayIndexOutOfBoundsException("Out of bounds " +
                    "in dest array: " + maxIndex) ;

        if(volume > elementsRemaining)
            throw new SectionSizeMismatchException("Trying to read past " +
                    "end of current section.") ;

         return volume ;
    }


    // Low-level methods used for sending and receiving the buffer.

    /**
     * Returns a representation of the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public RawBuffer getStaticBuffer() {
        return staticBuffer ;
    }

    /**
     * Set representation of the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public void setStaticBuffer(RawBuffer staticBuffer) {
        this.staticBuffer = staticBuffer ;
    }

    /**
     * Returns encoding of numeric data in the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public ByteOrder getEncoding() {
        return encoding ;
    } 

    /**
     * Set encoding of numeric data in the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public void setEncoding(ByteOrder encoding) {
        //not sure if we need it ...? aamir.	    
        if(staticBuffer instanceof NIOBuffer) {
	    ((NIOBuffer) staticBuffer).setEncoding(encoding);    
	}
	this.encoding = encoding ;
    }

    /**
     * Get total number of bytes of data currently in the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public int getSize() {
        return size ;
    } 

    /**
     * Set total number of bytes of data currently in the static buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public void setSize(int size) {
        this.size = size ;
    }
    /**
     * Get total number of bytes -- capacity of the static buffer.
     * Low-level method used for in native device for receiving the buffer.
     */
    public int getCapacity() {
        return capacity ;
    }
    /**
     * Get bytes of the dynamic buffer.
     * Low-level method used for sending and receiving the buffer.
     * Only use this on a commited (readable) buffer.
     */
    public byte [] getDynamicBuffer() {

        // Note return value may not be up to date if the buffer
        // has not been commited.

        return dynamicBuffer ;
    }

    /**
     * Set bytes of the dynamic buffer.
     * Low-level method used for sending and receiving the buffer.
     * Buffer should be empty prior to this call.
     */
    public void setDynamicBuffer(byte [] dynamicBuffer) {

        // Note if buffer is not empty, `out' may not be `null'.
        // This may result in the new value of `dynamicBuffer' being
        // overwritten if, for example, there is a subsequent call
        // `commit()'.

        this.dynamicBuffer = dynamicBuffer ;
    }

    /**
     * Determine read-write mode of buffer.
     * Low-level method used for sending and receiving the buffer.
     */
    public boolean isWritable() {
        return writeable ;
    }
}

