function leave() {
    window.location.href = 'index.html';
    if(window.location.pathname === "/play.html") {
        console.log(user + " left the game.");
        
        post('user/playerLeftTheTable', user);
        user = null;
        ingame = false;
    }
}

var userPick;


function pingPlayerTurn() {
    console.log(data.message);
    var current = document.getElementById('currentPlayer');
    userPick = get('game/ping/getPlayerTurn');;
    if (userPick != user) {
        current.textContent = "Spieler " + userPick + " ist an der Reihe!";
    } else {
        current.textContent = "Du bist an der Reihe!";
    }
    
}

setInterval(pingPlayerTurn, 1000);

var dealerHand = "";
var olddealerhand = "";
var displayedCards = 0;

function pingDealerHand() {
    dealerHand = get('game/ping/getDealerHand');
    
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

