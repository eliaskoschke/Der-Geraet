function get(endpoint) {
    console.log(endpoint + "Wurde abgefragt.")
    return fetch('/api/' + endpoint, {
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
            if(data.message === 'true') {
                return true;
            } else if(data.message === 'false') {
                return false;
            } else {
                return data.message;
            }
        }
        return data;
    })
    .catch(error => console.error('Fehler:', error));
}
    

function post(endpoint, messageString) {
    console.log(endpoint + "Wurde abgefragt.")
    return fetch('/api/' + endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: messageString })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        return response.json();
    })
    .then(data => {
        if(data && data.message) {
            if(data.message === 'true') {
                return true;
            } else if(data.message === 'false') {
                return false;
            } else {
                return data.message;
            }
        }
        return data;
    })
    .catch(
    error => console.error('Fehler:', error)
    );
}

