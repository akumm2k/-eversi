package org.reversi.web.controller.dto;

import lombok.Data;
import org.reversi.web.model.Coordinate;
import org.reversi.web.model.Disk;

/**
 * Wrapper for the Move config used in the Http requests.
 * @see org.reversi.web.controller.GameController
 */
@Data
public class Move {
    private Disk disk;
    private Coordinate coord;
    private String gameId;
}
