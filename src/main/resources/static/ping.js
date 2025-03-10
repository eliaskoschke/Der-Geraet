window.onload = function() {
    setInterval(pingActor, 50);
}

function pingActor() {
    const currentPage = window.location.pathname.split('/').pop();
    switch (currentPage) {
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