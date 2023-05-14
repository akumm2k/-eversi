package org.reversi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    String getInput() throws RemoteException;
    void notify(String msg) throws RemoteException;
}
