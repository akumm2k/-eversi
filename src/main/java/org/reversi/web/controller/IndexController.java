package org.reversi.web.controller;

import org.reversi.web.model.ReversiGame;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The Index Page controller.
 */
@Controller
public class IndexController {
    /**
     * Sets up a dummy board on the first web page get request.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/")
    public String index(Model model) {
        // Add the dummy game to the model
        ReversiGame dummyGame = new ReversiGame();
        dummyGame.setBoard(4);

        // allow thymeleaf access to the dummyGame
        model.addAttribute("game", dummyGame);

        return "index";
    }
}