import java.util.Scanner;

public class ex1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Partea a) - Citirea numărului n între [0, 5]
        int n;
        do {
            System.out.print("Introduceți un numar între 0 și 5: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Numarul nu este intreg");
                scanner.next(); // Eliberăm intrarea invalidă
                System.out.print("Introduceți un numar între 0 și 5: ");
            }
            n = scanner.nextInt();
        } while (n < 0 || n > 5);

        // Partea b) - Citirea elementelor tabloului
        int[] array = new int[n];
        System.out.println("Introduceți " + n + " numere întregi:");
        for (int i = 0; i < n; i++) {
            System.out.print("Elementul " + (i + 1) + ": ");
            while (!scanner.hasNextInt()) {
                System.out.println("Numarul nu este intreg");
                scanner.next(); // Eliberăm intrarea invalidă
                System.out.print("Elementul " + (i + 1) + ": ");
            }
            array[i] = scanner.nextInt();
        }

        // Partea c) - Inversarea și afișarea tabloului
        System.out.print("Input: ");
        printArray(array);

        reverseArray(array);

        System.out.print("Output: ");
        printArray(array);

        scanner.close();
    }

    public static void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    // Metodă pentru afișarea tabloului
    public static void printArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}