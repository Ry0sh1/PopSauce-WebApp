document.getElementById('getText').addEventListener('click', function() {
    fetch('/text')
        .then(response => response.text())
        .then(data => {
            document.getElementById('text-here').textContent = data;
        })
        .catch(error => {
            console.log('Fehler beim Abrufen des Textes:', error);
        });
});