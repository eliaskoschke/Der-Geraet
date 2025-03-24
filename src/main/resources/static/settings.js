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

function calibrateAuswerfmotorshowanddo() {
    menu.classList.add('hidden');
    let calibrateAuswerfmotorElement = document.getElementById('calibrateAuswerfmotor');
    calibrateAuswerfmotorElement.classList.remove('hidden');

    let currentDataMsg = ["vorwaertsGeschwindigkeit", "vorwaertsDauer", "pauseDauer", "rueckwaertsGeschwindigkeit", "rueckwaertsDauer"];
    let defaultDataMsg = ["standartVorwaertsGeschwindigkeit", "standartVorwaertsDauer", "standartPauseDauer", "standartRueckwaertsGeschwindigkeit", "StandartRueckwaertsDauer"];
    
    motorWerte = [];
    standartWerte = [];
    
    // Daten abrufen und in Felder einfügen
    Promise.all(currentDataMsg.map(name => getDataByName(name)))
        .then(values => {
            motorWerte = values;
            return Promise.all(defaultDataMsg.map(name => getDataByName(name)));
        })
        .then(values => {
            standartWerte = values;
            // Daten in die Felder einfügen
            fuelleFelderMitDaten();
        })
        .catch(error => {
            console.error('Fehler beim Abrufen der Daten:', error);
        });
}

function fuelleFelderMitDaten() {
    document.getElementById('1').value = motorWerte[0]; // vorwaertsGeschwindigkeit
    document.getElementById('3').value = motorWerte[1]; // vorwaertsDauer
    document.getElementById('5').value = motorWerte[2]; // pauseDauer
    document.getElementById('2').value = motorWerte[3]; // rueckwaertsGeschwindigkeit
    document.getElementById('4').value = motorWerte[4]; // rueckwaertsDauer
}

function getDataByName(name) {
    return fetch('/api/admin/adminPanel/getDatabaseValuesByName', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: name })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        return data.message;
    })
    .catch(error => {
        console.error('Fehler beim Abrufen der Daten:', error);
        return null;
    });
}


function saveDataindatabase() {
    let dataNames = ["vorwaertsGeschwindigkeit", "vorwaertsDauer", "pauseDauer", "rueckwaertsGeschwindigkeit", "rueckwaertsDauer"];
    
    let fieldIds = ['1', '3', '5', '2', '4'];
    
    for (let i = 0; i < dataNames.length; i++) {
        let value = document.getElementById(fieldIds[i]).value;
        
        if (value === "" || value === null || value === undefined) {
            value = "0";
        }
        
        let message = dataNames[i] + "-" + value;
        
        // Sende die Daten an den Server
        fetch('/api/admin/adminPanel/saveUpdateInDatabase', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: message })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Daten gespeichert:', data);
        })
        .catch(error => {
            console.error('Fehler beim Speichern der Daten:', error);
        });
    }
    
}

function resetToDefault() {
    let fieldIds = ['1', '3', '5', '2', '4'];
    
    motorWerte = standartWerte;
    fuelleFelderMitDaten();
    
}
