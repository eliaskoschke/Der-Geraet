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
            document.getElementById('admin').removeAttribute('disabled');
            document.getElementById('deactiveText').classList.add('hidden');
        } else {
            document.getElementById('admin').setAttribute('disabled', 'true');
            document.getElementById('deactiveText').classList.remove('hidden');
        }
    })
    .catch(error => console.error('Fehler:', error));
}