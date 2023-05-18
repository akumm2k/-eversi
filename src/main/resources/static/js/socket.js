
const SEP = "/";
const pathJoin = (...parts) =>
    parts.join(SEP)
        .replace(new RegExp(SEP + '{1,}', 'g'), SEP);

const STOMP_ENDPOINT = "/move";
const MSG_BROKER_DEST_PREFIX = `/topic/game-progress`;

let STOMP_CLIENT;
let GAME_ID;
let GAME_STARTED = false;


/**
 * The connectToSocket function connects to the socket endpoint and subscribes to a topic.
 *
 * @param gameId Identifies the game that is being played
 *
 * @return nothing really
 */
function connectToSocket(gameId) {
    const socket = new SockJS(STOMP_ENDPOINT);
    STOMP_CLIENT = Stomp.over(socket)
    STOMP_CLIENT.connect({}, (frame) => {
        console.log(`connected to the frame: ${frame}`);
        const msgBrokerDest = pathJoin(MSG_BROKER_DEST_PREFIX, gameId)
        STOMP_CLIENT.subscribe('/topic/game-progress/' + gameId, (response) => {
            const data = JSON.parse(response.body);

            if (!GAME_STARTED && data.gamePlayer2 !== null) {
                // alert player1 that player 2 joined
                if (sessionStorage.getItem(SESSION_PLAYER_KEY) === "WHITE") {
                    alert(`Game started`);
                }
                GAME_STARTED = true;

                updateFooterBothPlayers(data);
            }
            // TODO: flip only stolen disks
            updateGameBoard(data);
            if (data.status === "FINISHED") {
                console.log(data);
                const heading = document.getElementById("main-heading");
                const winnerAnnouncement = document.createElement("h2");
                if (data.winner === null) {
                    winnerAnnouncement.innerText = "Game Drawn.";
                } else {
                    winnerAnnouncement.innerText = `Game over: Winner = ${data.winner.login}`;
                }
                heading.after(winnerAnnouncement);
            }
        });
    });
}


/**
 * The function sends a POST request to /game/start with a JSON body containing the login of
 * the player who created it. If successful, it will connect to a websocket for that game ID,
 * and update all relevant UI elements (the board, footer). Otherwise, an error message will be displayed.
 *
 *
 * @return a void promise, essentially nothing, given the function is async
 */
async function createGame() {
    const login = document.getElementById("loginInput").value;
    if (login === null || login === '') {
        alert("login missing. Enter your login.");
    } else {
        try {
            const response = await fetch("/game/start", {
                method: "POST",
                body: JSON.stringify({"login": login}),
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            });

            if (response.ok) {
                const data = await response.json();
                GAME_ID = data.gameId;
                // the java enums return a string in the response
                sessionStorage.setItem(SESSION_PLAYER_KEY, "WHITE");
                connectToSocket(data.gameId);
                alert(`You created a game with game ID: ${data.gameId}`);
                softHideGameConfigPrompts();
                updateGameBoard(data);
                updateFooter(data.gamePlayer1.login, DISK.WHITE);
            }
        } catch (err) {
            console.log(`Error creating game: ${err}`);
        }
    }
}


/**
 * The specificConnect function is used to connect a player to an existing game.
 *
 * @param gameId Connect to the specific game given by the gameId
 *
 * @return a void promise, essentially nothing, given the function is async
 */
async function specificConnect(gameId) {
    const login = document.getElementById("loginInput").value;
    if (login === null || login === '') {
        alert("login missing. Enter your login.");
    } else {
        try {
            const response = await fetch("/game/connect", {
                method: "POST",
                body: JSON.stringify({
                    "client": {"login": login},
                    "gameId": gameId
                }),
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            });

            if (response.ok) {
                const data = await response.json();
                // the java enums return a string in the response
                sessionStorage.setItem(SESSION_PLAYER_KEY, "BLACK");

                GAME_ID = data.gameId;
                connectToSocket(data.gameId);
                alert(`Connected to the game with ${data.gamePlayer1.login}`);
                softHideGameConfigPrompts();
                updateGameBoard(data);
                updateFooterBothPlayers(data);
            }
        } catch (err) {
            console.log(`Error connecting to game: ${err} with gameId: ${gameId}`);
        }
    }
}

/**
 * The randomConnect function is called when the user clicks on the &quot;Random Connect&quot; button.
 * It sends a POST request to /game/connect/random with the login of the current player.
 * If successful, it will connect to a socket and update all game elements accordingly.
 *
 *
 * @return a void promise, essentially nothing, given the function is async
 */
async function randomConnect() {
    const login = document.getElementById("loginInput").value;
    if (login === null || login === '') {
        alert("login missing. Enter your login.");
    } else {
        try {
            const response = await fetch("/game/connect/random", {
                method: "POST",
                body: JSON.stringify({
                    "login": login
                }),
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            });

            if (response.ok) {
                const data = await response.json();
                // the java enums return a string in the response
                sessionStorage.setItem(SESSION_PLAYER_KEY, "BLACK");

                GAME_ID = data.gameId;
                connectToSocket(data.gameId);
                alert(`Connected to the game with ${data.gamePlayer1.login}`);
                softHideGameConfigPrompts();
                updateGameBoard(data);
                updateFooterBothPlayers(data);
            }
        } catch (err) {
            console.log(`Error connecting to game: ${err}`);
        }
    }
}

/**
 * The makeMove function is called when a player clicks on a cell in the board.
 * It sends an HTTP POST request to the server with information about which disk was played,
 * where it was played, and what game it belongs to. The server then updates its state of the game
 * and broadcasts that update to all connected clients (including this one). This client's socket connection will receive
 * that broadcasted message from the server and update its own state of the game accordingly.
 *
 * @param xCoordinate represents the row of the board to place a disk in
 * @param yCoordinate represents the column of the board to place a disk in
 *
 * @return a void promise, essentially nothing, given the function is async
 */
async function makeMove(xCoordinate, yCoordinate) {
    const diskContainer = document.querySelector(
        `div.disk-container[data-row="${xCoordinate}"][data-col="${yCoordinate}"]`
    );
    if (!diskContainer.classList.contains("possible-move-cell")) {
        return;
    }
    try {
        const response = await fetch("/game/move", {
            method: "POST",
            body: JSON.stringify({
                "disk": sessionStorage.getItem(SESSION_PLAYER_KEY),
                "coord": {
                    "x": xCoordinate,
                    "y": yCoordinate
                },
                "gameId": GAME_ID
            }),
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });
        if (response.ok) {
            const data = await response.json();
            // board is updated by the socket connection
        }
    } catch (error) {
        console.log(`Error making a move: ${error}`)
    }
}

/**
 * The parseGameConfigAndConnect function is called when the user clicks the &quot;Play&quot; button.
 * It parses the game configuration from the HTML form and then connects to a game based on that configuration.
 *
 * @return a void promise, essentially nothing, given the function is async
 */
async function parseGameConfigAndConnect() {
    const gamePreference = document.getElementById("actionSelect").value;
    if (gamePreference === "create") {
        await createGame();
    } else if (gamePreference === "joinRandom") {
        await randomConnect();
    } else if (gamePreference === "joinSpecific") {
        const gameId = document.getElementById("gameIdInput").value;
        await specificConnect(gameId);
    } else {
        console.log(`Unexpected preference: ${gamePreference}`);
    }
    lockInputs();
}