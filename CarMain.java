// Car class (matches the UML diagram)
class Car {

    // Attributes (private)
    private String make;
    private String model;
    private int speed;

    // Constructor
    public Car(String make, String model) {
        this.make = make;
        this.model = model;
        this.speed = 0;
    }

    // Methods (public)
    public void accelerate(int amount) {
        speed = speed + amount;
    }

    public void brake(int amount) {
        speed = speed - amount;
        if (speed < 0) {
            speed = 0;
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void displayInfo() {
        System.out.println(make + " " + model + " is going " + speed + " mph");
    }
}

// Program that uses the Car class
public class CarMain {
    public static void main(String[] args) {
        Car myCar = new Car("Toyota", "Camry");

        myCar.displayInfo();
        myCar.accelerate(60);
        myCar.displayInfo();
        myCar.brake(25);
        myCar.displayInfo();
        System.out.println("Current speed: " + myCar.getSpeed());
    }
}