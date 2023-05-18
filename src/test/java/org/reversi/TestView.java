package org.reversi;

import org.reversi.cli.ReversiModel;
import org.reversi.cli.ReversiView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the MVC View
 */
public class TestView {

    private ReversiView view;
    @SuppressWarnings("unused")
    private ReversiModel model;
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    /**
     * empty constructor
     */
    TestView() {}

    @BeforeEach
    void initView() {
        model = new ReversiModel(4);
        view = ReversiView.getInstance();

        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @AfterEach
    void destroyView() {
        System.setOut(System.out);

        model = null;
    }

    @Test
    void testPlayerPrint() {
        view.printCurrentPlayer(-1);
        assertEquals(
            String.format("Turn: %s%n", ReversiView.PLAYER_TILES[0]),
            byteArrayOutputStream.toString()
        );

        byteArrayOutputStream.reset();
        view.printCurrentPlayer(1);
        assertEquals(
            String.format("Turn: %s%n", ReversiView.PLAYER_TILES[1]),
            byteArrayOutputStream.toString()
        );
    }

}
