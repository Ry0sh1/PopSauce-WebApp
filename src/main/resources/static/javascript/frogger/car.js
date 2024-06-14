class Car{
    x;
    y;
    speed;
    width;
    height;

    constructor(x,y,speed) {
        this.x = x;
        this.speed = speed;
        this.width = Math.floor(Math.random() * (settings.carMaxWidth - settings.carMinWidth + 1) + settings.carMinWidth);
        this.height = Math.floor(Math.random() * (settings.carMaxHeight - settings.carMinHeight + 1) + settings.carMinHeight);
        this.y = y - this.height / 2;
    }

}