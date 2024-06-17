class Spider{
    x = 0;
    y = 0;
    targetX;
    targetY;
    speed = settings.gridSize;

    constructor(targetX, targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    move(){
        if (this.x !== this.targetX){
            if (this.x > this.targetX){
                this.x -= this.speed;
            }else {
                this.x += this.speed;
            }
        }else if (this.y !== this.targetY){
            if (this.y > this.targetY){
                this.y -= this.speed;
            }else {
                this.y += this.speed;
            }
        }else {
            const playerTileX = Math.floor(player.x / settings.gridSize) * gridSize;
            const playerTileY = Math.floor(player.y / settings.gridSize) * gridSize;
            this.targetX = playerTileX;
            this.targetY = playerTileY;
        }
    }

}