package org.reversi;

import org.reversi.mvc.Coordinate;
import org.reversi.mvc.ReversiModel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReversiServer extends UnicastRemoteObject implements ServerRemote {
    private final ReversiModel model;
    private final Set<Integer> availablePlayers;
    private final Map<ClientRemote, Integer> clientToPlayer = new HashMap<>();
    private final Set<ClientRemote> connectedClients = new HashSet<>();
    public ReversiServer(ReversiModel model) throws RemoteException {
        this.model = model;
        this.availablePlayers =
                new HashSet<>(Set.of(ReversiModel.PLAYER1,
                        ReversiModel.PLAYER2));
    }
    private boolean forfeit = false;

    public synchronized void registerClient(ClientRemote client) throws GameException {
        if (this.availablePlayers.isEmpty()) {
            throw new GameException("Game server is busy");
        }

        int player = availablePlayers.iterator().next();

        clientToPlayer.put(client, player);
        availablePlayers.remove(player);

        connectedClients.add(client);
    }

    @Override
    public int getPlayerID(ClientRemote client) throws RemoteException {
        return clientToPlayer.get(client);
    }

    @Override
    public synchronized boolean isGameOver() throws RemoteException {
        return forfeit || model.isGameOver();
    }

    @Override
    public synchronized int getCurrentPlayer() throws RemoteException {
        return model.getCurrentPlayer();
    }

    @Override
    public synchronized Coordinate getOpponentMove(ClientRemote client) throws RemoteException {
        return null;
    }

    @Override
    public synchronized void forfeit(ClientRemote client) throws RemoteException {
        // TODO: implement forfeit logic after prototype
        forfeit = true;
    }


    @Override
    public synchronized boolean makeMove(ClientRemote client, int row,
                                         int col) throws RemoteException, GameException {
        // Implement the logic to make a move in the game using the provided row and col
        // You can delegate the actual move to your existing ReversiController or ReversiModel
        // For example:
        if (!connectedClients.contains(client)) {
            throw new GameException("clientID not found");
        }

        if (model.getCurrentPlayer() != clientToPlayer.get(client)) {
            return false;
        }

        return model.makeMove(row, col);
    }

    @Override
    public synchronized int[][] getBoard() throws RemoteException {
        return model.getBoard();
    }

    @Override
    public synchronized int getAvailablePlayers() throws RemoteException {
        return availablePlayers.size();
    }

    // Implement other remote methods if needed
    public static void main(String[] args) {
        try {
            // Create an instance of the ReversiModel
            ReversiModel model = new ReversiModel(8); // Example: 8x8 board size

            // Create the server object
            ServerRemote server = new ReversiServer(model);

            // Create the RMI registry
            Registry registry = LocateRegistry.createRegistry(1099);
            // Default RMI registry port is 1099

            // Bind the server object to the registry
            registry.rebind("ReversiServer", server);

            System.out.println("Reversi server is running...");
        } catch (Exception e) {
            System.err.println("Error starting the Reversi server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
