var user = null;
var ingame = false;
var gameStarted = false;

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

    if(post('user/playerJoinedTheTable', id) == "not acknowledged"){
        window.location.reload();
    } else {
        sitzplatzClicked(id)
    }
}


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

    if (gameStarted === true) {
        document.getElementById('start').classList.add('hidden');
        document.getElementById('playerGame').classList.remove('hidden');
    }

    if(userPick == user) {
        document.getElementById('pickBtn1').disabled = false;
        document.getElementById('pickBtn2').disabled = false;
    } else {
        document.getElementById('pickBtn1').disabled = true;
        document.getElementById('pickBtn2').disabled = true;
    }
}

function inGame() {
    if (gameStarted && user == null) {
        window.location.href = 'error.html?error=gameAlreadyStarted';
    }

    console.log('ingame vor fetch');

    var GameState = get('user/ping');
    
        if (GameState == 'Game has started') {
            gameStarted = true;
            console.log('Spiel wurde gestartet');
        } else if(GameState == "Game beendet") {
            var winnerTable = get('game/getWinner');
            if(winnerTable == false) {

            } else {
                gameStarted = false
                playerGame = document.getElementById('playerGame');
                playerGame.classList.add('hidden');
                table = document.getElementById('winnerTable');
                table.classList.remove('hidden');
                table.innerHTML = "";
                const d = winnerTable.message.split(',');
                for(const inhalt of d) {
                    const h2inhalt = document.createElement("h2");
                    h2inhalt.textContent = inhalt;
                    table.appendChild(h2inhalt);
                }
            }
        } else if(GameState == "Game was reseted"){
            gameStarted = true;
            playerGame = document.getElementById('playerGame');
            playerGame.classList.remove('hidden');
            table = document.getElementById('winnerTable');
            table.classList.add('hidden');
            console.log("Resetted")
            leave();
        } 
}

function loadTables() {
    handleServerResponse(get('onload'));
}

function handleServerResponse(message) {
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
}

function pingLobbyAsUser() {
    if(user != null) {
        if(get('user/ping') == "Game was reseted"){
            console.log("Game was resseted")
            leave();
        }
    }
}

window.onload = function() {
    loadTables(); 
}



function getCard() {
    post('user/hit', user);
}


function holdCard() {
    post('user/stay', user);
}


