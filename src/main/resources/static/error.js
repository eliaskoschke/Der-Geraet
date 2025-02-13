window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    var h1 = document.getElementById('errorBox');

    switch (error) {
        case 'gameAlreadyStarted': 
            h1.textContent = "Error: Das Spiel wurde bereits gestartet.";
            break;
        case 'wrongPassword':
            h1.textContent = "Falsches Passwort!";
            break;
    }
}

function back() {
    window.location.href = 'index.html';
}