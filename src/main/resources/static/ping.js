window.onload = function() {
    setInterval(pingActor, 50);
}

function pingActor() {
    const currentPage = window.location.pathname.split('/').pop();
    switch (currentPage) {
        case 'play.html':
            Game();
            if(!document.getElementById('btns-sitzplaetze').classList.contains('hidden')){

            }
            if(!document.getElementById('start').classList.contains('hidden')) {

                pingLobbyAsUser();
            }
            if(!document.getElementById('playerGame').classList.contains('hidden')) {

                pingPlayerTurn();
                pingDealerHand();
            }
            break;
        case 'admin.html':
                pingPlayerTurn();
                pingDealerHand();
                pingLobby();
            break;
        case 'settings.html':
            break;
    }
}