function leave() {
    user = null;
    ingame = false;
    window.location.href = 'index.html';
    // Einbindung dass Spieler / Sitzplatz (user) frei ist.
}

var reloaded;
window.onbeforeunload = function() {
    reloaded = true;
    localStorage.setItem(reloaded);
    localStorage.setItem(user)
};