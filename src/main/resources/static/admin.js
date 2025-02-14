document.addEventListener('DOMContentLoaded', (event) => {
    const inputs = document.querySelectorAll('.psw-in');

    // Funktion zum Leeren der Eingabefelder beim Laden der Seite
    function clearInputs() {
        inputs.forEach(input => {
            input.value = '';
        });
    }

    function sendPassword() {
        var passwordLine = document.getElementById('passwordLine');
        var wait = document.getElementById('start');
        if (passwordLine && wait) {
            passwordLine.classList.add('hidden');
            wait.classList.remove('hidden');
        }
        let password = '';
        inputs.forEach(input => {
            password += input.value;
        });
        console.log("Password entered:", password);
        fetch('/api/admin/sendPassword', {
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
                            if (data && data.message) {
                                if(data.message === "true"){
                                    rightPassword();
                                } else{
                                    wrongPassword();
                                }
                            } else {
                                alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
                            }
                        })
                        .catch(
                        //error => console.error('Fehler:', error)
                        );
    }

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

    window.onload = function() {
    
        var dots = document.getElementById('dots');
        if (!dots) {
            console.error("Element 'dots' nicht gefunden");
            return;
        }
        var dotCount = 0;
    
        setInterval(function() {
            dotCount = (dotCount + 1) % 4;
            dots.textContent = '.'.repeat(dotCount);
        }, 500);
    };


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
                var test = document.getElementById('playerCount');
                test.textContent = data.message + "/12";
            } else {
                console.error('Keine Nachricht in der Antwort gefunden.');
            }
        })
        .catch(error => console.error('Fehler:', error));
    }
}

setInterval(pingLobby, 1000);