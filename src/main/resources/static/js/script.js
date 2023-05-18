
const DISK= {
    WHITE: 1,
    BLACK: 2
};
const DISK_DIV_CLASS = {
    WHITE: "white-disk",
    BLACK: "black-disk"
};
const SESSION_PLAYER_KEY = "player";


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

function restorePossibleMoveCells() {
    document.querySelectorAll("div.possible-move-cell")
        .forEach(diskCell => {
            diskCell.classList.remove("possible-move-cell");
        });
}

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

function softHideGameConfigPrompts() {
    const form = document.getElementById("loginForm")
    form.classList.add('is-removed');
}

function lockInputs() {
    const form = document.getElementById("loginForm");
    Array.from(form.children)
        .forEach(c => {c.disabled = true;});
}

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