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

window.onload = function() {
    isUserLoggedIn();

    var dots = document.getElementById('dots');
    var dotCount = 0;
    
    setInterval(function() {
        dotCount = (dotCount + 1) % 4; 
        dots.textContent = '.'.repeat(dotCount);
    }, 500);
};

function inGame() {
    //if (gameAlreadyStarted) {
    //    window.location.href = 'error.html?error=gameAlreadyStarted';
    //}
    console.log('ingame vor fetch');

    fetch('/api/user/ping', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            if (data.message == 'Game has started') {
                gameStarted = true;
                console.log('Spiel wurde gestartet');
            } else if(data.message == "Game beendet"){
                console.log("Siegertabelle")
                fetch('api/game/getWinner', {
                    method: 'GET',
                    headers: {
                        'Content-Type' : 'application/json'
                    }
                })
                .then(response => {
                    if(!response.ok) {
                        throw new Error('Netzwerkantwort war nicht ok')
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.message) {
                        if(data.message == 'false') {

                        } else {
                            playerGame = document.getElementById('playerGame');
                            playerGame.classList.add('hidden');
                            table = document.getElementById('winnerTable');
                            table.classList.remove('hidden');
                            table.innerHTML = "";
                            const d = data.message.split(',');
                            for(const inhalt of d) {
                                const h2inhalt = document.createElement("h2");
                                h2inhalt.textContent = inhalt;
                                table.appendChild(h2inhalt);
                            }
                        }
                    }
                })
                
            } else if(data.message == "Game was reseted"){
                gameStarted = false;
                console.log("Resetted")
                leave();
            } 
        } else {
            console.error('Keine Nachricht in der Antwort gefunden!');
        }
    })
    .catch(error => console.error('Fehler:', error));

    setTimeout(1000);
}


function Game() {
    
    inGame();

    if (!document.getElementById('start').classList.contains('hidden')){document.getElementById('start').classList.add('hidden');}
    if (document.getElementById('playerGame').classList.contains('hidden')){document.getElementById('playerGame').classList.remove('hidden');}


    if(userPick == user) {
        document.getElementById('pickBtn1').disabled = false;
        document.getElementById('pickBtn2').disabled = false;
    } else {
        document.getElementById('pickBtn1').disabled = true;
        document.getElementById('pickBtn2').disabled = true;
    }
}
setInterval(Game, 100);

function joinTable(id) {
    sitzplatzClicked(id)
    const button = document.getElementById(id);
    if (button) {
        button.disabled = true;
    } else {
        console.warn(`Button mit ID ${id} nicht gefunden.`);
    }
    fetch('/api/user/playerJoinedTheTable', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: id })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            if(data.message == "not acknowledged"){
                window.location.reload();
            }
        } else {
            alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(
    //error => console.error('Fehler:', error)
    );
}

function loadTables() {
    fetch('/api/onload', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            handleServerResponse(data.message);
        } else {
            console.error('Keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(error => console.error('Fehler:', error));
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
    console.log('ausgeführt');
    if(user != null) {
        fetch('/api/user/ping', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht ok');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.message) {
                if(data.message == "Game was reseted"){
                console.log("Resseted")
                leave();
                }
            } else {
                console.error('Keine Nachricht in der Antwort gefunden.');
            }
        })
        .catch(error => console.error('Fehler:', error));
        }
}

window.onload = loadTables; // Zuweisung der Funktion, nicht der Aufruf
setInterval(pingLobbyAsUser, 1000);



function get() {
    fetch('/api/user/hit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: user })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {

        } else {
            alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(
    error => console.error('Fehler:', error)
    );
}


function hold() {
    fetch('/api/user/stay', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: user })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {

        } else {
            alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(
    error => console.error('Fehler:', error)
    );
}


