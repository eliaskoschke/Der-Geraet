document.addEventListener('DOMContentLoaded', function checkGeraetStatus() {
    fetch('/api/isConnected', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            console.log('Der Gerät befindet sich im freien Modus');
            document.getElementById('admin').removeAttribute('disabled');
            const statusText = document.getElementById('deactiveText');
            statusText.textContent = 'Der Gerät befindet sich im freien Modus';
            statusText.classList.add('active');
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.message === "false") {
            console.log('Der Gerät befindet sich im freien Modus');
            document.getElementById('admin').removeAttribute('disabled');
            const statusText = document.getElementById('deactiveText');
            statusText.textContent = 'Der Gerät befindet sich im freien Modus';
            statusText.classList.add('active');
        } else {
            console.log('Der Gerät befindet sich im Tisch Modus');
            document.getElementById('admin').setAttribute('disabled', 'true');
            const statusText = document.getElementById('deactiveText');
            statusText.textContent = 'Derzeit nicht möglich da der Gerät sich im Tisch befindet.';
            statusText.classList.remove('active');
        }
    })
    .catch(error => console.error('Fehler:', error));
});