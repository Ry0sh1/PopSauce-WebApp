const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');
const pointContainer = document.getElementById('point-container');
const points = document.getElementById('points');
const highScore = document.getElementById('high-score');
const playButton = document.getElementById('play');
const spaceToStart = document.getElementById('space-to-start');
const loseScreen = document.getElementById('lose');
const gameTitle = document.getElementById('game-title');
let running = false;
let allowGameToStart = false;

highScore.innerText = localStorage.getItem("highScore");
if (localStorage.getItem("highScore") == null){
    highScore.innerText = `0`;
}

let player = null;

const settings= {
    playerX: 50,
    playerWidth: 30,
    playerHeight: 30,
    playerJumpForce: 10,
    playerGravity: 0.8,
    spaceBetweenPipes: 150,
    pipeWidth: 50,
    pipeSpeed: 5,
}

let obstacles = [];

function update() {
    obstacles.forEach(obstacles => {
        obstacles.x = obstacles.x - settings.pipeSpeed
    })

    player.y += player.dy;
    player.dy += player.gravity;

    if (player.y + player.height > canvas.height) {
        player.y = canvas.height - player.height;
        lose();
    }

    obstacles.forEach(obstacle => {
        if (player.x < obstacle.x + obstacle.width &&
            player.x + player.width > obstacle.x &&
            player.y < obstacle.y + obstacle.height &&
            player.y + player.height > obstacle.y) {
            lose();
        }
    });

    if (obstacles[0].x <= 0 - settings.pipeWidth){
        obstacles[0].x = canvas.width;
        obstacles[1].x = canvas.width;
        obstacles[0].height = Math.floor(Math.random() * (canvas.height - settings.spaceBetweenPipes));
        obstacles[1].y = obstacles[0].height + settings.spaceBetweenPipes;
        obstacles[1].height = canvas.height - obstacles[1].y;

        let currentPoints = parseInt(points.innerText) + 1;
        if (currentPoints >= parseInt(highScore.innerText)){
            highScore.innerText = `${currentPoints}`;
        }
        points.innerText = `${currentPoints}`;

    }

    if (running){
        draw();
        requestAnimationFrame(update);
    }
}
function lose(){
    running = false;
    allowGameToStart = false;
    localStorage.setItem("highScore",`${parseInt(highScore.innerText)}`);
    loseScreen.classList.remove('hidden');
    playButton.classList.remove('hidden');
}
function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = 'blue';
    ctx.fillRect(player.x, player.y, player.width, player.height);

    ctx.fillStyle = 'green';
    obstacles.forEach(obstacle => {
        ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    });
}
playButton.addEventListener('click', e => {
    obstacles = getSleepObstacles();
    player = getSleepPlayer();
    canvas.classList.remove('hidden');
    pointContainer.classList.remove('hidden');
    spaceToStart.classList.remove('hidden');
    gameTitle.classList.add('hidden');
    loseScreen.classList.add('hidden');
    playButton.classList.add('hidden');
    allowGameToStart = true;
    points.innerText = '0';
    draw();
})
document.addEventListener('keypress', (e) => {
    if (allowGameToStart){
        if (!running){
            if (e.key === ' '){
                jump(true);
            }
        }else {
            if (e.key === ' '){
                jump(false);
            }
        }
    }
});
function jump(forStart){
    if (forStart){
        spaceToStart.classList.add('hidden');
        running = true;
        update();
    }
    player.dy = -player.jumpForce;
}

function getSleepObstacles(){
    return [
        {
            x: (canvas.width + settings.pipeWidth),
            y: 0,
            width: settings.pipeWidth,
            height: ((canvas.height - settings.spaceBetweenPipes) / 2)},
        {
            x: (canvas.width + settings.pipeWidth),
            y: (((canvas.height - settings.spaceBetweenPipes) / 2) + settings.spaceBetweenPipes),
            width: settings.pipeWidth,
            height: ((canvas.height - settings.spaceBetweenPipes) / 2)
        },
    ]
}
function getSleepPlayer(){
    return {
        x: settings.playerX,
        y: canvas.height / 2 - settings.playerHeight,
        width: settings.playerWidth,
        height: settings.playerHeight,
        dy: 0,
        jumpForce: settings.playerJumpForce,
        gravity: settings.playerGravity
    }
}