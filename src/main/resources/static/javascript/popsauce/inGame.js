let stompClient = null;

let currentPicture;
const timerElement = document.getElementById('timer');
let players = [];
let username = localStorage.getItem('username');
const code = window.location.href.slice(window.location.href.length-4,window.location.href.length);
let host = false;
let isShowingResult;
let timer;
let alreadyGuessedRight = false;

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
    stompClient.send("/app/game.chat/"+code,
        {},
        JSON.stringify({gameCode:code,sender:username,content:content,messageType:'CHAT'})
    );
}


//Only for Hosts to start the game!
function setStart(){
    stompClient.send("/app/game.start/"+code,
        {},
        JSON.stringify({gameCode:code,sender:username,messageType:'START'})
    );
}


//Game gets Started
function start(){

    resetPoints();

    //Show Playground
    inputElement.classList.remove('invisible');
    document.getElementById('timer').classList.remove('invisible');
    document.getElementById('picture-here').classList.remove("invisible");
    document.getElementById('guess-container').classList.remove("invisible");
    document.getElementById('guess-field').classList.remove("invisible");

    //Remove Start Button
    document.getElementById('field').classList.add('invisible');
    document.getElementById('start-button').classList.add("invisible");
    document.getElementById('waiting').classList.add("invisible");
    document.getElementById("play-again-button").classList.add("invisible");
    document.getElementById("winner").classList.add("invisible");
    document.getElementById("play-again-button").classList.add("invisible");
    document.getElementById("play-again-button").disabled = true;
    document.getElementById("start-button").disabled = true;

    refreshPicture();
}
function showResult(){
    document.getElementById('question').innerText = currentPicture.rightGuess.split(',')[0];
}
function hideResult(){
    document.getElementById('question').innerText = "Where is this picture from?";
    players.forEach(player=>{
        console.log(player.username);
        let wrongAnswerElement = document.getElementById(`player-field-wrongAnswer-${player.username}`);
        wrongAnswerElement.innerText = '';
    });
    alreadyGuessedRight = false;
    document.getElementById('input-field').classList.remove("invisible");
}
function rightAnswer(){
    if (!isShowingResult){
        let trueInput = inputElement.value;
        let result = currentPicture.rightGuess.toLowerCase().split(',');
        let input = trueInput.toLowerCase();
        inputElement.value = '';
        for (let i = 0; i < result.length;i++){
            if (input === result[i]){
                stompClient.send("/app/game.addPoints/"+code,
                    {},
                    JSON.stringify({gameCode:code,sender:username,content:10,messageType:'POINTS'})
                );
                document.getElementById('input-field').classList.add("invisible");
                alreadyGuessedRight = true;
                return;
            }
        }
        stompClient.send("/app/game.wrongAnswer/"+code,
            {},
            JSON.stringify({gameCode:code,sender:username,content:trueInput,messageType:'WRONG_ANSWER'})
        );
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
    }else if (timer-resultTime === -1){
        isShowingResult = true;
        showResult();
        timerElement.innerText = timer;
    }else {
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
    stompClient.subscribe("/start-game/game/"+code, onMessageReceived);

    stompClient.send("/app/game.join/"+code,
        {},
        JSON.stringify({gameCode:code,sender:username,messageType:'JOIN'})
    );

    fetchPlayers();
    fetchStatus();
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
            refreshPoints(message);
        }
        if (message.messageType === 'TIME'){
            timer = parseInt(message.content);
            if (timer === guessTime+resultTime){
                isShowingResult = false;
            }
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
            hideResult();
            refreshPicture();
        }
        if (message.messageType === 'POINTS'){
            refreshPoints(message);
        }
        if (message.messageType === 'CHAT'){
            newChatMessage(message);
        }
        if (message.messageType === 'LEAVE'){
            removePlayer(message);
        }
        if (message.messageType === 'END'){
            endGame(message);
        }
        if (message.messageType === 'PLAY_AGAIN'){
            fetchTime();
            fetchCurrentPicture();
            start(); //Make sure to start after both got fetched
        }
    }
}

//Game End
function endGame(message){
    //Hide Playground
    inputElement.classList.add('invisible');
    document.getElementById('timer').classList.add('invisible');
    document.getElementById('picture-here').classList.add("invisible");
    document.getElementById('guess-container').classList.add("invisible");

    //Show Winner
    let winnerField = document.getElementById("winner");
    document.getElementById('field').classList.remove('invisible');
    winnerField.classList.remove("invisible");
    winnerField.innerText = `${message.sender} won the Game`;
    let startButton = document.getElementById("play-again-button");
    startButton.classList.remove("invisible");
    startButton.disabled = false;
}
function playAgain(){
    stompClient.send("/app/game.playAgain/"+code,
        {},
        JSON.stringify({gameCode:code,sender:username,messageType:'PLAY_AGAIN'})
    );
}
function resetPoints(){
    players.forEach(player =>{
        document.getElementById(`player-field-points-${player.username}`).innerText = "0";
    })
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
    if (document.getElementById(`player-field-${message.sender}`)==null){
        const container = document.getElementById('all-player');
        const newContainer = document.createElement('div');
        newContainer.classList.add('player-field');
        newContainer.classList.add('flex');
        newContainer.id = `player-field-${message.sender}`;
        const usernameElement = document.createElement('p');
        const pointsElement = document.createElement('p');
        const wrongAnswerElement = document.createElement('p');
        usernameElement.id = `player-field-username-${message.sender}`;
        pointsElement.id = `player-field-points-${message.sender}`;
        wrongAnswerElement.id = `player-field-wrongAnswer-${message.sender}`;
        usernameElement.innerText = `${message.sender}: `;
        if (message.points === undefined){
            pointsElement.innerText = "0";
        }else {
            pointsElement.innerText = message.points;
        }
        newContainer.appendChild(usernameElement);
        newContainer.appendChild(pointsElement);
        newContainer.appendChild(wrongAnswerElement);
        container.appendChild(newContainer);
    }
}
function newChatMessage(message){
    const chat = document.getElementById('chat-list');
    const newMessage = document.createElement('p');
    if (message.messageType === 'JOIN'){
        newMessage.innerText = `${message.sender} joined the Game!`;
    }else if (message.messageType === 'LEAVE'){
        newMessage.innerText = `${message.sender} left the Game!`;
    }
    else {
        newMessage.innerText = `${message.sender}: ${message.content}`;
    }
    chat.appendChild(newMessage);
}
function removePlayer(message){
    document.getElementById(`player-field-username-${message.sender}`).remove();
    document.getElementById(`player-field-points-${message.sender}`).remove();
    document.getElementById(`player-field-wrongAnswer-${message.sender}`).remove();
    document.getElementById(`player-field-${message.sender}`).remove();
    newChatMessage(message);
    for (let i = 0;i < players.length;i++){
        if (players[i].username === message.sender){
            players.splice(i,1);
            break;
        }
    }
}

//Only for player who joins after the games started!!!
function fetchTime(){
    fetch("/popsauce/get-current-timer/"+code)
        .then(response=>response.text())
        .then(data=>{
            timer = parseInt(data);
        })
}
function fetchPlayers(){
    fetch("/popsauce/getAllPlayer/" + code)
        .then(response=>response.json())
        .then(data => {
            players = data;
            players.forEach(player=>{
                let message = {sender:player.username,points: player.points};
                newPlayer(message);
            })
        });
}
function fetchStatus(){
    fetch("/popsauce/is-started/" + code)
        .then(response=>response.text())
        .then(data => {
            if (data==='true'){
                fetchTime();
                fetchCurrentPicture();
                start();
            }
        });
}
function fetchCurrentPicture(){
    fetch("/popsauce/get-current-picture/"+code)
        .then(response=>response.json())
        .then(data=>{
            currentPicture = data;
        })
}