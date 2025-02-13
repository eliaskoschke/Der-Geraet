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