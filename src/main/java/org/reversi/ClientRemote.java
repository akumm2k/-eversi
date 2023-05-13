package org.reversi;

import org.reversi.mvc.Coordinate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    Coordinate getMove(ServerRemote server) throws RemoteException;
}
