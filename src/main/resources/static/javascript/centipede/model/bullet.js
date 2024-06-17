class Bullet{
    x;
    y;
    speed = 15;
    width = 3;
    height = 15;
    constructor(x,y) {
        this.x = x;
        this.y = y;
    }

    move(){
        this.y -= this.speed;
    }

    isTouching(x,y){
        return (this.x >= x && this.x <= x + gridSize) &&
            (this.y >= y && this.y <= y + gridSize);
    }

}