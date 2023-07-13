let stompClient = null;

let currentPicture;
const h1Element = document.getElementById('anime');
const timerElement = document.getElementById('timer');
let players = [];
let username = localStorage.getItem('username');
const code = window.location.href.slice(window.location.href.length-4,window.location.href.length);
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

//Only for Hosts to start the game!
function setStart(){
    stompClient.send("/app/game.start",
        {},
        JSON.stringify({gameCode:code,sender:username,messageType:'START'})
    );
}


//Game gets Started
function start(){

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
    if (!isShowingResult){
        let result = currentPicture.rightGuess.toLowerCase().split(',');
        let input = inputElement.value.toLowerCase();
        inputElement.value = '';
        for (let i = 0; i < result.length;i++){
            if (input === result[i]){
                //TODO replace fetch on "/add-points/"
                inputLabelElement.classList.add('right-answer');
                goButton.classList.add('invisible');
            }
            else {
                stompClient.send("/app/game.wrongAnswer",
                    {},
                    JSON.stringify({gameCode:code,sender:username,content:input,messageType:'WRONG_ANSWER'})
                );
            }
        }
    }
}
function refreshPicture() {
    //Display Current Picture
    let container = document.getElementById('picture-here');
    container.innerHTML = '';  // Clear existing content

    let base64Image = currentPicture.content;
    let byteCharacters = atob(base64Image);
    let byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    let uint8Array = new Uint8Array(byteNumbers);
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
            hideResult();
            refreshPicture();
        }
    }else {
        if (timer-resultTime===0){
            isShowingResult = true;
            showResult();
        }
        timerElement.innerText = "" + (timer-resultTime);
    }
}


//Start WebSocket Connection
function connect(){
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);
}
function onError(){
    console.log("Error trying to connect to a WebSocket")
}
function onConnected(){
    stompClient.subscribe("/start-game/game", onMessageReceived);
    stompClient.send("/app/game.chat",
        {},
        JSON.stringify({gameCode:code,sender:username,messageType:'JOIN'})
    );
    fetchPlayers();
    fetchTime();
    fetchStatus();
    fetchCurrentPicture();
}

connect();


//Listening on WebSocket
function onMessageReceived(payload){
    let message = (JSON.parse(payload.body));
    console.log("Message Received");
    if (message.gameCode === code){
        if (message.messageType === 'JOIN'){
            console.log(message.sender + ' Joined');
            players.push({username:message.sender,points:0});
            refreshPoints();
        }
        if (message.messageType === 'TIME'){
            timer = parseInt(message.content);
            refreshTimer();
        }
        if (message.messageType === 'START'){
            start();
        }
        if (message.messageType === 'WRONG_ANSWER'){
            //TODO
        }
        if (message.messageType === 'PICTURE'){
            currentPicture = message.content;
            refreshPicture();
        }
    }
}


//Updating Points and Player on Change
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


//Only for player who joins after the games started!!!
function fetchTime(){
    fetch("/get-current-timer/"+code)
        .then(response=>response.text())
        .then(data=>{
            timer = parseInt(data);
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
            if (data==='true'){
                start();
            }
        });
}
function fetchCurrentPicture(){
    fetch("/get-current-picture/"+code)
        .then(response=>response.json())
        .then(data=>{
            currentPicture = data;
        })
}