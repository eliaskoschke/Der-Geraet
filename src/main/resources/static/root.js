function leave() {
    window.location.href = 'index.html';
    if(window.location.pathname === "/play.html") {
        console.log(user + " left the game.");
        fetch('/api/user/playerLeftTheTable', {
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
        user = null;
        ingame = false;
    }
}




function pingPlayerTurn() {

    fetch('api/game/ping/getPlayerTurn', {
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
            console.log(data.message);
            var current = document.getElementById('currentPlayer');
            if (data.message != user) {
                current.textContent = "Spieler " + data.message + " ist an der Reihe!";
            } else {
                current.textContent = "Du bist an der Reihe!";
            }
        } else {
            console.error('Keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(error => console.error('Fehler:', error));
}

setInterval(pingPlayerTurn, 1000);


function pingDealerHand() {
    fetch('/api/game/ping/getDealerHand', {
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
            data.message
        } else {
            console.error('Keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(error => console.error('Fehler:', error));
}
