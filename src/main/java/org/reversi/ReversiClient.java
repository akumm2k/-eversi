package org.reversi;

import org.reversi.mvc.Coordinate;
import org.reversi.mvc.ReversiModel;
import org.reversi.mvc.ReversiView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO: Move game control to server, making the server the referee
 * TODO: Provide ClientRemote methods to Server to retrieve moves
 *       and report results
 */
public class ReversiClient extends UnicastRemoteObject implements ClientRemote {
    private static ReversiView VIEW = ReversiView.getInstance();
    private final static Pattern INPUT_PATT = Pattern.compile("(\\d) (\\d)");

    protected static final String EXIT_KEY = "q";

    public ReversiClient() throws RemoteException {

    }
    Logger logger = Logger.getLogger(ReversiClient.class.getName());

    private ServerRemote connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            return (ServerRemote) registry.lookup("ReversiServer");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to server", e);
            throw new RuntimeException("Server connection failure");
        }
    }

    /**
     *
     * @param input the input string
     * @return the parsed coordinates from the input
     */
    private Coordinate getInputCoordFrom(final String input) {
        final Matcher inputMatcher = INPUT_PATT.matcher(input);

        if (!inputMatcher.find()) {
            throw new RuntimeException("Bad input");
        }

        return new Coordinate(
                Integer.parseInt(inputMatcher.group(1)),
                Integer.parseInt(inputMatcher.group(2))
        );
    }

    private void waitForOtherPlayer(ServerRemote server) throws InterruptedException, RemoteException {
        while (server.getAvailablePlayers() != 0) {
            //noinspection BusyWait
            Thread.sleep(1000);
            // Wait for 1 second before checking again
        }
        System.out.println("Game started!");
    }

    public void play(ServerRemote server, ClientRemote client) throws RemoteException {
        final int myPlayerID = server.getPlayerID(client);
        final ReversiModel model = new ReversiModel(8,
                myPlayerID);

        VIEW.welcome(EXIT_KEY);

        final int currPlayer = server.getCurrentPlayer();
        VIEW.printBoard(model);
        VIEW.printCurrentPlayer(currPlayer);
        if (currPlayer != myPlayerID) {
            Coordinate opponentMove =
                    server.getOpponentMove(this);
            model.makeMove(opponentMove.x(), opponentMove.y());
        }
    }

    public void run() {
        ServerRemote server = connectToServer();
        try {
            server.registerClient(this);
            waitForOtherPlayer(server);
            play(server, this);
        } catch (GameException | RemoteException |
                 InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Coordinate getMove(ServerRemote server) throws RemoteException {
        try (BufferedReader input =
                     new BufferedReader(new InputStreamReader(System.in))) {
            final String line = input.readLine();
            if (line.equals(EXIT_KEY)) {
                server.forfeit(this);
            }
            return getInputCoordFrom(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}