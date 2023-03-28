package ReverseModule;


/**
* ReverseModule/ReversePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ReverseModule.idl
* Monday, 27 March, 2023 10:05:19 PM IST
*/

public abstract class ReversePOA extends org.omg.PortableServer.Servant
 implements ReverseModule.ReverseOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("reverse_string", new java.lang.Integer (0));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // ReverseModule/Reverse/reverse_string
       {
         String str = in.read_string ();
         String $result = null;
         $result = this.reverse_string (str);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ReverseModule/Reverse:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Reverse _this() 
  {
    return ReverseHelper.narrow(
    super._this_object());
  }

  public Reverse _this(org.omg.CORBA.ORB orb) 
  {
    return ReverseHelper.narrow(
    super._this_object(orb));
  }


} // class ReversePOA
