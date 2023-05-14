package org.reversi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemote extends Remote {
    void registerClientAndPlay(ClientRemote client)
            throws GameException, RemoteException;
}
