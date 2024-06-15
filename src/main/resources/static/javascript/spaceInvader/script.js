//Get Dom
const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d', { willReadFrequently: true });
const gameTitle = document.getElementById('game-title');
const levelContainer = document.getElementById('level-container');
const level = document.getElementById('level');
const highScore = document.getElementById('high-score');
const playButton = document.getElementById('play');
const lose = document.getElementById('lose');
if (localStorage.getItem('space-invader-highScore') === null){
    localStorage.setItem('space-invader-highScore','1');
}
highScore.innerText = `${localStorage.getItem('space-invader-highScore')}`;

//Invader SVG to Image
const svgData = `
    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" style="fill: rgba(16,129,49,0.82);transform: ;msFilter:;">
    <path d="M6 3h2v2H6zm2 16h3v2H8zm8-16h2v2h-2zm-3 16h3v2h-3zm7-8V9h-2V7h-2V5h-2v2h-4V5H8v2H6v2H4v2H2v8h2v-4h2v4h2v-3h8v3h2v-4h2v4h2v-8zm-10 1H8V9h2zm6 0h-2V9h2z">
</path></svg>`;
const img = new Image();
img.src = 'data:image/svg+xml;base64,' + btoa(svgData);

//Game
const settings = {
    invaderSpeed: 10,
    invaderNextLineJump: 15,
    invaderFpsStart: 2,
    invaderFps: 2,
    invaderHorizontalGap: 40,
    invaderVerticalGap: 25,
    hitsForSpeedUp: 3,
    bulletSpeed: 10,
    bulletWidth: 3,
    bulletHeight: 10,
    powerUpSpeed: 5,
    powerUpWidth: 15,
    powerUpHeight: 15,
    powerUpProb: 0.05,
}
const powers = ['normalShot','doubleShot','tripleShot'];
let currentShot = powers[0];

const player = {
    x: canvas.width / 2,
    y: canvas.height - 25,
    width: 15,
    height: 15,
    speed: 5
}

const pressedKeys = {
    a: false,
    d: false
};

let bullets= [];
let invader = [];
let powerUps = [];

let shootCooldown = true;
let running = false;

let lastTimeInvader = 0;

let lastTime = 0;
const fpsInterval = 1000 / 60;
function gameLoop(currentTime){
    if (!running) return;
    requestAnimationFrame(gameLoop);

    const elapsed = currentTime - lastTime;
    if (elapsed > fpsInterval) {
        lastTime = currentTime - (elapsed % fpsInterval);

        let fpsIntervalInvader = 1000 / settings.invaderFps;
        const elapsedInvader = currentTime - lastTimeInvader;
        if (elapsedInvader > fpsIntervalInvader) {
            lastTimeInvader = currentTime - (elapsedInvader % fpsIntervalInvader);
            moveInvader();
        }

        update();
        draw();
    }
}

function update(){
    if (pressedKeys.a && player.x > 0){
        player.x -= player.speed;
    }
    if (pressedKeys.d && player.x < canvas.width - player.width){
        player.x += player.speed;
    }
    bullets.forEach(bullet => {
        bullet.y -= bullet.speed;
        if (bullet.y < 0){
            bullets.splice(bullets.indexOf(bullet), 1);
            shootCooldown = true;
        }
        invader.forEach(invaderRow => {
            invaderRow.forEach((alien, index) => {
                if (bullet.x >= alien.x &&
                    bullet.x <= alien.x + alien.width &&
                    bullet.y >= alien.y &&
                    bullet.y <= alien.y + alien.height) {
                    shootCooldown = true;
                    bullets.splice(bullets.indexOf(bullet), 1);
                    invaderRow.splice(index,1);
                    if (invaderRow.length === 0){
                        invader.splice(invader.indexOf(invaderRow),1);
                    }
                    if (Math.random() < settings.powerUpProb){
                        powerUps.push(new PowerUp(alien.x, alien.y));
                    }
                }
            })
        })
        if (invader.length === 0){
            nextLevel();
        }
    })
    powerUps.forEach(powerUp => {
        powerUp.y += powerUp.speed;
        if (powerUp.y <= 0){
            powerUps.splice(powerUps.indexOf(powerUp),1);
        }
        if (powerUp.x >= player.x &&
            powerUp.x <= player.x + player.width &&
            powerUp.y >= player.y &&
            powerUp.y <= player.y + player.height){
            powerUps.splice(powerUps.indexOf(powerUp),1);
            console.log("POWER UP");
            if (currentShot !== powers[powers.length-1]){
                currentShot = powers[powers.indexOf(currentShot)+1]
            }
        }
    })
}
function nextLevel(){
    let newLevel = parseInt(level.innerText) + 1;
    level.innerText = `${newLevel}`;
    if (parseInt(highScore.innerText) <= newLevel){
        highScore.innerText = `${newLevel}`;
        localStorage.setItem('space-invader-highScore', `${newLevel}`);
    }
    if (settings.invaderFps !== 60){
        settings.invaderFps += 0.2;
    }
    setInvaderStart();
}
function gameLose(){
    lose.classList.remove('hidden');
    playButton.classList.remove('hidden');
}
function moveInvader(){
    let nextLine = false;
    invader.forEach(invaderRow => {
        if (invaderRow[0].x - settings.invaderSpeed <= 0 || invaderRow[invaderRow.length - 1].x + settings.invaderSpeed >= canvas.width - 30){
            nextLine = true;
        }
    })
    invader.forEach(invaderRow => {
        for(let j = 0; j < invaderRow.length; j++){
            if (nextLine){
                invaderRow[j].y += settings.invaderNextLineJump;
                invaderRow[j].speed *= -1;
            }
            invaderRow[j].x += invaderRow[j].speed;
            if (invaderRow[j].y >= canvas.height - 25 - player.height - invaderRow[j].height) {
                gameLose();
                running = false;
                break;
            }
        }
    })
}
function draw(){
    ctx.clearRect(0,0,canvas.width,canvas.height);

    ctx.fillStyle = 'rgb(30,41,224)';
    ctx.fillRect(player.x,player.y,player.width,player.height);

    ctx.fillStyle = 'rgb(255,0,0)';
    bullets.forEach(bullet => {
        ctx.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
    })

    ctx.fillStyle = 'rgb(173,144,0)';
    powerUps.forEach(powerUp => {
        ctx.fillRect(powerUp.x, powerUp.y, settings.powerUpWidth, settings.powerUpHeight);
    })

    invader.forEach(invaderRow => {
        invaderRow.forEach(alien => {
            drawInvader(alien);
        })
    })
}

function drawInvader(alien){
    ctx.drawImage(img, alien.x, alien.y);
}

function shoot(){
    if (bullets.length === 0){
        switch (currentShot){
            case 'normalShot': {
                bullets.push(new Bullet(player.x + player.width / 2, player.y));
                console.log('normal')
                break;
            }
            case 'doubleShot': {
                console.log('double')
                bullets.push(new Bullet(player.x, player.y))
                bullets.push(new Bullet(player.x + player.width, player.y))
                break;
            }
            case 'tripleShot': {
                bullets.push(new Bullet(player.x, player.y));
                bullets.push(new Bullet(player.x + player.width, player.y))
                bullets.push(new Bullet(player.x + player.width / 2, player.y))
                break;
            }
        }
    }
}

document.addEventListener('keydown', e => {
    if (!running) return;
    pressedKeys[e.key] = true;
})
document.addEventListener('keyup', e => {
    if (!running) return;
    pressedKeys[e.key] = false;
})
document.addEventListener('keypress', e => {
    if (!running) return;
    if (e.key === ' '){
        shoot();
    }
})

function start(){
    running = true;
    setInvaderStart();
    requestAnimationFrame(gameLoop);
}

function setInvaderStart(){
    invader = [];
    let start;
    let invaderRow;
    for (let i = 1; i < 6; i++){
        invaderRow = [];
        start = 25;
        while (start < 575){
            invaderRow.push(new Invader(start, i * settings.invaderVerticalGap))
            start += settings.invaderHorizontalGap;
        }
        invader.push(invaderRow);
    }
}
function resetGameState(){
    settings.invaderFps = settings.invaderFpsStart;
    player.x = canvas.width / 2;
    pressedKeys.a = false;
    pressedKeys.d = false;
    bullets = [];
    powerUps = [];
    currentShot = powers[0];
    lastTimeInvader = 0;
    lastTime = 0;
}

playButton.addEventListener('click', e => {
    canvas.classList.remove('hidden');
    levelContainer.classList.remove('hidden');
    gameTitle.classList.add('hidden');
    playButton.classList.add('hidden');
    lose.classList.add('hidden');
    resetGameState();
    start();
})