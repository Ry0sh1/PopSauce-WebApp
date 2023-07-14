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

//Send Messages to server
function sendMessage(){
    const content = document.getElementById('chat-input').value;
    document.getElementById('chat-input').value = '';
    stompClient.send("/app/game.chat",
        {},
        JSON.stringify({gameCode:code,sender:username,content:content,messageType:'CHAT'})
    );
}


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
        let trueInput = inputElement.value;
        let result = currentPicture.rightGuess.toLowerCase().split(',');
        let input = trueInput.toLowerCase();
        inputElement.value = '';
        for (let i = 0; i < result.length;i++){
            if (input === result[i]){
                stompClient.send("/app/game.addPoints",
                    {},
                    JSON.stringify({gameCode:code,sender:username,content:10,messageType:'POINTS'})
                );
                inputLabelElement.classList.add('right-answer');
                goButton.classList.add('invisible');
            }
            else {
                stompClient.send("/app/game.wrongAnswer",
                    {},
                    JSON.stringify({gameCode:code,sender:username,content:trueInput,messageType:'WRONG_ANSWER'})
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
    stompClient.send("/app/game.join",
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
            newChatMessage(message);
            players.push({username:message.sender,points:0});
            newPlayer(message);
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
            displayWrongAnswer(message);
        }
        if (message.messageType === 'PICTURE'){
            currentPicture = message.content;
            refreshPicture();
        }
        if (message.messageType === 'POINTS'){
            refreshPoints(message);
        }
        if (message.messageType === 'CHAT'){
            newChatMessage(message);
        }
    }
}


//Updating Points and Player on Change
function refreshPoints(message){
    const pointsElement = document.getElementById(`player-field-points-${message.sender}`);
    pointsElement.innerText = (parseInt(pointsElement.innerText) + message.content);
}
function displayWrongAnswer(message){
    const wrongAnswerElement = document.getElementById(`player-field-wrongAnswer-${message.sender}`);
    wrongAnswerElement.innerText = message.content;
}
function newPlayer(message){
    const container = document.getElementById('all-player');
    const newContainer = document.createElement('div');
    newContainer.classList.add('player-field');
    const usernameElement = document.createElement('p');
    const pointsElement = document.createElement('p');
    const wrongAnswerElement = document.createElement('p');
    usernameElement.id = `player-field-username-${message.sender}`;
    pointsElement.id = `player-field-points-${message.sender}`;
    wrongAnswerElement.id = `player-field-wrongAnswer-${message.sender}`;
    usernameElement.innerText = `${message.sender}: `;
    pointsElement.innerText = "0";
    wrongAnswerElement.innerText = 'Test';
    newContainer.appendChild(usernameElement);
    newContainer.appendChild(pointsElement);
    newContainer.appendChild(wrongAnswerElement);
    container.appendChild(newContainer);
}
function newChatMessage(message){
    const chat = document.getElementById('chat-list');
    const newMessage = document.createElement('li');
    if (message.messageType === 'JOIN'){
        newMessage.innerText = `${message.sender} joined the Game!`;
    }else {
        newMessage.innerText = `${message.sender}: ${message.content}`;
    }
    chat.appendChild(newMessage);
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