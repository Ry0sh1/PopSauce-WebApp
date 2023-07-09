let currentPicture;
const h1Element = document.getElementById('anime');
const timerElement = document.getElementById('timer');
let players;
let username = localStorage.getItem('username');
const code = window.location.href.slice(window.location.href.length-4,window.location.href.length);
let currentPictureIndex;
let started = false;
let host;
let alreadySent = false;

//Playground
const inputElement = document.getElementById('input');
const inputLabelElement = document.getElementById('input-label');
const goButton = document.getElementById('go');

//Settings
const seconds = parseInt(document.getElementById('set-guess-timer').innerText);
const resultTime = parseInt(document.getElementById('set-result-timer').innerText);

function start(){
    started = true;
    //Show Playground
    inputElement.classList.remove('invisible');
    inputLabelElement.classList.remove('invisible');
    goButton.classList.remove('invisible');

    //Remove Start Button
    document.getElementById('start-button').remove();
    document.getElementById('waiting').remove();

    //Start Showing Picture
    setInterval(changeTime,1000);
    setInterval(nextPicture,1000*(seconds+resultTime));
    nextPicture();
}

function showResult(){
    h1Element.innerText = currentPicture.rightGuess.split(',')[0];
    goButton.classList.add('invisible');
    currentPictureIndex++;
}

function hideResult(){
    h1Element.innerText = '';
}

function displayCurrentPicture() {
    let container = document.getElementById('picture-here');
    container.innerHTML = '';  // Clear existing content
    let uint8Array = new Uint8Array(currentPicture.content);
    let blob = new Blob([uint8Array], { type: 'image/jpg' });
    let imageUrl = URL.createObjectURL(blob);
    let img = document.createElement('img');
    img.classList.add("image");
    img.src = imageUrl;
    container.appendChild(img);
}

function nextPicture() {
    fetch("/next-picture/"+code+"/"+currentPictureIndex)
        .then(response => response.json())
        .then(data => {
            currentPicture = data;
            goButton.classList.remove('invisible');
            inputLabelElement.classList.remove('right-answer');
            hideResult();
            setTime(seconds);
            displayCurrentPicture();
        })
}

function changeTime(){
    if (parseInt(getTime())===0){
        showResult();
        setTime(resultTime);
    }
    timerElement.innerText = parseInt(getTime())-1;
    if (host){
        fetch("/set-current-timer/"+code+"/"+timerElement.innerText);
    }
}

function getTime(){
    return timerElement.innerText;
}

function setTime(time){
    timerElement.innerText = time;
}

function rightAnswer(){
    let result = currentPicture.rightGuess.toLowerCase().split(',');
    let input = inputElement.value.toLowerCase();
    inputElement.value = '';
    for (let i = 0; i < result.length;i++){
        if (input === result[i]){
            fetch("/add-points/" + username);
            inputLabelElement.classList.add('right-answer');
            goButton.classList.add('invisible');
        }
    }
}

function refreshPoints(){
    const container = document.getElementById('all-player');
    for (let i = 0;i < players.length;i++){
        if (document.getElementById(players[i].username) == null){
            let newElement = document.createElement('p');
            newElement.classList.add('player');
            newElement.innerText = `${players[i].username} Points: ${players[i].points}`;
            newElement.id = players[i].username;
            container.appendChild(newElement);
        }else {
            document.getElementById(players[i].username).innerText = `${players[i].username} Points: ${players[i].points}`;
        }
    }
}

function getPlayers(){
    fetch("/getAllPlayer/" + code)
        .then(response=>response.json())
        .then(data => {
            players = data;
            refreshPoints();
        });
}

function getStatus(){
    fetch("/is-started/" + code)
        .then(response=>response.text())
        .then(data => {
            if (data==='false'){
                started = false;
            }else if (data==='true'){
                started = true;
                start();
            }
        });
}

function updatingData(){
    getPlayers();
    if (!alreadySent){
        if (host){
            if (started === true){
                fetch("/started/"+code)
                    .then(alreadySent=true);
            }
        }else {
            if (started===false){
                getStatus();
            }
        }
    }
}

function getCurrentPictureIndex(){
    fetch("/get-current-picture-index/" + code)
        .then(response=>response.text())
        .then(data => {
            currentPictureIndex = parseInt(data);
        });
}

//On Join
fetch("/get-host/"+code)
    .then(response=>response.text())
    .then(data=>{
        if (username !== data){
            host = false;
            document.getElementById('start-button').classList.add('invisible');
            document.getElementById('waiting').classList.remove('invisible');
            fetch("/is-started/" + code)
                .then(response=>response.text())
                .then(data => {
                    if (data==='true'){
                        fetch("/get-current-timer/"+code)
                            .then(response=>response.text())
                            .then(data=>setTime(parseInt(data)))
                    }
                });
        }else {
            host = true;
        }
        getCurrentPictureIndex();
        setInterval(updatingData,10); //Put This number up when server is lagging!!!!
    })