var user = 'admin';

document.addEventListener('DOMContentLoaded', (event) => {
    const urlParams = new URLSearchParams(window.location.search);
    const href = urlParams.get('href');

    const inputs = document.querySelectorAll('.psw-in');

    // Funktion zum Leeren der Eingabefelder beim Laden der Seite
    function clearInputs() {
        inputs.forEach(input => {
            input.value = '';
        });
    }

    function sendPassword() {
        const password = document.getElementById('password').value;
        const password2 = document.getElementById('password2').value;

        if (password === '') {
            console.log('Bitte Passwort eingeben');
            return;
        }

        fetch('/api/admin/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: password })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.message === true) {
                if (password2 === '') {
                    document.getElementById('password').classList.add('hidden');
                    document.getElementById('password2').classList.remove('hidden');
                    document.getElementById('password2').focus();
                } else {
                    const urlParams = new URLSearchParams(window.location.search);
                    const href = urlParams.get('href');
                    if (href) {
                        window.location.href = href;
                    } else {
                        window.location.href = 'admin.html?loggedIn=true';
                    }
                }
            } else {
                console.log('Falsches Passwort');
                document.getElementById('password').value = '';
                document.getElementById('password2').value = '';
            }
        })
        .catch(error => console.error('Fehler beim Login:', error));
    }

    // Event Listener für Enter-Taste
    document.addEventListener('DOMContentLoaded', function() {
        const password1 = document.getElementById('password');
        const password2 = document.getElementById('password2');

        password1.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                sendPassword();
            }
        });

        password2.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                sendPassword();
            }
        });
    });

    inputs.forEach((input, index) => {
        input.addEventListener('input', (event) => {
            // Nur numerische Eingaben zulassen
            if (!/^\d$/.test(input.value)) {
                input.value = '';
            }

            if (input.value.length === 1) {
                if (index < inputs.length - 1) {
                    inputs[index + 1].focus();
                } else {
                    sendPassword();
                }
            }
        });

        input.addEventListener('keydown', (event) => {
            if (event.key === "Backspace" && input.value.length === 0 && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });

    // Eingabefelder leeren beim Laden der Seite
    clearInputs();

    // Dots Animation (aus dem ersten window.onload)
    var dots = document.getElementById('dots');
    if (dots) {
        var dotCount = 0;
        
        setInterval(function() {
            dotCount = (dotCount + 1) % 4;
            dots.textContent = '.'.repeat(dotCount);
        }, 500);
    }

    const loggedIn = urlParams.get('loggedIn');

    if (loggedIn === "true") {
        var pswIn = document.getElementById('passwordLine');
        pswIn.classList.add('hidden');
        var adminPanel = document.getElementById('adminPanel');
        adminPanel.classList.remove('hidden');
    }
});


    function wrongPassword() {
        window.location.href = 'error.html?error=wrongPassword';
    }

    function rightPassword() {
        var dots = document.getElementById('start');
        var adminPanel = document.getElementById('adminPanel');
        dots.classList.add('hidden');
        adminPanel.classList.remove('hidden')
    }

function startGameForAll() {
    var adminPanel = document.getElementById('adminPanel')
    adminPanel.classList.add('hidden')

    fetch('/api/admin/startGame', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: gameSel })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            if(data.message === "true"){
                document.getElementById('adminGame').classList.remove('hidden');
            } else {
                window.location.href = 'error.html?error=gamestarterror';
            }
        } else {
            alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(
    //error => console.error('Fehler:', error)
    );
}

function sendReset() {
    fetch('/api/admin/sendReset', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: "reset" })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            var message = document.getElementById('');
                
            if (data.message === "true"){
                message.textContent = 'Alle Spieler wurden erfolgreich entfernt.';
                message.classList.remove('hidden');
                message.style.add('color: green !important;')
                setTimeout(2300);
                message.classList.add('hidden');
            } else {
                message.textContent = 'Fehler beim entfernen der Spieler.';
                message.classList.remove('hidden');
                message.style.add('color: red !important;')
                setTimeout(2300);
                message.classList.add('hidden');
            }
        } else {
            alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
        }
    })
    .catch(
    //error => console.error('Fehler:', error)
    );
}

var gameSel = "blackjack"
function setActiveGame(game) {
    var altSel = document.getElementById(gameSel);
    altSel.classList.add('deactive') 
    gameSel = game;
    var element = document.getElementById(game);
    if(element.classList.contains('deactive')) {
        element.classList.remove('deactive')
    }
}

function playerCountTest(players) {
    var elementBtn = document.getElementById('start-game');
    if (players >= 2) {
        elementBtn.disabled = false;
    } else {
        elementBtn.disabled = true;
    }
}


function pingLobby() {
    console.log('ausgeführt');
    if(window.location.pathname === "/admin.html") {    //um sicher zu gehen.
        fetch('/api/admin/ping', {
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
                var count = document.getElementById('playerCount');
                count.textContent = data.message + "/12";
                playerCountTest(data.message);
            } else {
                console.error('Keine Nachricht in der Antwort gefunden.');
            }
        })
        .catch(error => console.error('Fehler:', error));
    }
}

function pingGameEnded() {
    fetch('api/user/ping', {
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
            if (data.message === "Game beendet") {
                fetch('api/game/getWinner')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Netzwerkantwort war nicht ok');
                    }
                    return response.json();
                })
                .then(winnerTable => {
                    if(!winnerWasAsked) {
                        gameStarted = false;

                        dealerHand = document.getElementById('dealerHand');
                        dealerHand.classList.add('hidden');

                        currentPlayer = document.getElementById('currentPlayer');
                        currentPlayer.classList.add('hidden');

                        table = document.getElementById('winnerTable');
                        table.classList.remove('hidden');

                        table.innerHTML = "";
                        if( winnerTable.message.includes(",")){
                            let d = winnerTable.message.split(',');
                            for(const inhalt of d) {
                                const h2inhalt = document.createElement("h2");
                                h2inhalt.textContent = inhalt;
                                table.appendChild(h2inhalt);
                            }
                        } else{
                            const h2inhalt = document.createElement("h2");
                            h2inhalt.textContent = winnerTable.message;
                            table.appendChild(h2inhalt);
                        }
                        winnerWasAsked = true;

                        controllBtns = document.getElementById('controllBtns');
                        controllBtns.classList.remove('hidden');
                    } else {

                    }
                })
            }
        }
    })
    .catch(error => console.error('Fehler:', error));
} 

function restartGame() {
    fetch('/api/admin/restartGame', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: "restart" })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            if (data.message === "thanks") {
                controllBtns = document.getElementById('controllBtns');
                controllBtns.classList.add('hidden');

                dealerHand = document.getElementById('dealerHand');
                dealerHand.classList.remove('hidden');

                currentPlayer = document.getElementById('currentPlayer');
                currentPlayer.classList.remove('hidden');

                table = document.getElementById('winnerTable');
                table.classList.add('hidden');

                winnerWasAsked = false;
            }
        }
    })
    .catch(error => console.error('Fehler:', error));
}

function backToMenu() {
    fetch('/api/admin/restartGameChoice', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: "reset" })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.message) {
            if (data.message === "thanks") {
                dealerHand = document.getElementById('dealerHand');
                dealerHand.classList.remove('hidden');

                currentPlayer = document.getElementById('currentPlayer');
                currentPlayer.classList.remove('hidden');

                table = document.getElementById('winnerTable');
                table.classList.add('hidden');

                winnerWasAsked = false;

                gameStarted = false;

                game = document.getElementById('adminGame');
                game.classList.add('hidden');

                adminPanel = document.getElementById('adminPanel');
                adminPanel.classList.remove('hidden');
            }
        }
    })
    .catch(error => console.error('Fehler:', error));
}
