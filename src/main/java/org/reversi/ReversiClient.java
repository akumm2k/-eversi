package org.reversi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReversiClient extends UnicastRemoteObject implements ClientRemote {

    private final BufferedReader inputReader;
    public ReversiClient() throws RemoteException {
        inputReader = new BufferedReader(new InputStreamReader(System.in));

        // Register the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        try {
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInput() throws IOException {
        return inputReader.readLine();
    }

    @Override
    public void notify(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void heartbeat() throws RemoteException {
        // just send a heartbeat back to the server
    }


    public static void main(String[] args) {
        try {
            ClientRemote cl = new ReversiClient();
            ServerRemote server = (ServerRemote) Naming.lookup("ReversiServer");
            String clientID = String.format("cl-%s", server.getAvailableClientId());
            Naming.rebind(clientID, cl);
            server.registerClientAndPlay(cl);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}