document.addEventListener('DOMContentLoaded', function initializeError() {
    console.log("Error loaded");
    error();
});

function error() {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');

    var errorMessage = "Unerklärlicher Fehler :/";

    switch (error) {
        case 'gameAlreadyStarted': 
            errorMessage = "Das Spiel wurde bereits gestartet.";
            break;
        case 'wrongPassword':
            errorMessage = "Falsches Passwort!";
            break;
        case 'gamestarterror':
            errorMessage = "Fehler beim Starten des Spiels!";
            break;
        case 'networkerror':
            errorMessage = "Netzwerkfehler!";
            break;
        case null:
        case undefined:
        default:
            errorMessage = "Unerklärlicher Fehler :/";
            break;
    }

    document.getElementById('errorBox').textContent = errorMessage;
}

function back() {
    window.location.href = 'index.html';
}