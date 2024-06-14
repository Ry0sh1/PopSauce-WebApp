const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');
const level = document.getElementById('level');
const highScore = document.getElementById('high-score');
const gameTitle = document.getElementById('game-title');
const playButton = document.getElementById('play');
const resumeButton = document.getElementById('resume');
const levelContainer = document.getElementById('level-container');

highScore.innerText = localStorage.getItem("frogger-highScore");
if (localStorage.getItem("frogger-highScore") == null){
    highScore.innerText = `1`;
}

const settings = {
    carSpawnRate: 0.005,
    carMinHeight: 10,
    carMaxHeight: 20,
    carMinWidth: 20,
    carMaxWidth: 30,
    carMaxSpeed: 8,
    carMinSpeed: 3
}

const player = {
    x: canvas.width / 2,
    y: canvas.height - 30,
    width: 10,
    height: 10,
    speed: 10
}

const streets = [];

function generateStreets(){
    let start = canvas.height - 100;
    while (start > 0){
        streets.push(new Street(start,40,Math.random() < 0.5));
        start -= 40;
    }
}

function update(){
    streets.forEach(street => {
        if (Math.random() < settings.carSpawnRate){
            street.createCar();
        }
        street.cars.forEach(car => {
            car.x += car.speed;
            if (car.x < 0 || car.x > canvas.width){
                street.removeCar(car);
            }
        })
    })

    if (checkPlayerCollision()){
        player.y = canvas.height - 30;
    }
    if (player.y <= 20){
        let currentLevel = parseInt(level.innerText) + 1;

        if (parseInt(highScore.innerText) <= currentLevel){
            highScore.innerText = `${currentLevel}`;
            localStorage.setItem('frogger-highScore',`${currentLevel}`);
        }

        level.innerText = `${currentLevel}`;
        player.x = canvas.width / 2;
        player.y = canvas.height - 30;
        settings.carSpawnRate += 0.001;
        if (currentLevel % 10 === 0){
            settings.carMinSpeed++;
            settings.carMaxSpeed++;
        }
        streets.forEach(street => {
            street.nextLevel();
        })
    }
    draw();
    requestAnimationFrame(update);
}
function checkCollision(rect1, rect2) {
    return rect1.x < rect2.x + rect2.width &&
        rect1.x + rect1.width > rect2.x &&
        rect1.y < rect2.y + rect2.height &&
        rect1.y + rect1.height > rect2.y;
}

function checkPlayerCollision() {
    for (let street of streets) {
        for (let car of street.cars) {
            if (checkCollision(player, car)) {
                return true;
            }
        }
    }
}
function draw(){
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    streets.forEach(street => {
        ctx.fillStyle = 'rgb(105,105,105)';
        ctx.fillRect(0, street.y, canvas.width, street.height);

        ctx.strokeStyle = 'rgb(0,0,0)';
        ctx.beginPath();
        ctx.moveTo(0, street.y);
        ctx.lineTo(canvas.width, street.y);
        ctx.stroke();
        ctx.strokeStyle = 'rgb(255,255,255)';
        ctx.beginPath();
        ctx.lineWidth = 5;
        ctx.moveTo(0, street.y + 20);
        ctx.lineTo(canvas.width, street.y + 20);
        ctx.setLineDash([20, 15]);
        ctx.stroke();
        ctx.setLineDash([]);
    })
    streets.forEach(street => {
        street.cars.forEach(car => {
            ctx.fillStyle = 'red';
            ctx.fillRect(car.x, car.y, car.width, car.height);
        })
    })

    //Draw Green Area
    ctx.fillStyle = 'rgb(51,169,46)';
    ctx.fillRect(0, 540, canvas.width,140);

    //Draw Bottom Border of first Street
    ctx.strokeStyle = 'rgb(0,0,0)';
    ctx.beginPath();
    ctx.moveTo(0, 540);
    ctx.lineTo(canvas.width, 540);
    ctx.stroke();

    //Draw Water Area
    ctx.fillStyle = 'rgb(46,165,169)';
    ctx.fillRect(0, 0, canvas.width,20);

    ctx.fillStyle = 'blue';
    ctx.fillRect(player.x, player.y, player.width, player.height);
}

playButton.addEventListener('click', e => {
    buttonPress();
})

resumeButton.addEventListener('click', e => {
    buttonPress();
    let currentHighscore = parseInt(highScore.innerText);
    if (currentHighscore > 0){
        level.innerText = `${currentHighscore}`;
        settings.carSpawnRate += (0.001 * currentHighscore);
        settings.carMaxSpeed += Math.floor(currentHighscore/10);
        settings.carMinSpeed += Math.floor(currentHighscore/10);
    }
})

function buttonPress(){
    gameTitle.classList.add('hidden');
    playButton.classList.add('hidden');
    resumeButton.classList.add('hidden');
    canvas.classList.remove('hidden');
    levelContainer.classList.remove('hidden');
    document.addEventListener('keypress', e => {
        switch (e.key){
            case 'w' : {
                if (player.y >= player.speed){
                    player.y -= player.speed;
                }
                break;
            }
            case 'a': {
                if (player.x >= player.speed){
                    player.x -= player.speed;
                }
                break;
            }
            case 's': {
                if (player.y <= canvas.height - (player.speed + player.height)){
                    player.y += player.speed;
                }
                break;
            }
            case 'd': {
                if (player.x <= canvas.width - (player.speed + player.height)){
                    player.x += player.speed;
                }
                break;
            }
        }
    })
    generateStreets();
    update();
}