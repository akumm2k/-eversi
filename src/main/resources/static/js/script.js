
const DISK = {
    WHITE: 1,
    BLACK: 2
};
const DISK_DIV_CLASS = {
    WHITE: "white-disk",
    BLACK: "black-disk"
};
const SESSION_PLAYER_KEY = "player";


/**
 * The flipDisk function takes in a board and a currentDisk.
 * It then adds the flip-animation class to the currentDisk, which will cause it to flip over.
 * After 500ms, it removes the animation class from the disk and changes its color based on what is stored in that position of board.

 *
 * @param board the state of the board
 * @param currentDisk disk div
 *
 * @return nothing really
 */
function flipDisk(board, currentDisk) {
    const row = currentDisk.getAttribute("data-row");
    const col = currentDisk.getAttribute("data-col");

    currentDisk.classList.add("flip-animation");

    setTimeout(() => {
        // Remove flip animation class
        currentDisk.classList.remove("flip-animation");

        if (board[row][col] === DISK.WHITE) {
            currentDisk.classList.add(DISK_DIV_CLASS.WHITE);
            currentDisk.classList.remove(DISK_DIV_CLASS.BLACK);
        } else if (board[row][col] === DISK.BLACK) {
            currentDisk.classList.add(DISK_DIV_CLASS.BLACK);
            currentDisk.classList.remove(DISK_DIV_CLASS.WHITE);
        } else {
            currentDisk.classList.remove(DISK_DIV_CLASS.BLACK, DISK_DIV_CLASS.WHITE);
        }
    }, 500);
}

/**
 * The restorePossibleMoveCells function removes the &quot;possible-move-cell&quot; class from all cells in the game board.
 * This function is called when a player clicks on a disk cell, or when a player clicks on an empty cell that is not
 * adjacent to any of their disks. The purpose of this function is to remove any possible move cells that were previously
 * highlighted by calling the highlightPossibleMoveCells function.

 *
 *
 * @return nothing really
 */
function restorePossibleMoveCells() {
    document.querySelectorAll("div.possible-move-cell")
        .forEach(diskCell => {
            diskCell.classList.remove("possible-move-cell");
        });
}



/**
 * The updateGameBoard function updates the game board with the current state of the game.
 *
 * @param data board state
 *
 * @return nothing really
 */
function updateGameBoard(data) {
    restorePossibleMoveCells();

    const board = data.board;
    const gameBoardCells = document.querySelectorAll("table#gameBoard div.disk-container");
    gameBoardCells.forEach(cell => {
        flipDisk(board, cell);
    });

    if (data.status === "IN_PROGRESS" && data.currentGamePlayer.disk === sessionStorage.getItem(SESSION_PLAYER_KEY)) {
        const possibleMoves = data.possibleMoves;
        possibleMoves.forEach(({x, y}) => {
            const diskCell = document.querySelector(
                `div.disk-container[data-row="${x}"][data-col="${y}"]`
            );
            diskCell.classList.add("possible-move-cell");
        });
    }
}

/**
 * The softHideGameConfigPrompts function hides the game configuration prompts by adding a class to the login form.
 * This function is called when a user clicks on one of the &quot;Play&quot; buttons in order to hide those prompts and show
 * the game board. The softHideGameConfigPrompts function does not remove any elements from DOM, it simply adds a
 * class that makes them invisible. This allows us to easily make them visible again if we need to (for example, if
 * we want users who are already logged in or have created an account be able to change their settings).
 *
 *
 * @return nothing really
 */
function softHideGameConfigPrompts() {
    const form = document.getElementById("loginForm")
    form.classList.add('is-removed');
}

/**
 * The lockInputs function disables all the inputs in the login form.
 *
 *
 * @return nothing really
 */
function lockInputs() {
    const form = document.getElementById("loginForm");
    Array.from(form.children)
        .forEach(c => {c.disabled = true;});
}


/**
 * The updateFooter function updates the footer of the game board with a player's login and disk color.
 *
 *
 * @param login Display the user's login in the footer
 * @param disk Determine which disk to display in the footer
 *
 * @return nothing really
 */
function updateFooter(login, disk) {
    const footer = document.querySelector("div#info-footer");

    if (disk === DISK.WHITE) {
        footer.innerHTML =
            `<div>${login}:</div> 
            <div class="white-disk disk-container"></div>
            `;
    } else if (disk === DISK.BLACK) {
        footer.innerHTML =
            `<div>${login}:</div> 
                <div class="black-disk disk-container"></div>
            `;
    }
}

/**
 * The updateFooterBothPlayers function updates the footer of the game board with both players' names and their respective disk colors.
 *
 * @param data game state
 *
 * @return nothing really
 */
function updateFooterBothPlayers(data) {
    const footer = document.querySelector("div#info-footer");
    footer.style.display = "flex";
    footer.style.justifyContent = "space-between";
    footer.innerHTML =
        `<div style="display: inline-block">${data.gamePlayer1.login}
            <div class="white-disk disk-container"></div>
        </div>
        <div style="display: inline-block">${data.gamePlayer2.login}
            <div class="black-disk disk-container"></div>
        </div>
        `
}