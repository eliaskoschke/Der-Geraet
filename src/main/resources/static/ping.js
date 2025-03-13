window.onload = function() {
    console.log("Ping.js geladen, starte Interval");
    setInterval(pingActor, 200);
}

function pingActor() {
    const currentPage = window.location.pathname.split('/').pop();
    console.log("Aktuelle Seite:", currentPage);
    
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

            if(!document.getElementById('adminGame').classList.contains('hidden')) {
                console.log("AdminGame ist sichtbar, führe Pings aus");
                pingPlayerTurn();
                pingDealerHand();
            } else {
                console.log("AdminGame ist versteckt");
            }
            break;
        case 'settings.html':
            break;
    }
}