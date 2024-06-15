class Bullet{
    width = settings.bulletWidth;
    height = settings.bulletHeight;
    speed = settings.bulletSpeed;
    x;
    y;
    constructor(x,y) {
        this.x = x - this.width;
        this.y = y;
    }
}