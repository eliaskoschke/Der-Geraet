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

var dealerHand = "";
var olddealerhand = "";
var displayedCards = 0;

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
            dealerHand = data.message;
        } else {
            console.error('Keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(error => console.error('Fehler:', error));

    
    if (dealerHand == olddealerhand) {
        console.log('Keine Neuen karten vorhanden');
    } else {
        updateDealerHand();
    }
    
}
setInterval(pingDealerHand, 1000);



function updateDealerHand() {
    console.log('DEALERHAND' + dealerHand)

    displayedCards = 0

    let div = document.getElementById('dealerHand');     
    div.innerHTML = "";

    let cardIds = dealerHand.split(',');
    let element = document.getElementById('dealerHand');

    console.log('Element gefunden:', element);
    console.log('Karten-IDs:', cardIds);
    olddealerhand = dealerHand;

    for (var i = 0; i < cardIds.length; i++) {
        if (cardIds.length <= 1) {
            break;
        }

        console.log('Füge Bild hinzu für Karte:', cardIds[i]);
        element.insertAdjacentHTML('beforeend', '<img id="karte' + i + '" src="img/cards/' + cardIds[i] + '.png">');
    }
}