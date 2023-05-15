package org.reversi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    String getInput() throws IOException;
    void notify(String msg) throws RemoteException;
    void heartbeat() throws RemoteException;
}
