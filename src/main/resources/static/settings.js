//kick all players()

//admin/adminPanel/rotateStepper - POST
// id in msg -> user ID
// id 0 -> Ursprung

// .../activateCardMotor -> POST
// message -> leer


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

function back() {
    window.location.replace('admin.html?loggedIn=true');
}