document.getElementById('getPicture').addEventListener('click', function() {
    fetch('/picture')
        .then(response => response.arrayBuffer())  // Convert response to ArrayBuffer
        .then(buffer => {
            let uint8Array = new Uint8Array(buffer);
            let blob = new Blob([uint8Array], { type: 'image/jpg' });
            let imageUrl = URL.createObjectURL(blob);
            let img = document.createElement('img');
            img.classList.add("image");
            img.src = imageUrl;
            let container = document.getElementById('picture-here');
            container.innerHTML = '';  // Clear existing content
            container.appendChild(img);
        })
        .catch(error => {
            console.log('Fehler beim Abrufen des Bildes:', error);
        });
});
