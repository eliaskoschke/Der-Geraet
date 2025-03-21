var user = null;
var ingame = false;
var gameStarted = false;
var winnerWasAsked = false;
var gameMode = null;

function sitzplatzClicked(index) {
    var sitzplaetze = document.getElementById('btns-sitzplaetze');
    sitzplaetze.classList.add('hidden');
    user = index;
    console.log("User Logged in with id: " + user);
    ingame = true;
    var start = document.getElementById('start');
    start.classList.remove('hidden');
}

function joinTable(id) {
    const button = document.getElementById(id);
    if (button) {
        button.disabled = true;
    } else {
        console.warn(`Button mit ID ${id} nicht gefunden.`);
    }

    post('user/playerJoinedTheTable', id)
        .then(belegtFrageueichen => {
            if(belegtFrageueichen === false) {
                window.location.reload();
            } else if(belegtFrageueichen === true) {
                sitzplatzClicked(id);
            } else {
                window.location.href = 'error.html?error=networkerror';
            }
        })
        .catch(error => console.error('Fehler beim Beitreten zum Tisch:', error));
}

window.addEventListener("load", function(){
    loadTables();
})

window.onload = function() {
    var dots = document.getElementById('dots');
    var dotCount = 0;


    setInterval(function() {
        dotCount = (dotCount + 1) % 4; 
        dots.textContent = '.'.repeat(dotCount);
    }, 500);



};

function Game() {
    inGame();


    get('user/getGameMode')
            .then(message => {
                console.log('Serverantwort:', message);
                gameMode = message;
            })
            .catch(error => console.error('Fehler beim Laden der Tische:', error));


    if (gameStarted === true) {
        winnerWasAsked = false;
        document.getElementById('start').classList.add('hidden');
        document.getElementById('winnerTable').classList.add('hidden');
        document.getElementById('playerGame').classList.remove('hidden');
        if(gameMode === "Poker"){
            button = document.getElementById('pickBtn2');
            button.classList.add('hidden')
        }
        document.getElementById('dealerHand').classList.remove('hidden');
        document.getElementById('pickBtns').classList.remove('hidden');
        document.getElementById('currentPlayer').classList.remove('hidden');
    }

}

function inGame() {
    if (gameStarted && user == null) {
        window.location.href = 'error.html?error=gameAlreadyStarted';
    }

    console.log('ingame vor fetch');
    fetch('/api/user/ping')
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht ok');
            }
            return response.json();
        })
        .then(data => {
            const GameState = data.message;
            console.log('Aktueller Gamestate: ' + GameState);

            if(GameState === "\"true\"") {
                user = null;
                ingame = false;
                gameStarted = false;
                winnerWasAsked = false;
                gameMode = null;
                playerGame = document.getElementById('playerGame');
                playerGame.classList.remove('hidden');
                table = document.getElementById('winnerTable');
                table.classList.add('hidden');
                console.log("Resetted");
                leave();
            } else if (GameState == 'Game has started') {
                leaveBtn = document.getElementById('leaveBtn');
                leaveBtn.classList.add('hidden');
                gameStarted = true;
                console.log('Spiel wurde gestartet');
            } else if(GameState == "Game beendet") {
                fetch('/api/game/getWinner')
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Netzwerkantwort war nicht ok');
                        }
                        return response.json();
                    })
                    .then(winnerTable => {
                        if(!winnerWasAsked) {
                            gameStarted = false;

                            dealerHand = document.getElementById('dealerHand');
                            dealerHand.classList.add('hidden');

                            currentPlayer = document.getElementById('currentPlayer');
                            currentPlayer.classList.add('hidden');

                            table = document.getElementById('winnerTable');
                            table.classList.remove('hidden');

                            pickBtns = document.getElementById('pickBtns');
                            pickBtns.classList.add('hidden');

                            table.innerHTML = "";
                            if( winnerTable.message.includes(",")){
                                let d = winnerTable.message.split(',');
                                for(const inhalt of d) {
                                    const h2inhalt = document.createElement("h2");
                                    h2inhalt.textContent = inhalt;
                                    table.appendChild(h2inhalt);
                                }
                            } else{
                                const h2inhalt = document.createElement("h2");
                                h2inhalt.textContent = winnerTable.message;
                                table.appendChild(h2inhalt);
                            }
                            winnerWasAsked = true;
                        } else {

                        }
                    })
                    .catch(error => console.error('Fehler beim Abrufen des Gewinners:', error));
            }
        })
        .catch(error => console.error('Fehler beim Abrufen des Spielstatus:', error));
}

function loadTables() {
    console.log("Wurde geladen!!!!!!!!!!!!!!!!")
    get('onload')
        .then(message => {
            console.log('Serverantwort:', message);
            const buttonIds = JSON.parse(message);
            buttonIds.forEach(id => {
                console.log(id)
                const button = document.getElementById(id);
                if (button) {
                    button.disabled = true;
                } else {
                    console.warn(`Button mit ID ${id} nicht gefunden.`);
                }
            });
        })
        .catch(error => console.error('Fehler beim Laden der Tische:', error));
}

function pingLobbyAsUser() {
    if(user != null) {
        get('user/ping')
            .then(response => {
                if(response === "Game was reseted"){
                    console.log("Game was resseted")
                    leave();
                }
            })
            .catch(error => console.error('Fehler beim Ping:', error));
    }
}

window.onload = function() {
    loadTables(); 
}

function getCard() {
    post('user/hit', user)
        .then(response => {
            console.log('Karte gezogen:', response);
        })
        .catch(error => console.error('Fehler beim Ziehen der Karte:', error));
}

function holdCard() {
    post('user/stay', user)
        .then(response => {
            console.log('Spieler bleibt:', response);
        })
        .catch(error => console.error('Fehler beim Halten:', error));
}


