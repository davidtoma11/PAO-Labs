public class Circle extends Form {
    private float radius;

    // Constructori
    public Circle() {
        super();
        this.radius = 0;
    }

    public Circle(String color, float radius) {
        super(color);
        this.radius = radius;
    }

    // Suprascriere getArea()
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    // Suprascriere toString()
    @Override
    public String toString() {
        return "Cerc: " + super.toString() + " " + getArea();
    }

    // Metodă specifică
    public void printCircleDimensions() {
        System.out.println("Rază: " + radius);
    }
    // În Circle:
    @Override
    public void printDimensions() {
        printCircleDimensions();
    }
}