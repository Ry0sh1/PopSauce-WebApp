class Centipede{
    x;
    y;
    tailLength;
    speed = settings.gridSize;
    verticalSpeed = settings.gridSize;
    direction;
    tail = [];

    constructor(x,y,tailLength,direction) {
        this.x = x;
        this.y = y;
        this.tailLength = tailLength;
        this.direction = direction;
    }

    move(){
        this.appendPart();
        if (this.direction === 'd'){
            this.x += this.speed;
        }
        if (this.direction === 'a'){
            this.x -= this.speed;
        }
        if (this.x < 0 || this.x >= canvas.width){
            this.turn();
        }
    }

    isTouching(x,y){
        return this.x === x && this.y === y;
    }

    turn(){
        if (this.y + this.verticalSpeed >= canvas.height || this.y + this.verticalSpeed < 0){
            this.verticalSpeed *= -1;
        }
        this.y += this.verticalSpeed;
        this.direction = this.direction === 'a' ? 'd' : 'a';
    }

    appendPart(){
        if (this.tailLength !== 0){
            if (this.tail.length === this.tailLength){
                this.removePartAtEnd();
            }
            this.addPartToBeginning(this.x, this.y);
        }
    }

    addPartToBeginning(x,y){
        this.tail.unshift([x,y]);
    }

    removePartAtEnd(){
        this.tail.pop();
    }

    removePartAtBeginning(){
        this.tail.shift();
    }

    headHit(){
        if (this.tailLength === 0) return;
        this.tailLength--;
        this.x = this.tail[0][0];
        this.y = this.tail[0][1];
        this.removePartAtBeginning();
        this.direction = this.direction === 'a' ? 'd' : 'a';
    }

    partHit(index){
        //If hit Part is last Element
        if (this.tail.length - 1 === index){
            this.tail.pop();
            this.tailLength--;
            return null;
        }

        const newHead = this.tail[index + 1];
        const newDir = this.direction === 'a' ? 'd' : 'a';
        const newTail = [];
        let newTailLength = 0;

        //If newCent does have a tail
        if (this.tail.length - index + 2 !== 0){
            for (let i = index + 2; i < this.tail.length; i++){
                newTail.unshift(this.tail[i]);
            }
            newTailLength = newTail.length;
        }
        const newCent = new Centipede(newHead[0],newHead[1],newTailLength,newDir);
        newCent.verticalSpeed = this.verticalSpeed;

        //If this does not have a tail
        if (index === 0){
            this.tailLength = 0;
            this.tail = [];
        }else {
            let newTail = [];
            for (let i = 0; i < index; i++){
                newTail.push(this.tail[i]);
            }
            this.tail = newTail;
            this.tailLength = newTail.length;
        }

        return newCent;
    }

    reset(){
        this.x = 0;
        this.y = 0;
        this.tailLength = 5;
        this.direction = 'd';
        this.tail = [];
        this.verticalSpeed = gridSize;
    }
}