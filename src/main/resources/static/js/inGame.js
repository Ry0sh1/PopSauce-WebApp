let currentPicture;
let nextPicture;
const h1Element = document.getElementById('anime');
const timerElement = document.getElementById('timer');
let players;
let username = localStorage.getItem('username');
const code = window.location.href.slice(window.location.href.length-4,window.location.href.length);
let started = false;
let alreadySent = false;
let host = false;
let timer;
let isShowingResult;

//Playground
const inputElement = document.getElementById('input');
const inputLabelElement = document.getElementById('input-label');
const goButton = document.getElementById('go');

//Settings
const guessTime = parseInt(document.getElementById('set-guess-timer').innerText);
const resultTime = parseInt(document.getElementById('set-result-timer').innerText);

fetchStatus();
fetchCurrentPicture();
fetchNextPicture();

function start(){
    started = true;
    //Show Playground
    inputElement.classList.remove('invisible');
    inputLabelElement.classList.remove('invisible');
    goButton.classList.remove('invisible');
    document.getElementById('timer').classList.remove('invisible');

    //Remove Start Button
    document.getElementById('start-button').remove();
    document.getElementById('waiting').remove();

    refreshPicture();
}

function showResult(){
    h1Element.innerText = currentPicture.rightGuess.split(',')[0];
    goButton.classList.add('invisible');
}

function hideResult(){
    h1Element.innerText = "";
    goButton.classList.remove('invisible');
    inputLabelElement.classList.remove('right-answer');
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

function refreshPicture() {
    //Display Current Picture
    let container = document.getElementById('picture-here');
    container.innerHTML = '';  // Clear existing content
    let uint8Array = new Uint8Array(currentPicture.content);
    let blob = new Blob([uint8Array], {type: 'image/jpg'});
    let imageUrl = URL.createObjectURL(blob);
    let img = document.createElement('img');
    img.classList.add("image");
    img.src = imageUrl;
    container.appendChild(img);
}

function refreshTimer(){
    if (isShowingResult){
        timerElement.innerText = timer;
        if (timer===0){
            isShowingResult = false;
            currentPicture = nextPicture;
            hideResult();
            refreshPicture();
        }
    }else {
        if (timer-resultTime===0){
            isShowingResult = true;
            fetchNextPicture();
            showResult();
        }
        timerElement.innerText = "" + (timer-resultTime);
    }
}

function fetchCurrentPicture(){
    fetch("/get-current-picture/"+code)
        .then(response=>response.json())
        .then(data=>{
            currentPicture = data;
        })
}

function fetchNextPicture(){
    fetch("/get-next-picture/"+code)
        .then(response=>response.json())
        .then(data=>{
            nextPicture = data;
        })
}

function fetchPlayers(){
    fetch("/getAllPlayer/" + code)
        .then(response=>response.json())
        .then(data => {
            players = data;
            refreshPoints();
        });
}

function fetchStatus(){
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

function fetchTime(){
    fetch("/get-current-timer/"+code)
        .then(response=>response.text())
        .then(data=>{
            timer = parseInt(data);
        })
}

function updatingData(){
    fetchPlayers();
    fetchTime();
    if (!alreadySent){
        if (host){
            if (started === true){
                fetch("/started/"+code)
                    .then(alreadySent=true);
            }
        }else {
            if (started===false){
                fetchStatus();
            }
        }
    }
    refreshPoints();
    refreshTimer();
}


//On Join
fetch("/get-host/"+code)
    .then(response=>response.text())
    .then(data=>{
        if (username !== data){
            document.getElementById('start-button').classList.add('invisible');
            document.getElementById('waiting').classList.remove('invisible');
            fetch("/is-started/" + code)
                .then(response=>response.text())
                .then(data => {
                    if (data==='true'){
                        start();
                    }
                });
        }else {
            host = true;
        }
        setInterval(updatingData,10);
    })


//TODO: Fix Bug -5
//TODO: Fix Bug Join
//TODO: Fix Bug p1_0.game_id existiert nicht