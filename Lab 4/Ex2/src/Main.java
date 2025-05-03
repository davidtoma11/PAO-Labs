public class Main {
    public static void main(String[] args) {
        // c) Instanțiere și apel toString()
        Triangle triangle = new Triangle("roșu", 5, 4);
        Circle circle = new Circle("verde", 2);
        System.out.println(triangle.toString()); // Triunghi: roșu 10.0
        System.out.println(circle.toString());   // Cerc: verde 12.566...

        // f) Vector de Form (upcasting)
        Form[] forms = new Form[3];
        forms[0] = new Triangle("albastru", 3, 6);
        forms[1] = new Circle("galben", 5);
        forms[2] = new Triangle("roșu", 5, 4);

        // Parcurgere fără instanceof (g)
        for (Form form : forms) {
            System.out.println(form.toString()); // Polimorfism: apelează metoda corectă
            if (form instanceof Triangle) {
                ((Triangle) form).printTriangleDimensions();
            } else if (form instanceof Circle) {
                ((Circle) form).printCircleDimensions();
            }
        }

        // h) Soluție fără instanceof (folosind design pattern Visitor sau metode adiționale)
        // Exemplu simplificat:
        for (Form form : forms) {
            form.printDimensions(); // Adăugăm o metodă abstractă în Form
        }
    }
}