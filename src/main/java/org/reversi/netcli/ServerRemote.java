package org.reversi.netcli;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemote extends Remote {
    int getAvailableClientId() throws RemoteException;
    void registerClientAndPlay(ClientRemote client)
            throws GameException, RemoteException;
}
