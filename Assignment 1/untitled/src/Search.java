Search.java

import java.rmi.Remote;
import java.rmi.RemoteException;

interface Search extends Remote
{
    // Declaring the method prototype
    public String query(String search) throws RemoteException;
}