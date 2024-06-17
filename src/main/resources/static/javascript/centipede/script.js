const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');
const hpElement = document.getElementById('hp');
const levelContainer = document.getElementById('level-container');
const level = document.getElementById('level');
const hpContainer = document.getElementById('hp-container');
const playButton = document.getElementById('play');
const hpLoseElement = document.getElementById('hp-lose');
const loseElement = document.getElementById('lose');
const gameTitle = document.getElementById('game-title');
const highScoreElement = document.getElementById('high-score');
const highScoreContainer = document.getElementById('high-score-container');
const description = document.getElementById('game-description');

if (localStorage.getItem('centipede-highScore') === null){
    localStorage.setItem('centipede-highScore','1');
}else {
    highScoreElement.innerText = `${localStorage.getItem('centipede-highScore')}`;
}

let running = false;

hpElement.innerText = `3`;

const settings = {
    gridSize: 25,
    playerStartX: canvas.width / 2,
    playerStartY: canvas.height - 25,
    playerStartHP: 3,
    centipedeStartX: 0,
    centipedeStartY: 0,
    centipedeStartTailLength: 5,
    centipedeStartDirection: 'd',
    spiderProb: 0.005,
    wormProb: 0.005,
}

const gridSize = 25;

const player = new Player();

const centipede = new Centipede(0,0, 5,'d');

let centipedes = [centipede];
let worms = [];
let spiders = [];

let mushroom = {
    width: 25,
    height: 25,
    prob: 0.1,
}

let mushrooms = [];
let bullet = null;

const centipedeFps = 12; //12
const centipedeFpsInterval = 1000 / centipedeFps;
let centipedeLastTime = 0;

const gameFps = 60;
const fpsInterval = 1000 / gameFps;
let lastTime = 0;
function gameLoop(currentTime){
    if (!running) return;
    requestAnimationFrame(gameLoop);

    const elapsed = currentTime - lastTime;
    if (elapsed > fpsInterval){
        lastTime = currentTime - (elapsed % fpsInterval);

        const centipedeElapsed = currentTime - centipedeLastTime;
        if (centipedeElapsed > centipedeFpsInterval) {
            centipedeLastTime = currentTime - (centipedeElapsed % centipedeFpsInterval);
            updateCentipede();
            updateWorm();
            updateSpider();
        }

        update();
        draw();
    }
}
function updateCentipede(){
    centipedes.forEach(cent => {
        cent.move();

        centipedes.forEach(cent2 => {
            if (cent !== cent2 && cent.isTouching(cent2.x,cent2.y)){
                cent.turn();
                cent2.turn();
            }
        })
        mushrooms.forEach(mush => {
            if (cent.isTouching(mush.x, mush.y)){
                cent.turn();
            }
        })
    })
}
function updateSpider(){
    if (Math.random() < settings.spiderProb){
        const playerTileX = Math.floor(player.x / settings.gridSize) * gridSize;
        const playerTileY = Math.floor(player.y / settings.gridSize) * gridSize;
        spiders.push(new Spider(playerTileX, playerTileY));
    }
    spiders.forEach(spider => {
        spider.move();
    })
}
function updateWorm(){
    if (Math.random() < settings.wormProb){
        let x = 25 * Math.floor(Math.random() * Math.floor(canvas.width / 25) + 1);
        worms.push(new Worm(x));
    }
    worms.forEach(worm => {
        worm.move();
        if (worm.y >= canvas.height){
            worms.splice(worms.indexOf(worm),1);
        }
        if (worm.ready){
            worm.ready = false;
            mushrooms.push(new Mushroom(worm.x, worm.y));
        }
    })
}
function update(){
    player.move();

    if (player.keys[' ']){
        if (bullet == null){
            shoot();
        }
    }

    if (bullet != null) {
        bullet.move();
    }

    mushrooms.forEach(mush => {
        if (bullet != null && bullet.isTouching(mush.x, mush.y)){
            bullet = null;
            mush.hit();
            if (mush.hp === 0){
                mushrooms.splice(mushrooms.indexOf(mush),1);
            }
        }
        if (player.isTouching(mush.x, mush.y)){
            loseHp();
        }
    })

    worms.forEach(worm => {
        if (bullet != null && bullet.isTouching(worm.x,worm.y)){
            bullet = null;
            worms.splice(worms.indexOf(worm),1);
        }
        if (player.isTouching(worm.x,worm.y)){
            loseHp();
        }
    })

    spiders.forEach(spider => {
        if (bullet != null && bullet.isTouching(spider.x,spider.y)){
            bullet = null;
            spiders.splice(spiders.indexOf(spider),1);
            mushrooms.push(new Mushroom(spider.x, spider.y) );
        }
        if (player.isTouching(spider.x,spider.y)){
            loseHp();
        }
    })

    for (let i = 0; i < centipedes.length; i++) {
        if (player.isTouching(centipedes[i].x, centipedes[i].y)){
            loseHp();
        }
        for (let j = 0; j < centipedes[i].tail.length; j++) {
            if (player.isTouching(centipedes[i].tail[j][0], centipedes[i].tail[j][1])){
                loseHp();
            }
            if (bullet != null && bullet.isTouching(centipedes[i].tail[j][0],centipedes[i].tail[j][1])) {
                bullet = null;
                centipedeHit(centipedes[i], j);
            }
        }
        if (bullet != null && bullet.isTouching(centipedes[i].x, centipedes[i].y)) {
            bullet = null;
            centipedeHeadHit(centipedes[i]);
        }
    }

    if (bullet != null && bullet.y <= 0){
        bullet = null;
    }
}
function loseHp(){
    if (player.hp <= 0){
        gameOver();
    }else {
        player.loseHp();
        hpElement.innerText = `${player.hp}`;
        hpLoseElement.classList.remove('hidden');
    }
    running = false;
}
function gameOver(){
    loseElement.classList.remove('hidden');
    playButton.classList.remove('hidden');
    player.restartGame();
}
function resetBoard(){
    player.reset();
    centipede.reset();
    centipedes = [];
    centipedes.push(centipede);
    worms = [];
    spiders = [];
    centipede.tailLength = 5;
    settings.spiderProb = 0.005;
    settings.wormProb = 0.005;
    bullet = null;
}
function resetGame(){
    resetBoard();
    level.innerText = '1';
    hpElement.innerText = `${player.hp}`;
    resetBoard();
    mushrooms = [];
}
function centipedeHeadHit(cent){
    mushrooms.push(new Mushroom(cent.x, cent.y));

    if (cent.tailLength === 0){
        centipedes.splice(centipedes.indexOf(cent),1);
        if (centipedes.length === 0){
            nextLevel();
        }
    }else {
        cent.headHit();
    }
}
function nextLevel(){
    spiders.forEach(spider => {
        mushrooms.push(new Mushroom(spider.x, spider.y));
    })
    worms.forEach(worm => {
        mushrooms.push(new Mushroom(worm.x, worm.y));
    })
    resetBoard();
    settings.spiderProb += 0.001;
    settings.wormProb += 0.001;
    const newLevel = parseInt(level.innerText) + 1;
    level.innerText = `${newLevel}`;
    if (newLevel % 5 === 0){
        centipede.tailLength++;
    }
    if (newLevel > parseInt(localStorage.getItem('centipede-highScore'))){
        localStorage.setItem('centipede-highScore',`${newLevel}`);
        highScoreElement.innerText = `${localStorage.getItem('centipede-highScore')}`;
    }
}
function centipedeHit(cent, index){
    mushrooms.push(new Mushroom(cent.tail[index][0], cent.tail[index][1]));

    const newCent = cent.partHit(index);
    //NewCent should be null if hit part is the last part in tail
    if (newCent != null){
        centipedes.push(newCent);
    }
}
function draw(){
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = 'rgb(50,87,196)';
    ctx.fillRect(player.x, player.y, player.width, player.height);

    if (bullet != null){
        ctx.fillStyle = 'rgb(208,57,57)';
        ctx.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
    }

    centipedes.forEach(cent => {
        ctx.fillStyle = 'rgb(39,103,14)';
        ctx.fillRect(cent.x, cent.y, gridSize, gridSize);
        ctx.fillStyle = 'rgba(74,199,27,0.7)';

        cent.tail.forEach(part => {
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
    })
    worms.forEach(worm => {
        ctx.fillStyle = 'rgb(183,112,33)';
        ctx.fillRect(worm.x, worm.y, 25, 25);
    })
    spiders.forEach(spider => {
        ctx.fillStyle = 'rgb(54,54,51)';
        ctx.fillRect(spider.x, spider.y, 25, 25);
    })
    mushrooms.forEach(mush => {
        switch (mush.hp){
            case 3 : ctx.fillStyle = 'rgb(97,14,103)'; break;
            case 2 : ctx.fillStyle = 'rgba(97,14,103,0.8)'; break;
            case 1 : ctx.fillStyle = 'rgba(97,14,103,0.6)'; break;
        }
        ctx.fillRect(mush.x, mush.y, gridSize, gridSize);
    })
}
document.addEventListener('keydown',e => {
    if (e.key === ' ' && running === false && !hpLoseElement.classList.contains('hidden')){
        hpLoseElement.classList.add('hidden');
        resetBoard();
        running = true;
        requestAnimationFrame(gameLoop);
    }
    player.keys[e.key] = true;
})
document.addEventListener('keyup', e => {
    player.keys[e.key] = false;
})
function shoot(){
    bullet = new Bullet(player.x, player.y);
}
function start(){
    for (let i = gridSize; i < canvas.width; ){
        for (let j = gridSize; j < canvas.height - 4 * gridSize; ){
            if (Math.random() < mushroom.prob){
                mushrooms.push(new Mushroom(i,j))
            }
            j+= 25;
        }
        i += 25;
    }
    running = true;
    requestAnimationFrame(gameLoop);
}

document.getElementById('play').addEventListener('click', e => {
    gameTitle.classList.add('hidden');
    playButton.classList.add('hidden');
    canvas.classList.remove('hidden');
    hpContainer.classList.remove('hidden');
    levelContainer.classList.remove('hidden');
    loseElement.classList.add('hidden');
    description.classList.add('hidden');
    resetGame();
    start();
})