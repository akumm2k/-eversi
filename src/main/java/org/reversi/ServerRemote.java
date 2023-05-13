package org.reversi;

import org.reversi.mvc.Coordinate;
import org.reversi.mvc.ReversiModel;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemote extends Remote {
    boolean makeMove(ClientRemote client, int row, int col) throws GameException, RemoteException;
    int[][] getBoard() throws RemoteException;
    int getAvailablePlayers() throws RemoteException;
    void registerClient(ClientRemote client) throws GameException,
            RemoteException;
    int getPlayerID(ClientRemote client) throws RemoteException;
    boolean isGameOver() throws RemoteException;
    int getCurrentPlayer() throws RemoteException;
    Coordinate getOpponentMove(ClientRemote client) throws RemoteException;
    void forfeit(ClientRemote client) throws RemoteException;
}
