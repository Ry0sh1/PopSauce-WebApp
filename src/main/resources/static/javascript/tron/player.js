class Player{
    x;
    y;
    direction;
    speed;
    keys;
    trail;
    height;
    width;
    trailArray = [];
    constructor(x,y,settings,direction,keys) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = settings.playerSpeed;
        this.keys = keys;
        this.height = settings.playerHeight;
        this.width = settings.playerWidth;
        this.trail = new PositionMap();
        this.addPosInTrails();
    }

    keyUp(key){
        if (this.keys.includes(key)){
            if (Math.abs(this.keys.indexOf(key) - this.keys.indexOf(this.direction)) !== 2){
                this.direction = key;
            }
        }
    }

    addPosInTrails(){
        if (this.trail.has([this.x, this.y]) === false){
            this.trail.set([this.x, this.y],true);
            this.trailArray.push([this.x, this.y]);
        }
    }

    move(canvas){
        this.addPosInTrails();
        if (this.direction === this.keys[0] && this.y > 0){
            this.y -= this.speed;
        }
        if (this.direction === this.keys[1] && this.x > 0){
            this.x -= this.speed;
        }
        if (this.direction === this.keys[2] && this.y < canvas.height - this.height){
            this.y += this.speed;
        }
        if (this.direction === this.keys[3] && this.x < canvas.width - this.width){
            this.x += this.speed;
        }
    }

    isTouching(trail) {
        return !!(this.trail.has([this.x, this.y]) || trail.has([this.x, this.y]));
    }

    reset(){
        this.trail = new PositionMap();
        this.trailArray = [];
    }

}