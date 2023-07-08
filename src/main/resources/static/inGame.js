let pictures = [];
let currentIndex = 0;
const h1Element = document.getElementById('anime');
const timerElement = document.getElementById('timer');
const player = [localStorage.getItem('username')];
let playerPoints = [];

//Playground
const inputElement = document.getElementById('input');
const inputLabelElement = document.getElementById('input-label');
const goButton = document.getElementById('go');

//Settings
const seconds = parseInt(document.getElementById('set-guess-timer').innerText);
const resultTime = parseInt(document.getElementById('set-result-timer').innerText);

function start(){
    //Show Playground
    inputElement.classList.remove('invisible');
    inputLabelElement.classList.remove('invisible');
    goButton.classList.remove('invisible');
    document.getElementById('host').classList.remove('invisible');
    document.getElementById('host').innerText = player[0] + " Points: " + playerPoints[0];

    //Remove Start Button
    document.getElementById('start-button').remove();

    //Start Showing Picture
    nextPicture();
    setInterval(changeTime,1000);
    setInterval(nextPicture,1000*(seconds+resultTime));
}

function showResult(){
    h1Element.innerText = pictures[currentIndex].rightGuess.split(',')[0];
    goButton.classList.add('invisible');
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
            shuffle(pictures);
            document.getElementById('start-button').classList.remove('invisible');
        })
        .catch(error => {
            console.log('Fehler beim Abrufen der Bilder:', error);
        });
}

function nextPicture() {
    refreshPoints();
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

function changeTime(){
    if (parseInt(getTime())===0){
        showResult();
        setTime(resultTime);
    }
    refreshPoints()
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
    let input = inputElement.value.toLowerCase();
    inputElement.value = '';
    for (let i = 0; i < result.length;i++){
        if (input === result[i]){
            fetch("/" + player[0] + "/" + 10);
            getPointsOfPlayer();
            inputLabelElement.classList.add('right-answer');
            goButton.classList.add('invisible');
        }
    }
}

function shuffle(list){
    for (let i = 0;i<list.length;i++){
        let first = list[i];
        let random = Math.floor(Math.random()*list.length);
        list[i] = list[random];
        list[random] = first;
    }
}

function refreshPoints(){
    document.getElementById('host').innerText = player[0] + " Points: " + playerPoints[0];
}

function getPointsOfPlayer(){
    for (let i = 0; i < player.length;i++){
        fetch("/get-points-of-user/" + player[i])
            .then(response=>response.text())
            .then(points => playerPoints[i] = parseInt(points));
    }
}

fetchPictures();
getPointsOfPlayer();