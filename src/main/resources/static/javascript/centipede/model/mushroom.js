class Mushroom{
    x;
    y;
    hp = 3;
    constructor(x,y) {
        this.x = x;
        this.y = y;
    }

    hit(){
        this.hp--;
    }
}