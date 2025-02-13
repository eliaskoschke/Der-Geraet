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
    // Einbindung dass Spieler / Sitzplatz (user) nun belegt ist.
}

function leave() {
    console.log(user + " left the game.");
    user = null;
    ingame = false;
    window.location.href = 'index.html';
    // Einbindung dass Spieler / Sitzplatz (user) frei ist.
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
    //test ob game gestartet, wenn ja user weiterleitung auf error.html mit parameter gameAlreadyStarted

    var gameStarted = false;
    while(!gameStarted) {
        //test ob host spiel gestartet hat
        setTimeout(100);
    }

    //
}

function startGame() {
    inGame.gameStarted = true;
}

function sendMessage(id) {
            sitzplatzClicked(id)
            const button = document.getElementById(id);
            if (button) {
                button.disabled = true;
            } else {
                console.warn(`Button mit ID ${id} nicht gefunden.`);
            }
            fetch('/api/message', {
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

                } else {
                    alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
                }
            })
            .catch(
            //error => console.error('Fehler:', error)
            );
        }

        function fetchRandomNumber() {
            fetch('/api/onload', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ message: "hallo" })
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
            .catch(
            //error => console.error('Fehler:', error)
            );
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

        window.onload = fetchRandomNumber; // Zuweisung der Funktion, nicht der Aufruf