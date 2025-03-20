function rotateStepper(id) {
    fetch('/api/admin/adminPanel/rotateStepper', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: id })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Stepper rotiert:', data);
    })
    .catch(error => console.error('Fehler beim Rotieren des Steppers:', error));
}

function activateCardMotor() {
    fetch('/api/admin/adminPanel/activateCardMotor', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: '' })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Kartenmotor aktiviert:', data);
    })
    .catch(error => console.error('Fehler beim Aktivieren des Kartenmotors:', error));
}
var menu = document.getElementById('menu');

function backVar() {
    if(!menu.classList.contains('hidden')) {
        window.location.replace('admin.html?loggedIn=true');
    } else {
        menu.classList.remove('hidden');
        let rotateStepperMenuElement = document.getElementById('rotateStepperMenu');
        let calibrateAuswerfmotor = document.getElementById('calibrateAuswerfmotor');

        rotateStepperMenuElement.classList.add('hidden');
        calibrateAuswerfmotor.classList.add('hidden');

    }
}

document.addEventListener('DOMContentLoaded', (event) => {
    const urlParams = new URLSearchParams(window.location.search);
    const loggedIn = urlParams.get('loggedIn');

    if (loggedIn === "true") {} else {window.location.href = 'admin.html?href=settings.html?loggedIn=true';}
});

function rotateStepperMenu() {
    menu.classList.add('hidden');
    
    let rotateStepperMenuElement = document.getElementById('rotateStepperMenu');
    rotateStepperMenuElement.classList.remove('hidden');
}

function calibrateAuswerfmotor() {
    menu.classList.add('hidden');
    let calibrateAuswerfmotorElement = document.getElementById('calibrateAuswerfmotor');
    calibrateAuswerfmotorElement.classList.remove('hidden');
}

function calibrateAuswerfmotorSave() {}

document.addEventListener('DOMContentLoaded', function() {
    // Geschwindigkeits-Inputs (IDs 1 und 2) überwachen
    const speedInputs = document.querySelectorAll('#calibrateAuswerfmotor input[type="number"]');
    
    speedInputs.forEach(input => {
        if (input.id === '1' || input.id === '2') {  // Nur Geschwindigkeits-Inputs
            input.addEventListener('input', function() {
                let value = parseInt(this.value);
                
                // Leere Eingabe erlauben
                if (this.value === '') return;
                
                // Wert auf 1-100 beschränken
                if (isNaN(value)) {
                    this.value = '';
                } else if (value > 100) {
                    this.value = 100;
                } else if (value < 1) {
                    this.value = 1;
                }
            });

            // Verhindert das Einfügen ungültiger Werte
            input.addEventListener('paste', function(e) {
                e.preventDefault();
                let pastedValue = parseInt(e.clipboardData.getData('text'));
                
                if (!isNaN(pastedValue)) {
                    if (pastedValue > 100) pastedValue = 100;
                    if (pastedValue < 1) pastedValue = 1;
                    this.value = pastedValue;
                }
            });
        }
    });
});


