class Player{
    x = canvas.width / 2;
    y = canvas.height - 25;
    width = 15;
    height = 15;
    speed = 5;
    hp = 3;
    keys = {
        'w':false,
        'a':false,
        's':false,
        'd':false,
        ' ':false,
    };

    constructor() { }

    reset(){
        this.x = settings.playerStartX;
        this.y = settings.playerStartY;
    }

    move(){
        if (this.keys.w && this.y > 0){
            this.y -= this.speed;
        }
        if (this.keys.a && this.x > 0){
            this.x -= this.speed;
        }
        if (this.keys.s && this.y < canvas.height - this.height){
            this.y += this.speed;
        }
        if (this.keys.d && this.x < canvas.width - this.width){
            this.x += this.speed;
        }
    }

    loseHp(){
        this.hp--;
    }

    restartGame(){
        this.reset();
        this.hp = settings.playerStartHP;
    }

    isTouching(x, y){
        return this.x < x + gridSize &&
            this.x + this.width > x &&
            this.y < y + gridSize &&
            this.y + this.height > y;
    }

}