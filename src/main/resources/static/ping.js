window.onload = function() {
    const window = window.location.filename
    setInterval(pingActor, 50);
}

function pingActor() {
    switch (window) {
        case 'play.html':
            if(!document.getElementById('start').classList.contains('hidden')) {
                Game();
                pingLobbyAsUser();
            }
            if(!document.getElementById('playerGame').classList.contains('hidden')) {
                pingPlayerTurn();
                pingDealerHand();
            }
            break;
        case 'admin.html':
            if(!document.getElementById('adminGame').classList.contains('hidden')) {
                pingPlayerTurn();
                pingDealerHand();
                pingLobby();
            }
            break;
        case 'settings.html':
            break;
    }
}