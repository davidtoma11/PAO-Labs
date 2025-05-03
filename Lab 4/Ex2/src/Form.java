public abstract class Form {
    protected String color;

    // Constructori
    public Form() {
        this.color = "necunoscută";
    }

    public Form(String color) {
        this.color = color;
    }

    // Metode
    public double getArea() {
        return 0;
    }

    @Override
    public String toString() {
        return color;
    }
    public abstract void printDimensions();
}