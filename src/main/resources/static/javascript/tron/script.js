const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');
const playButton = document.getElementById('play');
const gameTitle = document.getElementById('game-title');
const lose = document.getElementById('lose');
const explainSetting = document.getElementById('explain-setting');

const settings = {
    playerSpeed: 5,
    playerWidth: 15,
    playerHeight: 15,
    player1Keys: ['w','a','s','d'],
    player2Keys: ['ArrowUp','ArrowLeft','ArrowDown','ArrowRight'],
    trailWidth: 15,
    trailHeight: 15,
    player1StartX: 50,
    player1StartY: 50,
    player2StartX: 535,
    player2StartY: 535,
}
const player1 = new Player(settings.player1StartX,settings.player1StartY,settings,settings.player1Keys[3],settings.player1Keys);
const player2 = new Player(settings.player2StartX,settings.player2StartY,settings,settings.player2Keys[1],settings.player2Keys);

//Event Listener
document.addEventListener('keydown', e => {
    player1.keyUp(e.key);
    player2.keyUp(e.key);
})

function update(){
    player1.move(canvas);
    player2.move(canvas);

    if (player1.isTouching(player2.trail)){
        loseScreen();
        lose.innerText = "Player 2 hat gewonnen";
    }
    else if (player2.isTouching(player1.trail)){
        loseScreen();
        lose.innerText = "Player 1 hat gewonnen";
    }
    else {
        draw();
        requestAnimationFrame(update);
    }
}
function draw(){
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    //Draw Player 1
    ctx.fillStyle = 'blue';
    ctx.fillRect(player1.x, player1.y, settings.playerWidth, settings.playerHeight);

    //Draw Player 2
    ctx.fillStyle = 'green';
    ctx.fillRect(player2.x, player2.y, settings.playerWidth, settings.playerHeight);

    ctx.fillStyle = 'rgba(43,175,255,0.5)';
    drawTrail(player1);
    ctx.fillStyle = 'rgba(57,255,43,0.5)'
    drawTrail(player2);
}

function drawTrail(player){
    let ignore = player.width / player.speed;
    if (player.trailArray.length > ignore){
        for (let i = 0; i < player.trailArray.length - ignore; i++){
            ctx.fillRect(player.trailArray[i][0],player.trailArray[i][1], settings.trailWidth, settings.trailHeight)
        }
    }
}

function loseScreen(){
    playButton.classList.remove('hidden');
    lose.classList.remove('hidden');
}

function resetCanvas(){
    player1.x = settings.player1StartX;
    player1.y = settings.player1StartY;
    player2.x = settings.player2StartX;
    player2.y = settings.player2StartY;
    player1.reset();
    player2.reset();
    player1.direction = settings.player1Keys[3];
    player2.direction = settings.player2Keys[1];
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}

playButton.addEventListener('click', e => {
    playButton.classList.add('hidden');
    gameTitle.classList.add('hidden');
    lose.classList.add('hidden');
    explainSetting.classList.add('hidden');
    canvas.classList.remove('hidden');
    resetCanvas();
    update();
})

