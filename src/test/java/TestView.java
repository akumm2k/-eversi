import org.reversi.mvc.ReversiModel;
import org.reversi.mvc.ReversiView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestView {

    private ReversiView view;
    private ReversiModel model;
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

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
    void testBoardPrint() {
        view.printBoard(model);
        assertEquals(
                """
                _ _ * _\s
                _ o x *\s
                * x o _\s
                _ * _ _\s
                """,
                byteArrayOutputStream.toString()
        );
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
