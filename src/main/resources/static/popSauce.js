let pictures = [];
let currentIndex = 0;
const h1Element = document.getElementById('anime');
const timerElement = document.getElementById('timer');

//Playground
const inputElement = document.getElementById('input');
const inputLabelElement = document.getElementById('input-label');
const goButton = document.getElementById('go');
const pointElement = document.getElementById('points');

//Settings
const seconds = 15;
const resultTime = 5;

function start(){
    //Show Playground
    inputElement.classList.remove('invisible');
    inputLabelElement.classList.remove('invisible');
    goButton.classList.remove('invisible');
    pointElement.classList.remove('invisible');

    //Remove Start Button
    document.getElementById('start-button').remove();

    //Start Showing Picture
    nextPicture();
    let timer = setInterval(changeTime,1000);
    let getNextPictures = setInterval(nextPicture,1000*(seconds+resultTime));
}

function showResult(){
    h1Element.innerText = pictures[currentIndex].rightGuess.split(',')[0];
}

function hideResult(){
    h1Element.innerText = '';
}

function displayCurrentPicture() {
    let container = document.getElementById('picture-here');
    container.innerHTML = '';  // Clear existing content

    let buffer = pictures[currentIndex].content;
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
            document.getElementById('start-button').classList.remove('invisible');
        })
        .catch(error => {
            console.log('Fehler beim Abrufen der Bilder:', error);
        });
}

function nextPicture() {
    if (pictures.length === 0) {
        console.log('No pictures available.');
        return;
    }
    currentIndex = (currentIndex + 1) % pictures.length;
    console.log(pictures.length);
    console.log(currentIndex);
    goButton.classList.remove('invisible');
    inputLabelElement.classList.remove('right-answer');
    hideResult();
    setTime(seconds);
    displayCurrentPicture();
}

function changeTime(time){
    if (parseInt(getTime())===0){
        showResult();
        setTime(resultTime);
    }
    timerElement.innerText = parseInt(getTime())-1;
}

function getTime(){
    return timerElement.innerText;
}

function setTime(time){
    timerElement.innerText = time;
}

function rightAnswer(){
    let result = pictures[currentIndex].rightGuess.toLowerCase().split(',');
    let input = inputElement.value;
    inputElement.value = '';
    for (let i = 0; i < result.length;i++){
        if (input === result[i]){
            pointElement.innerText = (parseInt(pointElement.innerText) + 10);
            inputLabelElement.classList.add('right-answer');
            goButton.classList.add('invisible');
        }
    }
}
fetchPictures();