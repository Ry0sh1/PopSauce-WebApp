class Worm{
    x;
    y = 0;
    speed = 12.5;
    mushRoomCountdown = 0;
    ready = false;
    constructor(x) {
        this.x = x;
    }

    move(){
        this.y += this.speed;
        this.mushRoomCountdown++;
        if (this.mushRoomCountdown % 8 === 0){
            this.mushRoomCountdown = 0;
            this.ready = true;
        }
    }

}