class Street{
    y;
    height;
    right;
    cars;
    speed;

    constructor(y,height,right) {
        this.y = y;
        this.height = height;
        this.right = right;
        this.cars = [];
        this.speed = Math.floor(Math.random() * (settings.carMaxSpeed - settings.carMinSpeed + 1) + settings.carMinSpeed);
    }

    createCar(){
        if (this.right){
            this.cars.push(new Car(0,this.y + this.height / 2, this.speed));
        }else {
            this.cars.push(new Car(600,this.y + this.height / 2, -this.speed));
        }
    }

    removeCar(car){
        this.cars.splice(this.cars.indexOf(car), 1);
    }

    nextLevel(){
        this.speed = Math.floor(Math.random() * (settings.carMaxSpeed - settings.carMinSpeed + 1) + settings.carMinSpeed);
        this.cars.forEach(car => {
            if (this.right){
                car.speed = this.speed;
            }else {
                car.speed = -this.speed;
            }
        })
    }

}