package org.reversi.netcli;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.reversi.mvc.Coordinate;
import org.reversi.mvc.ReversiModel;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.reversi.mvc.ReversiModel.DRAW;

public class ReversiServer extends UnicastRemoteObject implements ServerRemote {
    private final static int DEFAULT_BOARD_SIZE = 4;
    private ReversiModel model;
    private final Set<Integer> availablePlayers;
    private final Map<Integer, ClientRemote> playerToClient = new HashMap<>();
    private final Map<ClientRemote, Integer> clientToPlayer = new HashMap<>();
    private final Set<ClientRemote> connectedClients = new HashSet<>();
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ReversiServer.class);

    private final static long HEARTBEAT_INTERVAL = 10;
    private Thread inputThread;

    /**
     * Pattern to match input format
     */
    private final static Pattern INPUT_PATT = Pattern.compile("(\\d) (\\d)");

    @SuppressWarnings("FieldCanBeLocal")
    private final String EXIT_KEY = "q";

    public ReversiServer(ReversiModel model) throws RemoteException {
        this.model = model;
        this.availablePlayers =
                new HashSet<>(Set.of(ReversiModel.PLAYER1,
                        ReversiModel.PLAYER2));
    }

    private void play() throws RemoteException {
        for (ClientRemote cl: connectedClients)
            cl.notify(String.format("Press %s to quit", EXIT_KEY));

        final StringBuilder gameStateStrBuilder = new StringBuilder();
        while (!model.isGameOver() && connectedClients.size() == 2) {
            gameStateStrBuilder.setLength(0);
            gameStateStrBuilder.append("\n");

            View.addTextBoardTo(gameStateStrBuilder, model);
            final int currPlayer = model.getCurrentPlayer();
            View.addCurrPlayer(gameStateStrBuilder, currPlayer);

            for (ClientRemote cl: connectedClients) cl.notify(gameStateStrBuilder.toString());

            ClientRemote currClient = playerToClient.get(currPlayer);
            currClient.notify("Enter Move:");
            try {
                final AtomicReference<String> inputRef = new AtomicReference<>();
                inputThread = new Thread(() -> {
                    try {
                        inputRef.set(currClient.getInput());
                    } catch (IOException e) {
                        restoreGame();
                    }
                });
                inputThread.start();
                inputThread.join();

                String input = inputRef.get();
                if (input.equals(EXIT_KEY)) {
                    forfeit(currClient);
                    break;
                }
                final Coordinate inputCoords = getInputCoordFrom(input);

                if (!model.makeMove(inputCoords.x(), inputCoords.y())) {
                    currClient.notify("Invalid move, try again.");
                }
            } catch (Exception e) {
                LOGGER.error(e, () -> "Couldn't make a move.");
                currClient.notify("Couldn't make move. Please retry.");
                break;
            }
        }
        reportEndGame();
    }

    private void reportEndGame() throws RemoteException {
        if (model.getWinner() == DRAW) {
            for (ClientRemote cl : connectedClients) {
                cl.notify("it is a draw");
            }
        } else {
            for (ClientRemote cl : connectedClients) {
                String winnerStmt = String.format(
                        "Player %s won",
                        View.getTile(model.getWinner())
                );
                cl.notify(winnerStmt);
            }
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

    @Override
    public int getAvailableClientId() throws RemoteException {
        // this impl can be more sophisticated. For now, it is simple.
        return availablePlayers.size();
    }

    public synchronized void registerClientAndPlay(ClientRemote client) throws org.reversi.netcli.GameException, RemoteException {
        if (this.availablePlayers.isEmpty()) {
            throw new org.reversi.netcli.GameException("Game server is busy");
        }

        int player = availablePlayers.iterator().next();

        playerToClient.put(player, client);
        clientToPlayer.put(client, player);
        availablePlayers.remove(player);
        connectedClients.add(client);

        final String playerTile = View.getTile(player);
        LOGGER.info(() -> "Adding " + playerTile);

        client.notify("Assigned tile: " + playerTile);

        if (availablePlayers.size() > 0) {
            client.notify("Waiting for opponent.");
        } else {

            for (ClientRemote cl : connectedClients)
                cl.notify("Fasten your seatbelts. The game begins.");

            try ( ScheduledExecutorService heartbeatExecutor =
                          Executors.newSingleThreadScheduledExecutor()) {

                heartbeatExecutor.scheduleAtFixedRate(
                        this::sendHeartbeat, 0,
                        HEARTBEAT_INTERVAL, TimeUnit.SECONDS
                );

                play();
            }
            restoreGame();
        }
    }

    private void sendHeartbeat() {
        try {
            for (ClientRemote client : connectedClients) {
                client.heartbeat();
            }
        } catch (RemoteException e) {
            for (ClientRemote cl: connectedClients) {
                try {
                    LOGGER.info(() -> "client disconnected");
                    cl.notify("a client disconnected.");
                } catch (RemoteException ignored) {}
            }
            restoreGame();
        }
    }

    void restoreGame() {
        LOGGER.info(() -> "restoring game");
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
        }
        inputThread = null;
        connectedClients.clear();
        playerToClient.clear();
        clientToPlayer.clear();

        availablePlayers.add(ReversiModel.PLAYER1);
        availablePlayers.add(ReversiModel.PLAYER2);
        model = new ReversiModel(DEFAULT_BOARD_SIZE);
    }

    public void forfeit(final ClientRemote client) throws RemoteException {
        final String forfeitMsg = String.format(
                "Player %s forfeited.",
                View.getTile(clientToPlayer.get(client))
        );
        for (ClientRemote cl: connectedClients) {
            cl.notify(forfeitMsg);
        }
        restoreGame();
    }


    public static void main(String[] args) {
        try {
            // Create an instance of the ReversiModel
            ReversiModel model = new ReversiModel(DEFAULT_BOARD_SIZE);

            // Create the server object
            ServerRemote server = new ReversiServer(model);

            // Create the RMI registry
            Registry registry = LocateRegistry.createRegistry(1099);
            // Default RMI registry port is 1099

            // Bind the server object to the registry
            registry.rebind("ReversiServer", server);

            LOGGER.info(() -> "Reversi server is running...");
        } catch (Exception e) {
            LOGGER.error(e, () -> "Error starting the Reversi server. ");
        }
    }

    private static class View {
        private View() {}

        public final static String[] PLAYER_TILES = {"x", "o"};

        /**
         * a colored string to represent a possible / feasible move given a game state
         */
        private final static String POSSIBLE_MOVE_TILE =
                String.format("%s%s%s", Color.YELLOW, "*", Color.RESET);
        /**
         * a string to represent an empty tile on the board
         */
        private final static String EMPTY_TILE = "_";

        public static void addTextBoardTo(StringBuilder stringBuilder,
                                          ReversiModel gameState) {
            final int[][] board = gameState.getBoard();
            final Set<Coordinate> possibleMoves = gameState.getPossibleMoves();

            // append col IDs
            stringBuilder.append("  ");
            for (int i = 0; i < board.length; i++) {
                final String colID = String.format("%s%d%s ", Color.BLUE, i, Color.RESET);
                stringBuilder.append(colID);
            }
            stringBuilder.append("\n");

            // append rows
            for (int i = 0; i < board.length; i++) {
                final String rowID = String.format("%s%d%s ", Color.BLUE, i, Color.RESET);
                stringBuilder.append(rowID);

                for (int j = 0; j < board.length; j++) {
                    // print the appropriate tile

                    if (possibleMoves.contains(new Coordinate(i, j))) {
                        stringBuilder.append(POSSIBLE_MOVE_TILE);
                    } else if (board[i][j] == ReversiModel.EMPTY) {
                        stringBuilder.append(EMPTY_TILE);
                    } else {
                        final int playerID = ReversiModel.getPlayerIndex(board[i][j]);
                        stringBuilder.append(PLAYER_TILES[playerID]);
                    }

                    stringBuilder.append(" ");

                }
                stringBuilder.append("\n");
            }

        }

        public static String getTile(final int player) {
            final int playerIdx = ReversiModel.getPlayerIndex(player);
            return PLAYER_TILES[playerIdx];
        }

        public static void addCurrPlayer(final StringBuilder stringBuilder,
                                         final int currentPlayer) {
            final int playerIdx =
                    ReversiModel.getPlayerIndex(currentPlayer);
            stringBuilder.append("Turn: ")
                    .append(PLAYER_TILES[playerIdx])
                    .append("\n");
        }

        /**
         * An enum to hold all the colors for displaying strings printed by the View
         * using ANSI strings
         * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">ANSI wikipedia</a>
         */
        private enum Color {
            /**
             * turns off all ANSI attributes set so far, which should return the console to its defaults
             */
            RESET("\033[0m"),

            /**
             * ANSI red
             */
            @SuppressWarnings("unused")
            RED("\033[0;31m"),

            /**
             * ANSI yellow
             */
            YELLOW("\033[0;33m"),

            /**
             * ANSI blue
             */
            BLUE("\033[0;34m");

            /**
             * string holding the ANSI color code
             */
            private final String colorCode;

            /**
             * The enum constructor
             * @param colorCode string holding the ANSI color code
             */
            Color(final String colorCode) {
                this.colorCode = colorCode;
            }

            /**
             * Overridden toString to directly access color code
             * @return the enum's colorCode
             */
            @Override
            public String toString() {
                return colorCode;
            }
        }
    }

}
