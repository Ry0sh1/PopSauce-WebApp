const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');

const playButton = document.getElementById('play');
const gameTitle = document.getElementById('game-title');
const pointContainer = document.getElementById('point-container');
const highScore = document.getElementById('high-score');
const loseText = document.getElementById('lose');
const points = document.getElementById('points');

if (localStorage.getItem('snake-highScore') === null){
    localStorage.setItem('snake-highScore','0');
}else {
    highScore.innerText = localStorage.getItem('snake-highScore');
}

let running = true;
const gridSize = 25;
const fps = 12;
const player = {
    width: gridSize,
    x: gridSize,
    y: gridSize,
    direction: 'd',
    tailLength: 3,
    speed: gridSize,
}
const keys = ['w', 'a', 's', 'd'];

let parts = [];
let apple = {}

let lastTime = 0;
const fpsInterval = 1000 / fps;
function gameLoop(currentTime){
    if (!running) return;
    requestAnimationFrame(gameLoop)
    const elapsed = currentTime - lastTime;
    if (elapsed > fpsInterval) {
        lastTime = currentTime - (elapsed % fpsInterval);
        update();
        draw();
    }
}

function update(){
    if (parts.length === player.tailLength){
        parts.shift();
    }
    parts.push([player.x,player.y]);
    if (player.direction === keys[0]){
        player.y -= player.speed;
    }
    if (player.direction === keys[1]){
        player.x -= player.speed;
    }
    if (player.direction === keys[2]){
        player.y += player.speed;
    }
    if (player.direction === keys[3]){
        player.x += player.speed;
    }
    if (player.x === apple.x && player.y === apple.y){
        generateApple();
        player.tailLength++;
        let newPoints = parseInt(points.innerText) + 1;
        if (newPoints >= parseInt(highScore.innerText)){
            highScore.innerText = `${newPoints}`;
            localStorage.setItem('snake-highScore',`${newPoints}`);
        }
        points.innerText = `${newPoints}`;
    }
    parts.forEach(part => {
        if (part[0] === player.x && part[1] === player.y){
            lose();
        }
    })
    if (player.x >= canvas.width || player.x < 0 || player.y < 0 || player.y >= canvas.height){
        lose();
    }
}
function lose(){
    running = false;
    loseText.classList.remove('hidden');
    playButton.classList.remove('hidden');
}
function generateApple(){
    let validPosition = false;
    while (!validPosition) {
        apple.x = gridSize * Math.floor(Math.random() * (canvas.width / gridSize));
        apple.y = gridSize * Math.floor(Math.random() * (canvas.height / gridSize));
        validPosition = !parts.some(part => part[0] === apple.x && part[1] === apple.y) &&
            (apple.x !== player.x || apple.y !== player.y);
    }
}

function draw(){
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    ctx.fillStyle = 'red';
    ctx.fillRect(apple.x, apple.y, gridSize, gridSize);

    ctx.fillStyle = 'rgb(22,155,0)';
    ctx.fillRect(player.x, player.y, gridSize, gridSize);
    ctx.fillStyle = 'rgba(68,227,17,0.6)'
    parts.forEach(part => {
        ctx.fillRect(part[0], part[1], gridSize, gridSize);

        ctx.strokeStyle = 'rgb(22,155,0)';
        ctx.beginPath();
        ctx.moveTo(part[0],part[1]);
        ctx.lineTo(part[0],part[1] + gridSize);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(part[0],part[1]);
        ctx.lineTo(part[0] + gridSize,part[1]);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(part[0] + gridSize, part[1]);
        ctx.lineTo(part[0]+ gridSize,part[1] + gridSize);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(part[0], part[1] + gridSize);
        ctx.lineTo(part[0]+ gridSize,part[1] + gridSize);
        ctx.stroke();
    })
}

function resetGame(){
    points.innerText = `0`;
    player.x = gridSize;
    player.y = gridSize;
    player.tailLength = 3;
    parts = [];
    apple = {};
    player.direction = 'd';
    running = true;
}

document.addEventListener('keydown', e => {
    if (keys.includes(e.key)) {
        if (Math.abs(keys.indexOf(e.key) - keys.indexOf(player.direction)) !== 2){
            player.direction = e.key;
        }
    }
})

playButton.addEventListener('click', e => {
    pointContainer.classList.remove('hidden');
    canvas.classList.remove('hidden');
    playButton.classList.add('hidden');
    gameTitle.classList.add('hidden');
    loseText.classList.add('hidden');
    resetGame();
    generateApple();
    requestAnimationFrame(gameLoop);
})
