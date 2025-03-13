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
        rotateStepperMenuElement.classList.add('hidden');
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


