window.onload = function isGeraetInPlace() {
    fetch('/api/isConnected', {
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
        if (data.message === "false") {
            console.log('Der Gerät befindet sich im freien Modus');
            document.getElementById('admin').removeAttribute('disabled');
            const statusText = document.getElementById('deactiveText');
            statusText.classList.add('hidden');
            statusText.textContent = 'Der Gerät befindet sich im freien Modus';
        } else {
            console.log('Der Gerät befindet sich im Tisch Modus');
            document.getElementById('admin').setAttribute('disabled', 'true');
            const statusText = document.getElementById('deactiveText');
            statusText.classList.remove('hidden');
            statusText.textContent = 'Derzeit nicht möglich da der Gerät sich im Tisch befindet.';
        }
    })
    .catch(error => console.error('Fehler:', error));
}