let pictures = [];
let currentIndex = 0;

function displayCurrentPicture() {
    let container = document.getElementById('picture-here');
    container.innerHTML = '';  // Clear existing content

    let buffer = pictures[currentIndex];
    let uint8Array = new Uint8Array(buffer);
    let blob = new Blob([uint8Array], { type: 'image/jpg' });
    let imageUrl = URL.createObjectURL(blob);
    let img = document.createElement('img');
    img.classList.add("image");
    img.src = imageUrl;
    container.appendChild(img);
}

function fetchPictures() {
    fetch('/picture')
        .then(response => response.json())
        .then(data => {
            pictures = data;
            if (pictures.length === 0) {
                console.log('No pictures available.');
                return;
            }
            console.log(pictures.length);
        })
        .catch(error => {
            console.log('Fehler beim Abrufen der Bilder:', error);
        });
}

document.getElementById('getPicture').addEventListener('click', function() {
    if (pictures.length === 0) {
        console.log('No pictures available.');
        return;
    }
    currentIndex = (currentIndex + 1) % pictures.length;
    console.log(pictures.length);
    console.log(currentIndex);
    displayCurrentPicture();
});
