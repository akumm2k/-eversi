package org.reversi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReversiClient extends UnicastRemoteObject implements ClientRemote {

    public ReversiClient() throws RemoteException {}

    public String getInput() throws RemoteException {
        try (BufferedReader input =
                     new BufferedReader(new InputStreamReader(System.in))) {
            return input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notify(String msg) throws RemoteException {
        System.out.println(msg);
    }


    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                ClientRemote cl = new ReversiClient();
                ServerRemote server = (ServerRemote) Naming.lookup("ReversiServer");
                Naming.rebind(String.format("cl-%s", args[0]), cl);

                server.registerClientAndPlay(cl);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}