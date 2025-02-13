function leave() {
    console.log(user + " left the game.");
    window.location.href = 'index.html';
    fetch('/api/user/playerLeftTheTable', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ message: user })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Netzwerkantwort war nicht ok');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.message) {

                    } else {
                        alert('Nachricht gesendet, aber keine Nachricht in der Antwort gefunden.');
                    }
                })
                .catch(
                error => console.error('Fehler:', error)
                );
    user = null;
    ingame = false;
}

var reloaded;
window.onbeforeunload = function() {
    reloaded = true;
    localStorage.setItem(reloaded);
    localStorage.setItem(user)
};