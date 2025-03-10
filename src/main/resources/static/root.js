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
            user = null;
            ingame = false;
        })
        .catch(error => console.error('Fehler beim Verlassen des Spiels:', error));
    }
}

var userPick;


function pingPlayerTurn() {
    var current = document.getElementById('currentPlayer');
    
    fetch('/api/game/ping/getPlayerTurn', {
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
        userPick = data.message;
        if (userPick != user) {
            current.textContent = "Spieler " + userPick + " ist an der Reihe!";

                document.getElementById('pickBtn1').disabled = true;
                document.getElementById('pickBtn2').disabled = true;

        } else {
            current.textContent = "Du bist an der Reihe!";

                document.getElementById('pickBtn1').disabled = false;
                document.getElementById('pickBtn2').disabled = false;

        }
    })
    .catch(error => console.error('Fehler beim Abrufen des aktuellen Spielers:'));
}

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
        dealerHand = data.message;
        if (dealerHand == olddealerhand) {
            console.log('Keine Neuen karten vorhanden');
        } else {
            updateDealerHand();
        }
    })
    .catch(error => console.error('Fehler beim Abrufen der Dealer-Hand:', error));
}


function updateDealerHand() {
    console.log('DEALERHAND' + dealerHand)

    displayedCards = 0

    let div = document.getElementById('dealerHand');     
    div.innerHTML = "";
    if (dealerHand != "") {
        let cardIds = dealerHand.split(',');
        let element = document.getElementById('dealerHand');

        console.log('Element gefunden:', element);
        console.log('Karten-IDs:', cardIds);
        olddealerhand = dealerHand;

        for (var i = 0; i < cardIds.length; i++) {
            if (cardIds.length < 1) {
                break;
            }

            console.log('Füge Bild hinzu für Karte:', cardIds[i]);
            element.insertAdjacentHTML('beforeend', '<img id="karte' + i + '" src="img/cards/' + cardIds[i] + '.png">');
        }
    } 
    
}

function setFavicon() {
    const faviconLink = document.createElement("link");
    faviconLink.rel = "icon";

    var card = Math.round((Math.random() * 4) + 1);
    var newRandom = Math.round((Math.random() * 13) + 1);

    if (newRandom < 10) {
        newRandom = "0" + newRandom;
    }

    card = card + "" + newRandom;
    faviconLink.href = "img/cards/" + card + ".png"; 

    const head = document.querySelector("head");
    head.appendChild(faviconLink);
}


window.onload = setFavicon();
 