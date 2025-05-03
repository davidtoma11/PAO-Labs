public class Triangle extends Form {
    private float height;
    private float base;

    // Constructori (cu reutilizare cod din clasa de bază)
    public Triangle() {
        super();
        this.height = 0;
        this.base = 0;
    }

    public Triangle(String color, float height, float base) {
        super(color);
        this.height = height;
        this.base = base;
    }

    // Suprascriere getArea()
    @Override
    public double getArea() {
        return (base * height) / 2;
    }

    // Suprascriere toString() (cu reutilizare cod din clasa de bază)
    @Override
    public String toString() {
        return "Triunghi: " + super.toString() + " " + getArea();
    }

    // Metoda equals() (criteriu: ariile egale și culori identice)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Triangle)) return false;
        Triangle other = (Triangle) obj;
        return this.getArea() == other.getArea() && this.color.equals(other.color);
    }

    // Metodă specifică
    public void printTriangleDimensions() {
        System.out.println("Bază: " + base + ", Înălțime: " + height);
    }
    @Override
    public void printDimensions() {
        printTriangleDimensions();
    }
}