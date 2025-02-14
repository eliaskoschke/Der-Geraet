var user = null;
var ingame = false;

function sitzplatzClicked(index) {
    var sitzplaetze = document.getElementById('btns-sitzplaetze');
    sitzplaetze.classList.add('hidden');
    user = index;
    console.log("User Logged in with id: " + user);
    ingame = true;
    var start = document.getElementById('start');
    start.classList.remove('hidden');
}



function isUserLoggedIn() {
    // Wenn spieler nicht ingame aber auf dem part der seite dann zurück zum start
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
    if (gameAlreadyStarted) {
        window.location.href = 'error.html?error=gameAlreadyStarted';
    }

    var gameStarted = false;
    while(!gameStarted) {
        fetch('/api/hasGameStarted', {
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

        setTimeout(1000);
    }

    //
}

function startGame() {
    inGame.gameStarted = true;
}

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