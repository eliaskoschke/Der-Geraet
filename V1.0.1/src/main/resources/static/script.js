var user = null;
var ingame = false;

function sitzplatzClicked(index) {
    var sitzplaetze = document.getElementById('btns-sitzplaetze');
    sitzplaetze.classList.add('hidden');
    user = index;
    console.log("User Logged in with id: " + user);
    ingame = true;
    var start = document.getElementById('wait');
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
    if (gameAlreadyStarted) {
        window.location.href = 'error.html?error=gameAlreadyStarted';
    }

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
