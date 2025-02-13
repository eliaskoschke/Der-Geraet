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
        } else {
            console.error("Elemente 'passwordLine' oder 'start' nicht gefunden");
        }
        let password = '';
        inputs.forEach(input => {
            password += input.value;
        });
        console.log("Password entered:", password);
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
        //Aufrufen wenn Passwort Falsch
    }

    function rightPassword() {
        //Aufrufen wenn Passwort richtig
    }

    window.onload = function() {
        isUserLoggedIn();
    
        var dots = document.getElementById('dots');
        if (!dots) {
            console.error("Element 'dots' nicht gefunden");
            return; // Beende die Funktion, wenn das Element nicht gefunden wird
        }
        var dotCount = 0;
    
        setInterval(function() {
            dotCount = (dotCount + 1) % 4;
            dots.textContent = '.'.repeat(dotCount);
        }, 500);
    };